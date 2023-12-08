package davidvalentin.ioc.hrentradaclienteapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import modelo.Empleados;

/**
 *
 * Test sobre la clase modelo Empleado (empleados)
 *
 */

//info: https://www.nestoralmeida.com/testing-con-junit-4/

public class EmpleadosUnitTest_2 {


    static String ip = "192.168.1.12";
    static int puerto = 8888;
    static String codigo = "";
    static Socket socket;
    static BufferedReader lector;
    static BufferedWriter escriptor;
    static String nombreTabla = "0";
    static String orden = "0";



    @BeforeClass
    public  static void login(){

        try {
            ClassLoader classLoader = EmpleadosUnitTest_2.class.getClassLoader();
            InputStream keystoreInputStream = classLoader.getResourceAsStream("certificados/client/clientTrustedCerts.bks");
            //InputStream keystoreInputStream = new FileInputStream(file);
            if (keystoreInputStream == null) {
                System.out.println("No se pudo encontrar el archivo en el classpath.");
                return;
            }
            Security.addProvider(new BouncyCastleProvider());
            //KeyStore trustStore = KeyStore.getInstance("BKS");
            //trustStore.load(keystoreInputStream, "254535fd32_A".toCharArray());
            KeyStore trustStore = KeyStore.getInstance("BKS");  // Cambié el tipo de almacén a BKS
            trustStore.load(keystoreInputStream, "254535fd32_A".toCharArray());  // Cambié la contraseña y el método load

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

            SSLSocketFactory clientFactory = sslContext.getSocketFactory();

            socket = clientFactory.createSocket(ip, puerto);

            //socket = new Socket(ip, puerto);
            lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            escriptor = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            String mensajeServer = lector.readLine();
            //enviamos los datos de un usuario administrador
            escriptor.write("admin,admin");
            escriptor.newLine();
            escriptor.flush();
            codigo = lector.readLine();





        } catch (IOException | KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
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
            //Ahora vamos a dejar como estaba los update (los insert no hace falta porqué
            // lo eliminamos en la parte de test de delete)
            ObjectInputStream perEnt;
            ArrayList<Empleados> listaEmpleados = new ArrayList<>();
            String mensaje;
            String palabra;
            Object receivedData;
            //Dejamos el empleado David tal y como estaba al principio de los test
            palabra = codigo+",2,"+nombreTabla+",dniNuevo"+",11111111A"+",nomNuevo,"+"David"+",apellidoNuevo"+","+"Valentin M"+",nomempresaNuevo"+
                    ",Frigo"+",departamentNuevo"+",Produccion"+",codicardNuevo"+","+"32624"+",mailNuevo"+",david@gmail.com"+
                    ",telephonNuevo"+",34+12345678"+",dni,"+"11111111A"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();
            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();


            //por ultimo cerramos
            escriptor.write("exit");
            escriptor.newLine();
            escriptor.flush();
            //leemos el último mensaje  que nos envia el server para poder cerrar correctamente
            String mensajeServer = lector.readLine();
            lector.close();
            escriptor.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }





    /**
     * Test nª1. Comprobamos que el dni concuerda con su correspondiente empleado
     */

    @Test
    public  void selectEmpleado_dni_OK_01(){

        try {
            String mensaje = "";
            ObjectInputStream perEnt;
            ArrayList<Empleados> listaEmpleados = new ArrayList<>();

            String palabra = codigo+",0,"+nombreTabla+",dni"+",12345678A"+","+orden;
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
            String mensaje = "";
            ObjectInputStream perEnt;
            ArrayList<Empleados> listaEmpleados = new ArrayList<>();

            String palabra = codigo+",0,"+nombreTabla+",dni"+",84574589A"+","+orden;
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

            assertEquals("Juan",listaEmpleados.get(0).getNom());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }



}