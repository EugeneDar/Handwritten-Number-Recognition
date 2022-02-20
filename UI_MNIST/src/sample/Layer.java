package sample;

import java.io.Serializable;

public class Layer implements Serializable {

    public int size;
    public double[] biases;
    public double[] neurons;
    public double[][] weights;

    public Layer (int size, int nextSize) {
        this.size = size;
        biases = new double[size];
        neurons = new double[size];
        weights = new double[size][nextSize];
    }
}
