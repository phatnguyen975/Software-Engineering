import java.util.Scanner;
import expressiontree.*;
import expressionparser.*;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Enter a mathematical expression (e.g., 2 * (3 + 4)) or 'q' to quit.");

        while (true) {
            System.out.print("> ");
            String input = sc.nextLine();

            if (input.equalsIgnoreCase("q") || input.equalsIgnoreCase("quit")) {
                break;
            }

            if (input.trim().isEmpty()) {
                continue;
            }

            try {
                Node expressionTree = ExpressionParser.parse(input);
                double result = expressionTree.evaluate();
                System.out.println("Result: " + result);
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }

        sc.close();
    }
}
