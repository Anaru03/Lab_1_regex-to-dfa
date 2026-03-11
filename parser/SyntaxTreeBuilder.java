package com.dfa.parser;

import java.util.*;

/**
 * Recorre el árbol sintáctico construido por {@link RegexParser} y calcula,
 * para cada nodo, las funciones del método directo:
 *
 *   nullable  : ¿puede el subárbol generar ε?
 *   firstpos  : conjunto de posiciones que pueden ser "primera posición"
 *   lastpos   : conjunto de posiciones que pueden ser "última posición"
 *
 * Además construye la tabla followpos (Map<posición, Set<posición>>),
 * que es la entrada directa para el algoritmo de construcción del AFD.
 *
 * Referencia: Aho, Lam, Sethi & Ullman – "Compilers" 2ª ed., §3.9.
 *
 * Uso típico:
 *   RegexParser  parser  = new RegexParser("(a|b)*abb");
 *   Node         root    = parser.parse();
 *   SyntaxTreeBuilder builder = new SyntaxTreeBuilder(root, parser.getPositionCount());
 *   builder.build();
 *
 *   Map<Integer, Set<Integer>> followpos = builder.getFollowpos();
 *   Map<Integer, Character>    symbols   = builder.getPositionSymbols();
 *   int endPosition = builder.getEndMarkerPosition();
 */
public class SyntaxTreeBuilder {

    // ------------------------------------------------------------------ estado
    private final Node root;
    private final int  totalPositions;

    /** followpos[p] = conjunto de posiciones que pueden seguir a la posición p. */
    private final Map<Integer, Set<Integer>> followpos;

    /** Mapea cada posición a su símbolo (hoja del árbol). */
    private final Map<Integer, Character> positionSymbols;

    /** Posición asignada al marcador de fin '#'. */
    private int endMarkerPosition = -1;

    // ------------------------------------------------------------------ ctor
    public SyntaxTreeBuilder(Node root, int totalPositions) {
        this.root           = root;
        this.totalPositions = totalPositions;
        this.followpos      = new HashMap<>();
        this.positionSymbols = new HashMap<>();

        for (int i = 1; i <= totalPositions; i++)
            followpos.put(i, new HashSet<>());
    }

    // ------------------------------------------------------------------ API pública

    /**
     * Ejecuta el recorrido post-orden del árbol:
     *   1. Calcula nullable, firstpos, lastpos para cada nodo.
     *   2. Construye la tabla followpos.
     *   3. Registra el símbolo de cada posición.
     */
    public void build() {
        computeAnnotations(root);
    }

    public Map<Integer, Set<Integer>> getFollowpos()     { return followpos; }
    public Map<Integer, Character>    getPositionSymbols(){ return positionSymbols; }
    public int                        getEndMarkerPosition() { return endMarkerPosition; }
    public Node                       getRoot()          { return root; }

    /** Devuelve el conjunto firstpos de la raíz (= estado inicial del AFD). */
    public Set<Integer> getInitialState() {
        return root.getFirstpos();
    }

    // ------------------------------------------------------------------ recorrido

    private void computeAnnotations(Node node) {
        if (node == null) return;

        switch (node.getType()) {

            // ---- HOJA -------------------------------------------------------
            case SYMBOL -> {
                int p = node.getPosition();
                positionSymbols.put(p, node.getSymbol());

                if (node.isEndMarker()) endMarkerPosition = p;

                // nullable: false (símbolo concreto, no ε)
                node.setNullable(false);

                // firstpos = lastpos = {p}
                node.setFirstpos(new HashSet<>(Set.of(p)));
                node.setLastpos (new HashSet<>(Set.of(p)));
            }

            // ---- UNIÓN  (c1 | c2) -------------------------------------------
            case UNION -> {
                computeAnnotations(node.getLeft());
                computeAnnotations(node.getRight());
                Node c1 = node.getLeft(), c2 = node.getRight();

                node.setNullable(c1.isNullable() || c2.isNullable());
                node.setFirstpos(Node.union(c1.getFirstpos(), c2.getFirstpos()));
                node.setLastpos (Node.union(c1.getLastpos(),  c2.getLastpos()));
                // followpos: no se modifica aquí
            }

            // ---- CONCATENACIÓN  (c1 · c2) ------------------------------------
            case CONCAT -> {
                computeAnnotations(node.getLeft());
                computeAnnotations(node.getRight());
                Node c1 = node.getLeft(), c2 = node.getRight();

                node.setNullable(c1.isNullable() && c2.isNullable());

                // firstpos(c1·c2) = firstpos(c1) ∪ (nullable(c1) ? firstpos(c2) : ∅)
                Set<Integer> fp = new HashSet<>(c1.getFirstpos());
                if (c1.isNullable()) fp.addAll(c2.getFirstpos());
                node.setFirstpos(fp);

                // lastpos(c1·c2) = lastpos(c2) ∪ (nullable(c2) ? lastpos(c1) : ∅)
                Set<Integer> lp = new HashSet<>(c2.getLastpos());
                if (c2.isNullable()) lp.addAll(c1.getLastpos());
                node.setLastpos(lp);

                // followpos: para cada i ∈ lastpos(c1), followpos(i) += firstpos(c2)
                for (int i : c1.getLastpos())
                    followpos.get(i).addAll(c2.getFirstpos());
            }

            // ---- KLEENE  (c1*) ----------------------------------------------
            case KLEENE -> {
                computeAnnotations(node.getLeft());
                Node c1 = node.getLeft();

                node.setNullable(true);
                node.setFirstpos(new HashSet<>(c1.getFirstpos()));
                node.setLastpos (new HashSet<>(c1.getLastpos()));

                // followpos: para cada i ∈ lastpos(c1*), followpos(i) += firstpos(c1*)
                for (int i : node.getLastpos())
                    followpos.get(i).addAll(node.getFirstpos());
            }

            // ---- POSITIVA  (c1+)  →  equivalente a c1·c1* ------------------
            // Se trata de la misma manera que Kleene pero nullable = false
            case POSITIVE -> {
                computeAnnotations(node.getLeft());
                Node c1 = node.getLeft();

                // r+ es nullable solo si r lo es
                node.setNullable(c1.isNullable());
                node.setFirstpos(new HashSet<>(c1.getFirstpos()));
                node.setLastpos (new HashSet<>(c1.getLastpos()));

                // followpos igual que en KLEENE: repetición
                for (int i : node.getLastpos())
                    followpos.get(i).addAll(node.getFirstpos());
            }

            // ---- OPCIONAL  (c1?)  →  equivalente a c1|ε --------------------
            case OPTIONAL -> {
                computeAnnotations(node.getLeft());
                Node c1 = node.getLeft();

                node.setNullable(true);
                node.setFirstpos(new HashSet<>(c1.getFirstpos()));
                node.setLastpos (new HashSet<>(c1.getLastpos()));
                // followpos: no agrega nada extra para OPTIONAL
            }
        }
    }

    // ------------------------------------------------------------------ debug

    /**
     * Imprime en consola un resumen de las anotaciones de cada nodo
     * (recorrido in-orden simplificado) y la tabla followpos.
     * Útil para depuración.
     */
    public void printDebugInfo() {
        System.out.println("\n=== Árbol sintáctico (post-orden) ===");
        printNode(root, 0);

        System.out.println("\n=== Tabla followpos ===");
        System.out.printf("%-10s %-12s %-12s%n", "Pos", "Símbolo", "followpos");
        System.out.println("-".repeat(40));
        for (int p = 1; p <= totalPositions; p++) {
            char sym = positionSymbols.getOrDefault(p, '?');
            System.out.printf("%-10d %-12s %-12s%n",
                p, "'" + sym + "'", followpos.get(p).toString());
        }
    }

    private void printNode(Node node, int depth) {
        if (node == null) return;
        String indent = "  ".repeat(depth);
        System.out.printf("%s%s  nullable=%-5b  firstpos=%s  lastpos=%s%n",
            indent, node, node.isNullable(),
            node.getFirstpos(), node.getLastpos());
        printNode(node.getLeft(),  depth + 1);
        printNode(node.getRight(), depth + 1);
    }
}
