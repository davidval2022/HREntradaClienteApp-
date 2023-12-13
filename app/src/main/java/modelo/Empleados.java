package modelo;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author david
 * Es una clase persistente y representa  en la BD a la tabla Empleados
 */
import java.io.Serializable;

@SuppressWarnings("serial")


public class Empleados implements Serializable, Comparable<Empleados> {
    private static final long serialVersionUID = 6529685098267757690L;

    private String dni;
    private String nom;

    private String apellido;
    private String nomempresa;
    private String departament;
    private String codicard;
    private String mail;
    private String telephon;

    public Empleados(String dni, String nom, String apellido, String nomempresa, String departament, String codicard, String mail, String telefono) {
        this.dni = dni;
        this.nom = nom;
        this.apellido = apellido;
        this.nomempresa = nomempresa;
        this.departament = departament;
        this.codicard = codicard;
        this.mail = mail;
        this.telephon = telefono;
    }

    public Empleados(String dni, String nom, String apellido, String nomempresa) {
        this.dni = dni;
        this.nom = nom;
        this.apellido = apellido;
        this.nomempresa = nomempresa;
    }



    public Empleados() {

    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getNomempresa() {
        return nomempresa;
    }

    public void setNomempresa(String nomempresa) {
        this.nomempresa = nomempresa;
    }

    public String getDepartament() {
        return departament;
    }

    public void setDepartament(String departament) {
        this.departament = departament;
    }

    public String getCodicard() {
        return codicard;
    }

    public void setCodicard(String codicard) {
        this.codicard = codicard;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getTelephon() {
        return telephon;
    }

    public void setTelephon(String telephon) {
        this.telephon = telephon;
    }

    @Override
    public int compareTo(Empleados t) {
        String a = new String(String.valueOf(this.getNom()));
        String b = new String(String.valueOf(t.getNom()));
        return a.toLowerCase().compareTo(b.toLowerCase());
    }




}

