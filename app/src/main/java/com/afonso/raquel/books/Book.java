package com.afonso.raquel.books;

/**
 * Created by Raquel on 25/09/2017.
 */
//Definimos la clase Book, que contendrá el paramento titulo

public class Book {
    private String titulo;

    public Book(){
        // Es obligatorio incluir un contructor por defecto
    }

    public Book(String titulo){
        this.titulo = titulo;
    }

    public String getTitulo(){
        return titulo;
    }
    public void setTitulo(){
        this.titulo = titulo;
    }
}
