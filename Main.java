import java.util.Scanner;

/**
 * Programa principal.
 * Permite:
 *  1. Ingresar expresión regular
 *  2. Construir el AFD
 *  3. Mostrar tabla
 *  4. Evaluar una cadena
 */
public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.print("Ingrese expresión regular: ");
        String regex = scanner.nextLine();

        RegexParser parser = new RegexParser(regex);
        Node root = parser.parse();

        SyntaxTreeBuilder builder =
                new SyntaxTreeBuilder(root, parser.getPositionCount());

        builder.build();

        Builder dfaBuilder = new Builder(builder);
        dfa automata = dfaBuilder.build();

        // Simula una cadena
        automata.printTransitionTable();

        System.out.print("\nIngrese cadena a evaluar: ");
        String input = scanner.nextLine();

        if (automata.simulate(input))
            System.out.println("CADENA ACEPTADA :D");
        else
            System.out.println("CADENA RECHAZADA >:(");
    }
}