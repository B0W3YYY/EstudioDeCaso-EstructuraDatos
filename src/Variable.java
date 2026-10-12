/**
 * Variable de la tabla de símbolos: su nombre y su valor.
 *
 * @author Braden Lee Knuter Córdoba
 * @version 1.2
 */
public class Variable {

    //Atributos.

    /** Nombre de la variable, por ejemplo "x". */
    private String nombre;

    /** Valor numérico de la variable. */
    private double valor;

    //Constructor.

    /**
     * Crea una variable.
     *
     * @param nombre nombre de la variable
     * @param valor  valor de la variable
     */
    public Variable(String nombre, double valor) {
        this.nombre = nombre;
        this.valor = valor;
    }

    //Getters y setters.

    /**
     * Devuelve el nombre de la variable.
     *
     * @return nombre de la variable
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Cambia el nombre de la variable.
     *
     * @param nombre nombre nuevo
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Devuelve el valor de la variable.
     *
     * @return valor de la variable
     */
    public double getValor() {
        return valor;
    }

    /**
     * Cambia el valor de la variable.
     *
     * @param valor valor nuevo
     */
    public void setValor(double valor) {
        this.valor = valor;
    }

    //Métodos.

    /**
     * Describe la variable, por ejemplo "x = 5".
     *
     * @return nombre y valor de la variable
     */
    @Override
    public String toString() {
        return nombre + " = " + Token.formatearNumero(valor);
    }
}
