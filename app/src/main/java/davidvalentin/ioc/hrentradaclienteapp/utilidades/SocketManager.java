package davidvalentin.ioc.hrentradaclienteapp.utilidades;

import java.io.IOException;
import java.net.Socket;

public class SocketManager {
    private Socket socket;
    private String serverAddress;
    private int serverPort;

    public SocketManager(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public void openSocket() {
        try {
            socket = new Socket(serverAddress, serverPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return socket;
    }

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
