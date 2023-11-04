package davidvalentin.ioc.hrentradaclienteapp.utilidades;

import java.util.ArrayList;
import java.util.List;

import modelo.Empleados;
import modelo.Empresa;
import modelo.Jornada;
import modelo.Users;

public class Utilidades {
    public static final String ip = "127.0.0.1";
    public static final String puerto = "8888";
    public static  String codigo = "0";
    public static SocketManager socketManager = null;
    public static int tipoUser  = 1;//por defecto será solo usuario
    public static String nombreUser = "";
    public static ArrayList<Empleados> listaEmpleados = new ArrayList<>();
    public static ArrayList<Users> listaUsers = new ArrayList<>();
    public static ArrayList<Empresa> listaEmpresas = new ArrayList<>();
    public static ArrayList<Jornada> listaJornadas = new ArrayList<>();
    public static String mensajeDelServer = "";
    public static String mensajeCliente = "";




}
