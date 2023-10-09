package model;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author david
 */

import java.io.Serializable;

@SuppressWarnings("serial")
public class Persona implements Serializable {

    String nombre;
    int edad;

    public Persona(String nombre, int edad) {
        super();
        this.nombre = nombre;
        this.edad = edad;
    }
    public Persona() { super(); }

    public String getNombre() {  return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public int getEdad() { return edad; }
    public void setEdad(int edad) {this.edad = edad; }

}// Fin de Persona

