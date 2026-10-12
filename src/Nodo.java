/**
 * Nodo de la pila. Es una clase autorreferenciada: además del token de la
 * expresión (con su valor y su tipo), guarda la referencia al nodo que está
 * debajo de él en la pila.
 *
 * @author Braden Lee Knuter Córdoba
 * @version 1.2
 */
public class Nodo {

    //Atributos.

    /** Token de la expresión que guarda el nodo. */
    private Token token;

    /** Nodo que está debajo en la pila; null si este nodo es la base. */
    private Nodo siguiente;

    //Constructores.

    /**
     * Crea un nodo que todavía no apunta a ningún otro.
     *
     * @param token token que guarda el nodo
     */
    public Nodo(Token token) {
        this.token = token;
        this.siguiente = null;
    }

    /**
     * Crea un nodo que apunta a otro nodo.
     *
     * @param token     token que guarda el nodo
     * @param siguiente nodo que queda debajo
     */
    public Nodo(Token token, Nodo siguiente) {
        this.token = token;
        this.siguiente = siguiente;
    }

    //Getters y setters.

    /**
     * Devuelve el token guardado en el nodo.
     *
     * @return token del nodo
     */
    public Token getToken() {
        return token;
    }

    /**
     * Cambia el token guardado en el nodo.
     *
     * @param token token nuevo
     */
    public void setToken(Token token) {
        this.token = token;
    }

    /**
     * Devuelve el nodo que está debajo de este.
     *
     * @return nodo siguiente, o null si este nodo es la base
     */
    public Nodo getSiguiente() {
        return siguiente;
    }

    /**
     * Cambia el nodo que está debajo de este.
     *
     * @param siguiente nodo siguiente, o null para que este nodo sea la base
     */
    public void setSiguiente(Nodo siguiente) {
        this.siguiente = siguiente;
    }

    //Métodos.

    /**
     * Describe el nodo con el valor y el tipo de su token, por ejemplo "x (Variable)".
     *
     * @return descripción del nodo
     */
    @Override
    public String toString() {
        return token.describir();
    }
}
