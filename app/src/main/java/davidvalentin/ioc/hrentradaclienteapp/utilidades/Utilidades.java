package davidvalentin.ioc.hrentradaclienteapp.utilidades;

import java.util.ArrayList;
import java.util.List;

import modelo.Empleados;
import modelo.Empresa;
import modelo.Jornada;
import modelo.Users;

/**
 *  Esta clase simplemente contiene variables que se utilizarán (algunas) durante todo el programa
 *  Se podría hacer de otra manera, es decir crear un clase que no solo fuesen variables estaticas
 *  y luego instanciar esta clase y ir pasandola como parámetro pero he preferido hacerlo así.
 */
public class Utilidades {
    public static final String ip = "127.0.0.1"; // variable static para la ip
    public static final String puerto = "8888"; // variable static para el puerto
    public static  String codigo = "0"; //codigo por defecto
    public static SocketManager socketManager = null;
    public static int tipoUser  = 1;//por defecto será solo usuario normal y no admin
    public static String nombreUser = "";
    //variables necesarias para los diferentes arraylist
    public static ArrayList<Empleados> listaEmpleados = new ArrayList<>();
    public static ArrayList<Users> listaUsers = new ArrayList<>();
    public static ArrayList<Empresa> listaEmpresas = new ArrayList<>();
    public static ArrayList<Jornada> listaJornadas = new ArrayList<>();

    public static String mensajeDelServer = ""; //mensaje del server que iremos mostrando al usuario





}
