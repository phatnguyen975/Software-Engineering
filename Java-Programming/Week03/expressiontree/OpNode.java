package expressiontree;

public class OpNode extends Node {
    private char operator;
    private Node leftNode;
    private Node rightNode;

    public OpNode(String operator) {
        if (operator == null || operator.length() != 1) {
            throw new IllegalArgumentException("Invalid operator: " + operator);
        }
        this.operator = operator.charAt(0);
    }

    public void addLeft(Node leftNode) {
        this.leftNode = leftNode;
    }

    public void addRight(Node rightNode) {
        this.rightNode = rightNode;
    }

    @Override
    public double evaluate() {
        if (leftNode == null || rightNode == null) {
            throw new IllegalStateException("Operation node '" + operator + "' is missing one or both children.");
        }

        double leftValue = leftNode.evaluate();
        double rightValue = rightNode.evaluate();

        switch (operator) {
            case '+':
                return leftValue + rightValue;
            case '-':
                return leftValue - rightValue;
            case '*':
                return leftValue * rightValue;
            case '/':
                if (rightValue == 0) {
                    throw new ArithmeticException("Division by zero.");
                }
                return leftValue / rightValue;
            default:
                throw new UnsupportedOperationException("Unknown operator: " + operator);
        }
    }
}
