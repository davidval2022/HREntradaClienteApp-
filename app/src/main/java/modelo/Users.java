package modelo;

/**
 *
 * @author david
 * Es una clase persistente y representa  en la BD a la tabla Users
 */
import java.io.Serializable;

@SuppressWarnings("serial")


public class Users implements Serializable,Comparable<Users>{
    private static final long serialVersionUID = 6529685098267757690L;

    private String login;
    private String pass;
    private int numtipe;
    private String dni;
    private int codigo;//no se utiliza



    public Users(String login, String pass, int numTipe, String dni) {
        this.login = login;
        this.pass = pass;
        this.numtipe = numTipe;
        this.dni = dni;
    }
    public Users(String login, String pass, int numTipe, String dni, int codigo) {
        this.login = login;
        this.pass = pass;
        this.numtipe = numTipe;
        this.dni = dni;
        this.codigo = codigo;
    }


    public Users(){

    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public int getNumtipe() {
        return numtipe;
    }

    public void setNumtipe(int numtipe) {
        this.numtipe = numtipe;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public int getCodigo() {return codigo;}

    public void setCodigo(int codigo) {this.codigo = codigo;}

    @Override
    public int compareTo(Users t) {
        String a = new String(String.valueOf(this.getLogin()));
        String b = new String(String.valueOf(t.getLogin()));
        return a.toLowerCase().compareTo(b.toLowerCase());
    }




}
