import java.util.ArrayList;

/**
 * Tabla de texto para la consola, con bordes parecidos a las celdas de una
 * hoja de cálculo. La primera columna, "#", numera las filas.
 * Se usa para mostrar los tokens y el paso a paso de la pila.
 *
 * @author Braden Lee Knuter Córdoba
 * @version 1.2
 */
public class TablaConsola {

    //Atributos.

    /** Títulos de las columnas, incluida la columna "#". */
    private String[] columnas;

    /** Filas de la tabla; la primera celda de cada fila es su número. */
    private ArrayList<String[]> filas;

    //Constructor.

    /**
     * Crea una tabla vacía con los encabezados indicados.
     *
     * @param encabezados títulos de las columnas (la columna "#" se agrega sola)
     */
    public TablaConsola(String[] encabezados) {
        columnas = new String[encabezados.length + 1];
        columnas[0] = "#";
        for (int i = 0; i < encabezados.length; i++) {
            columnas[i + 1] = encabezados[i];
        }
        filas = new ArrayList<>();
    }

    //Métodos.

    /**
     * Agrega una fila al final de la tabla. Si faltan celdas, quedan vacías.
     *
     * @param celdas contenido de cada columna, en el orden de los encabezados
     */
    public void agregarFila(String[] celdas) {
        String[] fila = new String[columnas.length];
        fila[0] = String.valueOf(filas.size() + 1);
        for (int i = 1; i < fila.length; i++) {
            if (i - 1 < celdas.length) {
                fila[i] = celdas[i - 1];
            } else {
                fila[i] = "";
            }
        }
        filas.add(fila);
    }

    /**
     * Imprime la tabla completa en la consola.
     */
    public void imprimir() {
        // Cada columna mide lo mismo que su texto más largo.
        int[] anchos = new int[columnas.length];
        for (int c = 0; c < columnas.length; c++) {
            anchos[c] = columnas[c].length();
        }
        for (String[] fila : filas) {
            for (int c = 0; c < fila.length; c++) {
                if (fila[c].length() > anchos[c]) {
                    anchos[c] = fila[c].length();
                }
            }
        }
        String borde = crearBorde(anchos);
        System.out.println(borde);
        System.out.println(crearLinea(columnas, anchos));
        System.out.println(borde);
        for (String[] fila : filas) {
            System.out.println(crearLinea(fila, anchos));
        }
        System.out.println(borde);
    }

    /**
     * Agrega espacios al final de un texto hasta que tenga el ancho indicado.
     *
     * @param texto texto original
     * @param ancho cantidad de caracteres que debe tener
     * @return texto con espacios al final
     */
    public static String rellenar(String texto, int ancho) {
        String resultado = texto;
        while (resultado.length() < ancho) {
            resultado = resultado + " ";
        }
        return resultado;
    }

    /**
     * Crea la línea horizontal que separa el encabezado y las filas.
     *
     * @param anchos ancho de cada columna
     * @return línea del estilo +----+------+
     */
    private String crearBorde(int[] anchos) {
        String borde = "+";
        for (int c = 0; c < anchos.length; c++) {
            borde = borde + "-".repeat(anchos[c] + 2) + "+";
        }
        return borde;
    }

    /**
     * Crea una línea de la tabla con cada celda rellenada hasta su ancho.
     *
     * @param celdas texto de cada celda
     * @param anchos ancho de cada columna
     * @return línea del estilo | 1  | x    |
     */
    private String crearLinea(String[] celdas, int[] anchos) {
        String linea = "|";
        for (int c = 0; c < celdas.length; c++) {
            linea = linea + " " + rellenar(celdas[c], anchos[c]) + " |";
        }
        return linea;
    }
}
