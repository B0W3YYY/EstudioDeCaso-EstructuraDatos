/**
 * Pila de tokens implementada con nodos enlazados. Sigue el principio LIFO:
 * el último nodo que entra es el primero que sale, y solo la cima es accesible.
 *
 * <p>A diferencia de la pila estática con arreglo y variable top, esta pila
 * crece nodo por nodo, así que no se llena y no necesita un método isFull().</p>
 *
 * @author Braden Lee Knuter Córdoba
 * @version 1.2
 */
public class Pila {

    //Atributos.

    /** Nodo que está en la cima; null cuando la pila está vacía. */
    private Nodo cima;

    /** Cantidad de elementos, para que size() no tenga que recorrer la pila. */
    private int tamanio;

    //Constructor.

    /**
     * Crea una pila vacía.
     */
    public Pila() {
        cima = null;
        tamanio = 0;
    }

    //Métodos.

    /**
     * Agrega un token en la cima de la pila.
     *
     * @param token token que se quiere apilar
     */
    public void push(Token token) {
        if (token == null) {
            System.out.println("No se puede apilar un token vacío.");
            return;
        }
        // El nodo nuevo apunta a la cima anterior y pasa a ser la nueva cima.
        Nodo nuevo = new Nodo(token);
        nuevo.setSiguiente(cima);
        cima = nuevo;
        tamanio++;
    }

    /**
     * Quita el token de la cima y lo devuelve.
     *
     * @return token que estaba en la cima, o null si la pila está vacía
     */
    public Token pop() {
        if (isEmpty()) {
            return null; // valor convencional para indicar que la pila está vacía
        }
        Nodo nodoQueSale = cima;
        cima = cima.getSiguiente();
        // Se desconecta el nodo que sale para que no quede enlazado con la pila.
        nodoQueSale.setSiguiente(null);
        tamanio--;
        return nodoQueSale.getToken();
    }

    /**
     * Devuelve el token de la cima sin quitarlo.
     *
     * @return token de la cima, o null si la pila está vacía
     */
    public Token peek() {
        if (isEmpty()) {
            return null;
        }
        return cima.getToken();
    }

    /**
     * Indica si la pila no tiene elementos.
     *
     * @return true si la pila está vacía
     */
    public boolean isEmpty() {
        return cima == null;
    }

    /**
     * Devuelve la cantidad de elementos de la pila.
     *
     * @return cantidad de tokens apilados
     */
    public int size() {
        return tamanio;
    }

    /**
     * Elimina todos los elementos. Basta con soltar la referencia a la cima:
     * los nodos sin referencia los libera el recolector de basura de Java.
     */
    public void vaciar() {
        cima = null;
        tamanio = 0;
    }

    /**
     * Imprime la pila en forma vertical, con la cima arriba y la base abajo,
     * como una columna de casillas.
     */
    public void mostrar() {
        if (isEmpty()) {
            System.out.println("  (la pila está vacía)");
            return;
        }
        // Primero se busca el valor más largo para que todas las casillas tengan el mismo ancho.
        int ancho = 1;
        Nodo nodoActual = cima;
        while (nodoActual != null) {
            if (nodoActual.getToken().getValor().length() > ancho) {
                ancho = nodoActual.getToken().getValor().length();
            }
            nodoActual = nodoActual.getSiguiente();
        }
        String borde = "  +" + "-".repeat(ancho + 2) + "+";
        nodoActual = cima;
        while (nodoActual != null) {
            String marca = "";
            if (nodoActual == cima) {
                marca = "  <- cima";
            } else if (nodoActual.getSiguiente() == null) {
                marca = "  <- base";
            }
            System.out.println(borde);
            System.out.println("  | " + TablaConsola.rellenar(nodoActual.getToken().getValor(), ancho) + " |" + marca);
            nodoActual = nodoActual.getSiguiente();
        }
        System.out.println(borde);
    }

    /**
     * Devuelve la pila en una sola línea, de la base (izquierda) a la cima
     * (derecha). Es el formato que usan las tablas paso a paso.
     *
     * @return por ejemplo "( + *", o "(vacía)" si no hay elementos
     */
    @Override
    public String toString() {
        if (isEmpty()) {
            return "(vacía)";
        }
        // Se recorre de la cima a la base y cada valor se pone adelante del texto,
        // así el resultado queda ordenado de la base a la cima.
        String texto = "";
        Nodo nodoActual = cima;
        while (nodoActual != null) {
            texto = nodoActual.getToken().getValor() + " " + texto;
            nodoActual = nodoActual.getSiguiente();
        }
        return texto.trim();
    }
}
