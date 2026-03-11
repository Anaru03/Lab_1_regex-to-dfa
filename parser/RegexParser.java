package com.dfa.parser;

/**
 * Parser recursivo descendente para expresiones regulares.
 *
 * Gramática (en orden de precedencia ascendente):
 *
 *   expr    → term ( '|' term )*
 *   term    → factor ( factor )*          ← concatenación implícita
 *   factor  → atom ( '*' | '+' | '?' )*
 *   atom    → CHAR | '(' expr ')'
 *
 * Operadores soportados:
 *   |   unión
 *   ·   concatenación (implícita)
 *   *   cerradura de Kleene
 *   +   cerradura positiva
 *   ?   opcional
 *   ()  agrupación
 *   \x  escape de cualquier carácter especial
 *
 * Al final del parsing, el parser añade automáticamente la concatenación
 * con el marcador de fin '#' para que el método directo funcione
 * correctamente (la ER procesada internamente es:  (r)·# ).
 *
 * Uso:
 *   RegexParser parser = new RegexParser("(a|b)*abb");
 *   Node root = parser.parse();   // devuelve la raíz del árbol CON el marcador #
 *   int totalPositions = parser.getPositionCount();
 */
public class RegexParser {

    // ------------------------------------------------------------------ estado
    private final String input;
    private int          pos;          // cursor sobre input
    private int          positionCounter; // contador global de posiciones para hojas

    // ------------------------------------------------------------------ ctor
    public RegexParser(String regex) {
        if (regex == null || regex.isBlank())
            throw new IllegalArgumentException("La expresión regular no puede ser vacía.");
        this.input           = regex.trim();
        this.pos             = 0;
        this.positionCounter = 0;
    }

    // ------------------------------------------------------------------ API pública

    /**
     * Parsea la expresión regular y devuelve la raíz del árbol sintáctico.
     * La raíz es la concatenación de la ER con el marcador '#'.
     */
    public Node parse() {
        pos = 0;
        positionCounter = 0;

        Node exprNode = parseExpr();

        if (pos != input.length())
            throw new IllegalArgumentException(
                "Carácter inesperado '" + input.charAt(pos) + "' en posición " + pos + ".");

        // Agregar el marcador de fin '#' con la siguiente posición disponible
        Node endMarker = new Node('#', ++positionCounter);

        // Raíz = concat(expr, #)
        return new Node(Node.Type.CONCAT, exprNode, endMarker);
    }

    /** Número total de posiciones asignadas (incluyendo el marcador '#'). */
    public int getPositionCount() { return positionCounter; }

    // ------------------------------------------------------------------ gramática

    // expr → term ( '|' term )*
    private Node parseExpr() {
        Node left = parseTerm();

        while (pos < input.length() && input.charAt(pos) == '|') {
            pos++; // consume '|'
            Node right = parseTerm();
            left = new Node(Node.Type.UNION, left, right);
        }
        return left;
    }

    // term → factor ( factor )*
    private Node parseTerm() {
        Node left = parseFactor();

        // Mientras el siguiente token pueda iniciar un factor, concatenamos
        while (pos < input.length() && canStartFactor(input.charAt(pos))) {
            Node right = parseFactor();
            left = new Node(Node.Type.CONCAT, left, right);
        }
        return left;
    }

    // factor → atom ( '*' | '+' | '?' )*
    private Node parseFactor() {
        Node node = parseAtom();

        while (pos < input.length()) {
            char c = input.charAt(pos);
            if (c == '*') {
                pos++;
                node = new Node(Node.Type.KLEENE, node);
            } else if (c == '+') {
                pos++;
                node = new Node(Node.Type.POSITIVE, node);
            } else if (c == '?') {
                pos++;
                node = new Node(Node.Type.OPTIONAL, node);
            } else {
                break;
            }
        }
        return node;
    }

    // atom → CHAR | '(' expr ')'
    private Node parseAtom() {
        if (pos >= input.length())
            throw new IllegalArgumentException(
                "Se esperaba un átomo pero se alcanzó el final de la expresión.");

        char c = input.charAt(pos);

        // Agrupación
        if (c == '(') {
            pos++; // consume '('
            Node inner = parseExpr();
            if (pos >= input.length() || input.charAt(pos) != ')')
                throw new IllegalArgumentException(
                    "Falta ')' para cerrar el grupo (abierto cerca de posición " + (pos - 1) + ").");
            pos++; // consume ')'
            return inner;
        }

        // Escape: \x trata 'x' como literal
        if (c == '\\') {
            if (pos + 1 >= input.length())
                throw new IllegalArgumentException("Secuencia de escape incompleta al final.");
            pos++; // consume '\'
            c = input.charAt(pos);
            pos++; // consume el carácter escapado
            return new Node(c, ++positionCounter);
        }

        // Carácter ordinario (símbolo del alfabeto)
        pos++;
        return new Node(c, ++positionCounter);
    }

    // ------------------------------------------------------------------ helpers

    /**
     * Determina si el carácter dado puede iniciar un factor dentro de una
     * concatenación implícita. Los caracteres que NO inician un factor son:
     * '|', ')', '*', '+', '?'  (o el fin de cadena).
     */
    private boolean canStartFactor(char c) {
        return c != '|' && c != ')' && c != '*' && c != '+' && c != '?';
    }
}
