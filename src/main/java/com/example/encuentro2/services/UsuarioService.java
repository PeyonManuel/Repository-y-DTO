package com.example.encuentro2.services;

import com.example.encuentro2.factoryMethod.UsuarioFactory;
import com.example.encuentro2.handle.RecursoNoExistente;
import com.example.encuentro2.model.Usuario;
import com.example.encuentro2.repository.UsuarioMongoRepository;
import com.example.encuentro2.repository.UsuarioRedisRepository;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static org.springframework.data.mongodb.core.query.Criteria.where;


@Service
@RequiredArgsConstructor
public class UsuarioService {

    @Autowired
    MongoTemplate mongoTemplate;

    private final ObjectMapper mapper;
    @Autowired
    private UsuarioRedisRepository usuarioRedisRepository;
    @Autowired
    private UsuarioMongoRepository usuarioMongoRepository;
    UsuarioFactory usuarioFactory;

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    @PostConstruct
    private void PostConstruct() {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
    }

    private String mappearAString(Usuario usuario) throws JsonProcessingException {
        String usuarioString = mapper.writeValueAsString(usuario);
        logger.info("Usuario mapeado a string: \n" + usuarioString);
        return usuarioString;
    }

    private Map<String, Usuario> mappearAMap(String usuarioString) throws JsonProcessingException {
        Map<String, Usuario> usuarioMap = mapper.readValue(usuarioString, Map.class);
        logger.info("Usuario mapeado a map: \n" + usuarioMap);
        return usuarioMap;
    }

    private Usuario mappearAClase(String usuarioString) throws JsonProcessingException {
        var usuarioClase = mapper.readValue(usuarioString, Usuario.class);
        logger.info("Usuario mapeado a clase: \n" + usuarioClase);
        return usuarioClase;
    }

    public Usuario crearUsuario(Usuario usuario){
        logger.info("Guardando");
        Usuario usuarioDeFactory = usuarioFactory.factoryCrearUsuario(usuario.getNombre(), usuario.getType());
        if(usuarioDeFactory == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ingrese un type de usuario valido");
        }
        try {
            String usuarioString = mappearAString(usuarioDeFactory);
            mappearAClase(usuarioString);
            mappearAMap(usuarioString);
            Usuario usuarioNuevo =  usuarioMongoRepository.save(usuarioDeFactory, "usuarios");
            usuarioRedisRepository.save(mappearAString(usuarioDeFactory), usuarioDeFactory.getId());
            return usuarioNuevo;
        } catch (JsonProcessingException e) {
            logger.error("Error convirtiendo a string", e);
        }
        return usuario;
    }

   public  Usuario getUsuarioById(String id) throws JsonProcessingException, RecursoNoExistente {
        try {
            logger.info("Con redis");
            return mappearAClase(usuarioRedisRepository.findById(id));
        }catch (IllegalArgumentException e){
            Usuario usuario = usuarioMongoRepository.findById(id);
            try{
                logger.info("Con mongo");
                usuarioRedisRepository.save(mappearAString(usuario), usuario.getId());
                return usuario;
            }catch(NullPointerException e2){
                throw new RecursoNoExistente("No se ha encontrado un usuario con ese id");
            }
        }
   }

    private Usuario actualizar(Usuario usuario){
        try {
            String usuarioString = mappearAString(usuario);
            logger.info(usuarioString);
            usuario.setFechaModificacion((new Date()));
            Usuario usuarioActualizado = usuarioMongoRepository.save(usuario, "usuarios");
            usuarioRedisRepository.save(mappearAString(usuarioActualizado), usuarioActualizado.getId());
            return usuarioActualizado;
        } catch (JsonProcessingException e) {
            logger.error("Error convirtiendo a string", e);
        }
        return usuario;
    }

    public  Usuario updateUsuario(Usuario usuario) throws JsonProcessingException, RecursoNoExistente {
        logger.info("Actualizando");
        Usuario usuarioDeFactory = usuarioFactory.factoryCrearUsuario(usuario.getNombre(), usuario.getType());
        if(usuarioDeFactory == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ingrese un type de usuario valido");
        }
        if(usuarioRedisRepository.findById(usuario.getId()) != null){
            return actualizar(mappearAClase(usuarioRedisRepository.findById(usuario.getId())));
        }else if(usuarioMongoRepository.findById(usuario.getId()) != null){
            return actualizar(usuarioMongoRepository.findById(usuario.getId()));
        }else {
            throw new RecursoNoExistente("No se ha encontrado el usuario que esta intentado actualizar");
        }
    }

    public void deleteByName(String nombre) throws RecursoNoExistente {
        try {
            Usuario usuario = usuarioMongoRepository.findOne((new Query(where("nombre").is(nombre))));
            usuarioMongoRepository.findAndRemove((new Query(where("nombre").is(usuario.getNombre()))));
            usuarioRedisRepository.delete(usuario.getId());
        }catch(NullPointerException n){
            throw new RecursoNoExistente("No se ha encontrado un usuario con ese nombre");
        }
    }

}
