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

import modelo.Empleados;
import modelo.Empresa;

/**
 *
 * Test sobre la clase modelo Empresa
 *
 */

//info: https://www.nestoralmeida.com/testing-con-junit-4/
public class EmpresaUnitTest {


    static String ip = "192.168.1.12";
    static int puerto = 8888;
    static String codigo = "";
    static Socket socket;
    static BufferedReader lector;
    static BufferedWriter escriptor;
    static String nombreTabla = "2";
    static String orden = "0";
    /*
         1 - Selects (1-4)
         2 - Inserts (5-6)
         3 - Update  (7)
         4 - Delete  (8-9)

         codigos CRUD:
         0 - select
         1 - insert
         2 - update
         3 - delete

         codigos tablas:
         0 - empleados
         1 - Empresa
         2 - empresa
         3 - jornada
     */

    /*
        ********************************************************************************************
         Pruebas  Select iniciales
         01 - nombre empresa OK  ---------------------- selectEmpresa_nom_OK
         02 - nombre empresa NG  ---------------------- selectEmpresa_nom_NG
         04 - direccion   OK -------------------------- selectAddress_OK
         05 - direccion   NG -------------------------- selectAddress_NG

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
            insertEmpresaInicio();//creamos los datos de inicio.. en este caso una empresa que borraremos
            //en el test de deleteEmpresa

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
     * Al final, cuando finalicen los test nos desconectamos y dejamos dejamos la tabla empresa
     * como estaba antes de los test
     */
    @AfterClass
    public static  void logout(){

        //cerramos sesion
        try {

            //Ahora vamos a dejar como estaba los update (los insert no hace falta porqué
            // lo eliminamos en la parte de test de delete)
            ObjectInputStream perEnt;
            ArrayList<Empresa> ListaEmpresa = new ArrayList<>();
            String mensaje = "";
            String palabra = "";
            Object receivedData;
            //antes de cerrar
            //El problema de utilizar una empresa en el mismo test  por ejemplo para hacer insert y luego delete es
            //que los test no se ejecutan en orden, así que si el delete se ejecuta antes que el insert ya falla

            //Antes de cerrar eliminamos la empresa Camy para luego al iniciar otra vez el test que funcione sin errores
            //ya que esta empresa es la creamos en insertEmpresa
            palabra = codigo+",3,"+nombreTabla+",nom"+",Camy"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();
            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaEmpresa = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }

            //Tambien dejamos la direccion de Frigo tal y como estaba al principio en Tarragona ya que la modificamos en el updateEmpresa
            palabra = codigo+","+"2"+","+nombreTabla+",nomNuevo,"+"Frigo"+",addressNuevo,"+"Tarragona"+",telephonNuevo,"+"11111124"+",nom,"+"Frigo"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();
            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaEmpresa = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }

            //por ultimo cerramos la sesion
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
     * Método que no forma parte de los test, se ejecuta una vez al inicio para crear la empresa Nestle
     * que la borraremos en deleteEmpresa
     */
    public  static void insertEmpresaInicio(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Empresa> ListaEmpresa = new ArrayList<>();
            String mensaje = "";
            String palabra;
            Object receivedData;

            // Creamos la empresa nestle, para poder eliminarla en el delete
            palabra = codigo+",1,"+nombreTabla+",nom"+",Nestle"+",address"+",Barcelona"+",telephon"+",987654555,"+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaEmpresa = (ArrayList) receivedData;
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
     * Test nª1. Comprobamos que el nombre de la empresa existe en la BD
     */

    @Test
    public  void selectEmpresa_nom_OK(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Empresa> listaEmpresa = new ArrayList<>();

            String palabra = codigo+",0,"+nombreTabla+",nom"+",Frigo"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            Object receivedData = perEnt.readObject();
            listaEmpresa = (ArrayList) receivedData;

            assertEquals("Frigo",listaEmpresa.get(0).getNom());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test nª2. Comprobamos que el nombre de la empresa no existe en la BD
     */
    @Test
    public  void selectEmpresa_nom_NG(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Empresa> ListaEmpresa = new ArrayList<>();
            String mensaje = "";

            String palabra = codigo+",0,"+nombreTabla+",nom"+",EmpresaInexistente"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            Object receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaEmpresa = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }

            assertTrue(ListaEmpresa.isEmpty());
            assertEquals("\nEl nombre de la empresa no existe en el registro",mensaje);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test nª3. En este caso comprobamos que la direccion existe en la BD y corresponde a una
     * empresa (en este caso, tal y como lo hemos hecho no es muy real ya que la direccion
     * debería tener varios campos, ciudad,codigo postal..etc y solo hemos puesto uno..)
     *
     */
    @Test
    public  void selectAddress_OK(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Empresa> ListaEmpresa = new ArrayList<>();
            String mensaje;

            String palabra = codigo+",0,"+nombreTabla+",address"+",Barcelona"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            Object receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaEmpresa = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            assertEquals("Barcelona",ListaEmpresa.get(0).getAddress());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }



    /**
     * Test nª4. En este caso comprobamos que la direccion no existe
     */
    @Test
    public  void selectAddress_NG(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Empresa> ListaEmpresa = new ArrayList<>();
            String mensaje = "";

            String palabra = codigo+",0,"+nombreTabla+",address"+",BarcelonaAA"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            Object receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaEmpresa = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            assertTrue(ListaEmpresa.isEmpty());
            assertEquals("\nLa direccion de la empresa no existe en el registro",mensaje);

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
         05 - insert Empresa OK  ---------------------- insertEmpresa
         06 - insert Empresa NG  ---------------------- insertEmpresa_NG (la empresa ya existe)
         (Creamos al usuario Juanito

     */

    /**
     * Test nª05. En este caso lo que hacemos es insertar una Empresa en la BD
     */

    @Test
    public  void insertEmpresa(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Empresa> ListaEmpresa = new ArrayList<>();
            String mensaje = "";
            String palabra;
            Object receivedData;

            // FASE 1 - Lo primero es comprobar que no existe ese dni el la BD
            palabra = codigo+",0,"+nombreTabla+",nom"+",Camy"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaEmpresa = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            // FASE - 2  comprobamos que no existe.. para eso comprobamos que esta vacio el arrayList
            assertTrue(ListaEmpresa.isEmpty());
            assertEquals("\nEl nombre de la empresa no existe en el registro",mensaje);

            //Ahora  creamos la empresa de nombre Camy
            palabra = codigo+",1,"+nombreTabla+",nom"+",Camy"+",address"+",Tarragona"+",telephon"+",987654321,"+orden;
            //palabra = codigo+",1,"+nombreTabla+",nom"+",Nestle"+",address"+",Malaga"+",telephon"+",987654333,"+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();
            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaEmpresa = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }

            // FASE - 3 y por último comprobamos que ahora si existe ese dni.. Es el dni de Juanito
            palabra = codigo+",1,"+nombreTabla+",nom"+",Camy"+",address"+",Tarragona"+",telephon"+",987654321,"+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaEmpresa = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            assertEquals("Camy",ListaEmpresa.get(0).getNom());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test nª06. En este test comprobamos que no podemos insertar una Empresa que ya existe
     *
     */
    @Test
    public  void insertEmpresa_NG()  {

        try {

            ObjectInputStream perEnt;
            ArrayList<Empresa> ListaEmpresa = new ArrayList<>();
            String mensaje = "";
            String palabra;
            Object receivedData;


            //Ahora  creamos la empresa de nombre Camy
            palabra = codigo+",1,"+nombreTabla+",nom"+",Frigo"+",address"+",Tarragona"+",telephon"+",987654321,"+orden;
            //palabra = codigo+",1,"+nombreTabla+",nom"+",Nestle"+",address"+",Malaga"+",telephon"+",987654333,"+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();
            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaEmpresa = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            assertTrue(ListaEmpresa.isEmpty());
            assertEquals("\n" +
                    "La empresa que intenta crear ya esta\n" +
                    "dada de alta.\n" +
                    "Revise la lista de empresas.",mensaje);



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
         07 - update Empresa OK  ---------------------- updateEmpresa
     */
    /**
     * Test nª07. En este caso lo que hacemos es actualizar una empresa en la BD
     * Para comprobar el test, primero buscamos un empresa y guardamos sus datos
     * luego modificamos uno de sus datos.. en este caso la dirección.. en principio es de
     * Tarragona pero le vamos a dar la direccion de Japon
     */

    @Test
    public  void updateEmpresa(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Empresa> ListaEmpresa = new ArrayList<>();
            String mensaje;
            String palabra;
            Object receivedData;

            // FASE 1 - Lo primero es comprobar que  existe la empresa en el la BD.
            palabra = codigo+",0,"+nombreTabla+",nom"+",Frigo"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaEmpresa = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            //comprobamos que la empresa existe y se llama Frigo
            assertEquals("Frigo",ListaEmpresa.get(0).getNom());

            //FASE 2 - Ahora  modificamos la direccion, le vamos a poner que está en Japon
            palabra = codigo+","+"2"+","+nombreTabla+",nomNuevo,"+"Frigo"+",addressNuevo,"+"Japon"+",telephonNuevo,"+"11111124"+",nom,"+"Frigo"+","+orden;

            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();
            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaEmpresa = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }

            // FASE - 3 y por último comprobamos que se ha modificado el tipo de user a 1
            palabra = codigo+",0,"+nombreTabla+",nom"+",Frigo"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaEmpresa = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            //comprobamos que el tipo de user sea 1
            assertEquals("Japon",ListaEmpresa.get(0).getAddress());//acordarse que el atributo numtipe es int


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
         08 - delete Empresa OK  ---------------------- deleteEmpresa
         09 - delete Empresa NG  ---------------------- deleteEmpresa_NG

     */
    /**
     * Test nª08. En este caso lo que hacemos es eliminar una empresa en la BD
     * Para comprobar el test, primero buscamos una para comprobar que existe
     * luego lo eliminamos y  comprobamos que ya no  existe en la BD y también el mensaje
     * que el server nos envia al eliminarla
     */
    @Test
    public  void deleteEmpresa(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Empresa> ListaEmpresa = new ArrayList<>();
            String mensaje = "";
            String palabra;
            Object receivedData;

            // FASE 1 - Lo primero es comprobar que no existe ese dni el la BD
            palabra = codigo+",0,"+nombreTabla+",nom"+",Nestle"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaEmpresa = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            assertFalse(ListaEmpresa.isEmpty());//comprobamos que tenemos una empresa con ese dni

            // FASE - 2  eliminamos el empleado de la BD
            palabra = codigo+","+"3"+","+nombreTabla+",nom,"+"Nestle"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();
            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaEmpresa = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            //limpiamos el ArrayList ya que si no, nos dice que ListaEmpresa no está vacio
            ListaEmpresa.clear();
            // FASE - 3 y por último comprobamos que se ha eliminado la empresa Nestle
            palabra = codigo+",0,"+nombreTabla+",nom"+",Nestle"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaEmpresa = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            assertTrue(ListaEmpresa.isEmpty());//comprobamos que no tenemos una empresa con ese nombre
            assertEquals("\nEl nombre de la empresa no existe en el registro",mensaje);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test nª09. En este caso lo que hacemos es  intentar eliminar una empresa
     * que no existe en la BD.
     * El servidor nos enviará un error que compararemos con nuestro mensaje
     */
    @Test
    public  void deleteEmpresa_NG(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Empresa> ListaEmpresa = new ArrayList<>();
            String mensaje = "";
            String palabra;
            Object receivedData;


            palabra = codigo+","+"3"+","+nombreTabla+",nom,"+"EmpresaQueNoExiste"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();
            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                ListaEmpresa = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            assertTrue(ListaEmpresa.isEmpty());//comprobamos el arrayList está vacio
            // y comprobamos el mensaje del server, que la empresa no existe en el registro
            assertEquals("\nEl nombre de la empresa no existe en el registro",mensaje);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


}