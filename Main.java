import java.util.Scanner;

/**
 * Programa principal con menú interactivo.
 */
public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        dfa automata = null;

        boolean running = true;

        while (running) {

            System.out.println("\n===== MENÚ =====");
            System.out.println("1. Ingresar nueva expresión regular");
            System.out.println("2. Evaluar cadena");
            System.out.println("3. Salir");
            System.out.print("Seleccione una opción: ");

            String option = scanner.nextLine();

            switch (option) {

                case "1" -> {
                    System.out.print("Ingrese expresión regular: ");
                    String regex = scanner.nextLine();

                    try {
                        RegexParser parser = new RegexParser(regex);
                        Node root = parser.parse();

                        SyntaxTreeBuilder builder =
                                new SyntaxTreeBuilder(root, parser.getPositionCount());

                        builder.build();

                        Builder dfaBuilder = new Builder(builder);
                        automata = dfaBuilder.build();

                        automata.printTransitionTable();

                        System.out.println("AFD construido correctamente :D");

                    } catch (Exception e) {
                        System.out.println("Error en la expresión regular: " + e.getMessage());
                    }
                }

                case "2" -> {
                    if (automata == null) {
                        System.out.println("Primero debe ingresar una expresión regular.");
                    } else {
                        System.out.print("Ingrese cadena a evaluar: ");
                        String input = scanner.nextLine();

                        if (automata.simulate(input))
                            System.out.println("CADENA ACEPTADA :D");
                        else
                            System.out.println("CADENA RECHAZADA >:(");
                    }
                }

                case "3" -> {
                    running = false;
                    System.out.println("Saliendo del programa...");
                }

                default -> System.out.println("Opción inválida.");
            }
        }

        scanner.close();
    }
}