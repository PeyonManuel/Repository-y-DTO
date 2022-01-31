package com.example.encuentro2.factoryMethod;

import com.example.encuentro2.model.Admin;
import com.example.encuentro2.model.Client;
import com.example.encuentro2.model.Editor;
import com.example.encuentro2.model.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UsuarioFactory {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioFactory.class);

    public static Usuario factoryCrearUsuario(String nombre, String type){
        switch(type){
            case "ADMIN":
                return new Admin(nombre, type);
            case "EDITOR":
                return new Editor(nombre, type);
            case "CLIENT":
                return new Client(nombre, type);
            default:
                return null;
        }
    }
}
