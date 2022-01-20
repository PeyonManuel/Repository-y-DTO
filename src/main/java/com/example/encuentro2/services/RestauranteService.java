package com.example.encuentro2.services;

import com.example.encuentro2.model.Restaurante;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class RestauranteService {
    @Autowired
    private MongoTemplate mongoTemplate;
    private final ObjectMapper mapper;

    private static final Logger logger = LoggerFactory.getLogger(RestauranteService.class);

    @PostConstruct
    private void PostConstruct() {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
    }

    private void mappearAString(Restaurante restaurante) throws JsonProcessingException {
        String restauranteString = mapper.writeValueAsString(restaurante);
        logger.info("Restaurante mapeado a string: \n" + restauranteString);
    }

    private Map<String, Restaurante> mappearAMap(Restaurante restaurante) throws JsonProcessingException {
        String restauranteString = mapper.writeValueAsString(restaurante);
        Map<String, Restaurante> restauranteMap = mapper.readValue(restauranteString, Map.class);
        logger.info("Restaurante mapeado a map: \n" + restauranteMap);
        return restauranteMap;
    }

    private Map<String, Restaurante> mappearDeStringAMap(String restaurante) throws JsonProcessingException {
        Map<String, Restaurante> restauranteMap = mapper.readValue(restaurante, Map.class);
        logger.info("Restaurante mapeado a map: \n" + restauranteMap);
        return restauranteMap;
    }

    private void mappearAClase(Restaurante restaurante) throws JsonProcessingException {
        String restauranteString = mapper.writeValueAsString(restaurante);
        var restauranteClase = mapper.readValue(restauranteString, Restaurante.class);
        logger.info("Restaurante mapeado a clase: \n" + restauranteClase);
    }

    public Restaurante crearRestaurante(Restaurante restaurante){
        logger.info("Guardando");
        try {
            mappearAString(restaurante);
            mappearAClase(restaurante);
            mappearAMap(restaurante);
            return mongoTemplate.save(restaurante, "restaurantes");
        } catch (JsonProcessingException e) {
            logger.error("Error convirtiendo a string", e);
        }
        return restaurante;
    }

    public Map<String, Restaurante> restauranteAMap(String restaurante) throws JsonProcessingException {
        return mappearDeStringAMap(restaurante);
    }

}
