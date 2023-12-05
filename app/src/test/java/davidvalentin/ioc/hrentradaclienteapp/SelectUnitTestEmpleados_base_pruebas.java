package davidvalentin.ioc.hrentradaclienteapp;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import modelo.Empleados;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

//info: https://www.nestoralmeida.com/testing-con-junit-4/
public class SelectUnitTestEmpleados_base_pruebas {


     String ip = "192.168.1.12";
     int puerto = 8888;
     String codigo = "";


    @Test
    public  void selectEmpleado_dni_OK_01(){
        //como todo son variables estaticas.. todo queda guardado. buferes..codigo..etc
        try {
            Socket socket = new Socket(ip, puerto);
            BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter escriptor = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            ObjectInputStream perEnt;
            ArrayList<Empleados> listaEmpleados = new ArrayList<>();
            String mensaje;

            String mensajeServer = lector.readLine();
            //enviamos los datos de un usuario administrador
            escriptor.write("admin,admin");
            escriptor.newLine();
            escriptor.flush();
            String codigo = lector.readLine();

            String palabra = codigo+",0"+",0"+",dni"+",12345678A"+",0";
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            Object receivedData = perEnt.readObject();
            listaEmpleados = (ArrayList) receivedData;

            assertEquals("Administrador",listaEmpleados.get(0).getNom());
            escriptor.write("exit");
            escriptor.newLine();
            escriptor.flush();
            //leemos lo ultimo que no envía el server
            mensajeServer = lector.readLine();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Test
    public  void selectEmpleado_dni_OK_2(){
        //como todo son variables estaticas.. todo queda guardado. buferes..codigo..etc
        try {
            Socket socket = new Socket(ip, puerto);
            BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter escriptor = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            ObjectInputStream perEnt;
            ArrayList<Empleados> listaEmpleados = new ArrayList<>();
            String mensaje;

            String mensajeServer = lector.readLine();
            //enviamos los datos de un usuario administrador
            escriptor.write("admin,admin");
            escriptor.newLine();
            escriptor.flush();
            String codigo = lector.readLine();

            String palabra = codigo+",0"+",0"+",dni"+",12345678A"+",0";
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            Object receivedData = perEnt.readObject();
            listaEmpleados = (ArrayList) receivedData;


            assertEquals("Administrador",listaEmpleados.get(0).getNom());
            escriptor.write("exit");
            escriptor.newLine();
            escriptor.flush();
            //leemos lo ultimo que no envía el server
            mensajeServer = lector.readLine();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Test
    public  void selectEmpleado_dni_NG_01(){
        //como todo son variables estaticas.. todo queda guardado. buferes..codigo..etc
        try {
            Socket socket = new Socket(ip, puerto);
            BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter escriptor = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            ObjectInputStream perEnt;
            ArrayList<Empleados> listaEmpleados = new ArrayList<>();
            String mensaje;

            String mensajeServer = lector.readLine();
            //enviamos los datos de un usuario administrador
            escriptor.write("admin,admin");
            escriptor.newLine();
            escriptor.flush();
            String codigo = lector.readLine();

            String palabra = codigo+",0"+",0"+",dni"+",12345678A"+",0";
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            Object receivedData = perEnt.readObject();

            if (receivedData instanceof List) {
                listaEmpleados = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            } else {
                mensaje="Datos inesperados recibidos del servidor";
            }

            assertEquals("Administrador",listaEmpleados.get(0).getNom());
            escriptor.write("exit");
            escriptor.newLine();
            escriptor.flush();
            //leemos lo ultimo que no envía el server
            mensajeServer = lector.readLine();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public  void selectEmpleado_dni_NG_02(){
        //como todo son variables estaticas.. todo queda guardado. buferes..codigo..etc
        try {
            Socket socket = new Socket(ip, puerto);
            BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter escriptor = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            ObjectInputStream perEnt;
            ArrayList<Empleados> listaEmpleados = new ArrayList<>();
            String mensaje;

            String mensajeServer = lector.readLine();
            //enviamos los datos de un usuario administrador
            escriptor.write("admin,admin");
            escriptor.newLine();
            escriptor.flush();
            String codigo = lector.readLine();

            String palabra = codigo+",0"+",0"+",dni"+",12345678A"+",0";
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            Object receivedData = perEnt.readObject();

            if (receivedData instanceof List) {
                listaEmpleados = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            } else {
                mensaje="Datos inesperados recibidos del servidor";
            }

            assertEquals("Administrador",listaEmpleados.get(0).getNom());
            escriptor.write("exit");
            escriptor.newLine();
            escriptor.flush();
            //leemos lo ultimo que no envía el server
            mensajeServer = lector.readLine();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}