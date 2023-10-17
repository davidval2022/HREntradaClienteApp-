package modelo;

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


public class Empleados implements Serializable, Comparable<Empleados> {
    private static final long serialVersionUID = 6529685098267757690L;

    private String dni;
    private String nom;
    private String apellido;
    private String nomEmpresa;
    private String departament;
    private int codiCard;
    private String mail;
    private int telefono;

    public Empleados(String dni, String nom, String apellido, String nomEmpresa, String departament, int codiCard, String mail, int telefono) {
        this.dni = dni;
        this.nom = nom;
        this.apellido = apellido;
        this.nomEmpresa = nomEmpresa;
        this.departament = departament;
        this.codiCard = codiCard;
        this.mail = mail;
        this.telefono = telefono;
    }

    public Empleados(String dni, String nom, String apellido, String nomEmpresa) {
        this.dni = dni;
        this.nom = nom;
        this.apellido = apellido;
        this.nomEmpresa = nomEmpresa;
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

    public String getNomEmpresa() {
        return nomEmpresa;
    }

    public void setNomEmpresa(String nomEmpresa) {
        this.nomEmpresa = nomEmpresa;
    }

    public String getDepartament() {
        return departament;
    }

    public void setDepartament(String departament) {
        this.departament = departament;
    }

    public int getCodiCard() {
        return codiCard;
    }

    public void setCodiCard(int codiCard) {
        this.codiCard = codiCard;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }

    @Override
    public int compareTo(Empleados t) {
        String a = new String(String.valueOf(this.getNom()));
        String b = new String(String.valueOf(t.getNom()));
        return a.toLowerCase().compareTo(b.toLowerCase());
    }




}

