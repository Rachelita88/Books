package com.afonso.raquel.books;

/**
 * Created by Raquel on 04/09/2017.
 */
//Definimos la clase Author, que contendrá el paramento nombre

public class Author {
    private String nombre;

    public Author(){
        //Es obligatorio incluir constructor por defecto
    }

    public Author(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre(){
        return nombre;
    }
    public void setNombre(){
        this.nombre = nombre;
    }

}
