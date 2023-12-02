package davidvalentin.ioc.hrentradaclienteapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

//info: https://www.nestoralmeida.com/testing-con-junit-4/
public class LoginUnitTest {
    /*@Test
    public void addition_isCorrect() {//lo dejo para probar que los test funcionan
        assertEquals(4, 2 + 2);
    }*/

    String ip = "192.168.1.12";
    int puerto = 8888;

    //////////////////  PRUEBAS PARTE DE LOGIN / LOGOUT ////////////////////////////////////

    /**
     * Probamos el login de admin, el test debe dar ok.. comprobamos que el server nos envía un código
     * y que la primera letra de ese codigo en el caso de admin es un A
     * Nos mandará un código tipo como este: A70013
     */
    @Test
    public void loginAdmin(){
        Socket socket = null;
        try {
            socket = new Socket(ip, puerto);
            BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter escriptor = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            String mensajeServer = lector.readLine();
            //enviamos los datos de un usuario administrador
            escriptor.write("admin,admin");
            escriptor.newLine();
            escriptor.flush();
            mensajeServer = lector.readLine();
            //El primer carácter del codigo recibido debe ser una A (admin)(notar que la A va entre '' por se character)
            assertEquals('A',mensajeServer.charAt(0));
            //cerramos sesion
            escriptor.write("exit");
            escriptor.newLine();
            escriptor.flush();
            //leemos el último mensaje  que nos envia el server para poder cerrar correctamente
            mensajeServer = lector.readLine();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Probamos el login de admin, el test debe dar ok.. comprobamos que el server nos envía un código
     * y en este caso al reves que el anterior dará bueno si la respuesta es erronea..
     * es asserNotEquals, la primera letra de la respuesta no puede ser la letra A
     */
    @Test
    public void loginAdminError(){
        Socket socket = null;
        try {
            socket = new Socket(ip, puerto);
            BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter escriptor = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            String mensajeServer = lector.readLine();
            //enviamos los datos de un usuario administrador
            escriptor.write("admin,bimbo");
            escriptor.newLine();
            escriptor.flush();
            mensajeServer = lector.readLine();
            //Sí el mesaje recibido es -1, es que el login es erroneo)
            assertEquals("-1",mensajeServer);
            //cerramos sesion
            escriptor.write("exit");
            escriptor.newLine();
            escriptor.flush();
            //leemos el último mensaje  que nos envia el server para poder cerrar correctamente
            mensajeServer = lector.readLine();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Probamos el login de user, el test debe dar ok.. comprobamos que el server nos envía un código
     * y que la primera letra de ese codigo en el caso de user es un U
     * Nos mandará un codigo con este aspecto: U70013
     */
    @Test
    public void loginUser(){
        Socket socket = null;
        try {
            socket = new Socket(ip, puerto);
            BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter escriptor = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            String mensajeServer = lector.readLine();
            //enviamos los datos de un usuario administrador
            escriptor.write("user,user");
            escriptor.newLine();
            escriptor.flush();
            mensajeServer = lector.readLine();
            //el primer carácter del codigo recibido debe ser una U (user)
            assertEquals('U',mensajeServer.charAt(0));
            //cerramos sesion
            escriptor.write("exit");
            escriptor.newLine();
            escriptor.flush();
            //leemos el último mensaje  que nos envia el server para poder cerrar correctamente
            mensajeServer = lector.readLine();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Probamos el login de user, el test debe dar ok.. comprobamos que el server nos envía un código
     * y en este caso al reves que el anterior dará bueno si la respuesta es erronea..
     * es asserNotEquals, la respuesta no puede ser la letra U
     */
    @Test
    public void loginUserError(){
        Socket socket = null;
        try {
            socket = new Socket(ip, puerto);
            BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter escriptor = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            String mensajeServer = lector.readLine();
            //enviamos los datos de un usuario administrador
            escriptor.write("admin,bimbo");
            escriptor.newLine();
            escriptor.flush();
            mensajeServer = lector.readLine();
            //Sí el mesaje recibido es -1, es que el login es erroneo)
            assertEquals("-1", mensajeServer);
            //cerramos sesion
            escriptor.write("exit");
            escriptor.newLine();
            escriptor.flush();
            //leemos el último mensaje  que nos envia el server para poder cerrar correctamente
            mensajeServer = lector.readLine();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}