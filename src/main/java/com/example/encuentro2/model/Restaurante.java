package com.example.encuentro2.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;


@Getter
@Setter
@ToString
@Document("restaurantes")
public class Restaurante implements Serializable {
    @Id
    private String id;
    private String nombre;
    private Ciudad ciudad;
    private Menu menuComida;
    private Menu menuBebidas;
    private String hora_inicio;
    private String hora_fin;
    private Date fecha_creacion;
}
