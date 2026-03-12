/**
 * Parser recursivo descendente para expresiones regulares.
 *
 * Gramática:
 *   expr    → term ( '|' term )*
 *   term    → factor ( factor )*
 *   factor  → atom ( '*' | '+' | '?' )*
 *   atom    → CHAR | '(' expr ')'
 *
 * Al final se concatena automáticamente con '#'
 * para que el método directo funcione correctamente.
 */
public class RegexParser {

    private final String input;
    private int pos;
    private int positionCounter;

    public RegexParser(String regex) {
        if (regex == null || regex.isBlank())
            throw new IllegalArgumentException("La expresión regular no puede ser vacía.");
        this.input = regex.trim();
    }

    // ---------------------- método principal
    public Node parse() {
        pos = 0;
        positionCounter = 0;

        Node exprNode = parseExpr();

        if (pos != input.length())
            throw new IllegalArgumentException("Error en posición " + pos);

        // agregar marcador final '#'
        Node endMarker = new Node('#', ++positionCounter);
        return new Node(Node.Type.CONCAT, exprNode, endMarker);
    }

    public int getPositionCount() {
        return positionCounter;
    }

    // ---------------------- expr
    private Node parseExpr() {
        Node left = parseTerm();

        while (pos < input.length() && input.charAt(pos) == '|') {
            pos++;
            Node right = parseTerm();
            left = new Node(Node.Type.UNION, left, right);
        }
        return left;
    }

    // ---------------------- term (concatenación implícita)
    private Node parseTerm() {
        Node left = parseFactor();

        while (pos < input.length() && canStartFactor(input.charAt(pos))) {
            Node right = parseFactor();
            left = new Node(Node.Type.CONCAT, left, right);
        }
        return left;
    }

    // ---------------------- factor (* + ?)
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
            } else break;
        }
        return node;
    }

    // ---------------------- atom
    private Node parseAtom() {
        char c = input.charAt(pos);

        if (c == '(') {
            pos++;
            Node inner = parseExpr();
            pos++; // consume ')'
            return inner;
        }

        pos++;
        return new Node(c, ++positionCounter);
    }

    private boolean canStartFactor(char c) {
        return c != '|' && c != ')' && c != '*' && c != '+' && c != '?';
    }
}