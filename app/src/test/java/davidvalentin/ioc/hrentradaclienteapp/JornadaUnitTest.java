package davidvalentin.ioc.hrentradaclienteapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

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

import modelo.Jornada;


/**
 *
 * Test sobre la clase modelo Jornada
 *
 */

//info: https://www.nestoralmeida.com/testing-con-junit-4/
public class JornadaUnitTest {


    static String ip = "192.168.1.12";
    static int puerto = 8888;
    static String codigo = "";
    static Socket socket;
    static BufferedReader lector;
    static BufferedWriter escriptor;
    static String nombreTabla = "3";
    static String orden = "0";

    /*
         1 - Selects (1-18)
         2 - Inserts (19-24)
         3 - Update  (25-30)
         4 - Delete  (31-32)

         codigos CRUD:
         0 - select
         1 - insert
         2 - update
         3 - delete

         codigos tablas:
         0 - empleados
         1 - Jornada
         2 - Jornada
         3 - jornada
     */

    /*
        ********************************************************************************************
         Pruebas  Select iniciales
         01 - dni  OK  ----------------------------------- selectJornada_dni_OK
         02 - dni  NG  ----------------------------------- selectJornada_dni_NG
         03 - nom  OK  ----------------------------------- selectJornada_nom_OK
         04 - nom  NG ------------------------------------ selectJornada_nom_NG
         05 - apellido  OK ------------------------------- selectJornada_apellido_OK
         06 - apellido  NG ------------------------------- selectJornada_apellido_NG
         07 - codicard OK -------------------------------- selectJornada_codicard_OK
         08 - codicard NG -------------------------------- selectJornada_codicard_NG
         09 - fecha OK  ---------------------------------- selectJornada_fecha_OK
         10 - fecha NG  ---------------------------------- selectJornada_fecha__NG
         11 - dni - fecha OK ----------------------------- selectJornada_dni_fecha_OK
         12 - dni - fecha NG ----------------------------- selectJornada_dni_fecha_NG
         13 - nom - fecha OH ----------------------------- selectJornada_nom_fecha_OK
         14 - nom - fecha NG ----------------------------- selectJornada_nom_fecha_NG --
         15 - apellido - fecha OK ------------------------ selectJornada_apellido_fecha_OK
         16 - nom - apellido OK -------------------------- selectJornada_nom_apellido_OK
         17 - codicard -fecha OK ------------------------- selectJornada_codicard_fecha_OK
         18 - codicard -fecha NG ------------------------- selectJornada_codicard_fecha_NG


     */

    /**
     * Lo primero antes de comenzar los test iniciamos sesion con usuario admin
     */
    @BeforeClass
    public  static void login(){

        try {
            //NOTA: esta parte es para cargar el certificado.. además de necesitar alguna dependecia *****************************************************
            //Hay que guardar la carpeta certificados dentro de Test/resources .. IMPORTANTE: esta carpeta resources no existe y hay que crearla a mano..
            //esto es para poder utilizar el metoodo getResourceAsStream.
            //la ruta completa en este caso es: /home/david/AndroidStudioProjects/HREntradaClienteApp/app/src/test/resources/certificados/
            ClassLoader classLoader = EmpleadosUnitTest_2.class.getClassLoader();
            InputStream keystoreInputStream = classLoader.getResourceAsStream("certificados/client/clientTrustedCerts.bks");
            //InputStream keystoreInputStream = new FileInputStream(file);
            if (keystoreInputStream == null) {
                System.out.println("No se pudo encontrar el archivo en el classpath.");
                return;
            }
            Security.addProvider(new BouncyCastleProvider());//linea necesaria también.. he cargado la dependencia y el import

            KeyStore trustStore = KeyStore.getInstance("BKS");  // Cambié el tipo de almacén a BKS
            trustStore.load(keystoreInputStream, "254535fd32_A".toCharArray());  // Cambié la contraseña y el método load

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

            SSLSocketFactory clientFactory = sslContext.getSocketFactory();

            socket = clientFactory.createSocket(ip, puerto);
            // hasta aquí la creacion del socket con el certificado.. si la conexion no fuese cifrada bastaría con la linea de aquí abajo**********************
            //socket = new Socket(ip, puerto);
            lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            escriptor = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            String mensajeServer = lector.readLine();
            //enviamos los datos de un usuario administrador
            escriptor.write("admin,admin");
            escriptor.newLine();
            escriptor.flush();
            codigo = lector.readLine();
            insertJornadaInicio();//creamos los datos de inicio.. en este caso 4 jornadas de 4
            //empleados diferentes, 2 para los test del update que se borraran con el metodo logout
            //al acabar los test y otros 2 que se eliminaran con los test del delete

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
     * Al final, cuando finalicen los test nos desconectamos y dejamos dejamos la tabla Jornada
     * como estaba antes de los test
     */
    @AfterClass
    public static  void logout(){

        //cerramos sesion
        try {

            //Ahora vamos a dejar como estaba los update (los insert no hace falta porqué
            // lo eliminamos en la parte de test de delete)
            ObjectInputStream perEnt;
            ArrayList<Jornada> ListaJornada = new ArrayList<>();
            String mensaje = "";
            String palabra = "";
            Object receivedData;
            //antes de cerrar
            //El problema de utilizar una Jornada en el mismo test  por ejemplo para hacer insert y luego delete es
            //que los test no se ejecutan en orden, así que si el delete se ejecuta antes que el insert ya falla

            //Antes de cerrar eliminamos la Jornada de Federico para luego al iniciar otra vez el test que funcione sin errores
            //ya que esta Jornada es la creamos en insertJornada..
            // Pero esto no es tan sencillo.. porque para eliminar una jornada
            //necesitamos tambien además del codicard o dni la fecha.. así que primero hemos de buscar la fecha,
            //la vamos a buscar por el dni, sabiendo de antemano que solo hay 1 jorndada iniciada por este empleado
            //así que vamos sin opcion de error.
            palabra = codigo+",0,"+nombreTabla+",dni"+",88888888A,"+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaJornada = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            //nos guardamos la jornada
            String fechaJornada = ListaJornada.get(0).getFecha();
            //ahora si eliminamos la jornada para poder volver a ejecutar el test cuando queramos
            palabra = codigo+",3,"+nombreTabla+",dni"+",88888888A,"+"fecha,"+fechaJornada+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();
            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaJornada = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            ListaJornada.clear();

            //vamos a hacer lo mismo con la jornada iniciada de Sonia.. esta la iniciamos en el insertJornada
            //pero con el codicard (la de Federico la inicamos con dni)..
            palabra = codigo+",3,"+nombreTabla+",dni"+",88888888C,"+"fecha,"+fechaJornada+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();
            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaJornada = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }

            //vamos a hacer lo mismo con las jornadas iniciadas para los test del Update que hemos creado en el metodo
            //insertJornadaInicio. Las eliminamos al final.. las otras dos que hemos creado para el delete
            //no nos hace falta eliminarlas porque en los test de  delete se eliminaran
            //1
            palabra = codigo+",3,"+nombreTabla+",dni"+",99999999A,"+"fecha,"+fechaJornada+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();
            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaJornada = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            //2
            palabra = codigo+",3,"+nombreTabla+",dni"+",99999999B,"+"fecha,"+fechaJornada+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();
            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaJornada = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }

            //por ultimo cerramos la sesion
            escriptor.write("exit");
            escriptor.newLine();
            escriptor.flush();
            //leemos el último mensaje  que nos envia el server para poder cerrar correctamente
            if (receivedData instanceof List) {
                ListaJornada = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            socket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * Método que no forma parte de los test en sí, se ejecuta una vez al inicio para iniciar las 4 Jornadas que utilizaremos en
     * updateJorndas y en deleteJorndadas..
     * 2 de ellas las utilizaremos en UpdateJorndas para cerrar las jornadas y las otras dos en deleteJornadas. Luego al fina
     * en el metodo logout eliminaremos las dos jorndas utilizadas en UpdateJorndas.
     * La razon por la que hago esto y no reutilizo las jornadas es porqué los test no se ejecutan en orden y eso puede alterar
     * el resultado
     */
    public  static void insertJornadaInicio(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Jornada> ListaJornada = new ArrayList<>();
            String mensaje = "";
            String palabra;
            Object receivedData;
            //para los update
            // iniciamos Jornada para Juanito1
            palabra = codigo+",1,"+nombreTabla+",dni"+",99999999A,"+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();
            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaJornada = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }

            // iniciamos Jornada para Juanito2
            palabra = codigo+",1,"+nombreTabla+",dni"+",99999999B,"+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();
            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaJornada = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }

            // iniciamos Jornada para Juanito3
            palabra = codigo+",1,"+nombreTabla+",dni"+",99999999C,"+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();
            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaJornada = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * Test nª1. Comprobamos que el empleado con un dni dado ha iniciado alguna jornada.
     * En este caso es David
     */

    @Test
    public  void selectJornada_dni_OK(){
        try {

            ObjectInputStream perEnt;
            ArrayList<Jornada> ListaJornada = new ArrayList<>();
            String mensaje = "";

            String palabra = codigo+",0,"+nombreTabla+",dni"+",11111111A"+",0"+",0,"+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            Object receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaJornada = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }

            assertFalse(ListaJornada.isEmpty());
            assertEquals("David",ListaJornada.get(0).getNom());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * Test nª2. Comprobamos que el empleado con un dni dado no ha iniciado ninguna jornada
     * aún
     */
    @Test
    public  void selectJornada_dni_NG(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Jornada> ListaJornada = new ArrayList<>();
            String mensaje = "";

            String palabra = codigo+",0,"+nombreTabla+",dni"+",11111111ABA"+",0"+",0,"+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            Object receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaJornada = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }

            assertTrue(ListaJornada.isEmpty());
            assertEquals("\nEl Dni no existe en el registro",mensaje);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test nª3. En este caso comprobamos que el nombre existe en la la tabla jornada,
     * y que este empleado a iniciado alguna jornada.
     */
    @Test
    public  void selectJornada_nom_OK(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Jornada> ListaJornada = new ArrayList<>();
            String mensaje = "";

            String palabra = codigo+",0,"+nombreTabla+",nom"+",Administrador"+",0"+",0,"+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            Object receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaJornada = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }

            //assertFalse(ListaJornada.isEmpty());//comprobamos que no este vacío en arrayList
            assertEquals("1",ListaJornada.get(0).getCodicard());//el codicard de Administrador es 1

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }



    /**
     * Test nª4. En este caso comprobamos que este empleado no iniciado ninguna jornada aún.
     * Lo comprobamos por el nombre.
     */
    @Test
    public  void selectJornada_nom_NG(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Jornada> ListaJornada = new ArrayList<>();
            String mensaje = "";

            String palabra = codigo+",0,"+nombreTabla+",nom"+",Users"+",0"+",0,"+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            Object receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaJornada = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            //como Users no tiene ninguna jornada iniciada el arrayList estará vacio
            assertTrue(ListaJornada.isEmpty());//comprobamos que no este vacío en arrayList
            assertEquals("\nEl nombre no existe en el registro",mensaje);//el codicard de Administrador es 1

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test nª5. En este caso comprobamos que el apellido existe en la la tabla jornada, o sea que
     * un empleado con ese apellido y  ha iniciado alguna jornada.
     */
    @Test
    public  void selectJornada_apellido_OK(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Jornada> ListaJornada = new ArrayList<>();
            String mensaje = "";

            String palabra = codigo+",0,"+nombreTabla+",apellido"+",Perez"+",0"+",0,"+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            Object receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaJornada = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }

            assertFalse(ListaJornada.isEmpty());//comprobamos que no este vacío en arrayList
            assertEquals("3",ListaJornada.get(0).getCodicard());//el codicard de Juan es 1

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test nª6. En este caso comprobamos que este empleado no iniciado ninjuna jornada aún.
     * Lo comprobamos por el apellido. O al menos comprobamos que no hay ningun empleado con ese
     * apellido que haya iniciado sesion en la tabla jornada
     */
    @Test
    public  void selectJornada_apellido_NG(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Jornada> ListaJornada = new ArrayList<>();
            String mensaje = "";

            String palabra = codigo+",0,"+nombreTabla+",apellido"+",Torres Altas"+",0"+",0,"+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            Object receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaJornada = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            //como Users no tiene ninguna jornada iniciada el arrayList estará vacio
            assertTrue(ListaJornada.isEmpty());//comprobamos que no este vacío en arrayList
            assertEquals("\nEl apellido no existe en el registro",mensaje);//el codicard de Administrador es 1

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * Test nª7. En este caso comprobamos que el codicard existe en la la tabla jornada,
     * y que este empleado a iniciado alguna jornada.
     */
    @Test
    public  void selectJornada_codicard_OK(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Jornada> ListaJornada = new ArrayList<>();
            String mensaje = "";

            String palabra = codigo+",0,"+nombreTabla+",codicard"+",32624"+",0"+",0,"+orden;//1 es el codicard de David
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            Object receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaJornada = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }

            assertFalse(ListaJornada.isEmpty());//comprobamos que no este vacío en arrayList
            assertEquals("David",ListaJornada.get(0).getNom());//el nombre es correcto

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test nª8. En este caso comprobamos que este empleado no iniciado ninguna jornada aún.
     * Lo comprobamos por el codicard.
     */
    @Test
    public  void selectJornada_codicard_NG(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Jornada> ListaJornada = new ArrayList<>();
            String mensaje = "";

            String palabra = codigo+",0,"+nombreTabla+",codicard"+",2"+",0"+",0,"+orden;//el codicard 2 es el de Users
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            Object receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaJornada = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            //como Users no tiene ninguna jornada iniciada el arrayList estará vacio
            assertTrue(ListaJornada.isEmpty());//comprobamos que no este vacío en arrayList
            assertEquals("\nEl codigo de tarjeta no existe en el registro",mensaje);//el codicard de Administrador es 1

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * Test nª9. En este caso comprobamos que en esta fecha se ha iniciado alguna jornada,
     */
    @Test
    public  void selectJornada_fecha_OK(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Jornada> ListaJornada = new ArrayList<>();
            String mensaje = "";

            String palabra = codigo+",0,"+nombreTabla+",fecha"+",2023/11/26"+",0"+",0,"+orden;//1 es el codicard de David
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            Object receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaJornada = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }

            assertFalse(ListaJornada.isEmpty());//comprobamos que no este vacío en arrayList

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test nª10. En este caso comprobamos que esta fecha ningun empleado ha iniciado  jornada aún.
     */
    @Test
    public  void selectJornada_fecha_NG(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Jornada> ListaJornada = new ArrayList<>();
            String mensaje = "";

            String palabra = codigo+",0,"+nombreTabla+",fecha"+",2021/12/11"+",0"+",0,"+orden;//el codicard 2 es el de Users
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            Object receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaJornada = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            //como Users no tiene ninguna jornada iniciada el arrayList estará vacio
            assertTrue(ListaJornada.isEmpty());//comprobamos que no este vacío en arrayList
            assertEquals("\nLa fecha no existe en el registro",mensaje);//el codicard de Administrador es 1

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /*
        A partir de aqui ya viene las busquedas dobles, con dos campos
     */

    /**
     * Test nª11. En este caso comprobamos que en esta fecha el empleado con un dni especifico
     * ha iniciado una jornada. En este caso es Juan
     */
    @Test
    public  void selectJornada_dni_fecha_OK(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Jornada> ListaJornada = new ArrayList<>();
            String mensaje = "";

            String palabra = codigo+",0,"+nombreTabla+",dni"+",84574589A"+",fecha"+",2023/11/26,"+orden;//en nombre del empleado es Juan
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            Object receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaJornada = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }

            assertFalse(ListaJornada.isEmpty());//comprobamos que no este vacío en arrayList
            assertEquals("Juan",ListaJornada.get(0).getNom());//el nombre es correcto
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test nª12. En este caso comprobamos que en esta fecha ningun empleado con un dni
     * dado ha iniciado  jornada aún.
     * Es el mismo empleado anterior pero en esta fecha no tiene iniciada jornada
     */
    @Test
    public  void selectJornada_dni_fecha_NG(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Jornada> ListaJornada = new ArrayList<>();
            String mensaje = "";

            String palabra = codigo+",0,"+nombreTabla+",dni"+",84574589A"+",fecha"+",2023/11/30,"+orden;//en nombre del empleado es Juan
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            Object receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaJornada = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            //como Users no tiene ninguna jornada iniciada el arrayList estará vacio
            assertTrue(ListaJornada.isEmpty());//comprobamos que no este vacío en arrayList
            assertEquals("\nEl dni o la fecha no existe en el registro",mensaje);//el codicard de Administrador es 1

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * Test nª13. En este caso comprobamos que un  empleado con un nombre
     * dado ha iniciado  jornada en esta fecha
     */
    @Test
    public  void selectJornada_nom_fecha(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Jornada> ListaJornada = new ArrayList<>();
            String mensaje = "";

            String palabra = codigo+",0,"+nombreTabla+",nom"+",Juan"+",fecha"+",2023/11/26,"+orden;//en nombre del empleado es Juan
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            Object receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaJornada = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            assertFalse(ListaJornada.isEmpty());//comprobamos que no este vacío en arrayList
            assertEquals("Juan",ListaJornada.get(0).getNom());//el nombre es correcto

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test nª14. En este caso comprobamos que en esta fecha ningun empleado con un nombre
     * dado ha iniciado  jornada aún.

     */
    @Test
    public  void selectJornada_nom_fecha_NG(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Jornada> ListaJornada = new ArrayList<>();
            String mensaje = "";

            String palabra = codigo+",0,"+nombreTabla+",nom"+",Juan"+",fecha"+",2023/11/30,"+orden;//en nombre del empleado es Juan
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            Object receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaJornada = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            //como Users no tiene ninguna jornada iniciada el arrayList estará vacio
            assertTrue(ListaJornada.isEmpty());//comprobamos que no este vacío en arrayList
            assertEquals("\nEl nombre o la fecha no existe en el registro",mensaje);//el codicard de Administrador es 1

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * Test nª15. En este caso comprobamos que un  empleado con un apellido
     * dado ha iniciado  jornada en esta fecha
     */
    @Test
    public  void selectJornada_apellido_fecha(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Jornada> ListaJornada = new ArrayList<>();
            String mensaje = "";

            String palabra = codigo+",0,"+nombreTabla+",apellido"+",Perez"+",fecha"+",2023/11/26,"+orden;//en nombre del empleado es Juan
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            Object receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaJornada = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            assertFalse(ListaJornada.isEmpty());//comprobamos que no este vacío en arrayList
            assertEquals("Juan",ListaJornada.get(0).getNom());//el nombre es correcto

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test nª16. En este caso comprobamos que un  empleado con un nombre y apellido
     * dado  ha iniciado  jornada alguna jornada
     */
    @Test
    public  void selectJornada_nom_apellido(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Jornada> ListaJornada = new ArrayList<>();
            String mensaje = "";

            String palabra = codigo+",0,"+nombreTabla+",nom"+",David"+",apellido"+",Valentin M,"+orden;//en nombre del empleado es Juan
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            Object receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaJornada = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            assertFalse(ListaJornada.isEmpty());//comprobamos que no este vacío en arrayList
            assertEquals("32624",ListaJornada.get(0).getCodicard());//el nombre es correcto

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test nª17. En este caso comprobamos un empleado con un codicard dado ha iniciado jornada
     * en una fecha concreta
     */
    @Test
    public void selectJornada_codicard_fecha_OK(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Jornada> ListaJornada = new ArrayList<>();
            String mensaje = "";

            String palabra = codigo+",0,"+nombreTabla+",codicard"+",32624"+",fecha"+",2023/12/01,"+orden;//en nombre del empleado es Juan
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            Object receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaJornada = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }

            assertFalse(ListaJornada.isEmpty());//comprobamos que no este vacío en arrayList
            assertEquals("David",ListaJornada.get(0).getNom());//el nombre es correcto
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * Test nª18. En este caso comprobamos que en esta fecha ningun empleado con un codicard
     * dado ha iniciado  jornada aún.
     */
    @Test
    public  void selectJornada_codicard_fecha_NG(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Jornada> ListaJornada = new ArrayList<>();
            String mensaje = "";

            String palabra = codigo+",0,"+nombreTabla+",codicard"+",999999"+",fecha"+",2023/11/30,"+orden;//en nombre del empleado es Juan
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            Object receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaJornada = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            //como Users no tiene ninguna jornada iniciada el arrayList estará vacio
            assertTrue(ListaJornada.isEmpty());//comprobamos que no este vacío en arrayList
            assertEquals("\nEl codigo de la tarjeta o la fecha no existe en el registro",mensaje);//el codicard de Administrador es 1

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    /*
        *************************  Fin Selects  ****************************************************
     */

    /*
         Pruebas  Inserts
         19 - insert Jornada dni OK  --------------------------- insertJornada_dni_OK
         20 - insert Jornada dni NG  --------------------------- insertJornada_dni_NG (dni ya tiene jornada iniciada)
         21 - insert Jornada dni NG_2  ------------------------- insertJornada_dni_NG_2 (dni no existe en la tabla joranda)
         22 - insert Jornada codicard OK  ---------------------- insertJornada_codicard_OK
         23 - insert Jornada codicard NG  ---------------------- insertJornada_codicard_NG (codicard no existe en tabla empleados)
         24 - insert Jornada codicard NG 2  ---------------------- insertJornada_codicard_NG_2 (codicard ya tiene iniciada sesion)

     */

    /**
     * Test nª19. En este caso lo que hacemos es insertar una Jornada en la BD
     * Esta jornada la vamos a iniciar con un empleado que no tenga ninguna jornada iniciada
     * Lo primero comprobaremos que no tenga ninguna jornada (lo haremos por el dni), luego crearemos la entrada y por
     * último comprobaremos que se ha creado el inicio de jornada.
     * Vamos a utilizar para esto al empleado Federico.. luego al acbar el test en logout
     * eliminaremos su entrada para que se puede volver a ejecutar el test sin problemas
     *
     * NOTA: Para poder insertar una jornada, el empleado debe existir en la tabla Empleado y
     * además no debe tener iniciada una jornada en ese momento, es decir que para iniciar una
     * jornada primero tiene que cerrar la que tenía iniciada. Aunque en este caso nos interesa
     * que no tenga ninguna jornada creada ya que si la  tiene el test no sería efectivo
     * Además comentar que solo se puede iniciar jornada con el dni o con el codicard
     *
     */

    @Test
    public  void insertJornada_dni_OK(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Jornada> ListaJornada = new ArrayList<>();
            String mensaje = "";
            String palabra;
            Object receivedData;

            // FASE 1 - Lo primero es comprobar que ese empleado no haya iniciado ninguna jornada
            palabra = codigo+",0,"+nombreTabla+",dni"+",88888888A"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaJornada = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }

            assertTrue(ListaJornada.isEmpty());
            assertEquals("\nEl Dni no existe en el registro",mensaje);

            // FASE 2 - Ahora  iniciamos Jornada para Federico
            palabra = codigo+",1,"+nombreTabla+",dni"+",88888888A,"+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();
            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaJornada = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }

            //
            // FASE 3 - Ahora comprobamos que se haya creado la entrada en la tabla jornada
            palabra = codigo+",0,"+nombreTabla+",dni"+",88888888A"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaJornada = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            assertEquals("Federico",ListaJornada.get(0).getNom());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test nª20. En este test comprobamos que no podemos insertar una Jornada porque el
     * dni que introducimos ya tiene una joranda iniciada y sin cerrar.. primero hay que
     * cerrar la jornada iniciada
     *
     */
    @Test
    public  void insertJornada_dni_NG(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Jornada> ListaJornada = new ArrayList<>();
            String mensaje = "";
            String palabra;
            Object receivedData;


            //Iniciamos Jornada para Administrador, pero nos dará error porque ya tiene una jornada
            //iniciada (una jornada que no vamos a cerrar para que siempre podamos ejecutar este test)
            palabra = codigo+",1,"+nombreTabla+",dni"+",12345678A,"+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();
            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaJornada = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            assertTrue(ListaJornada.isEmpty());
            assertEquals("\n" +
                    "El empleado que intenta iniciar jornada\n" +
                    "ya la tiene iniciada.\n" +
                    "Revise las jornada del dia de hoy.",mensaje);



        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test nª21. En este test comprobamos que no podemos insertar una Jornada porque el
     * codicard que introducimos no existe en la tabla empleados.. y capturamos y comparamos el mensaje
     *
     */

    @Test
    public  void insertJornada_dni_NG_2(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Jornada> ListaJornada = new ArrayList<>();
            String mensaje = "";
            String palabra;
            Object receivedData;


            //Iniciamos Jornada para Administrador, pero nos dará error porque ya tiene una jornada
            //iniciada (una jornada que no vamos a cerrar para que siempre podamos ejecutar este test)
            palabra = codigo+",1,"+nombreTabla+",dni"+",12345678ABA,"+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();
            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaJornada = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            assertTrue(ListaJornada.isEmpty());
            assertEquals("\n" +
                    "El Dni no existe en el registro",mensaje);



        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test nª22. En este caso lo que hacemos es insertar una Jornada en la BD
     * Esta jornada la vamos a iniciar con un empleado que no tenga ninguna jornada iniciada
     * Lo primero comprobaremos que no tenga ninguna jornada,(comprobaremos por codicard) luego crearemos la entrada y por
     * último comprobaremos que se ha creado el inicio de jornada.
     * Vamos a utilizar para esto a la empleada Sonia.. luego al acabar el test en logout
     * eliminaremos su entrada para que se puede volver a ejecutar el test sin problemas
     *
     */

    @Test
    public  void insertJornada_codicard_OK(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Jornada> ListaJornada = new ArrayList<>();
            String mensaje = "";
            String palabra;
            Object receivedData;

            // FASE 1 - Lo primero es comprobar que ese empleado no haya iniciado ninguna jornada
            palabra = codigo+",0,"+nombreTabla+",codicard"+",52568"+","+orden;//Codicard de Sonia
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaJornada = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }

            assertTrue(ListaJornada.isEmpty());
            assertEquals("\nEl codigo de tarjeta no existe en el registro",mensaje);

            // FASE 2 - Ahora  iniciamos Jornada para Federico
            palabra = codigo+",1,"+nombreTabla+",codicard"+",52568,"+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();
            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaJornada = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }

            //
            // FASE 3 - Ahora comprobamos que se haya creado la entrada en la tabla jornada
            palabra = codigo+",0,"+nombreTabla+",codicard"+",52568"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaJornada = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            assertEquals("Sonia",ListaJornada.get(0).getNom());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test nª23. En este test comprobamos que no podemos insertar una Jornada porque el
     * codicard que introducimos ya tiene una jornada iniciada y sin cerrar.. primero hay que
     * cerrar la jornada iniciada
     *
     */
    @Test
    public  void insertJornada_codicard_NG(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Jornada> ListaJornada = new ArrayList<>();
            String mensaje = "";
            String palabra;
            Object receivedData;


            //Iniciamos Jornada para Administrador, pero nos dará error porque ya tiene una jornada
            //iniciada (una jornada que no vamos a cerrar para que siempre podamos ejecutar este test)
            palabra = codigo+",1,"+nombreTabla+",codicard"+",1,"+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();
            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaJornada = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            assertTrue(ListaJornada.isEmpty());
            assertEquals("\n" +
                    "El empleado que intenta iniciar jornada\n" +
                    "ya la tiene iniciada.\n" +
                    "Revise las jornada del dia de hoy.",mensaje);



        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test nª24. En este test comprobamos que no podemos insertar una Jornada porque el
     * codicard que introducimos no existe en la tabla jornadas.. y capturamos y comparamos el mensaje
     *
     */
    @Test
    public  void insertJornada_codicard_NG_2(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Jornada> ListaJornada = new ArrayList<>();
            String mensaje = "";
            String palabra;
            Object receivedData;


            //Iniciamos Jornada para Administrador, pero nos dará error porque ya tiene una jornada
            //iniciada (una jornada que no vamos a cerrar para que siempre podamos ejecutar este test)
            palabra = codigo+",1,"+nombreTabla+",codicard"+",1ABA,"+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();
            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaJornada = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            assertTrue(ListaJornada.isEmpty());
            assertEquals("\n" +
                    "El codigo de tarjeta no existe en el registro",mensaje);



        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    /*
     *************************  Fin Inserts  ****************************************************
     */

    /*
         En este caso el update jornada consiste en cerrar la Jornada.. cuando creamos una
         jonrada se crea el inicio de jornada con el campo horaentrada en hora de ese momento
         pero horsalida se queda a nulo, al hacer el update se cierra esa horasalida a la hora de ese
         momento.
         La Jornada, al igual que cuando se crea, se puede cerrar por dni o por codicard
         Pruebas  Update
         25 - update Jornada dni OK  --------------------------- updateJornada_dni_OK (cerramos jornada con dni)
         26 - update Jornada dni NG  --------------------------- updateJornada_dni_NG (porque ya estaba cerrada, utilizamos dni)
         27 - update Jornada dni NG 2  ------------------------- updateJornada_dni_NG_2 (porque no se ha iniciado, no se encuentra dni)
         28 - update Jornada codicard OK  ---------------------- updateJornada_codicard_OK (cerramos jornada con codicard)
         29 - update Jornada codicard NG  ---------------------- updateJornada_codicard_NG (ya tenia jornada iniciada, lo hacemos con codicard)
         30 - update Jornada codicard NG  ---------------------- updateJornada_codicard_NG_2 (no tenía jornada iniciada, lo hacemos con codicard)
     */
    /**
     * Test nº 25 . En este test vamos ha cerrar un jornada iniciada. Esta jornada la hemos
     * iniciado con un metodo antes de iniciciar los test.
     * Cuando se inicia una jornada (se crea un registro nuevo en la tabla jornada), la horaentrada
     * se rellena con la hora de ese mismo momento, y la horsalida se rellena con la palabra 'nulo'
     * Lo que haremos para comprobar que es correcto el cierre de jornada es comprobar que horasalida
     * no es igual a 'nulo'.. quizá no sea la manera más ingeniosa pero funciona igualmente.
     */

    @Test
    public  void updateJornada_dni_OK(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Jornada> ListaJornada = new ArrayList<>();
            String mensaje;
            String palabra;
            Object receivedData;

            // FASE 1 - Cerramos la jornada (ya se que está iniciada no voy a hacer la comprobacion)
            palabra = codigo+",2,"+nombreTabla+",dni"+",99999999A"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaJornada = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }

            //FASE 2 - Comprobramos que el campo que nos indica que la jornada esta cerrada
            //"horasalida" contiene informacion y no esta con la palabra "nulo"
            palabra = codigo+",0,"+nombreTabla+",dni"+",99999999A"+","+orden;

            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();
            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaJornada = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            //comprobamos que el tipo de user sea 1
            assertNotEquals("nulo",ListaJornada.get(0).getHorasalida());//acordarse que el atributo numtipe es int


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test nº 26 . En este test vamos ha intentar cerrar un jornada de un empleado que ya tiene
     * cerrada su jornada, por lo tanto debería primero iniciar jornada antes de cerrarla.
     * Lo que haremos será comparar el contenido del mensaje. Como en muchas ocasiones utilizamos
     * una jornada que al comienzo de los test ya está cerrada ya que si usamos una que hayamos
     * utilizado en los test corremos el riesgo de error por la incertidumbre del orden en que se
     * ejecutaran los test.
     */

    @Test
    public  void updateJornada_dni_NG(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Jornada> ListaJornada = new ArrayList<>();
            String mensaje = "";
            String palabra;
            Object receivedData;

            // FASE 1 - Cerramos la jornada (ya se que está iniciada no voy a hacer la comprobacion)
            palabra = codigo+",2,"+nombreTabla+",dni"+",11111111A"+","+orden;//dni de David
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaJornada = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }

            assertEquals("\n" +
                    "La jornada ya esta finalizada con anterioridad.",mensaje);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test nº 27 . En este test vamos ha intentar cerrar un jornada de un empleado que no ha
     * iniciado jornada y por lo tanto nos tendrá que dar error y nos mandará un mensaje.
     * Recogeremos ese mensaje y lo compararemos.
     */

    @Test
    public  void updateJornada_dni_NG_2(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Jornada> ListaJornada = new ArrayList<>();
            String mensaje = "";
            String palabra;
            Object receivedData;

            // FASE 1 - Cerramos la jornada (ya se que está iniciada no voy a hacer la comprobacion)
            palabra = codigo+",2,"+nombreTabla+",dni"+",99999999ABA"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaJornada = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }

            assertTrue(ListaJornada.isEmpty());//primero vemos que el arayList esta vacio
            assertEquals("\n" +
                    "El Dni no existe en el registro",mensaje);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /*
        Repetimos los mismo ahora pero cambiando dni por codicard
     */

    /**
     * Test nº 28 . En este test vamos ha cerrar un jornada iniciada. Esta jornada la hemos
     * iniciado con un metodo antes de iniciciar los test.
     * En este caso vamos a utilizar el codicard en lugar del dni
     * Cuando se inicia una jornada (se crea un registro nuevo en la tabla jornada), la horaentrada
     * se rellena con la hora de ese mismo momento, y la horsalida se rellena con la palabra 'nulo'
     * Lo que haremos para comprobar que es correcto el cierre de jornada es comprobar que horasalida
     * no es igual a 'nulo'.. quizá no sea la manera más ingeniosa pero funciona igualmente.
     */

    @Test
    public  void updateJornada_codicard_OK(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Jornada> ListaJornada = new ArrayList<>();
            String mensaje;
            String palabra;
            Object receivedData;

            // FASE 1 - Cerramos la jornada (ya se que está iniciada no voy a hacer la comprobacion)
            palabra = codigo+",2,"+nombreTabla+",codicard"+",30348"+","+orden;//codicard de Juanito2
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaJornada = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }

            //FASE 2 - Comprobramos que el campo que nos indica que la jornada esta cerrada
            //"horasalida" contiene informacion y no esta con la palabra "nulo"
            palabra = codigo+",0,"+nombreTabla+",codicard"+",30348"+","+orden;

            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();
            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaJornada = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            //comprobamos que el tipo de user sea 1
            assertNotEquals("nulo",ListaJornada.get(0).getHorasalida());//acordarse que el atributo numtipe es int


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test nº 29 . En este test vamos ha intentar cerrar un jornada de un empleado que ya tiene
     * cerrada su jornada, por lo tanto debería primero iniciar jornada antes de cerrarla.
     * Lo que haremos será comparar el contenido del mensaje. Como en muchas ocasiones utilizamos
     * una jornada que al comienzo de los test ya está cerrada ya que si usamos una que hayamos
     * utilizado en los test corremos el riesgo de error por la incertidumbre del orden en que se
     * ejecutaran los test.
     * En lugar de utilizar el dni utilizaremos su codicard
     */

    @Test
    public  void updateJornada_codicard_NG(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Jornada> ListaJornada = new ArrayList<>();
            String mensaje = "";
            String palabra;
            Object receivedData;

            // FASE 1 - Cerramos la jornada (ya se que está iniciada no voy a hacer la comprobacion)
            palabra = codigo+",2,"+nombreTabla+",codicard"+",32624"+","+orden;//codicard de David
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaJornada = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }

            assertEquals("\n" +
                    "La jornada ya esta finalizada con anterioridad.",mensaje);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test nº 30 . En este test vamos ha intentar cerrar un jornada de un empleado que no ha
     * iniciado jornada y por lo tanto nos tendrá que dar error y nos mandará un mensaje.
     * Recogeremos ese mensaje y lo compararemos.
     * En lugar de utilizar su dni utilizaresmo su codicard
     */

    @Test
    public  void updateJornada_codicard_NG_2(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Jornada> ListaJornada = new ArrayList<>();
            String mensaje = "";
            String palabra;
            Object receivedData;

            // FASE 1 - Cerramos la jornada (ya se que está iniciada no voy a hacer la comprobacion)
            palabra = codigo+",2,"+nombreTabla+",codicard"+",ABACD45"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaJornada = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }

            assertTrue(ListaJornada.isEmpty());//primero vemos que el arayList esta vacio
            assertEquals("\n" +
                    "El codigo de tarjeta no existe en el registro",mensaje);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /*
     *************************  Fin Update  ****************************************************
     */

    /*
         Pruebas  Delete
         31 - delete Jornada dni OK  ---------------------- deleteJornada_dni_OK
         32 - delete Jornada dni NG  ---------------------- deleteJornada_dni_NG


         La eliminación de jornadas al igual que las demás opciones del CRUD se hacen por dni
         o por codicard. Para eliminar una jornada necesitamos además del dni o codicard la fecha
         ya que sino eliminariamos todas las jornadas de un empleado. Podría ser una opción pero
         no la hemos implementado en nuestro programa ya que creemos que no hay ningún motivo para
         implementar esta opcion en una app o aplicacion de escritorio.. si hiciese falta se prodría
         hacer directamente desde la BD.
         En estos test solo lo vamos a hacer con el dni.. (haré un prueba con codicard en la parte
         gráfica de la app en el video)

     */
    /**
     * Test nª30. En este caso lo que hacemos es eliminar una Jornada en la BD
     * La vamos a eliminar utilizar su dni y la fecha de la entrada que queremos eliminar.
     *
     * (La verdad es que ahora que lo pienso podría haber 2 fechas diferentes una para la entrada
     * y otra para la salida ya que podrían ser fechas diferentes.. ,aunque igualmente no alteraría para nada
     * el programa ya que la eliminación del registro sería igual.. supongo que tal y como lo hemos
     * hecho lo ideal sería que automáticamente a las 00:00 se cerrara una jornada y se abriese otra
     * con la fecha del siguiente dia, o sea que por ejemplo un turno de noche de 22:00-06:00 tendría
     * 2 horas de un dia y 6 del dia siguiente
     *
     *
     * Vamos a eliminar la jornada del usuario Juanito3 creado al inicio del test para este fin
     * Para hacerlo primero necesitamos la fecha y esta se crea automaticamente al crear el registro
     * por lo tanto no lo sabemos de antemano.
     * Como ya contamos con este problema este empleado solo tendrá una jornada iniciada, la buscaremos
     * y conseguiremos la fecha.. a partir de ahí ya tendremos la fecha y el dni
     */
    @Test
    public  void deleteJornada_dni_OK(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Jornada> ListaJornada = new ArrayList<>();
            String mensaje = "";
            String palabra;
            Object receivedData;
            // FASE 1 - buscamos la jornada de Juanito3 (solo tiene 1) para obtener la fecha
            palabra = codigo+",0,"+nombreTabla+",dni"+",99999999C,"+orden;//dni de Juanito3
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaJornada = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            //nos guardamos la fecha de la jornada
            String fechaJornada = ListaJornada.get(0).getFecha();
            //ahora si eliminamos la jornada para poder volver a ejecutar el test cuando queramos
            palabra = codigo+",3,"+nombreTabla+",dni"+",99999999C,"+"fecha,"+fechaJornada+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();
            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaJornada = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }

            //limpiamos el ArrayList ya que si no, nos dice que ListaJornada no está vacio
            ListaJornada.clear();
            // FASE - 3 y por último comprobamos que se ha eliminado la Jornada de Juanito3
            palabra = codigo+",0,"+nombreTabla+",dni"+",99999999C"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaJornada = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            assertTrue(ListaJornada.isEmpty());//comprobamos que no tenemos una Jornada con ese nombre
            assertEquals("\nEl Dni no existe en el registro",mensaje);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test nª31. En este caso lo que hacemos es  intentar eliminar una Jornada
     * que no existe en la BD. Lo hacemos buscando con el dni, aunque da igual que el error
     * este en el dni o la fecha.
     * El servidor nos enviará un error que compararemos con nuestro mensaje
     */
    @Test
    public  void deleteJornada_dni_NG(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Jornada> ListaJornada = new ArrayList<>();
            String mensaje = "";
            String palabra;
            Object receivedData;


            palabra = codigo+",3,"+nombreTabla+",dni"+",99999999Q,"+"fecha,"+"2023/11/26"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();
            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaJornada = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            assertTrue(ListaJornada.isEmpty());//comprobamos que no tenemos un empleado con ese dni
            // y comprobamos el mensaje del server, que no puede eliminar al empleado
            assertEquals("\nEl dni o la fecha no existe en el registro",mensaje);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


}