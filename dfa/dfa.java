import java.util.*;

/**
 * Representa el Autómata Finito Determinista.
 * (Versión inicial)
 */
public class dfa {

    private final int initialState;
    private final Set<Integer> finalStates;
    private final Map<Integer, Map<Character, Integer>> transitions;

    public dfa(int initialState,
               Set<Integer> finalStates,
               Map<Integer, Map<Character, Integer>> transitions) {
        this.initialState = initialState;
        this.finalStates = finalStates;
        this.transitions = transitions;
    }

    // Solo imprimimos la tabla por ahora
    public void printTransitionTable() {
        System.out.println("\n=== TABLA DE TRANSICIÓN ===");

        for (int state : transitions.keySet()) {
            System.out.print("Estado " + state);
            if (finalStates.contains(state))
                System.out.print(" (FINAL)");
            System.out.println();

            for (var entry : transitions.get(state).entrySet()) {
                System.out.println("  " + entry.getKey()
                        + " -> " + entry.getValue());
            }
        }
    }
}