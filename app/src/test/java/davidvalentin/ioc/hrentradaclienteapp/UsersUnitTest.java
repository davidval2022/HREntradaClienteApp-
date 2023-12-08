package davidvalentin.ioc.hrentradaclienteapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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

import davidvalentin.ioc.hrentradaclienteapp.utilidades.Utilidades;
import modelo.Empleados;
import modelo.Users;

/**
 *
 * Test sobre la clase modelo Empleado (empleados)
 *
 */

//info: https://www.nestoralmeida.com/testing-con-junit-4/
public class UsersUnitTest {


    static String ip = "192.168.1.12";
    static int puerto = 8888;
    static String codigo = "";
    static Socket socket;
    static BufferedReader lector;
    static BufferedWriter escriptor;
    static String nombreTabla = "1";
    static String orden = "0";
    /*
         1 - Selects (1-7)
         2 - Inserts (8-10)
         3 - Update  (11)
         4 - Delete  (12-13)

         codigos CRUD:
         0 - select
         1 - insert
         2 - update
         3 - delete

         codigos tablas:
         0 - empleados
         1 - users
         2 - empresa
         3 - jornada
     */

    /*
        ********************************************************************************************
         Pruebas  Select iniciales
         01 - dni OK  ---------------------- selectUser_dni_OK_01
         02 - dni OK  ---------------------- selectUser_dni_OK_02
         03 - dni NG (malo.. no existe)----- selectUser_dni_NG
         04 - nombre login  OK ----------------------- selectUser_login_OK
         05 - nombre login  NG ----------------------- selectUser_login_NG
         06 - tipo user OK ------------------ select_tipo_OK
         07 - tipo user NG ------------------ select_tipo_NG


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
            ArrayList<Empleados> ListaUsers = new ArrayList<>();
            String mensaje = "";
            String palabra;
            Object receivedData;
            //antes de cerrar
            //Dejamos el empleado David tal y como estaba al principio de los test,
            // simplemente le ponemos el tipo de user a 0
            palabra = codigo+","+"2"+","+nombreTabla+",passNuevo,"+"1234"+",numtipeNuevo,"+"0"+",login,"+"david"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();
            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaUsers = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }


            //por ultimo cerramos
            escriptor.write("exit");
            escriptor.newLine();
            escriptor.flush();
            //leemos el último mensaje  que nos envia el server para poder cerrar correctamente
            String mensajeServer = lector.readLine();
            socket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }


    /**
     * Test nª1. Comprobamos que el dni concuerda con su correspondiente user
     */

    @Test
    public  void selectUser_dni_OK_01(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Users> listaUsers = new ArrayList<>();

            String palabra = codigo+",0,"+nombreTabla+",dni"+",12345678A"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            Object receivedData = perEnt.readObject();
            listaUsers = (ArrayList) receivedData;

            assertEquals("admin",listaUsers.get(0).getLogin());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test nª2. Comprobamos que el dni concuerda con su correspondiente user
     */
    @Test
    public  void selectUser_dni_OK_02(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Users> ListaUsers = new ArrayList<>();

            String palabra = codigo+",0,"+nombreTabla+",dni"+",11111111A"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            Object receivedData = perEnt.readObject();
            ListaUsers = (ArrayList) receivedData;

            assertEquals("david",ListaUsers.get(0).getLogin());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test nª3. En este caso comprobamos que falla ya que el DNI no existe
     * Para ello buscamos un dni y si no existe el arrayList donde se guardan
     * debe estar vacio y además comprobamos el mensaje del server, que debe coincidir
     */
    @Test
    public  void selectUser_dni_NG(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Users> ListaUsers = new ArrayList<>();
            String mensaje = "";

            String palabra = codigo+",0,"+nombreTabla+",dni"+",22222222Z"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            Object receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaUsers = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            assertTrue(ListaUsers.isEmpty());
            assertEquals("\nEl Dni no existe en el registro",mensaje);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * Test nª4. En este caso comprobamos que el login de usuario existe en la BD
     */
    @Test
    public  void selectUser_login_OK(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Users> ListaUsers = new ArrayList<>();
            String mensaje;

            String palabra = codigo+",0,"+nombreTabla+",login"+",david"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            Object receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaUsers = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            assertEquals("david",ListaUsers.get(0).getLogin());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test nª5. En este caso comprobamos que el nombre de usuario no existe en la BD
     */
    @Test
    public  void selectUser_login_NG(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Users> ListaUsers = new ArrayList<>();
            String mensaje = "";

            String palabra = codigo+",0,"+nombreTabla+",login"+",Melissa"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            Object receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaUsers = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            assertTrue(ListaUsers.isEmpty());//el arrayList esta vacio
            //El mensaje recibido del server concuerda.
            assertEquals("\nEl login no existe en el registro",mensaje);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * Test nª6. En este caso comprobamos que el tipo de usuario 0 existe en la tabla
     */
    @Test
    public  void select_tipo_OK(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Users> ListaUsers = new ArrayList<>();
            String mensaje;

            String palabra = codigo+",0,"+nombreTabla+",numtipe"+",0"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            Object receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaUsers = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            //el arrayList no esta vacio, existe el tipo 0 es el tipo admin
            assertFalse(ListaUsers.isEmpty());


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test nª7. En este caso comprobamos que el tipo de usuario 15 no existe en la tabla (solo hay 0 y 1)
     */
    @Test
    public  void select_tipo_NG(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Users> ListaUsers = new ArrayList<>();
            String mensaje = "";

            String palabra = codigo+",0,"+nombreTabla+",numtipe"+",15"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            Object receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaUsers = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            assertTrue(ListaUsers.isEmpty());//el arrayList  esta vacio, no existe el tipo 15
            //El tipo de numero de usuario no existe en el registro
            //El mensaje recibido del server concuerda.
            assertEquals("\nEl tipo de numero de usuario no existe en el registro",mensaje);


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
         08 - insert User OK  ---------------------- insertUser
         09 - insert User NG  ---------------------- insertUser_NG (ya esta registrado)
         10 - insert User NG 2  -------------------- insertUser_NG_2 (no existe dni)
         (Creamos al usuario Juanito

     */

    /**
     * Test nª08. En este caso lo que hacemos es insertar un User en la BD
     * Para comprobar el test, lo que hacemos es primero comprobar que no existe un dni,
     * en la tabla User, luego creamos un User con ese dni, y por ultimo
     * volvemos a comprobar el dni ahora sí existe en la tabla..
     * En este caso lo vamos a hacer con el empleado Juan con dni 84574589A
     */

    @Test
    public  void insertUser(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Users> ListaUsers = new ArrayList<>();
            String mensaje = "";
            String palabra;
            Object receivedData;

            // FASE 1 - Lo primero es comprobar que no existe ese dni el la BD
            palabra = codigo+",0,"+nombreTabla+",dni"+",84574589A"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaUsers = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            // FASE - 2  comprobamos que no existe.. para eso comprobamos que esta vacio el arrayList
            assertTrue(ListaUsers.isEmpty());
            assertEquals("\nEl Dni no existe en el registro",mensaje);

            //Ahora  creamos al user con el empleado con el dni 84574589A
            palabra = codigo+","+1+","+nombreTabla+",login,"+"juan"+",pass,"+"1234"+",numtipe,"+"1"+",dni,"+"84574589A"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();
            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaUsers = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }

            // FASE - 3 y por último comprobamos que ahora si existe ese dni.. Es el dni de Juanito
            palabra = codigo+",0,"+nombreTabla+",dni"+",84574589A"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaUsers = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            assertEquals("juan",ListaUsers.get(0).getLogin());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test nª09. En este test comprobamos que no podemos insertar un User que ya tenga ese dni en
     * uso. Comprobaremos el mensaje que nos envia el server de error
     */
    @Test
    public  void insertUser_NG()  {

        try {

            ObjectInputStream perEnt;
            ArrayList<Users> ListaUsers = new ArrayList<>();
            String mensaje = "";
            String palabra;
            Object receivedData;


            //Ahora  creamos al user con el empleado con el dni 11111111A.. nos dara error porque ya existe
            palabra = codigo+","+1+","+nombreTabla+",login,"+"juan"+",pass,"+"1234"+",numtipe,"+"1"+",dni,"+"11111111A"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();
            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();

            if (receivedData instanceof List) {
                ListaUsers = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            assertTrue(ListaUsers.isEmpty());
            assertEquals("\n" +
                    "El dni que intenta utilizar ya\n" +
                    "se esta utilizando por otro usuario.\n" +
                    "Revise la lista de usuarios y vuelva a intentarlo.",mensaje);



        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * Test nª10. En este test comprobamos que no podemos insertar un User que no exista  como
     * empleado (no existe el dni).. comprobaremos el mensaje que nos envia el server de error
     */
    @Test
    public  void insertUser_NG_2()  {

        try {

            ObjectInputStream perEnt;
            ArrayList<Users> ListaUsers = new ArrayList<>();
            String mensaje = "";
            String palabra;
            Object receivedData;



            palabra = codigo+","+1+","+nombreTabla+",login,"+"juan"+",pass,"+"1234"+",numtipe,"+"1"+",dni,"+"11111111ABAB"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();
            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();

            if (receivedData instanceof List) {
                ListaUsers = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            assertTrue(ListaUsers.isEmpty());
            assertEquals("\n" +
                    "El empleado que utilizara este usuario\n" +
                    "no esta dado de alta como empleado.\n" +
                    "Revise la lista de empleados y cree\n" +
                    "el empleado antes de crear el usuario.",mensaje);



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
         Pruebas  Update
         11 - update User OK  ---------------------- updateUser


     */
    /**
     * Test nª11. En este caso lo que hacemos es actualizar un user en la BD
     * Para comprobar el test, primero buscamos un empleado y guardamos sus datos
     * luego modificamos uno de sus datos.. en este caso el tipo de user
     */

    @Test
    public  void updateUser(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Users> ListaUsers = new ArrayList<>();
            String mensaje;
            String palabra;
            Object receivedData;

            // FASE 1 - Lo primero es comprobar que  existe ese dni el la BD.
            palabra = codigo+",0,"+nombreTabla+",dni"+",11111111A"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaUsers = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            //comprobamos que el usuario con ese dni existe y se llama david
            assertEquals("david",ListaUsers.get(0).getLogin());

            //Ahora  modificamos el tipo de user de david y lo ponemos a 1 (en origen es 0, administrador), como nota
            //comentar que hemos de también modificar la contraseña obligatoriamente.. Se le notificaría la usuario y
            // luego que el mismo la cambie si quiere..
            palabra = codigo+","+"2"+","+nombreTabla+",passNuevo,"+"1234"+",numtipeNuevo,"+"1"+",login,"+"david"+","+orden;

            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();
            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaUsers = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }

            // FASE - 2 y por último comprobamos que se ha modificado el tipo de user a 1
            palabra = codigo+",0,"+nombreTabla+",dni"+",11111111A"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaUsers = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            //comprobamos que el tipo de user sea 1
            assertEquals("1",String.valueOf(ListaUsers.get(0).getNumtipe()));//acordarse que el atributo numtipe es int



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
         12 - delete User OK  ---------------------- deleteUser
         13 - delete User NG  ---------------------- deleteUser_NG

     */
    /**
     * Test nª12. En este caso lo que hacemos es eliminar un user en la BD
     * Para comprobar el test, primero buscamos un user para comprobar que existe
     * luego lo eliminamos y  comprobamos que ya no  existe en la BD
     */
    @Test
    public  void deleteUser(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Users> ListaUsers = new ArrayList<>();
            String mensaje = "";
            String palabra;
            Object receivedData;

            // FASE 1 - Lo primero es comprobar que  existe ese dni el la BD.
            //Existe porqué lo hemos creado en la prueba de los Inserts es juan
            palabra = codigo+",0,"+nombreTabla+",dni"+",84574589A"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaUsers = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            assertFalse(ListaUsers.isEmpty());//comprobamos que tenemos un empleado con ese dni

            // FASE - 2  eliminamos el empleado de la BD
            palabra = codigo+","+"3"+","+nombreTabla+",dni,"+"84574589A"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();
            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaUsers = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            //limpiamos el ArrayList ya que si no, nos dice que ListaUsers no está vacio
            ListaUsers.clear();
            // FASE - 3 y por último comprobamos que se ha eliminado al usuario juan
            palabra = codigo+",0,"+nombreTabla+",dni"+",84574589A"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaUsers = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            assertTrue(ListaUsers.isEmpty());//comprobamos que no tenemos un empleado con ese dni
            assertEquals("\nEl Dni no existe en el registro",mensaje);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test nª13. En este caso lo que hacemos es  intentar eliminar un user
     * que no existe en la BD.
     * El servidor nos enviará un error que compararemos con nuestro mensaje
     */
    @Test
    public  void deleteUser_NG(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Users> ListaUsers = new ArrayList<>();
            String mensaje = "";
            String palabra;
            Object receivedData;


            palabra = codigo+","+"3"+","+nombreTabla+",dni,"+"999999999CC"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();
            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaUsers = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            assertTrue(ListaUsers.isEmpty());//comprobamos que no tenemos un empleado con ese dni
            // y comprobamos el mensaje del server, que no puede eliminar al empleado
            assertEquals("\nEl Dni no existe en el registro",mensaje);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


}