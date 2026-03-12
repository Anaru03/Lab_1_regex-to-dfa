import java.util.HashSet;
import java.util.Set;

/**
 * Representa un nodo del árbol sintáctico construido
 * a partir de una expresión regular.
 *
 * Tipos:
 *  SYMBOL   → hoja (símbolo del alfabeto o '#')
 *  CONCAT   → concatenación
 *  UNION    → unión (|)
 *  KLEENE   → *
 *  POSITIVE → +
 *  OPTIONAL → ?
 *
 * Cada nodo almacena:
 *  - nullable
 *  - firstpos
 *  - lastpos
 */
public class Node {

    // ---------------------- tipos de nodo
    public enum Type {
        SYMBOL, CONCAT, UNION, KLEENE, POSITIVE, OPTIONAL
    }

    // ---------------------- atributos básicos
    private final Type type;
    private final char symbol;   // solo válido si es SYMBOL
    private final int position;  // solo válido si es SYMBOL

    private Node left;
    private Node right;

    // ---------------------- propiedades del método directo
    private boolean nullable;
    private Set<Integer> firstpos;
    private Set<Integer> lastpos;

    // ---------------------- constructor hoja
    public Node(char symbol, int position) {
        this.type = Type.SYMBOL;
        this.symbol = symbol;
        this.position = position;
    }

    // ---------------------- constructor binario
    public Node(Type type, Node left, Node right) {
        this.type = type;
        this.symbol = '\0';
        this.position = -1;
        this.left = left;
        this.right = right;
    }

    // ---------------------- constructor unario
    public Node(Type type, Node child) {
        this.type = type;
        this.symbol = '\0';
        this.position = -1;
        this.left = child;
        this.right = null;
    }

    // ---------------------- getters
    public Type getType() { return type; }
    public char getSymbol() { return symbol; }
    public int getPosition() { return position; }

    public Node getLeft() { return left; }
    public Node getRight() { return right; }

    public boolean isNullable() { return nullable; }
    public void setNullable(boolean nullable) { this.nullable = nullable; }

    public Set<Integer> getFirstpos() { return firstpos; }
    public void setFirstpos(Set<Integer> firstpos) { this.firstpos = firstpos; }

    public Set<Integer> getLastpos() { return lastpos; }
    public void setLastpos(Set<Integer> lastpos) { this.lastpos = lastpos; }

    // ---------------------- ¿es el marcador final '#'? 
    public boolean isEndMarker() {
        return type == Type.SYMBOL && symbol == '#';
    }

    // ---------------------- utilidad para unir conjuntos
    public static Set<Integer> union(Set<Integer> a, Set<Integer> b) {
        Set<Integer> result = new HashSet<>(a);
        result.addAll(b);
        return result;
    }
}