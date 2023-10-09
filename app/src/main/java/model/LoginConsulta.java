package model;

/**
 *
 * @author david
 */
import java.io.Serializable;

@SuppressWarnings("serial")

public class LoginConsulta implements Serializable{
    String tipoDeLogin;//Lo envía el cliente. hay 1-hacer login con usuario y contraseña 2-hacer login con codigo y consultas 3-logout
    String usuario;//para hacer el login con usurio y pass
    String contraseña;
    String codigo;//una vez logeados obtendremos este codigo, que será el que el cliente envie en el tipoDeLogin numero 2
    String tabla;//para las consultas el nombre de la tabla
    String columna;//para las consultas el nombre de la columna
    String palabra;//para las consultas la palabra a buscar (el where). De momento solo una,
    String orden;//lara las consultas el orden (será una palabra con el nombre de la columna)
    String infoDelServer;//Información que el server enviará al cliente en ocasiones
    String error; //0  no hay error / 1 para usuario ya registrado / 2 error en las credenciales / 3 ...

    public LoginConsulta(String tipoDeLogin, String usuario, String contraseña, String codigo, String tabla, String columna, String palabra, String orden, String infoDelServer, String error) {
        this.tipoDeLogin = tipoDeLogin;
        this.usuario = usuario;
        this.contraseña = contraseña;
        this.codigo = codigo;
        this.tabla = tabla;
        this.columna = columna;
        this.palabra = palabra;
        this.orden = orden;
        this.infoDelServer = infoDelServer;
        this.error = error;
    }



    public LoginConsulta(String tipoDeLogin, String usuario, String contraseña, String codigo, String tabla, String columna, String palabra, String orden, String infoDelServer) {
        this.tipoDeLogin = tipoDeLogin;
        this.usuario = usuario;
        this.contraseña = contraseña;
        this.codigo = codigo;
        this.tabla = tabla;
        this.columna = columna;
        this.palabra = palabra;
        this.orden = orden;
        this.infoDelServer = infoDelServer;
    }

    public LoginConsulta() {
    }
    //este objeto será para hacer el login
    public LoginConsulta(String tipoDeLogin, String usuario, String contraseña) {
        this.tipoDeLogin = tipoDeLogin;
        this.usuario = usuario;
        this.contraseña = contraseña;
    }
    //este ya para las consultas
    public LoginConsulta(String tipoDeLogin, String codigo, String tabla, String columna, String palabras, String orden) {
        this.tipoDeLogin = tipoDeLogin;
        this.codigo = codigo;
        this.tabla = tabla;
        this.columna = columna;
        this.palabra = palabra;
        this.orden = orden;
    }

    public  void LoginConsultaInicio(){
        String mensaje= "Bienvenido al server, por favor sigue las intrucciones, por favor";
        this.tipoDeLogin = "0";
        this.usuario = "0";
        this.contraseña = "0";
        this.codigo = "0";
        this.tabla = "0";
        this.columna = "0";
        this.palabra = "0";
        this.orden = "0";
        this.infoDelServer = mensaje;
        this.error = "0";

    }

    public String getTipoDeLogin() {
        return tipoDeLogin;
    }

    public void setTipoDeLogin(String tipoDeLogin) {
        this.tipoDeLogin = tipoDeLogin;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getTabla() {
        return tabla;
    }

    public void setTabla(String tabla) {
        this.tabla = tabla;
    }

    public String getColumna() {
        return columna;
    }

    public void setColumna(String columna) {
        this.columna = columna;
    }

    public String getPalabra() {
        return palabra;
    }

    public void setPalabra(String palabra) {
        this.palabra = palabra;
    }

    public String getOrden() {
        return orden;
    }

    public void setOrden(String orden) {
        this.orden = orden;
    }

    public String getInfoDelServer() {
        return infoDelServer;
    }

    public void setInfoDelServer(String infoDelServer) {
        this.infoDelServer = infoDelServer;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }











}
