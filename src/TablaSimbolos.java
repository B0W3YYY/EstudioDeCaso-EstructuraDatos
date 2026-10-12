import java.util.ArrayList;

/**
 * Tabla de símbolos con las variables de las expresiones, como la que usan
 * los compiladores. Las variables se guardan ordenadas por nombre, igual que
 * un arreglo ordenado: al insertar se mueven una casilla a la derecha las que
 * son mayores (idea del ordenamiento por inserción) y para buscar se usa
 * búsqueda binaria.
 *
 * @author Braden Lee Knuter Córdoba
 * @version 1.2
 */
public class TablaSimbolos {

    //Atributos.

    /** Variables ordenadas alfabéticamente por nombre. */
    private ArrayList<Variable> variables;

    //Constructor.

    /**
     * Crea una tabla de símbolos vacía.
     */
    public TablaSimbolos() {
        variables = new ArrayList<>();
    }

    //Métodos.

    /**
     * Crea la tabla que muestra los pasos de la búsqueda binaria.
     *
     * @return tabla con columnas Izquierda, Derecha, Medio y Decisión
     */
    public static TablaConsola crearTablaBusqueda() {
        return new TablaConsola(new String[] {"Izquierda", "Derecha", "Medio (variable)", "Decisión"});
    }

    /**
     * Guarda una variable nueva o cambia el valor de una que ya existe.
     *
     * @param nombre nombre de la variable
     * @param valor  valor que se le asigna
     * @return true si la variable es nueva, false si solo se cambió su valor
     */
    public boolean definir(String nombre, double valor) {
        Variable existente = buscar(nombre);
        if (existente != null) {
            existente.setValor(valor);
            return false;
        }
        insertarOrdenado(new Variable(nombre, valor));
        return true;
    }

    /**
     * Busca una variable sin mostrar los pasos.
     *
     * @param nombre nombre de la variable
     * @return la variable, o null si no está en la tabla
     */
    public Variable buscar(String nombre) {
        return buscar(nombre, null);
    }

    /**
     * Busca una variable con búsqueda binaria: compara con la variable del
     * medio y descarta la mitad donde no puede estar. Funciona porque la lista
     * siempre está ordenada.
     *
     * @param nombre nombre de la variable
     * @param pasos  tabla donde se anota cada comparación, o null para no anotarlas
     * @return la variable, o null si no está en la tabla
     */
    public Variable buscar(String nombre, TablaConsola pasos) {
        int izquierda = 0;
        int derecha = variables.size() - 1;
        while (izquierda <= derecha) {
            int medio = (izquierda + derecha) / 2;
            Variable variableMedio = variables.get(medio);
            int comparacion = nombre.compareTo(variableMedio.getNombre());
            String decision;
            if (comparacion == 0) {
                decision = "encontrada";
            } else if (comparacion < 0) {
                decision = nombre + " va antes: buscar a la izquierda";
            } else {
                decision = nombre + " va después: buscar a la derecha";
            }
            if (pasos != null) {
                pasos.agregarFila(new String[] {String.valueOf(izquierda), String.valueOf(derecha),
                        medio + " (" + variableMedio.getNombre() + ")", decision});
            }
            if (comparacion == 0) {
                return variableMedio;
            } else if (comparacion < 0) {
                derecha = medio - 1;
            } else {
                izquierda = medio + 1;
            }
        }
        return null;
    }

    /**
     * Devuelve una copia de la lista de variables, ya ordenada por nombre.
     *
     * @return lista de variables
     */
    public ArrayList<Variable> obtenerVariables() {
        return new ArrayList<>(variables);
    }

    /**
     * Indica cuántas variables hay definidas.
     *
     * @return cantidad de variables
     */
    public int cantidad() {
        return variables.size();
    }

    /**
     * Borra todas las variables.
     */
    public void vaciar() {
        variables.clear();
    }

    /**
     * Inserta la variable en su lugar alfabético. Es el paso central del
     * ordenamiento por inserción: la variable se agrega al final y se mueve
     * hacia la izquierda mientras la anterior tenga un nombre mayor.
     *
     * @param nueva variable que se inserta
     */
    private void insertarOrdenado(Variable nueva) {
        variables.add(nueva);
        int posicion = variables.size() - 1;
        while (posicion > 0 && variables.get(posicion - 1).getNombre().compareTo(nueva.getNombre()) > 0) {
            variables.set(posicion, variables.get(posicion - 1)); // la mayor se mueve una casilla a la derecha
            posicion--;
        }
        variables.set(posicion, nueva);
    }
}
