package com.example.whole9yards;

public class Fence {
    private double top;
    private double bottom;
    private double left;
    private double right;

    public Fence(double top, double bottom, double left, double right) {
        this.top = top;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
    }

    public double getTop() {
        return top;
    }

    public double getBottom() {
        return bottom;
    }

    public double getLeft() {
        return left;
    }

    @Override
    public String toString() {
        return "Fence{" +
                "top=" + top +
                ", bottom=" + bottom +
                ", left=" + left +
                ", right=" + right +
                '}';
    }

    public double getRight() {
        return right;
    }
}
