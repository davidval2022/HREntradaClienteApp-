package davidvalentin.ioc.hrentradaclienteapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
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
public class SelectUnitTest {


    static String ip = "192.168.1.12";
    static int puerto = 8888;
    static String codigo = "";
    static Socket socket;
    static BufferedReader lector;
    static BufferedWriter escriptor;

    /*
         1 - dni OK  ---------------------- selectEmpleado_dni_OK_01
         2 - dni OK  ---------------------- selectEmpleado_dni_OK_02
         3 - dni NG (malo.. no existe)----- selectEmpleado_dni_NG
         4 - nombre OK --------------------
         5 - nombre NG --------------------
         6 - apellido OK ------------------
         7 -

     */

    /**
     * Lo primero antes de comenzar los test
     */
    @BeforeClass
    public  static void login(){

        try {
            socket = new Socket(ip, puerto);
            lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            escriptor = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            String mensajeServer = lector.readLine();
            //enviamos los datos de un usuario administrador
            escriptor.write("admin,admin");
            escriptor.newLine();
            escriptor.flush();
            codigo = lector.readLine();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Al final, cuando finalicen los test nos desconectamos
     */
    @AfterClass
    public static  void logout(){

        //cerramos sesion
        try {
            escriptor.write("exit");
            escriptor.newLine();
            escriptor.flush();
            //leemos el último mensaje  que nos envia el server para poder cerrar correctamente
            String mensajeServer = lector.readLine();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * Test nª1. Comprobamos que el dni concuerda con su correspondiente empleado
     */

    @Test
    public  void selectEmpleado_dni_OK_01(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Empleados> listaEmpleados = new ArrayList<>();

            String palabra = codigo+",0"+",0"+",dni"+",12345678A"+",0";
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            Object receivedData = perEnt.readObject();
            listaEmpleados = (ArrayList) receivedData;

            assertEquals("Administrador",listaEmpleados.get(0).getNom());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test nª2. Comprobamos que el dni concuerda con su correspondiente empleado
     */
    @Test
    public  void selectEmpleado_dni_OK_02(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Empleados> listaEmpleados = new ArrayList<>();

            String palabra = codigo+",0"+",0"+",dni"+",84574589A"+",0";
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            Object receivedData = perEnt.readObject();
            listaEmpleados = (ArrayList) receivedData;

            assertEquals("Juan",listaEmpleados.get(0).getNom());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test nª3. En este caso comprobamos que falla ya que el DNI no existe
     * Para ello buscamos un dni y si no existe el arrayList donde se guardan
     * debe estar vacio
     */
    @Test
    public  void selectEmpleado_dni_NG(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Empleados> listaEmpleados = new ArrayList<>();
            String mensaje;

            String palabra = codigo+",0"+",0"+",dni"+",11111111W"+",0";
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            Object receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                listaEmpleados = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            assertTrue(listaEmpleados.isEmpty());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }




}