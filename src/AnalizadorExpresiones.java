import java.util.ArrayList;

/**
 * Algoritmos que usan la pila para analizar una expresión aritmética:
 * separarla en tokens, validar paréntesis y sintaxis, convertirla de
 * notación infija a postfija y evaluarla.
 *
 * <p>Los métodos que reciben una TablaConsola anotan en ella cada operación
 * que hacen sobre la pila, para que el usuario vea el proceso paso a paso.</p>
 *
 * @author Braden Lee Knuter Córdoba
 * @version 1.2
 */
public class AnalizadorExpresiones {

    //Tablas para mostrar el paso a paso.

    /**
     * Crea la tabla que usan la validación de paréntesis y la evaluación.
     *
     * @return tabla con columnas Token, Operaciones y Pila
     */
    public static TablaConsola crearTablaPila() {
        return new TablaConsola(new String[] {"Token", "Operaciones en la pila", "Pila (base -> cima)"});
    }

    /**
     * Crea la tabla que usa la conversión a postfija.
     *
     * @return tabla con columnas Token, Operaciones, Pila y Salida
     */
    public static TablaConsola crearTablaConversion() {
        return new TablaConsola(new String[] {"Token", "Operaciones en la pila", "Pila (base -> cima)", "Salida (postfija)"});
    }

    //1. Separar la expresión en tokens.

    /**
     * Separa el texto de la expresión en tokens. Los espacios se ignoran.
     * Un "-" que aparece donde se espera un operando (al inicio, después de
     * "(" o después de otro operador) es un signo negativo y se guarda como "~".
     *
     * @param expresion texto escrito por el usuario
     * @return tokens en el mismo orden en que aparecen
     * @throws ExpresionInvalidaException si hay un carácter no válido, un número
     *                                    mal escrito o la expresión está vacía
     */
    public static ArrayList<Token> separarTokens(String expresion) throws ExpresionInvalidaException {
        ArrayList<Token> tokens = new ArrayList<>();
        int inicio = 0;
        while (inicio < expresion.length()) {
            if (Character.isWhitespace(expresion.charAt(inicio))) {
                inicio++;
            } else {
                int fin = buscarFinDelLexema(expresion, inicio);
                String lexema = expresion.substring(inicio, fin);
                int posicion = inicio + 1; // el usuario cuenta las posiciones desde 1
                TipoToken tipo = clasificar(lexema, posicion);
                if (lexema.equals("-") && esperaOperando(tokens)) {
                    tokens.add(new Token(Token.MENOS_UNARIO, TipoToken.OPERADOR_UNARIO, posicion));
                } else {
                    tokens.add(new Token(lexema, tipo, posicion));
                }
                inicio = fin;
            }
        }
        if (tokens.isEmpty()) {
            throw new ExpresionInvalidaException("La expresión está vacía.");
        }
        return tokens;
    }

    /**
     * Crea un solo token a partir de un texto como "12", "x" o "+".
     * Lo usa el menú para hacer push manual y para definir variables.
     *
     * @param texto texto del token
     * @return token clasificado según su tipo
     * @throws ExpresionInvalidaException si el texto está vacío, no es válido o tiene más de un token
     */
    public static Token crearToken(String texto) throws ExpresionInvalidaException {
        String limpio = texto.trim();
        if (limpio.isEmpty() || buscarFinDelLexema(limpio, 0) != limpio.length()) {
            throw new ExpresionInvalidaException("Escriba un solo token, por ejemplo: 12, x, +, ( o ).");
        }
        return new Token(limpio, clasificar(limpio, 1), 0);
    }

    /**
     * Devuelve los nombres de las variables de la expresión, sin repetir y en
     * el orden en que aparecen.
     *
     * @param tokens tokens de la expresión
     * @return nombres de las variables
     */
    public static ArrayList<String> obtenerVariables(ArrayList<Token> tokens) {
        ArrayList<String> variables = new ArrayList<>();
        for (Token token : tokens) {
            if (token.getTipo() == TipoToken.VARIABLE && !variables.contains(token.getValor())) {
                variables.add(token.getValor());
            }
        }
        return variables;
    }

    /**
     * Une los valores de los tokens con espacios, por ejemplo "x 3 + 2 *".
     *
     * @param tokens tokens que se quieren mostrar
     * @return texto con los tokens separados por espacios
     */
    public static String unirTokens(ArrayList<Token> tokens) {
        String texto = "";
        for (int i = 0; i < tokens.size(); i++) {
            if (i > 0) {
                texto = texto + " ";
            }
            texto = texto + tokens.get(i).getValor();
        }
        return texto;
    }

    /**
     * Indica si un texto es un número válido: solo dígitos, como máximo un
     * punto decimal y al menos un dígito (por ejemplo 12, 3.5 o .75).
     *
     * @param texto texto que se revisa
     * @return true si el texto es un número válido
     */
    public static boolean esNumeroValido(String texto) {
        int puntos = 0;
        int digitos = 0;
        for (int i = 0; i < texto.length(); i++) {
            char caracter = texto.charAt(i);
            if (caracter == '.') {
                puntos++;
            } else if (caracter >= '0' && caracter <= '9') {
                digitos++;
            } else {
                return false;
            }
        }
        return puntos <= 1 && digitos > 0;
    }

    //2. Validar paréntesis y sintaxis.

    /**
     * Revisa toda la expresión: primero los paréntesis y después la sintaxis.
     *
     * @param tokens tokens de la expresión
     * @throws ExpresionInvalidaException con el primer error que se encuentre
     */
    public static void validar(ArrayList<Token> tokens) throws ExpresionInvalidaException {
        // Aquí solo interesa saber si hay error, por eso esta tabla no se muestra.
        validarParentesis(tokens, crearTablaPila());
        validarSintaxis(tokens);
    }

    /**
     * Revisa que cada "(" tenga su ")" usando una pila: cada "(" se apila y
     * cada ")" desapila el "(" más reciente. Es correcto si nunca se intentó
     * desapilar con la pila vacía y si al final la pila queda vacía.
     *
     * @param tokens tokens de la expresión
     * @param tabla  tabla donde se anota cada paso
     * @throws ExpresionInvalidaException si sobra o falta algún paréntesis
     */
    public static void validarParentesis(ArrayList<Token> tokens, TablaConsola tabla) throws ExpresionInvalidaException {
        Pila pila = new Pila();
        for (Token token : tokens) {
            String operaciones;
            if (token.getTipo() == TipoToken.PARENTESIS_IZQUIERDO) {
                pila.push(token);
                operaciones = "push (";
            } else if (token.getTipo() == TipoToken.PARENTESIS_DERECHO) {
                if (pila.isEmpty()) {
                    tabla.agregarFila(new String[] {")", "isEmpty=sí -> ERROR", pila.toString()});
                    throw new ExpresionInvalidaException("El ')' de la posición " + token.getPosicion()
                            + " no tiene un '(' que lo abra.");
                }
                Token apertura = pila.pop();
                operaciones = "pop ( (abierto en la posición " + apertura.getPosicion() + ")";
            } else {
                operaciones = "no es paréntesis";
            }
            tabla.agregarFila(new String[] {token.getValor(), operaciones, pila.toString()});
        }
        if (!pila.isEmpty()) {
            tabla.agregarFila(new String[] {"(fin)", "isEmpty=no -> ERROR", pila.toString()});
            String cantidad;
            if (pila.size() == 1) {
                cantidad = "Falta 1 paréntesis";
            } else {
                cantidad = "Faltan " + pila.size() + " paréntesis";
            }
            throw new ExpresionInvalidaException(cantidad + " de cierre. El último '(' sin cerrar está en la posición "
                    + pila.peek().getPosicion() + ".");
        }
        tabla.agregarFila(new String[] {"(fin)", "isEmpty=sí -> correcto", pila.toString()});
    }

    /**
     * Revisa que operandos y operadores estén bien alternados. Detecta, por
     * ejemplo, dos operadores seguidos, dos operandos seguidos, paréntesis
     * vacíos o una expresión que termina en un operador.
     *
     * @param tokens tokens de la expresión
     * @throws ExpresionInvalidaException con la posición del error
     */
    public static void validarSintaxis(ArrayList<Token> tokens) throws ExpresionInvalidaException {
        boolean esperaOperando = true; // al inicio debe venir un número, una variable, "(" o "-"
        for (Token token : tokens) {
            TipoToken tipo = token.getTipo();
            if (esperaOperando) {
                if (tipo == TipoToken.OPERADOR || tipo == TipoToken.PARENTESIS_DERECHO) {
                    throw new ExpresionInvalidaException("Se esperaba un número, una variable o '(' en la posición "
                            + token.getPosicion() + ", pero se encontró '" + token + "'.");
                }
                // Después de "(" o de "~" todavía se espera un operando.
                esperaOperando = !token.esOperando();
            } else {
                if (tipo != TipoToken.OPERADOR && tipo != TipoToken.PARENTESIS_DERECHO) {
                    throw new ExpresionInvalidaException("Falta un operador antes de '" + token
                            + "' (posición " + token.getPosicion() + ").");
                }
                esperaOperando = tipo == TipoToken.OPERADOR;
            }
        }
        if (esperaOperando) {
            throw new ExpresionInvalidaException("La expresión está incompleta: falta un número o una variable al final.");
        }
    }

    //3. Conversión de infija a postfija (algoritmo shunting-yard de Dijkstra).

    /**
     * Convierte la expresión a postfija sin mostrar el paso a paso.
     *
     * @param tokens tokens de la expresión en notación infija
     * @return tokens en notación postfija
     * @throws ExpresionInvalidaException si la expresión no es válida
     */
    public static ArrayList<Token> convertirAPostfija(ArrayList<Token> tokens) throws ExpresionInvalidaException {
        return convertirAPostfija(tokens, crearTablaConversion());
    }

    /**
     * Convierte la expresión de notación infija (a + b) a postfija (a b +).
     * Los operandos pasan directo a la salida y los operadores esperan en la
     * pila hasta que llega uno de menor precedencia o un ")".
     *
     * @param tokens tokens de la expresión en notación infija
     * @param tabla  tabla donde se anota cada paso
     * @return tokens en notación postfija
     * @throws ExpresionInvalidaException si la expresión no es válida
     */
    public static ArrayList<Token> convertirAPostfija(ArrayList<Token> tokens, TablaConsola tabla)
            throws ExpresionInvalidaException {
        validar(tokens); // así la conversión nunca trabaja con una expresión mal escrita
        ArrayList<Token> salida = new ArrayList<>();
        Pila operadores = new Pila();
        for (Token token : tokens) {
            String operaciones;
            if (token.esOperando()) {
                salida.add(token);
                operaciones = "a la salida";
            } else if (token.getTipo() == TipoToken.PARENTESIS_IZQUIERDO
                    || token.getTipo() == TipoToken.OPERADOR_UNARIO) {
                // Ni "(" ni el negativo tienen operando a la izquierda: se apilan sin comparar.
                operadores.push(token);
                operaciones = "push " + token;
            } else if (token.getTipo() == TipoToken.PARENTESIS_DERECHO) {
                operaciones = cerrarParentesis(operadores, salida);
            } else {
                operaciones = apilarOperador(token, operadores, salida);
            }
            tabla.agregarFila(new String[] {token.getValor(), operaciones, operadores.toString(), unirTokens(salida)});
        }
        // Los operadores que quedan salen de la cima hacia la base.
        while (!operadores.isEmpty()) {
            Token operador = operadores.pop();
            salida.add(operador);
            tabla.agregarFila(new String[] {"(fin)", "pop " + operador, operadores.toString(), unirTokens(salida)});
        }
        return salida;
    }

    /**
     * Saca de la pila los operadores que deben resolverse antes del operador
     * nuevo y luego apila el nuevo.
     *
     * @param operador   operador que acaba de llegar
     * @param operadores pila de operadores
     * @param salida     lista de salida en postfija
     * @return descripción de las operaciones hechas sobre la pila
     */
    private static String apilarOperador(Token operador, Pila operadores, ArrayList<Token> salida) {
        String pasos = "";
        while (!operadores.isEmpty() && debeSalirAntes(operadores.peek(), operador)) {
            Token sale = operadores.pop();
            salida.add(sale);
            pasos = pasos + "peek=" + sale + ", pop " + sale + ", ";
        }
        if (operadores.isEmpty()) {
            pasos = pasos + "isEmpty=sí, ";
        } else {
            pasos = pasos + "peek=" + operadores.peek() + ", ";
        }
        operadores.push(operador);
        return pasos + "push " + operador;
    }

    /**
     * Al llegar un ")" se sacan operadores hasta encontrar su "(". El "(" se
     * descarta porque la notación postfija no usa paréntesis.
     *
     * @param operadores pila de operadores
     * @param salida     lista de salida en postfija
     * @return descripción de las operaciones hechas sobre la pila
     */
    private static String cerrarParentesis(Pila operadores, ArrayList<Token> salida) {
        String pasos = "";
        while (operadores.peek().getTipo() != TipoToken.PARENTESIS_IZQUIERDO) {
            Token sale = operadores.pop();
            salida.add(sale);
            pasos = pasos + "peek=" + sale + ", pop " + sale + ", ";
        }
        operadores.pop();
        return pasos + "peek=(, pop (";
    }

    /**
     * Decide si el operador de la cima debe pasar a la salida antes de apilar
     * el nuevo: sale si tiene mayor precedencia, o la misma precedencia y el
     * nuevo se agrupa de izquierda a derecha. Un "(" nunca sale aquí.
     *
     * @param cima  operador que está en la cima de la pila
     * @param nuevo operador que acaba de llegar
     * @return true si la cima debe salir primero
     */
    private static boolean debeSalirAntes(Token cima, Token nuevo) {
        if (cima.getTipo() == TipoToken.PARENTESIS_IZQUIERDO) {
            return false;
        }
        if (cima.getPrecedencia() > nuevo.getPrecedencia()) {
            return true;
        }
        return cima.getPrecedencia() == nuevo.getPrecedencia() && !nuevo.esAsociativoDerecha();
    }

    //4. Evaluación de la expresión postfija.

    /**
     * Evalúa una expresión postfija con una pila: los operandos se apilan y
     * cada operador desapila sus operandos, calcula y apila el resultado.
     * Al final debe quedar un solo valor, que es el resultado.
     *
     * @param postfija  tokens en notación postfija
     * @param variables tabla de símbolos con el valor de cada variable
     * @param tabla     tabla donde se anota cada paso
     * @return resultado de la expresión
     * @throws ExpresionInvalidaException si falta el valor de una variable,
     *                                    hay una división entre cero o falta un operando
     */
    public static double evaluarPostfija(ArrayList<Token> postfija, TablaSimbolos variables, TablaConsola tabla)
            throws ExpresionInvalidaException {
        Pila pila = new Pila();
        for (Token token : postfija) {
            String operaciones;
            if (token.getTipo() == TipoToken.NUMERO) {
                pila.push(token);
                operaciones = "push " + token;
            } else if (token.getTipo() == TipoToken.VARIABLE) {
                // El valor se busca en la tabla de símbolos con búsqueda binaria.
                Variable variable = variables.buscar(token.getValor());
                if (variable == null) {
                    throw new ExpresionInvalidaException("La variable '" + token + "' no tiene valor.");
                }
                Token numero = Token.desdeNumero(variable.getValor());
                pila.push(numero);
                operaciones = "push " + numero + " (valor de " + token + ")";
            } else if (token.getTipo() == TipoToken.OPERADOR_UNARIO) {
                double operando = sacarOperando(pila, token);
                Token resultado = Token.desdeNumero(-operando);
                pila.push(resultado);
                operaciones = "pop " + Token.formatearNumero(operando) + ", push " + resultado;
            } else if (token.getTipo() == TipoToken.OPERADOR) {
                operaciones = resolverOperador(token, pila);
            } else {
                throw new ExpresionInvalidaException("La expresión postfija no debe tener paréntesis.");
            }
            tabla.agregarFila(new String[] {token.getValor(), operaciones, pila.toString()});
        }
        if (pila.size() != 1) {
            throw new ExpresionInvalidaException("La expresión no se pudo reducir a un solo valor.");
        }
        Token resultado = pila.pop();
        tabla.agregarFila(new String[] {"(fin)", "size=1, pop " + resultado + " = resultado", pila.toString()});
        return resultado.getValorNumerico();
    }

    /**
     * Desapila los dos operandos de un operador, calcula y apila el resultado.
     * El primero que sale es el operando derecho, porque fue el último en entrar.
     *
     * @param operador operador de dos operandos
     * @param pila     pila de evaluación
     * @return descripción de las operaciones hechas sobre la pila
     * @throws ExpresionInvalidaException si falta un operando o el cálculo no es válido
     */
    private static String resolverOperador(Token operador, Pila pila) throws ExpresionInvalidaException {
        double derecho = sacarOperando(pila, operador);
        double izquierdo = sacarOperando(pila, operador);
        Token resultado = Token.desdeNumero(aplicarOperador(operador.getValor(), izquierdo, derecho));
        pila.push(resultado);
        String izq = Token.formatearNumero(izquierdo);
        String der = Token.formatearNumero(derecho);
        return "pop " + der + ", pop " + izq + ", push " + resultado + " (" + izq + " " + operador + " " + der + ")";
    }

    /**
     * Saca un operando de la pila de evaluación.
     *
     * @param pila     pila de evaluación
     * @param operador operador que necesita el operando (para el mensaje de error)
     * @return valor numérico del operando
     * @throws ExpresionInvalidaException si la pila está vacía
     */
    private static double sacarOperando(Pila pila, Token operador) throws ExpresionInvalidaException {
        if (pila.isEmpty()) {
            throw new ExpresionInvalidaException("Falta un operando para el operador '" + operador + "'.");
        }
        return pila.pop().getValorNumerico();
    }

    /**
     * Calcula una operación y revisa que el resultado sea un número real.
     *
     * @param operador  "+", "-", "*", "/" o "^"
     * @param izquierdo operando izquierdo
     * @param derecho   operando derecho
     * @return resultado de la operación
     * @throws ExpresionInvalidaException si se divide entre cero o el resultado no es un número real
     */
    private static double aplicarOperador(String operador, double izquierdo, double derecho)
            throws ExpresionInvalidaException {
        double resultado;
        if (operador.equals("+")) {
            resultado = izquierdo + derecho;
        } else if (operador.equals("-")) {
            resultado = izquierdo - derecho;
        } else if (operador.equals("*")) {
            resultado = izquierdo * derecho;
        } else if (operador.equals("/")) {
            if (derecho == 0) {
                throw new ExpresionInvalidaException("División entre cero: no se puede calcular "
                        + Token.formatearNumero(izquierdo) + " / 0.");
            }
            resultado = izquierdo / derecho;
        } else if (operador.equals("^")) {
            resultado = Math.pow(izquierdo, derecho);
        } else {
            throw new ExpresionInvalidaException("Operador desconocido: " + operador);
        }
        // Por ejemplo, (-8)^0.5 no es un número real.
        if (Double.isNaN(resultado) || Double.isInfinite(resultado)) {
            throw new ExpresionInvalidaException("El resultado de " + Token.formatearNumero(izquierdo) + " "
                    + operador + " " + Token.formatearNumero(derecho) + " no es un número real o es demasiado grande.");
        }
        return resultado;
    }

    //Métodos auxiliares para separar tokens.

    /**
     * Busca dónde termina el lexema que empieza en la posición indicada.
     * Los números y las variables pueden tener varios caracteres; los
     * operadores, paréntesis y cualquier otro símbolo miden uno.
     *
     * @param texto  texto de la expresión
     * @param inicio índice (desde 0) donde empieza el lexema
     * @return índice justo después del último carácter del lexema
     */
    private static int buscarFinDelLexema(String texto, int inicio) {
        char primero = texto.charAt(inicio);
        int fin = inicio + 1;
        if (Character.isDigit(primero) || primero == '.') {
            // Un número sigue mientras haya dígitos o puntos.
            while (fin < texto.length() && (Character.isDigit(texto.charAt(fin)) || texto.charAt(fin) == '.')) {
                fin++;
            }
        } else if (esInicioDeVariable(primero)) {
            // Una variable sigue mientras haya letras, dígitos o guiones bajos.
            while (fin < texto.length() && (Character.isLetterOrDigit(texto.charAt(fin)) || texto.charAt(fin) == '_')) {
                fin++;
            }
        }
        return fin;
    }

    /**
     * Indica el tipo de un lexema.
     *
     * @param lexema   texto del token
     * @param posicion posición del lexema (desde 1), para el mensaje de error
     * @return tipo del token
     * @throws ExpresionInvalidaException si el número está mal escrito o el símbolo no es válido
     */
    private static TipoToken clasificar(String lexema, int posicion) throws ExpresionInvalidaException {
        char primero = lexema.charAt(0);
        if (Character.isDigit(primero) || primero == '.') {
            if (!esNumeroValido(lexema)) {
                throw new ExpresionInvalidaException("El número '" + lexema + "' está mal escrito (posición "
                        + posicion + ").");
            }
            return TipoToken.NUMERO;
        }
        if (esInicioDeVariable(primero)) {
            return TipoToken.VARIABLE;
        }
        if (lexema.equals("(")) {
            return TipoToken.PARENTESIS_IZQUIERDO;
        }
        if (lexema.equals(")")) {
            return TipoToken.PARENTESIS_DERECHO;
        }
        if (lexema.equals("+") || lexema.equals("-") || lexema.equals("*") || lexema.equals("/") || lexema.equals("^")) {
            return TipoToken.OPERADOR;
        }
        if (lexema.equals(Token.MENOS_UNARIO)) {
            return TipoToken.OPERADOR_UNARIO;
        }
        throw new ExpresionInvalidaException("El carácter '" + lexema + "' no es válido (posición " + posicion + ").");
    }

    /**
     * Indica si un carácter puede iniciar el nombre de una variable.
     *
     * @param caracter carácter que se revisa
     * @return true si es una letra o un guion bajo
     */
    private static boolean esInicioDeVariable(char caracter) {
        return Character.isLetter(caracter) || caracter == '_';
    }

    /**
     * Indica si en este punto de la expresión se espera un operando. Pasa al
     * inicio, después de "(" y después de cualquier operador.
     *
     * @param tokens tokens leídos hasta ahora
     * @return true si el siguiente token debería ser un operando
     */
    private static boolean esperaOperando(ArrayList<Token> tokens) {
        if (tokens.isEmpty()) {
            return true;
        }
        TipoToken anterior = tokens.get(tokens.size() - 1).getTipo();
        return anterior == TipoToken.OPERADOR || anterior == TipoToken.OPERADOR_UNARIO
                || anterior == TipoToken.PARENTESIS_IZQUIERDO;
    }
}
