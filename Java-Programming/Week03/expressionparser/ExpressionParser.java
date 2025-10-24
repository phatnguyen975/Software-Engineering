package expressionparser;

import java.util.*;
import expressiontree.*;

public class ExpressionParser {
    private static boolean isNumber(String s) {
        return s.matches("-?\\d+(\\.\\d+)?");
    }

    private static boolean isOperator(String s) {
        return "+-*/".contains(s);
    }

    private static int precedence(String operator) {
        switch (operator) {
            case "+":
            case "-":
                return 1;
            case "*":
            case "/":
                return 2;
            default:
                return 0;
        }
    }

    private static List<String> tokenize(String expression) {
        List<String> tokens = new ArrayList<>();
        StringBuilder number = new StringBuilder();

        for (char c : expression.toCharArray()) {
            if (Character.isDigit(c) || c == '.') {
                number.append(c);
            } else if ("+-*/()".indexOf(c) != -1) {
                if (number.length() > 0) {
                    tokens.add(number.toString());
                    number.setLength(0);
                }
                tokens.add(String.valueOf(c));
            } else if (Character.isWhitespace(c)) {
                if (number.length() > 0) {
                    tokens.add(number.toString());
                    number.setLength(0);
                }
            }
        }

        if (number.length() > 0) {
            tokens.add(number.toString());
        }

        return tokens;
    }

    public static Node parse(String expression) {
        List<String> tokens = tokenize(expression);
        Stack<Node> values = new Stack<>();
        Stack<String> operators = new Stack<>();

        for (String token : tokens) {
            if (isNumber(token)) {
                values.push(new NumNode(Double.parseDouble(token)));
            } else if (token.equals("(")) {
                operators.push(token);
            } else if (token.equals(")")) {
                while (!operators.peek().equals("(")) {
                    Node rightNode = values.pop();
                    Node leftNode = values.pop();
                    String operator = operators.pop();
                    OpNode newNode = new OpNode(operator);
                    newNode.addLeft(leftNode);
                    newNode.addRight(rightNode);
                    values.push(newNode);
                }
                operators.pop();
            } else if (isOperator(token)) {
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(token)) {
                    Node rightNode = values.pop();
                    Node leftNode = values.pop();
                    String operator = operators.pop();
                    OpNode newNode = new OpNode(operator);
                    newNode.addLeft(leftNode);
                    newNode.addRight(rightNode);
                    values.push(newNode);
                }
                operators.push(token);
            }
        }

        while (!operators.isEmpty()) {
            Node rightNode = values.pop();
            Node leftNode = values.pop();
            String operator = operators.pop();
            OpNode newNode = new OpNode(operator);
            newNode.addLeft(leftNode);
            newNode.addRight(rightNode);
            values.push(newNode);
        }

        return values.pop();
    }
}
