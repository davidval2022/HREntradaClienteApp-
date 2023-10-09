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

import java.util.ArrayList;
import java.util.List;


/**
 *
 *
 */
public class Personal {
    List<Empleados> personal = new ArrayList<Empleados>();
    List<Usuarios> vigilantes = new ArrayList<Usuarios>();

    public Personal() {
        crearPersonal();
    }


    public void crearPersonal(){
        //Creamos los Empleados
        Empleados juan = new Empleados("12345678L","Juan","Martinez Soria","Nissan","Seguridad",123456,"juanmartinez@hotmial.com",66666668);
        Empleados juan2 = new Empleados("22345678M","Juan","Fernandez Soria","Nissan","Mantenimiento",123456,"juanfernandez@hotmial.com",55666668);
        Empleados perico = new Empleados("32345678P","Perico","Soria Martin","Hub tech","Produccion",123456,"pericosoria@hotmial.com",44666668);
        Empleados andres = new Empleados("42345678P","Andres","Martin Soria","Hub tech","Seguridad",123456,"andresmartin@hotmial.com",33666668);
        personal.add(juan);
        personal.add(juan2);
        personal.add(perico);
        personal.add(andres);
        //creamos los usuarios de tipo Usuarios
        Usuarios userAndres = new Usuarios("andres","1234",0,"42345678P");//adminostrador
        Usuarios userJuan = new Usuarios("juan","1234",1,"12345678L");//usuario
        vigilantes.add(userAndres);
        vigilantes.add(userJuan);

    }


    public List<Empleados> buscar(String word) {
        List<Empleados> personas = new ArrayList<Empleados>();
        Empleados p = new Empleados();
        for(int i=0; i<personal.size();i++){
            if(word.equalsIgnoreCase(personal.get(i).getNom())){
                personas.add(personal.get(i));
            }
        }

        return personas;
    }

    public List<Empleados> todo() {
        return personal;
    }

    public List<Usuarios> buscarUser(String word) {
        List<Usuarios> personas = new ArrayList<Usuarios>();
        Usuarios p = new Usuarios();
        for(int i=0; i<vigilantes.size();i++){
            if(word.equalsIgnoreCase(vigilantes.get(i).getLogin())){
                personas.add(vigilantes.get(i));
            }
        }

        return personas;
    }

    public String[] InicioSesion(String login, String pass) {
        String[] codigosLogin = new String[2];
        codigosLogin[1] = "-1";
        String codigo = "-1";
        String dni = "0";
        int numeroAleatorio = 0;
        //  int valorEntero = (int) (Math.floor(Math.random()*(N-M+1)+M));
        for(int i=0; i<vigilantes.size();i++){
            if(login.equalsIgnoreCase(vigilantes.get(i).getLogin()) && pass.equals(vigilantes.get(i).getPass())){
                numeroAleatorio = (int) (Math.floor(Math.random()*(1-99999+1)+99999));
                if(vigilantes.get(i).getNumtipe() == 0){
                    codigo = "A"+String.valueOf(numeroAleatorio);
                }else if(vigilantes.get(i).getNumtipe() == 1){
                    codigo = "U"+String.valueOf(numeroAleatorio);
                }
                dni = vigilantes.get(i).getDni();
                codigosLogin[0] = dni;
                codigosLogin[1] = codigo;
            }
        }


        return codigosLogin;
    }


    public List<Usuarios> todoUser() {
        return vigilantes;
    }


}
