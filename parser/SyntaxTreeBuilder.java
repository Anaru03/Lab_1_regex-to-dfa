import java.util.*;

/**
 * Calcula:
 *  - nullable
 *  - firstpos
 *  - lastpos
 *  - followpos
 *
 * según el método directo (Aho, Sethi, Ullman).
 */
public class SyntaxTreeBuilder {

    private final Node root;
    private final int totalPositions;

    private final Map<Integer, Set<Integer>> followpos = new HashMap<>();
    private final Map<Integer, Character> positionSymbols = new HashMap<>();
    private int endMarkerPosition = -1;

    public SyntaxTreeBuilder(Node root, int totalPositions) {
        this.root = root;
        this.totalPositions = totalPositions;

        for (int i = 1; i <= totalPositions; i++)
            followpos.put(i, new HashSet<>());
    }

    public void build() {
        compute(root);
    }

    public Map<Integer, Set<Integer>> getFollowpos() { return followpos; }
    public Map<Integer, Character> getPositionSymbols() { return positionSymbols; }
    public int getEndMarkerPosition() { return endMarkerPosition; }
    public Set<Integer> getInitialState() { return root.getFirstpos(); }

    private void compute(Node node) {
        if (node == null) return;

        switch (node.getType()) {

            case SYMBOL -> {
                int p = node.getPosition();
                positionSymbols.put(p, node.getSymbol());

                if (node.isEndMarker())
                    endMarkerPosition = p;

                node.setNullable(false);
                node.setFirstpos(new HashSet<>(Set.of(p)));
                node.setLastpos(new HashSet<>(Set.of(p)));
            }

            case UNION -> {
                compute(node.getLeft());
                compute(node.getRight());

                Node l = node.getLeft();
                Node r = node.getRight();

                node.setNullable(l.isNullable() || r.isNullable());
                node.setFirstpos(Node.union(l.getFirstpos(), r.getFirstpos()));
                node.setLastpos(Node.union(l.getLastpos(), r.getLastpos()));
            }

            case CONCAT -> {
                compute(node.getLeft());
                compute(node.getRight());

                Node l = node.getLeft();
                Node r = node.getRight();

                node.setNullable(l.isNullable() && r.isNullable());

                Set<Integer> fp = new HashSet<>(l.getFirstpos());
                if (l.isNullable()) fp.addAll(r.getFirstpos());
                node.setFirstpos(fp);

                Set<Integer> lp = new HashSet<>(r.getLastpos());
                if (r.isNullable()) lp.addAll(l.getLastpos());
                node.setLastpos(lp);

                for (int i : l.getLastpos())
                    followpos.get(i).addAll(r.getFirstpos());
            }

            case KLEENE -> {
                compute(node.getLeft());
                Node c = node.getLeft();

                node.setNullable(true);
                node.setFirstpos(c.getFirstpos());
                node.setLastpos(c.getLastpos());

                for (int i : node.getLastpos())
                    followpos.get(i).addAll(node.getFirstpos());
            }

            case POSITIVE -> {
                compute(node.getLeft());
                Node c = node.getLeft();

                node.setNullable(c.isNullable());
                node.setFirstpos(c.getFirstpos());
                node.setLastpos(c.getLastpos());

                for (int i : node.getLastpos())
                    followpos.get(i).addAll(node.getFirstpos());
            }

            case OPTIONAL -> {
                compute(node.getLeft());
                Node c = node.getLeft();

                node.setNullable(true);
                node.setFirstpos(c.getFirstpos());
                node.setLastpos(c.getLastpos());
            }
        }
    }
}