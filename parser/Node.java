package com.dfa.parser;

import java.util.HashSet;
import java.util.Set;

/**
 * Representa un nodo del árbol sintáctico construido a partir de una expresión regular.
 *
 * Tipos de nodo:
 *   - SYMBOL   : hoja con un símbolo del alfabeto (incluye '#' para el marcador de fin)
 *   - CONCAT   : concatenación (·)
 *   - UNION    : unión (|)
 *   - KLEENE   : cerradura de Kleene (*)
 *   - POSITIVE : cerradura positiva (+)  →  r+ ≡ r·r*
 *   - OPTIONAL : opcional (?)            →  r? ≡ r|ε
 *
 * Cada nodo almacena las funciones del método directo:
 *   - nullable     : ¿puede el subárbol generar la cadena vacía?
 *   - firstpos     : conjunto de posiciones que pueden ser la primera posición de una cadena
 *   - lastpos      : conjunto de posiciones que pueden ser la última posición
 *
 * Las posiciones (enteros) se asignan únicamente a los nodos hoja (SYMBOL).
 */
public class Node {

    // ------------------------------------------------------------------ tipos
    public enum Type {
        SYMBOL, CONCAT, UNION, KLEENE, POSITIVE, OPTIONAL
    }

    // --------------------------------------------------------------- campos
    private final Type   type;
    private final char   symbol;   // válido solo si type == SYMBOL
    private final int    position; // válido solo si type == SYMBOL; -1 en otro caso

    private Node left;
    private Node right;

    // propiedades del método directo
    private boolean    nullable;
    private Set<Integer> firstpos;
    private Set<Integer> lastpos;

    // --------------------------------------------------------- constructores

    /** Nodo hoja: símbolo con posición asignada. */
    public Node(char symbol, int position) {
        this.type     = Type.SYMBOL;
        this.symbol   = symbol;
        this.position = position;
        this.left     = null;
        this.right    = null;
    }

    /** Nodo interno binario (CONCAT, UNION). */
    public Node(Type type, Node left, Node right) {
        if (type != Type.CONCAT && type != Type.UNION)
            throw new IllegalArgumentException("Este constructor es solo para CONCAT y UNION.");
        this.type     = type;
        this.symbol   = '\0';
        this.position = -1;
        this.left     = left;
        this.right    = right;
    }

    /** Nodo interno unario (KLEENE, POSITIVE, OPTIONAL). */
    public Node(Type type, Node child) {
        if (type != Type.KLEENE && type != Type.POSITIVE && type != Type.OPTIONAL)
            throw new IllegalArgumentException("Este constructor es solo para KLEENE, POSITIVE y OPTIONAL.");
        this.type     = type;
        this.symbol   = '\0';
        this.position = -1;
        this.left     = child;   // hijo único almacenado en left
        this.right    = null;
    }

    // ------------------------------------------------------- getters / setters

    public Type    getType()     { return type; }
    public char    getSymbol()   { return symbol; }
    public int     getPosition() { return position; }

    public Node getLeft()        { return left; }
    public Node getRight()       { return right; }
    public void setLeft(Node n)  { left  = n; }
    public void setRight(Node n) { right = n; }

    public boolean     isNullable()              { return nullable; }
    public void        setNullable(boolean v)    { nullable = v; }

    public Set<Integer> getFirstpos()            { return firstpos; }
    public void         setFirstpos(Set<Integer> s) { firstpos = s; }

    public Set<Integer> getLastpos()             { return lastpos; }
    public void         setLastpos(Set<Integer> s)  { lastpos = s; }

    // ------------------------------------------------------- utilidades

    /** Devuelve true si este nodo es el marcador de fin '#'. */
    public boolean isEndMarker() {
        return type == Type.SYMBOL && symbol == '#';
    }

    @Override
    public String toString() {
        return switch (type) {
            case SYMBOL   -> "SYMBOL('" + symbol + "', pos=" + position + ")";
            case CONCAT   -> "CONCAT";
            case UNION    -> "UNION";
            case KLEENE   -> "KLEENE";
            case POSITIVE -> "POSITIVE";
            case OPTIONAL -> "OPTIONAL";
        };
    }

    // ------------------------------------------------------- helper de conjuntos

    /** Une dos conjuntos y devuelve el resultado (sin modificar los originales). */
    public static Set<Integer> union(Set<Integer> a, Set<Integer> b) {
        Set<Integer> result = new HashSet<>(a);
        result.addAll(b);
        return result;
    }
}
