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


public class Usuarios implements Serializable,Comparable<Usuarios>{
    private String login;
    private String pass;
    private int numtipe;
    private String dni;

    public Usuarios(String login, String pass, int numTipe, String dni) {
        this.login = login;
        this.pass = pass;
        this.numtipe = numTipe;
        this.dni = dni;
    }


    public Usuarios(){

    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public int getNumtipe() {
        return numtipe;
    }

    public void setNumtipe(int numtipe) {
        this.numtipe = numtipe;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    @Override
    public int compareTo(Usuarios t) {
        String a = new String(String.valueOf(this.getLogin()));
        String b = new String(String.valueOf(t.getLogin()));
        return a.toLowerCase().compareTo(b.toLowerCase());
    }




}
