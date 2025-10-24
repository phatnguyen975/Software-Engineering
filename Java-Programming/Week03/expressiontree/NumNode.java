package expressiontree;

public class NumNode extends Node {
    private double value;

    public NumNode(double value) {
        this.value = value;
    }

    @Override
    public double evaluate() {
        return this.value;
    }
}
