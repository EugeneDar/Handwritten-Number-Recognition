package sample;

import java.io.*;
import java.util.function.UnaryOperator;

public class NeuralNetwork implements Serializable {

    private final double learningRate;
    private Layer[] layers;
    private final UnaryOperator<Double> activation;
    private final UnaryOperator<Double> derivative;

    public NeuralNetwork (double learningRate, UnaryOperator<Double> activation, UnaryOperator<Double> derivative, int... sizes) {
        this.learningRate = learningRate;
        this.activation = activation;
        this.derivative = derivative;
        layers = new Layer[sizes.length];

        for (int i = 0;i < sizes.length;i++) {
            int nextSize = 0;
            if (i < sizes.length - 1) {
                nextSize = sizes[i + 1];
            }
            layers[i] = new Layer(sizes[i], nextSize);
            for (int j = 0;j < sizes[i];j++) {
                layers[i].biases[j] = Math.random() * 2.0 - 1.0;
                for (int k = 0;k < nextSize;k++) {
                    layers[i].weights[j][k] = Math.random() * 2.0 - 1.0;
                }
            }
        }
    }

    public NeuralNetwork (double learningRate, UnaryOperator<Double> activation, UnaryOperator<Double> derivative) {
        this.learningRate = learningRate;
        this.activation = activation;
        this.derivative = derivative;

        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream("neurons_data.txt"))) {
            this.layers = (Layer[]) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double[] feedForward (double[] inputs) {
        System.arraycopy(inputs, 0, layers[0].neurons, 0, inputs.length);
        for (int i = 1;i < layers.length;i++) {
            Layer prefLayer = layers[i - 1];
            Layer currLayer = layers[i];
            for (int j = 0;j < currLayer.size;j++) {
                currLayer.neurons[j] = 0;
                for (int k = 0;k < prefLayer.size;k++) {
                    currLayer.neurons[j] += prefLayer.neurons[k] * prefLayer.weights[k][j];
                }
                currLayer.neurons[j] += currLayer.biases[j];
                currLayer.neurons[j] = activation.apply(currLayer.neurons[j]);
            }
        }
        return layers[layers.length - 1].neurons;
    }

    public void backpropagation (double[] targets) {
        double[] errors = new double[layers[layers.length - 1].size];
        for (int i = 0;i < layers[layers.length - 1].size;i++) {
            errors[i] = targets[i] - layers[layers.length - 1].neurons[i];
        }

        for (int k = layers.length - 2;k >= 0;k--) {
            Layer l = layers[k];
            Layer l1 = layers[k + 1];
            double[] errorsNext = new double[l.size];
            double[] gradients = new double[l1.size];
            for (int i = 0; i < l1.size; i++) {
                gradients[i] = errors[i] * derivative.apply(layers[k + 1].neurons[i]);
                gradients[i] *= learningRate;
            }
            double[][] deltas = new double[l1.size][l.size];
            for (int i = 0; i < l1.size; i++) {
                for (int j = 0; j < l.size; j++) {
                    deltas[i][j] = gradients[i] * l.neurons[j];
                }
            }
            for (int i = 0; i < l.size; i++) {
                errorsNext[i] = 0;
                for (int j = 0; j < l1.size; j++) {
                    errorsNext[i] += l.weights[i][j] * errors[j];
                }
            }
            errors = new double[l.size];
            System.arraycopy(errorsNext, 0, errors, 0, l.size);
            double[][] weightsNew = new double[l.weights.length][l.weights[0].length];
            for (int i = 0; i < l1.size; i++) {
                for (int j = 0; j < l.size; j++) {
                    weightsNew[j][i] = l.weights[j][i] + deltas[i][j];
                }
            }
            l.weights = weightsNew;
            for (int i = 0; i < l1.size; i++) {
                l1.biases[i] += gradients[i];
            }
        }
    }

    public void serialize () {
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("neurons_data.txt"))) {
            oos.writeObject(this.layers);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
