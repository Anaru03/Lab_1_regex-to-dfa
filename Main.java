import java.util.Scanner;

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

        // Solo mostramos tabla en esta fase
        automata.printTransitionTable();
    }
}