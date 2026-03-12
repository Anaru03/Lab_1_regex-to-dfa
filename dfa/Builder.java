import java.util.*;

/**
 * Construye el AFD (versión inicial).
 */
public class Builder {

    private final SyntaxTreeBuilder treeBuilder;

    public Builder(SyntaxTreeBuilder builder) {
        this.treeBuilder = builder;
    }

    public dfa build() {

        // Solo estado inicial vacío por ahora
        Map<Integer, Map<Character, Integer>> transitions = new HashMap<>();
        Set<Integer> finalStates = new HashSet<>();

        transitions.put(0, new HashMap<>());

        return new dfa(0, finalStates, transitions);
    }
}