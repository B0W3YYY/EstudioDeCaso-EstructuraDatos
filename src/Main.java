import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Programa de consola que analiza expresiones aritméticas con una pila.
 * Desde el menú se puede ingresar una expresión, ver sus tokens, definir
 * variables, validar los paréntesis, convertir la expresión a notación
 * postfija, evaluarla y usar la pila a mano.
 *
 * @author Braden Lee Knuter Córdoba
 * @version 1.2
 */
public class Main {

    //Constantes.

    /** Expresión que se usa si el usuario presiona Enter sin escribir nada. */
    private static final String EXPRESION_DE_EJEMPLO = "(x + 3) * 2 - y / 4";

    /** Línea que separa las partes de los menús. */
    private static final String LINEA = "-".repeat(56);

    /**
     * Punto de entrada del programa: crea el lector del teclado y abre el menú.
     *
     * @param args no se usan
     */
    public static void main(String[] args) {
        Scanner entrada = new Scanner(System.in);
        try {
            menu(entrada);
        } catch (NoSuchElementException e) {
            // Pasa si se cierra la entrada de datos (Ctrl+Z en Windows o Ctrl+D en Linux).
            System.out.println();
            System.out.println("Se cerró la entrada de datos.");
        }
        entrada.close();
        System.out.println("Programa finalizado.");
    }

    /**
     * Muestra el menú principal hasta que el usuario elige salir. La
     * expresión actual, sus tokens y la tabla de variables se crean aquí.
     *
     * @param entrada lector del teclado
     */
    private static void menu(Scanner entrada) {
        String expresion = null;
        ArrayList<Token> tokens = null;
        TablaSimbolos variables = new TablaSimbolos();
        int opcion;
        do {
            imprimirMenuPrincipal(expresion);
            opcion = leerEntero(entrada, "Seleccione una opción: ", 0, 8);
            System.out.println();
            // Las opciones 2, 4, 5 y 6 trabajan con la expresión, así que necesitan que exista una.
            boolean necesitaExpresion = opcion == 2 || opcion == 4 || opcion == 5 || opcion == 6;
            if (necesitaExpresion && tokens == null) {
                System.out.println("Primero ingrese una expresión con la opción 1.");
            } else {
                switch (opcion) {
                    case 1:
                        String nueva = leerExpresion(entrada);
                        ArrayList<Token> nuevosTokens = analizarNuevaExpresion(nueva);
                        if (nuevosTokens != null) {
                            expresion = nueva;
                            tokens = nuevosTokens;
                        }
                        break;
                    case 2:
                        mostrarTokens(tokens);
                        break;
                    case 3:
                        menuVariables(entrada, variables);
                        break;
                    case 4:
                        mostrarValidacionParentesis(tokens);
                        break;
                    case 5:
                        mostrarConversionPostfija(tokens);
                        break;
                    case 6:
                        evaluarExpresion(entrada, expresion, tokens, variables);
                        break;
                    case 7:
                        menuPila(entrada);
                        break;
                    case 8:
                        mostrarAyuda();
                        break;
                    default:
                        System.out.println("Gracias por usar el analizador de expresiones.");
                        break;
                }
            }
            // Los submenús (opciones 3 y 7) tienen su propio ciclo, así que no necesitan pausa.
            if (opcion != 0 && opcion != 3 && opcion != 7) {
                esperarEnter(entrada);
            }
        } while (opcion != 0);
    }

    //Opciones del menú principal.

    /**
     * Imprime el menú principal con la expresión que está cargada.
     *
     * @param expresion expresión actual, o null si todavía no hay una
     */
    private static void imprimirMenuPrincipal(String expresion) {
        String textoExpresion = expresion;
        if (expresion == null) {
            textoExpresion = "(ninguna)";
        }
        System.out.println();
        System.out.println("=".repeat(56));
        System.out.println("   ANALIZADOR DE EXPRESIONES ARITMÉTICAS CON PILAS");
        System.out.println("=".repeat(56));
        System.out.println(" Expresión actual: " + textoExpresion);
        System.out.println(LINEA);
        System.out.println(" 1. Ingresar una expresión");
        System.out.println(" 2. Ver los tokens de la expresión");
        System.out.println(" 3. Definir o ver variables");
        System.out.println(" 4. Validar los paréntesis paso a paso");
        System.out.println(" 5. Convertir a notación postfija paso a paso");
        System.out.println(" 6. Evaluar la expresión paso a paso");
        System.out.println(" 7. Usar la pila manualmente (push, pop, peek...)");
        System.out.println(" 8. Ver ayuda");
        System.out.println(" 0. Salir");
        System.out.println(LINEA);
    }

    /**
     * Pide la expresión al usuario. Si presiona Enter sin escribir nada, se
     * usa la expresión de ejemplo.
     *
     * @param entrada lector del teclado
     * @return expresión escrita o la de ejemplo
     */
    private static String leerExpresion(Scanner entrada) {
        System.out.println("Escriba la expresión (Enter para usar el ejemplo " + EXPRESION_DE_EJEMPLO + ").");
        String texto = leerTexto(entrada, "> ");
        if (texto.isEmpty()) {
            System.out.println("Se usará el ejemplo: " + EXPRESION_DE_EJEMPLO);
            return EXPRESION_DE_EJEMPLO;
        }
        return texto;
    }

    /**
     * Separa en tokens la expresión nueva e informa si es válida. Una
     * expresión con errores de sintaxis se guarda igual, para que el usuario
     * pueda revisar el error con las opciones 2 y 4.
     *
     * @param expresion texto de la expresión
     * @return tokens de la expresión, o null si no se pudo separar en tokens
     */
    private static ArrayList<Token> analizarNuevaExpresion(String expresion) {
        ArrayList<Token> tokens;
        try {
            tokens = AnalizadorExpresiones.separarTokens(expresion);
        } catch (ExpresionInvalidaException e) {
            System.out.println("No se guardó la expresión. " + e.getMessage());
            return null;
        }
        System.out.println("Expresión guardada con " + tokens.size() + " tokens.");
        try {
            AnalizadorExpresiones.validar(tokens);
            System.out.println("La expresión es válida.");
        } catch (ExpresionInvalidaException e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println("Puede revisarla con las opciones 2 y 4, o ingresar otra con la opción 1.");
        }
        return tokens;
    }

    /**
     * Muestra cada token con su tipo y su posición en la expresión.
     *
     * @param tokens tokens de la expresión actual
     */
    private static void mostrarTokens(ArrayList<Token> tokens) {
        TablaConsola tabla = new TablaConsola(new String[] {"Token", "Tipo", "Posición"});
        for (Token token : tokens) {
            tabla.agregarFila(new String[] {token.getValor(), token.getDescripcionTipo(),
                    String.valueOf(token.getPosicion())});
        }
        imprimirTitulo("Tokens de la expresión");
        tabla.imprimir();
        System.out.println("Total de tokens: " + tokens.size());
        System.out.println("Nota: el menos unario (por ejemplo en -x) se muestra como " + Token.MENOS_UNARIO + ".");
    }

    /**
     * Muestra paso a paso cómo la pila revisa los paréntesis.
     *
     * @param tokens tokens de la expresión actual
     */
    private static void mostrarValidacionParentesis(ArrayList<Token> tokens) {
        TablaConsola tabla = AnalizadorExpresiones.crearTablaPila();
        String resultado;
        try {
            AnalizadorExpresiones.validarParentesis(tokens, tabla);
            resultado = "los paréntesis están balanceados.";
        } catch (ExpresionInvalidaException e) {
            resultado = e.getMessage();
        }
        imprimirTitulo("Validación de paréntesis");
        tabla.imprimir();
        System.out.println("Resultado: " + resultado);
    }

    /**
     * Muestra paso a paso la conversión de la expresión a notación postfija.
     *
     * @param tokens tokens de la expresión actual
     */
    private static void mostrarConversionPostfija(ArrayList<Token> tokens) {
        TablaConsola tabla = AnalizadorExpresiones.crearTablaConversion();
        try {
            ArrayList<Token> postfija = AnalizadorExpresiones.convertirAPostfija(tokens, tabla);
            imprimirTitulo("Conversión de infija a postfija");
            tabla.imprimir();
            System.out.println("Expresión postfija: " + AnalizadorExpresiones.unirTokens(postfija));
        } catch (ExpresionInvalidaException e) {
            System.out.println("No se puede continuar: " + e.getMessage());
            System.out.println("Corrija la expresión con la opción 1.");
        }
    }

    /**
     * Completa los valores de las variables y muestra paso a paso la
     * evaluación de la expresión en notación postfija.
     *
     * @param entrada   lector del teclado
     * @param expresion texto de la expresión actual
     * @param tokens    tokens de la expresión actual
     * @param variables tabla de símbolos con los valores de las variables
     */
    private static void evaluarExpresion(Scanner entrada, String expresion, ArrayList<Token> tokens,
                                        TablaSimbolos variables) {
        try {
            ArrayList<Token> postfija = AnalizadorExpresiones.convertirAPostfija(tokens);
            System.out.println("Expresión postfija: " + AnalizadorExpresiones.unirTokens(postfija));
            completarValores(entrada, AnalizadorExpresiones.obtenerVariables(tokens), variables);
            TablaConsola tabla = AnalizadorExpresiones.crearTablaPila();
            double resultado = AnalizadorExpresiones.evaluarPostfija(postfija, variables, tabla);
            System.out.println();
            imprimirTitulo("Evaluación de la expresión postfija");
            tabla.imprimir();
            System.out.println("Resultado: " + expresion + " = " + Token.formatearNumero(resultado));
        } catch (ExpresionInvalidaException e) {
            // Aquí el error puede venir de los valores, por ejemplo una división entre cero.
            System.out.println("No se puede continuar: " + e.getMessage());
            System.out.println("Cambie los valores con la opción 3 o corrija la expresión con la opción 1.");
        }
    }

    /**
     * Muestra el valor de cada variable de la expresión. Si una variable
     * todavía no está en la tabla de símbolos, pide su valor y la guarda.
     *
     * @param entrada   lector del teclado
     * @param nombres   variables que aparecen en la expresión
     * @param variables tabla de símbolos
     */
    private static void completarValores(Scanner entrada, ArrayList<String> nombres, TablaSimbolos variables) {
        if (!nombres.isEmpty()) {
            System.out.println("Valores de las variables:");
        }
        for (String nombre : nombres) {
            Variable variable = variables.buscar(nombre);
            if (variable != null) {
                System.out.println("  " + variable + " (ya estaba en la tabla de variables)");
            } else {
                double valor = leerNumero(entrada, "  " + nombre + " = ");
                variables.definir(nombre, valor);
            }
        }
    }

    /**
     * Imprime un título subrayado para separar cada resultado del menú.
     *
     * @param titulo texto del título
     */
    private static void imprimirTitulo(String titulo) {
        System.out.println(titulo);
        System.out.println("=".repeat(titulo.length()));
    }

    /**
     * Muestra las reglas para escribir expresiones.
     */
    private static void mostrarAyuda() {
        imprimirTitulo("Ayuda");
        System.out.println(" Números    : 12, 3.5 o .75 (use punto decimal)");
        System.out.println(" Variables  : empiezan con letra o _, por ejemplo x, total, precio_1");
        System.out.println(" Operadores : +  -  *  /  ^ (potencia)");
        System.out.println(" Paréntesis : ( y ) para agrupar");
        System.out.println(" Negativos  : -x o -(a + b); el programa muestra el menos unario como " + Token.MENOS_UNARIO);
        System.out.println(" Precedencia: ^ primero, luego el negativo, luego * y /, y por último + y -");
        System.out.println(LINEA);
        System.out.println(" Ejemplos válidos  : " + EXPRESION_DE_EJEMPLO + "   |   2 ^ 3 ^ 2   |   -(a - b) * 4");
        System.out.println(" Ejemplos con error: (a + b))   |   3 + * 4   |   2x + 1");
        System.out.println(" Las variables se definen con la opción 3; si falta alguna, se pide al evaluar.");
    }

    //Submenú de variables (tabla de símbolos).

    /**
     * Submenú para definir, buscar y listar las variables de la tabla de símbolos.
     *
     * @param entrada   lector del teclado
     * @param variables tabla de símbolos
     */
    private static void menuVariables(Scanner entrada, TablaSimbolos variables) {
        int opcion;
        do {
            imprimirMenuVariables(variables);
            opcion = leerEntero(entrada, "Seleccione una opción: ", 0, 4);
            switch (opcion) {
                case 1:
                    definirVariable(entrada, variables);
                    break;
                case 2:
                    buscarVariable(entrada, variables);
                    break;
                case 3:
                    mostrarVariables(variables);
                    break;
                case 4:
                    variables.vaciar();
                    System.out.println("Se borraron todas las variables.");
                    break;
                default:
                    System.out.println("Volviendo al menú principal...");
                    break;
            }
        } while (opcion != 0);
    }

    /**
     * Imprime el submenú de variables con la cantidad de variables definidas.
     *
     * @param variables tabla de símbolos
     */
    private static void imprimirMenuVariables(TablaSimbolos variables) {
        System.out.println();
        System.out.println(LINEA);
        System.out.println(" Variables   |   definidas: " + variables.cantidad() + " (ordenadas por nombre)");
        System.out.println(LINEA);
        System.out.println(" 1. Definir o cambiar          3. Ver todas");
        System.out.println(" 2. Buscar (paso a paso)       4. Borrar todas");
        System.out.println(" 0. Volver al menú principal");
        System.out.println(LINEA);
    }

    /**
     * Pide el nombre y el valor de una variable y la guarda en la tabla.
     *
     * @param entrada   lector del teclado
     * @param variables tabla de símbolos
     */
    private static void definirVariable(Scanner entrada, TablaSimbolos variables) {
        Token token;
        try {
            token = AnalizadorExpresiones.crearToken(leerTexto(entrada, "Nombre de la variable: "));
        } catch (ExpresionInvalidaException e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }
        if (token.getTipo() != TipoToken.VARIABLE) {
            System.out.println("Error: '" + token + "' no es un nombre de variable. Debe empezar con letra o _.");
            return;
        }
        String nombre = token.getValor();
        double valor = leerNumero(entrada, "  " + nombre + " = ");
        if (variables.definir(nombre, valor)) {
            System.out.println("Se agregó " + nombre + " = " + Token.formatearNumero(valor) + ".");
        } else {
            System.out.println("Se cambió el valor de " + nombre + " = " + Token.formatearNumero(valor) + ".");
        }
    }

    /**
     * Busca una variable y muestra los pasos de la búsqueda binaria.
     *
     * @param entrada   lector del teclado
     * @param variables tabla de símbolos
     */
    private static void buscarVariable(Scanner entrada, TablaSimbolos variables) {
        String nombre = leerTexto(entrada, "Variable que desea buscar: ");
        TablaConsola pasos = TablaSimbolos.crearTablaBusqueda();
        Variable encontrada = variables.buscar(nombre, pasos);
        imprimirTitulo("Búsqueda binaria de " + nombre);
        if (variables.cantidad() == 0) {
            System.out.println("La tabla de variables está vacía.");
        } else {
            pasos.imprimir();
        }
        if (encontrada != null) {
            System.out.println("Resultado: " + encontrada);
        } else {
            System.out.println("Resultado: la variable " + nombre + " no está definida.");
        }
    }

    /**
     * Muestra todas las variables en orden alfabético.
     *
     * @param variables tabla de símbolos
     */
    private static void mostrarVariables(TablaSimbolos variables) {
        if (variables.cantidad() == 0) {
            System.out.println("No hay variables definidas.");
            return;
        }
        TablaConsola tabla = new TablaConsola(new String[] {"Variable", "Valor"});
        for (Variable variable : variables.obtenerVariables()) {
            tabla.agregarFila(new String[] {variable.getNombre(), Token.formatearNumero(variable.getValor())});
        }
        imprimirTitulo("Tabla de variables");
        tabla.imprimir();
    }

    //Submenú para usar la pila manualmente.

    /**
     * Submenú para probar las operaciones básicas de la pila una por una.
     *
     * @param entrada lector del teclado
     */
    private static void menuPila(Scanner entrada) {
        Pila pila = new Pila();
        int opcion;
        do {
            imprimirMenuPila(pila);
            opcion = leerEntero(entrada, "Seleccione una opción: ", 0, 7);
            switch (opcion) {
                case 1:
                    try {
                        Token token = AnalizadorExpresiones.crearToken(leerTexto(entrada, "Token para apilar (ej. 12, x, +, ( ): "));
                        pila.push(token);
                        System.out.println("push: se apiló " + token.describir());
                    } catch (ExpresionInvalidaException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                case 2:
                    Token sacado = pila.pop();
                    if (sacado == null) {
                        System.out.println("Error: No se puede hacer pop: la pila está vacía.");
                    } else {
                        System.out.println("pop: se quitó " + sacado.describir());
                    }
                    break;
                case 3:
                    Token cima = pila.peek();
                    if (cima == null) {
                        System.out.println("Error: No se puede hacer peek: la pila está vacía.");
                    } else {
                        System.out.println("peek: la cima es " + cima.describir());
                    }
                    break;
                case 4:
                    System.out.println("size: la pila tiene " + pila.size() + " elemento(s).");
                    break;
                case 5:
                    if (pila.isEmpty()) {
                        System.out.println("isEmpty: sí, la pila está vacía.");
                    } else {
                        System.out.println("isEmpty: no, la pila tiene elementos.");
                    }
                    break;
                case 6:
                    pila.mostrar();
                    break;
                case 7:
                    pila.vaciar();
                    System.out.println("Se vació la pila.");
                    break;
                default:
                    System.out.println("Volviendo al menú principal...");
                    break;
            }
        } while (opcion != 0);
    }

    /**
     * Imprime el submenú de la pila junto con su contenido actual.
     *
     * @param pila pila que se está usando
     */
    private static void imprimirMenuPila(Pila pila) {
        System.out.println();
        System.out.println(LINEA);
        System.out.println(" Pila manual   |   pila (base -> cima): " + pila);
        System.out.println(LINEA);
        // Dos columnas para que el submenú ocupe menos líneas, ya que se repite tras cada operación.
        System.out.println(" 1. push  (apilar un token)    5. isEmpty (¿está vacía?)");
        System.out.println(" 2. pop   (quitar la cima)     6. Mostrar la pila");
        System.out.println(" 3. peek  (ver la cima)        7. Vaciar la pila");
        System.out.println(" 4. size  (cantidad)           0. Volver al menú principal");
        System.out.println(LINEA);
    }

    //Lectura de datos con validación.

    /**
     * Lee una línea de texto sin espacios al inicio ni al final.
     *
     * @param entrada lector del teclado
     * @param mensaje texto que se muestra antes de leer
     * @return texto escrito por el usuario
     */
    private static String leerTexto(Scanner entrada, String mensaje) {
        System.out.print(mensaje);
        return entrada.nextLine().trim();
    }

    /**
     * Lee un número entero dentro de un rango y lo vuelve a pedir hasta que
     * sea válido, así el programa no se cae si el usuario escribe letras.
     *
     * @param entrada lector del teclado
     * @param mensaje texto que se muestra antes de leer
     * @param minimo  valor mínimo permitido
     * @param maximo  valor máximo permitido
     * @return número leído
     */
    private static int leerEntero(Scanner entrada, String mensaje, int minimo, int maximo) {
        while (true) {
            String texto = leerTexto(entrada, mensaje);
            try {
                int numero = Integer.parseInt(texto);
                if (numero >= minimo && numero <= maximo) {
                    return numero;
                }
            } catch (NumberFormatException e) {
                // Si no es un número, se muestra el mismo mensaje que si estuviera fuera de rango.
            }
            System.out.println("Opción no válida. Escriba un número del " + minimo + " al " + maximo + ".");
        }
    }

    /**
     * Lee un número real y lo vuelve a pedir hasta que sea válido. Acepta
     * coma o punto decimal, porque en Costa Rica se usan los dos.
     *
     * @param entrada lector del teclado
     * @param mensaje texto que se muestra antes de leer
     * @return número leído
     */
    private static double leerNumero(Scanner entrada, String mensaje) {
        while (true) {
            String texto = leerTexto(entrada, mensaje).replace(',', '.');
            String sinSigno = texto;
            if (texto.startsWith("-") || texto.startsWith("+")) {
                sinSigno = texto.substring(1);
            }
            if (AnalizadorExpresiones.esNumeroValido(sinSigno)) {
                return Double.parseDouble(texto);
            }
            System.out.println("Valor no válido. Escriba un número, por ejemplo 5, -2 o 3.75.");
        }
    }

    /**
     * Hace una pausa para que el usuario lea el resultado antes de volver al menú.
     *
     * @param entrada lector del teclado
     */
    private static void esperarEnter(Scanner entrada) {
        System.out.println();
        System.out.print("Presione Enter para volver al menú...");
        entrada.nextLine();
    }
}
