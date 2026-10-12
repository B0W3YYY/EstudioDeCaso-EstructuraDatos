/**
 * Token de una expresión aritmética: un número, una variable, un operador o
 * un paréntesis. Guarda su valor, su tipo y la posición donde aparece.
 *
 * @author Braden Lee Knuter Córdoba
 * @version 1.2
 */
public class Token {

    //Constantes.

    /** Símbolo con el que se guarda el menos unario, para no confundirlo con la resta. */
    public static final String MENOS_UNARIO = "~";

    //Atributos.

    /** Texto del token, por ejemplo "12", "x" o "+". */
    private String valor;

    /** Tipo del token. */
    private TipoToken tipo;

    /** Posición en la expresión contando desde 1 (0 si no viene de una expresión). */
    private int posicion;

    //Constructor.

    /**
     * Crea un token.
     *
     * @param valor    texto del token
     * @param tipo     tipo del token
     * @param posicion posición donde empieza el token en la expresión, contando desde 1
     */
    public Token(String valor, TipoToken tipo, int posicion) {
        this.valor = valor;
        this.tipo = tipo;
        this.posicion = posicion;
    }

    //Getters.

    /**
     * Devuelve el texto del token.
     *
     * @return valor del token
     */
    public String getValor() {
        return valor;
    }

    /**
     * Devuelve el tipo del token.
     *
     * @return tipo del token
     */
    public TipoToken getTipo() {
        return tipo;
    }

    /**
     * Devuelve la posición del token en la expresión.
     *
     * @return posición contando desde 1
     */
    public int getPosicion() {
        return posicion;
    }

    //Métodos.

    /**
     * Crea un token de tipo número a partir de un resultado calculado. Se usa
     * al evaluar, cuando la pila guarda los resultados parciales.
     *
     * @param numero valor del token
     * @return token de tipo NUMERO
     */
    public static Token desdeNumero(double numero) {
        return new Token(formatearNumero(numero), TipoToken.NUMERO, 0);
    }

    /**
     * Convierte un número a texto. Si el número es entero se muestra sin ".0",
     * por ejemplo 14 en lugar de 14.0.
     *
     * @param numero número que se quiere mostrar
     * @return el número como texto
     */
    public static String formatearNumero(double numero) {
        if (numero == Math.floor(numero) && Math.abs(numero) < 1e15) {
            return String.valueOf((long) numero);
        }
        return String.valueOf(numero);
    }

    /**
     * Convierte el valor del token a número. Solo se usa con tokens de tipo NUMERO.
     *
     * @return valor numérico del token
     */
    public double getValorNumerico() {
        return Double.parseDouble(valor);
    }

    /**
     * Indica si el token es un operando, es decir, un número o una variable.
     *
     * @return true si es número o variable
     */
    public boolean esOperando() {
        return tipo == TipoToken.NUMERO || tipo == TipoToken.VARIABLE;
    }

    /**
     * Devuelve la precedencia del operador: mientras más alto el número, antes
     * se resuelve. La potencia va por encima del negativo para que -2^2 dé -4.
     *
     * @return 1 para + y -, 2 para * y /, 3 para el negativo, 4 para ^, y 0 si no es operador
     */
    public int getPrecedencia() {
        if (tipo == TipoToken.OPERADOR_UNARIO) {
            return 3;
        }
        if (tipo != TipoToken.OPERADOR) {
            return 0;
        }
        if (valor.equals("+") || valor.equals("-")) {
            return 1;
        }
        if (valor.equals("*") || valor.equals("/")) {
            return 2;
        }
        return 4; // potencia
    }

    /**
     * Indica si el operador se agrupa de derecha a izquierda, como la potencia
     * (2^3^2 es 2^9) y el negativo.
     *
     * @return true si se agrupa de derecha a izquierda
     */
    public boolean esAsociativoDerecha() {
        return tipo == TipoToken.OPERADOR_UNARIO || valor.equals("^");
    }

    /**
     * Devuelve el nombre del tipo del token en español.
     *
     * @return descripción del tipo, por ejemplo "Variable"
     */
    public String getDescripcionTipo() {
        switch (tipo) {
            case NUMERO:
                return "Número";
            case VARIABLE:
                return "Variable";
            case OPERADOR:
                return "Operador";
            case OPERADOR_UNARIO:
                return "Operador unario (negativo)";
            case PARENTESIS_IZQUIERDO:
                return "Paréntesis de apertura";
            default:
                return "Paréntesis de cierre";
        }
    }

    /**
     * Describe el token con su valor y su tipo, por ejemplo "x (Variable)".
     *
     * @return descripción corta del token
     */
    public String describir() {
        return valor + " (" + getDescripcionTipo() + ")";
    }

    /**
     * Devuelve el valor del token, que es lo que se muestra en las tablas.
     *
     * @return valor del token
     */
    @Override
    public String toString() {
        return valor;
    }
}
