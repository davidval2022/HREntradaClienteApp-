package davidvalentin.ioc.hrentradaclienteapp.utilidades;

import java.util.ArrayList;
import java.util.List;

import modelo.Empleados;

public class Utilidades {
    public static final String ip = "127.0.0.1";
    public static final String puerto = "8888";
    public static  String codigo = "0";
    public static SocketManager socketManager = null;
    public static int tipoUser  = 1;//por defecto será solo usuario
    public static String nombreUser = "";
    public static List<Empleados> listaEmpleados = new ArrayList<>();



}
