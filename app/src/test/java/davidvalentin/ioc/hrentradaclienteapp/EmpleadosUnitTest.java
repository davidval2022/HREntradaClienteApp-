package davidvalentin.ioc.hrentradaclienteapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
 *
 * Test sobre la clase modelo Empleado (empleados)
 *
 */

//info: https://www.nestoralmeida.com/testing-con-junit-4/
public class EmpleadosUnitTest {


    static String ip = "192.168.1.12";
    static int puerto = 8888;
    static String codigo = "";
    static Socket socket;
    static BufferedReader lector;
    static BufferedWriter escriptor;
    static String nombreTabla = "0";
    static String orden = "0";
    /*
         1 - Selects (1-13)
         2 - Inserts (14-15)
         3 - Update  (16-17)
         4 - Delete  (18-19)

         codigos CRUD:
         0 - select
         1 - insert
         2 - update
         3 - delete
     */

    /*
        ********************************************************************************************
         Pruebas  Select iniciales
         01 - dni OK  ---------------------- selectEmpleado_dni_OK_01
         02 - dni OK  ---------------------- selectEmpleado_dni_OK_02
         03 - dni NG (malo.. no existe)----- selectEmpleado_dni_NG
         04 - nom OK ----------------------- selectEmpleado_nombre_OK
         05 - nom NG ----------------------- selectEmpleado_nombre_NG
         06 - apellido OK ------------------ selectEmpleado_apellido_OK
         07 - apellido NG ------------------ selectEmpleado_apellido_NG
         08 - nomempresa OK ---------------- selectEmpleado_nomempresa_OK
         09 - nomempresa NG ---------------- selectEmpleado_nomempresa_NG
         10 - departament OK --------------- selectEmpleado_departament_OK
         11 - departament NG --------------- selectEmpleado_departament_NG
         12 - codicard OK ------------------ selectEmpleado_codicard_OK
         13 - codicard NG ------------------ selectEmpleado_codicard_NG
         Las busquedas por mail y telefono no las hago ya que creo que ya quedan
         bastantes demostradas las busquedas


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
            //Ahora vamos a dejar como estaba los update (los insert no hace falta porqué
            // lo eliminamos en la parte de test de delete)
            ObjectInputStream perEnt;
            ArrayList<Empleados> listaEmpleados = new ArrayList<>();
            String mensaje;
            String palabra;
            Object receivedData;
            //Dejamos el empleado David tal y como estaba al principio de los test
            palabra = codigo+",2,"+nombreTabla+",dniNuevo"+",11111111A"+",nomNuevo,"+"David"+",apellidoNuevo"+","+"Valentin M"+",nomempresaNuevo"+
                    ",Frigo"+",departamentNuevo"+",Produccion"+",codicardNuevo"+","+"32624"+",mailNuevo"+",davicito@gmail.com"+
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

            ObjectInputStream perEnt;
            ArrayList<Empleados> listaEmpleados = new ArrayList<>();

            String palabra = codigo+",0,"+nombreTabla+",dni"+",12345678A"+","+orden;
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

            String palabra = codigo+",0,"+nombreTabla+",dni"+",84574589A"+","+orden;
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

            String palabra = codigo+",0,"+nombreTabla+",dni"+",11111111W"+","+orden;
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


    /**
     * Test nª4. En este caso comprobamos que el nombre existe en la BD
     */
    @Test
    public  void selectEmpleado_nombre_OK(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Empleados> listaEmpleados = new ArrayList<>();
            String mensaje;

            String palabra = codigo+",0,"+nombreTabla+",nom"+",David"+","+orden;
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
            assertEquals("David",listaEmpleados.get(0).getNom());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test nª5. En este caso comprobamos que el nombre no existe en la BD
     */
    @Test
    public  void selectEmpleado_nombre_NG(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Empleados> listaEmpleados = new ArrayList<>();
            String mensaje = "";

            String palabra = codigo+",0,"+nombreTabla+",nom"+",Melissa"+","+orden;
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
            assertTrue(listaEmpleados.isEmpty());//el arrayList esta vacio
            //El mensaje recibido del server concuerda.
            assertEquals("\nEl nombre no existe en el registro",mensaje);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * Test nª6. En este caso comprobamos que el apellido existe en la BD
     * El apellido Gutierrez pertenece a Maria
     */
    @Test
    public  void selectEmpleado_apellido_OK(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Empleados> listaEmpleados = new ArrayList<>();
            String mensaje;

            String palabra = codigo+",0,"+nombreTabla+",apellido"+",Gutierrez"+","+orden;
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
            assertEquals("Maria",listaEmpleados.get(0).getNom());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test nª7. En este caso comprobamos que el apellido no existe en la BD
     */
    @Test
    public  void selectEmpleado_apellido_NG(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Empleados> listaEmpleados = new ArrayList<>();
            String mensaje = "";

            String palabra = codigo+",0,"+nombreTabla+",apellido"+",Gutierrezilla"+","+orden;
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
            //El mensaje recibido del server concuerda.
            assertEquals("\nEl apellido no existe en el registro",mensaje);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test nª8. En este caso comprobamos que la empresa existe en la BD
     */
    @Test
    public  void selectEmpleado_empresa_OK(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Empleados> listaEmpleados = new ArrayList<>();
            String mensaje;

            String palabra = codigo+",0,"+nombreTabla+",nomempresa"+",Frigo"+","+orden;
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
            assertEquals("Frigo",listaEmpleados.get(0).getNomempresa());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test nª9. En este caso comprobamos que la empresa no existe en la BD
     */
    @Test
    public  void selectEmpleado_empresa_NG(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Empleados> listaEmpleados = new ArrayList<>();
            String mensaje = "";

            String palabra = codigo+",0,"+nombreTabla+",nomempresa"+",Nestle"+","+orden;
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
            //El mensaje recibido del server concuerda.
            assertEquals("\nEl nombre de la empresa no existe en el registro",mensaje);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * Test nª10. En este caso comprobamos que el departament existe en la BD
     */
    @Test
    public  void selectEmpleado_departament_OK(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Empleados> listaEmpleados = new ArrayList<>();
            String mensaje;

            String palabra = codigo+",0,"+nombreTabla+",departament"+",Informatica"+","+orden;
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
            assertEquals("Informatica",listaEmpleados.get(0).getDepartament());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test nª11. En este caso comprobamos que el departament no existe en la BD
     */
    @Test
    public  void selectEmpleado_departament_NG(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Empleados> listaEmpleados = new ArrayList<>();
            String mensaje = "";

            String palabra = codigo+",0,"+nombreTabla+",departament"+",Carpinteria"+","+orden;
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
            //El mensaje recibido del server concuerda.
            assertEquals("\nEl departamento no existe en el registro",mensaje);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test nª12. En este caso comprobamos que el codicard existe en la BD
     * El 1 es del Administrador
     */
    @Test
    public  void selectEmpleado_codicard_OK(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Empleados> listaEmpleados = new ArrayList<>();
            String mensaje;

            String palabra = codigo+",0,"+nombreTabla+",codicard"+",1"+","+orden;
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
     * Test nª13. En este caso comprobamos que el codicard no existe en la BD
     */
    @Test
    public  void selectEmpleado_codicard_NG(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Empleados> listaEmpleados = new ArrayList<>();
            String mensaje = "";

            String palabra = codigo+",0,"+nombreTabla+",codicard"+",8888888"+","+orden;
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
            //El mensaje recibido del server concuerda.
            assertEquals("\nEl codigo de tarjeta no existe en el registro",mensaje);

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
         14 - insert Empleado OK  ---------------------- insertEmpleado
         15 - insert Empleado NG  ---------------------- insertEmpleado_NG
         (Creamos al usuario Juanito

     */

    /**
     * Test nª14. En este caso lo que hacemos es insertar un empleado en la BD
     * Para comprobar el test, lo que hacemos es primero comprobar que no existe un dni,
     * luego creamos un empleado con ese dni, y por ultimo volvemos a comprobar que que
     * el dni ahora sí existe.. he comprobado de diferentes maneras el test y funciona
     * correctamente.
     */

    @Test
    public  void insertEmpleado(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Empleados> listaEmpleados = new ArrayList<>();
            String mensaje = "";
            String palabra;
            Object receivedData;

            // FASE 1 - Lo primero es comprobar que no existe ese dni el la BD
            palabra = codigo+",0,"+nombreTabla+",dni"+",12L"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                listaEmpleados = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            // FASE - 2  comprobamos que no existe.. para eso comprobamos que esta vacio el arrayList
            assertTrue(listaEmpleados.isEmpty());
            assertEquals("\nEl Dni no existe en el registro",mensaje);

            //Ahora  creamos al empleado con dni 12L
            palabra = codigo+",1,"+nombreTabla+",dni"+",12345678Z"+",nom"+",Juanito"+",apellido"+",Muntes"+",nomempresa"+
                    ",Frigo"+",departament"+",Produccion"+",codicard"+",0"+",mail"+",juanito@gmail.com"+
                    ",telephon"+",12345679"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();
            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                listaEmpleados = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }

            // FASE - 3 y por último comprobamos que ahora si existe ese dni.. Es el dni de Juanito
            palabra = codigo+",0,"+nombreTabla+",dni"+",12345678Z"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                listaEmpleados = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            assertEquals("Juanito",listaEmpleados.get(0).getNom());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test nª15. En este test comprobamos que no podemos insertar un empleado que pertenezca a una empresa
     * que no existe en la BD, lo que haremos es comprobar el mensaje que nos envia el server
     */
    @Test
    public  void insertEmpleado_NG()  {

        try {

            ObjectInputStream perEnt;
            ArrayList<Empleados> listaEmpleados = new ArrayList<>();
            String mensaje = "";
            String palabra;
            Object receivedData;


            //Ahora  creamos al empleado Juanita aunque no lo creara, dara un error
            palabra = codigo+",1,"+nombreTabla+",dni"+",12345678P"+",nom"+",Juanita"+",apellido"+",Martinez"+",nomempresa"+
                    ",EstaEmpresaNoExiste"+",departament"+",Produccion"+",codicard"+",0"+",mail"+",juanita@gmail.com"+
                    ",telephon"+",12345679"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();
            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                listaEmpleados = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            //assertEquals("La empresa en la que intenta añadir\n" +
                    //"el empleado no existe.\nRevise la lista de empresas y vuelva a intentarlo.",mensaje);
            assertEquals("\nLa empresa en la que intenta añadir\nel empleado no existe.\nRevise la lista de empresas y vuelva a intentarlo.",mensaje);

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
         16 - update Empleado OK  ---------------------- updateEmpleado
              (Modificamos al usuario David cambiando su telefono: 34+12345678
                por la palabra ALMERIA
         17 - update Empleado NG  ---------------------- updateEmpleado_NG


     */
    /**
     * Test nª16. En este caso lo que hacemos es actualizar un empleado en la BD
     * Para comprobar el test, primero buscamos un empleado y guardamos sus datos
     * luego modificamos uno de sus datos.. en este caso el telefono que será ALMERIA
     */

    @Test
    public  void updateEmpleado(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Empleados> listaEmpleados = new ArrayList<>();
            String mensaje;
            String palabra;
            Object receivedData;

            // FASE 1 - Lo primero es comprobar que  existe ese dni el la BD.
            //Luego cogeremos algunos datos que no vamos a modificar, para insertarlos directamente
            //y comprobaremos el dato que vamos a modificar
            palabra = codigo+",0,"+nombreTabla+",dni"+",11111111A"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                listaEmpleados = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            //comprobamos que el telefono sea este: 34+12345678
            assertEquals("34+12345678",listaEmpleados.get(0).getTelephon());

            // FASE - 2  comprobamos  existe.. para eso comprobamos que esta vacio el arrayList
            Empleados empleado = listaEmpleados.get(0);

            //Ahora  modificamos el empleado el teléfono con dni 11111111A (que es nuestro empleado David)
            palabra = codigo+",2,"+nombreTabla+",dniNuevo"+",11111111A"+",nomNuevo,"+empleado.getNom()+",apellidoNuevo"+","+empleado.getApellido()+",nomempresaNuevo"+
                    ",Frigo"+",departamentNuevo"+",Produccion"+",codicardNuevo"+","+empleado.getCodicard()+",mailNuevo"+",davicito@gmail.com"+
                    ",telephonNuevo"+",ALMERIA"+",dni,"+listaEmpleados.get(0).getDni()+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();
            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                listaEmpleados = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }

            // FASE - 3 y por último comprobamos que se ha modificado el telefono que ahora es
            //ALMERIA
            palabra = codigo+",0,"+nombreTabla+",telephon"+",ALMERIA"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                listaEmpleados = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            //comprobamos que ahora el teléfono sea ALMERIA
            assertEquals("ALMERIA",listaEmpleados.get(0).getTelephon());



        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test nª17. En este test comprobamos que no podemos actualizar un empleado si queremos modificar la empresa y
     * que esta empresa no exista en la BD, lo que haremos es comprobar el mensaje que no envia el server
     */
    @Test
    public  void updateEmpleado_NG()  {

        try {

            ObjectInputStream perEnt;
            ArrayList<Empleados> listaEmpleados = new ArrayList<>();
            String mensaje = "";
            String palabra;
            Object receivedData;


            palabra = codigo+",2,"+nombreTabla+",dniNuevo"+",11111111A"+",nomNuevo,"+"David"+",apellidoNuevo"+","+"Valentin M"+",nomempresaNuevo"+
                    ",EmpresaQueNoExiste"+",departamentNuevo"+",Produccion"+",codicardNuevo"+","+"32624"+",mailNuevo"+",davicito@gmail.com"+
                    ",telephonNuevo"+",34+12345678"+",dni,"+"11111111A"+","+orden;

            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();
            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                listaEmpleados = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }

            assertEquals("\nEl nombre de la empresa no existe en el registro",mensaje);

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
         18 - delete Empleado OK  ---------------------- deleteEmpleado
         (Modificamos al usuario David cambiando su telefono: 34+12345678
         por la palabra ALMERIA
         19 - delete Empleado NG  ---------------------- deleteEmpleado_NG

     */
    /**
     * Test nª18. En este caso lo que hacemos es eliminar un empleado en la BD
     * Para comprobar el test, primero buscamos un empleado para comprobar que existe
     * luego lo eliminamos y  comprobamos que ya no  existe en la BD
     */
    @Test
    public  void delecteEmpleado(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Empleados> listaEmpleados = new ArrayList<>();
            String mensaje;
            String palabra;
            Object receivedData;

            // FASE 1 - Lo primero es comprobar que  existe ese dni el la BD.
            //Existe porqué lo hemos creado en la prueba de los Inserts es Juanito
            palabra = codigo+",0,"+nombreTabla+",dni"+",12345678Z"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                listaEmpleados = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            assertFalse(listaEmpleados.isEmpty());//comprobamos que tenemos un empleado con ese dni

            // FASE - 2  eliminamos el empleado de la BD
            palabra = codigo+","+"3"+","+nombreTabla+",dni,"+"12345678Z"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();
            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                listaEmpleados = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            //limpiamos el ArrayList ya que si no, nos dice que listaEmpleados no está vacio
            listaEmpleados.clear();
            // FASE - 3 y por último comprobamos que se ha eliminado al usuario Juanito
            palabra = codigo+",0,"+nombreTabla+",dni"+",12345678Z"+","+orden;
            escriptor.write(palabra);
            escriptor.newLine();
            escriptor.flush();

            perEnt = new ObjectInputStream(socket.getInputStream());
            receivedData = perEnt.readObject();
            if (receivedData instanceof List) {
                listaEmpleados = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            assertTrue(listaEmpleados.isEmpty());//comprobamos que no tenemos un empleado con ese dni


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test nª19. En este caso lo que hacemos es  intentareliminar un empleado
     * que no existe en la BD.
     * El servidor nos enviará un error que compararemos con nuestro mensaje
     */
    @Test
    public  void delecteEmpleado_NG(){

        try {

            ObjectInputStream perEnt;
            ArrayList<Empleados> listaEmpleados = new ArrayList<>();
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
                listaEmpleados = (ArrayList) receivedData;
            } else if (receivedData instanceof String) {
                mensaje = (String) receivedData;
            }
            assertTrue(listaEmpleados.isEmpty());//comprobamos que no tenemos un empleado con ese dni
            // y comprobamos el mensaje del server, que no puede eliminar al empleado
            assertEquals("\nEl Dni no existe en el registro",mensaje);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


}