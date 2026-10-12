# Analizador de expresiones aritméticas con pilas

Primer estudio de caso del curso **Estructuras de Datos** (Universidad CENFOTEC).
Autor: Braden Lee Knuter Córdoba. Profesor: Romario Salas Cerdas.

Programa de consola en Java que usa una **pila implementada con nodos enlazados**
(sin `java.util.Stack` ni `ArrayDeque`) para analizar expresiones aritméticas que
pueden tener números, variables, operadores y paréntesis.

## ¿Qué hace?

| Opción del menú | Qué hace | Cómo usa la pila |
|---|---|---|
| 1. Ingresar una expresión | Lee la expresión, la separa en tokens y dice si es válida | Valida los paréntesis con una pila |
| 2. Ver los tokens | Muestra cada token con su tipo y posición | No la usa |
| 3. Definir o ver variables | Guarda variables en una tabla de símbolos ordenada y las busca paso a paso | Usa ordenamiento por inserción y búsqueda binaria |
| 4. Validar paréntesis paso a paso | Muestra cómo cada `(` se apila y cada `)` lo desapila | `push`, `pop`, `isEmpty`, `peek` |
| 5. Convertir a postfija paso a paso | Pasa de infija `(x + 3) * 2` a postfija `x 3 + 2 *` | Pila de operadores con precedencia |
| 6. Evaluar paso a paso | Usa los valores guardados (o los pide) y calcula el resultado | Pila de operandos y resultados parciales |
| 7. Usar la pila manualmente | Permite hacer `push`, `pop`, `peek`, `size`, `isEmpty`, mostrar y vaciar | Todas las operaciones básicas |
| 8. Ver ayuda | Explica cómo escribir las expresiones | No la usa |

La tabla de variables (`TablaSimbolos`) mantiene los nombres en orden alfabético:
cada variable nueva se inserta con el paso del **ordenamiento por inserción**, y las
consultas usan **búsqueda binaria**, que muestra en pantalla cada comparación.

Reglas de escritura:

- **Números:** `12`, `3.5` o `.75`
- **Variables:** empiezan con letra o `_`, por ejemplo `x`, `total`, `precio_1`
- **Operadores:** `+  -  *  /  ^` (potencia)
- **Paréntesis:** `(` y `)`
- **Negativos:** `-x` o `-(a + b)`. El programa muestra el menos unario como `~`
- **Precedencia:** `^` primero, luego el negativo, luego `*` y `/`, y por último `+` y `-`

El programa no se cae con entradas inválidas: avisa si hay caracteres no válidos,
paréntesis sin pareja, operadores seguidos, división entre cero, opciones de menú
fuera de rango o valores que no son números.

## Estructura del proyecto

```
.
├── README.md
├── .gitignore
├── EstudioDeCaso.iml                    # Módulo de IntelliJ IDEA
├── .idea/                               # Configuración mínima de IntelliJ (incluye la ejecución de Main)
├── docs
│   └── RepresentacionPila.xlsx          # Representación de la pila paso a paso en Excel
└── src
    ├── Main.java                        # main() y menu() con todas las opciones
    ├── Nodo.java                        # Nodo autorreferenciado: token + referencia al siguiente
    ├── Pila.java                        # Pila enlazada: push, pop, peek, isEmpty, size, mostrar, vaciar
    ├── Token.java                       # Valor, tipo y posición de un token; precedencia de operadores
    ├── TipoToken.java                   # Tipos: número, variable, operador, paréntesis...
    ├── AnalizadorExpresiones.java       # Tokens, validación, infija a postfija y evaluación
    ├── TablaSimbolos.java               # Variables ordenadas: inserción ordenada y búsqueda binaria
    ├── Variable.java                    # Nombre y valor de una variable
    ├── TablaConsola.java                # Imprime las tablas paso a paso
    └── ExpresionInvalidaException.java  # Error en la expresión (con su posición)
```

El código sigue el estilo visto en clase: atributos privados, constructores, getters y
setters, recorridos con `while` y `ArrayList`. Como en las diapositivas del curso, `pop()` y
`peek()` devuelven un valor convencional (`null`) cuando la pila está vacía, y el menú
muestra un mensaje de error en lugar de cerrarse.

## Requisitos

- JDK 17 o superior (probado con JDK 26). Para revisar la versión: `java -version`

## Cómo compilar y ejecutar

### Desde la consola

Desde la carpeta raíz del proyecto (donde está este README):

```bash
javac -encoding UTF-8 -d out src/*.java
java -cp out Main
```

Los mismos comandos funcionan en Windows (PowerShell o CMD), macOS y Linux.
La carpeta `out` guarda los `.class` y no se sube al repositorio.

### Desde IntelliJ IDEA

1. **File > Open** y seleccionar la carpeta del proyecto.
2. Si IntelliJ pide un JDK, elegir cualquier JDK 17 o superior (**File > Project Structure > SDK**).
3. Arriba a la derecha elegir la configuración **Main** y presionar **Run** (el triángulo verde).
   El menú aparece en la consola de IntelliJ.

## Ejemplo de uso

Con la opción 1 se presiona Enter para cargar el ejemplo `(x + 3) * 2 - y / 4`.
Con la opción 5 se ve la conversión a postfija:

```
Conversión de infija a postfija
===============================
+----+-------+-----------------------------------+---------------------+-------------------+
| #  | Token | Operaciones en la pila            | Pila (base -> cima) | Salida (postfija) |
+----+-------+-----------------------------------+---------------------+-------------------+
| 1  | (     | push (                            | (                   |                   |
| 2  | x     | a la salida                       | (                   | x                 |
| 3  | +     | peek=(, push +                    | ( +                 | x                 |
| 4  | 3     | a la salida                       | ( +                 | x 3               |
| 5  | )     | peek=+, pop +, peek=(, pop (      | (vacía)             | x 3 +             |
| 6  | *     | isEmpty=sí, push *                | *                   | x 3 +             |
| 7  | 2     | a la salida                       | *                   | x 3 + 2           |
| 8  | -     | peek=*, pop *, isEmpty=sí, push - | -                   | x 3 + 2 *         |
| 9  | y     | a la salida                       | -                   | x 3 + 2 * y       |
| 10 | /     | peek=-, push /                    | - /                 | x 3 + 2 * y       |
| 11 | 4     | a la salida                       | - /                 | x 3 + 2 * y 4     |
| 12 | (fin) | pop /                             | -                   | x 3 + 2 * y 4 /   |
| 13 | (fin) | pop -                             | (vacía)             | x 3 + 2 * y 4 / - |
+----+-------+-----------------------------------+---------------------+-------------------+
Expresión postfija: x 3 + 2 * y 4 / -
```

Con la opción 6, usando `x = 5` y `y = 8` (el programa pide los valores que no estén en la tabla de variables):

```
Evaluación de la expresión postfija
===================================
+----+-------+---------------------------------+---------------------+
| #  | Token | Operaciones en la pila          | Pila (base -> cima) |
+----+-------+---------------------------------+---------------------+
| 1  | x     | push 5 (valor de x)             | 5                   |
| 2  | 3     | push 3                          | 5 3                 |
| 3  | +     | pop 3, pop 5, push 8 (5 + 3)    | 8                   |
| 4  | 2     | push 2                          | 8 2                 |
| 5  | *     | pop 2, pop 8, push 16 (8 * 2)   | 16                  |
| 6  | y     | push 8 (valor de y)             | 16 8                |
| 7  | 4     | push 4                          | 16 8 4              |
| 8  | /     | pop 4, pop 8, push 2 (8 / 4)    | 16 2                |
| 9  | -     | pop 2, pop 16, push 14 (16 - 2) | 14                  |
| 10 | (fin) | size=1, pop 14 = resultado      | (vacía)             |
+----+-------+---------------------------------+---------------------+
Resultado: (x + 3) * 2 - y / 4 = 14
```

Si la expresión tiene un error, el programa indica cuál es y en qué posición (opción 4):

```
Validación de paréntesis
========================
+---+-------+----------------------------------+---------------------+
| # | Token | Operaciones en la pila           | Pila (base -> cima) |
+---+-------+----------------------------------+---------------------+
| 1 | (     | push (                           | (                   |
| 2 | x     | no es paréntesis                 | (                   |
| 3 | +     | no es paréntesis                 | (                   |
| 4 | 3     | no es paréntesis                 | (                   |
| 5 | )     | pop ( (abierto en la posición 1) | (vacía)             |
| 6 | )     | isEmpty=sí -> ERROR              | (vacía)             |
+---+-------+----------------------------------+---------------------+
Resultado: El ')' de la posición 8 no tiene un '(' que lo abra.
```

Con la opción 3 se definen variables y se buscan con búsqueda binaria paso a paso:

```
Búsqueda binaria de x
=====================
+---+-----------+---------+------------------+-----------------------------------+
| # | Izquierda | Derecha | Medio (variable) | Decisión                          |
+---+-----------+---------+------------------+-----------------------------------+
| 1 | 0         | 3       | 1 (precio)       | x va después: buscar a la derecha |
| 2 | 2         | 3       | 2 (x)            | encontrada                        |
+---+-----------+---------+------------------+-----------------------------------+
Resultado: x = 5
```
## Complejidad

`push`, `pop`, `peek`, `isEmpty` y `size` son O(1) porque solo trabajan con la cima.
Separar en tokens, validar, convertir y evaluar son O(n), donde n es la cantidad de
tokens: cada token entra y sale de la pila como máximo una vez. En la tabla de variables,
la búsqueda binaria es O(log n) y la inserción ordenada es O(n).

## Representación en Excel

El archivo [docs/RepresentacionPila.xlsx](docs/RepresentacionPila.xlsx) tiene la representación
de la pila paso a paso (conversión, evaluación, vista vertical y validación de paréntesis),
con una celda por cada casilla de la pila.
