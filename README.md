# Laboratorio 1 - Conversión Directa de una Expresión Regular a un AFD

## 📌 Descripción

Este proyecto implementa el método directo para convertir una
expresión regular en un Autómata Finito Determinista (AFD) sin pasar por
un AFN intermedio.

El programa permite:

1.  Ingresar una expresión regular.
2.  Construir el AFD utilizando el método directo.
3.  Generar e imprimir la tabla de transición.
4.  Simular el AFD con cadenas de entrada.
5.  Explicar detalladamente por qué una cadena es aceptada o rechazada.

------------------------------------------------------------------------

## 🧠 Fundamento Teórico

El método directo consiste en:

-   Construir el árbol sintáctico de la expresión regular.
-   Calcular para cada nodo:
    -   `nullable`
    -   `firstpos`
    -   `lastpos`
-   Construir la tabla `followpos`.
-   Generar los estados del AFD a partir de `followpos`.
-   Determinar estados de aceptación (aquellos que contienen la posición
    del marcador `#`).

------------------------------------------------------------------------

## 🗂️ Estructura del Proyecto

    Lab_1_regex-to-dfa/
    │
    ├── Main.java
    │
    ├── dfa/
    │   ├── Builder.java
    │   └── dfa.java
    │
    └── parser/
        ├── Node.java
        ├── RegexParser.java
        └── SyntaxTreeBuilder.java

⚠️ El proyecto no utiliza packages.\
⚠️ Todas las clases están en el *default package*.

------------------------------------------------------------------------

## ⚙️ Compilación

Desde la raíz del proyecto ejecutar:

``` bash
javac parser\*.java dfa\*.java Main.java
```

------------------------------------------------------------------------

## ▶️ Ejecución (Windows)

``` bash
java -cp ".;parser;dfa" Main
```

En Linux/Mac usar:

``` bash
java -cp ".:parser:dfa" Main
```

------------------------------------------------------------------------

## 🖥️ Uso del Programa

Al ejecutar, aparece un menú:

    1. Ingresar nueva expresión regular
    2. Evaluar cadena
    3. Salir

### Flujo recomendado:

1.  Elegir opción `1`
2.  Ingresar expresión regular
3.  Elegir opción `2`
4.  Evaluar cadenas
5.  Elegir opción `3` para salir

------------------------------------------------------------------------

## ✅ Operadores Soportados

-   `|` → Unión
-   `*` → Cerradura de Kleene
-   `+` → Cerradura positiva
-   `?` → Opcional
-   Concatenación implícita

------------------------------------------------------------------------

## 🎥 Expresiones utilizadas en el video

### 1️⃣ `(a|b)*abb`

-   Aceptada: `abb`
-   Rechazada: `aba`

### 2️⃣ `a+b?`

-   Aceptada: `aa`
-   Rechazada: `bb`

### 3️⃣ `(ab|c)+d?`

-   Aceptada: `ab`
-   Rechazada: `d`

------------------------------------------------------------------------

## 🔍 Explicación de Rechazos

El programa distingue tres tipos de rechazo:

1.  Símbolo no perteneciente al alfabeto.
2.  No existe transición desde el estado actual.
3.  La cadena termina en un estado que no es de aceptación.

------------------------------------------------------------------------

## 👨‍💻 Autores

Proyecto desarrollado para la materia: Diseño de Lenguajes de
Programación 2026
-   **Ruth de Léon** - [Anaru03](https://github.com/Anaru03)
-   **Alejandro Antón** - [Anton17303](https://github.com/Anton17303)

Universidad del Valle de Guatemala
