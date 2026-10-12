/**
 * Tipos de token que pueden aparecer en una expresión aritmética.
 *
 * @author Braden Lee Knuter Córdoba
 * @version 1.2
 */
public enum TipoToken {

    /** Número literal, por ejemplo 12 o 3.5. */
    NUMERO,

    /** Variable numérica, por ejemplo x o precio. */
    VARIABLE,

    /** Operador de dos operandos: + - * / ^ */
    OPERADOR,

    /** Signo negativo de un solo operando, como en -x. */
    OPERADOR_UNARIO,

    /** Paréntesis que abre. */
    PARENTESIS_IZQUIERDO,

    /** Paréntesis que cierra. */
    PARENTESIS_DERECHO
}
