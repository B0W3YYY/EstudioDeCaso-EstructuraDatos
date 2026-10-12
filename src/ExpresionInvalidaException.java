/**
 * Error en una expresión aritmética: un carácter no válido, un paréntesis sin
 * pareja, un operador sin operando, una división entre cero, etc.
 *
 * @author Braden Lee Knuter Córdoba
 * @version 1.2
 */
public class ExpresionInvalidaException extends Exception {

    //Constructor.

    /**
     * Crea el error con un mensaje para el usuario.
     *
     * @param mensaje explicación del error
     */
    public ExpresionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
