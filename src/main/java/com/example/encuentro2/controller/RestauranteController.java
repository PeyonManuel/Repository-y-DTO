package com.example.encuentro2.controller;

import com.example.encuentro2.handle.MensajeVacio;
import com.example.encuentro2.handle.RecursoNoExistente;
import com.example.encuentro2.model.Restaurante;
import com.example.encuentro2.services.RestauranteService;
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
public class RestauranteController {

    private static final Logger logger = LoggerFactory.getLogger(RestauranteController.class);
    @Autowired
    RestauranteService restauranteService;

    @PostMapping(path = "/restaurantes")
    private Restaurante crearRestaurante(@RequestBody @Validated Restaurante nuevoRestaurante) throws MensajeVacio {
        logger.info("Cargando peticion POST" + nuevoRestaurante.toString());
        return restauranteService.crearRestaurante(nuevoRestaurante);
    }

    @PostMapping(path = "/restaurantes/map")
    private Map<String, Restaurante> restauranteMap(@RequestBody @Validated String restaurante) throws JsonProcessingException {
        return restauranteService.restauranteAMap(restaurante);
    }

    @GetMapping(path = "/restaurantes/{id}")
    private Restaurante getRestauranteById(@PathVariable String id) throws ResponseStatusException, JsonProcessingException, RecursoNoExistente {
            try {
                return restauranteService.getRestauranteById(id);
            }catch(RecursoNoExistente recursoNoExistente){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, recursoNoExistente.getMessage());
            }
    }

    @PutMapping(path = "/restaurantes")
    private Restaurante updateRestaurante(@RequestBody @Validated Restaurante restaurante) throws ResponseStatusException, JsonProcessingException, RecursoNoExistente {
        try {
            return restauranteService.updateRestaurante(restaurante);
        }catch(RecursoNoExistente recursoNoExistente){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, recursoNoExistente.getMessage());
        }
    }

    @DeleteMapping(path = "/restaurantes/{nombre}")
    private void deleteRestaurante(@PathVariable String nombre) throws RecursoNoExistente {
        try{
            restauranteService.deleteByName(nombre);
        }catch(RecursoNoExistente recursoNoExistente){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, recursoNoExistente.getMessage());
        }

    }

}