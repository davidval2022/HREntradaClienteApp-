package davidvalentin.ioc.hrentradaclienteapp.login;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * @author David Valentin Mateo
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
    //nuevo para cifrado
    private InputStream keystoreInputStream;
    private Context context;



    /**
     * Constructor de la clase `SocketManager`.
     *
     * @param serverAddress Dirección IP del servidor.
     * @param serverPort    Puerto del servidor.
     */
    public SocketManager(String serverAddress, int serverPort, Context context) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.context = context;
    }

    /**
     * Abre un nuevo socket y establece la conexión con el servidor utilizando la dirección IP y el puerto proporcionados.
     */
    public void openSocket() {
        try {
            /*
            //Para comprobar que el archivo pueda leerse.. pruebas
            try {
                String[] assets = context.getAssets().list("certificados/client");
                for (String asset : assets) {
                    Log.d("Assets", "Asset in certificados/client: " + asset);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            */
            // ESTA PARTE ES PARA CIFRAR.. DESDE AQUÍ (la carpeta assets hay que crearla a mano)
            InputStream keystoreInputStream = context.getAssets().open("certificados/client/clientTrustedCerts.bks");  // Cambié la extensión a .bks
            KeyStore trustStore = KeyStore.getInstance("BKS");  // Cambié el tipo de almacén a BKS
            trustStore.load(keystoreInputStream, "254535fd32_A".toCharArray());  // Cambié la contraseña y el método load

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

            SSLSocketFactory clientFactory = sslContext.getSocketFactory();

            socket = clientFactory.createSocket(serverAddress, serverPort);
            //HASTA AQUI
            //SI SE QUIERE SIN CIFRAR COMENTAR LAS ANTERIORES LINEAS Y DESCOMENTAR LA DE ABAJO
           // socket = new Socket(serverAddress, serverPort);//manera sin cifrar..
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
