package com.example.encuentro2.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;


@Getter
@Setter
@ToString
@Document("restaurantes")
public class Usuario implements Serializable {
    @Id
    private String id;
    private String nombre;
    private String type;
    private Date fechaCreacion;
    private Date fechaModificacion;

    public Usuario(String nombre, String type) {
        this.nombre = nombre;
        this.type = type;
        this.fechaCreacion = new Date();
        this.fechaModificacion = new Date();
    }
}
