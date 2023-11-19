package davidvalentin.ioc.hrentradaclienteapp.utilidades;

import java.io.IOException;
import java.net.Socket;

/**
 * La clase `SocketManager` gestiona la creación, obtención y cierre de un socket para la comunicación
 * con un servidor remoto utilizando una dirección IP y un puerto específicos.
 */
public class SocketManager {
    // Instancia del socket utilizado para la comunicación con el servidor
    private Socket socket;
    // Dirección IP del servidor
    private String serverAddress;
    // Puerto del servidor
    private int serverPort;


    /**
     * Constructor de la clase `SocketManager`.
     *
     * @param serverAddress Dirección IP del servidor.
     * @param serverPort    Puerto del servidor.
     */
    public SocketManager(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    /**
     * Abre un nuevo socket y establece la conexión con el servidor utilizando la dirección IP y el puerto proporcionados.
     */
    public void openSocket() {
        try {
            socket = new Socket(serverAddress, serverPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Obtiene la instancia del socket utilizado para la comunicación con el servidor.
     *
     * @return Instancia del socket.
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * Cierra el socket si está abierto y no cerrado.
     */
    public void closeSocket() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
