package modelo;

public class Resultado<T> {
    private T valor;
    private String tipo;

    public Resultado(T valor, String tipo) {
        this.valor = valor;
        this.tipo = tipo;
    }

    public T getValor() {
        return valor;
    }

    public String getTipo() {
        return tipo;
    }
}
