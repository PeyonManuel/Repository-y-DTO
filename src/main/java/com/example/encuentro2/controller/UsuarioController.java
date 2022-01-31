package com.example.encuentro2.controller;

import com.example.encuentro2.factoryMethod.UsuarioFactory;
import com.example.encuentro2.handle.MensajeVacio;
import com.example.encuentro2.handle.RecursoNoExistente;
import com.example.encuentro2.model.Usuario;
import com.example.encuentro2.services.UsuarioService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping(path = "/api")
public class UsuarioController {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);
    @Autowired
    UsuarioService usuarioService;

    @PostMapping(path = "/usuario")
    private Usuario crearUsuario(@RequestBody @Validated Usuario nuevoUsuario) throws MensajeVacio {
        logger.info("Cargando peticion POST" + nuevoUsuario.toString());
        return usuarioService.crearUsuario(nuevoUsuario);
    }


    @GetMapping(path = "/usuario/{id}")
    private Usuario getUsuarioById(@PathVariable String id) throws ResponseStatusException, JsonProcessingException, RecursoNoExistente {
            try {
                return usuarioService.getUsuarioById(id);
            }catch(RecursoNoExistente recursoNoExistente){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, recursoNoExistente.getMessage());
            }
    }

    @PutMapping(path = "/usuario")
    private Usuario updateUsuario(@RequestBody @Validated Usuario usuario) throws ResponseStatusException, JsonProcessingException, RecursoNoExistente {
        try {
            return usuarioService.updateUsuario(usuario);
        }catch(RecursoNoExistente recursoNoExistente){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, recursoNoExistente.getMessage());
        }
    }

    @DeleteMapping(path = "/usuario/{nombre}")
    private void deleteUsuario(@PathVariable String nombre) throws RecursoNoExistente {
        try{
            usuarioService.deleteByName(nombre);
        }catch(RecursoNoExistente recursoNoExistente){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, recursoNoExistente.getMessage());
        }

    }

}