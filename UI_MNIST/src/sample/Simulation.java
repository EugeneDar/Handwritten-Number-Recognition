package sample;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.function.UnaryOperator;

public class Simulation {
    int[][] field;
    int fieldSize = 28;

    NeuralNetwork nn;
    double[][] inputs = new double[60000][784];

    public Simulation() {
        field = new int[fieldSize][fieldSize];

        UnaryOperator<Double> sigmoid = x -> 1 / (1 + Math.exp(-x));
        UnaryOperator<Double> dsigmoid = y -> y * (1 - y);
//        nn = new NeuralNetwork(0.001, sigmoid, dsigmoid, 784, 512, 128, 32, 10);
//        NeuralNetwork nn = new NeuralNetwork(0.001, sigmoid, dsigmoid,784, 2500, 2000, 1500, 1000, 500, 10);
        nn = new NeuralNetwork(0.001, sigmoid, dsigmoid);
    }

    public void train () {
        int samples = 60000;
        int[] digits = new int[samples];

        System.out.println("Downloading images");

        try {
            File file = new File("mnist_train.csv");
            BufferedReader reader = new BufferedReader(new FileReader(file));

            for (int i = 0;i < samples;i++) {
                String str = reader.readLine();
                digits[i] = Integer.parseInt(str.substring(0, 1));
                String[] image = str.substring(2).split(",");
                for (int x = 0; x < 28; x++) {
                    for (int y = 0; y < 28; y++) {
                        inputs[i][x + y * 28] = Double.parseDouble(image[28 * y + x]) / 255.0;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Starting...");
        if (0 == 0) {
            return;
        }

        for (int i = 0; i < samples; i++) {
            double errorSum = 0;
            int imgIndex = i;
            double[] targets = new double[10];
            int digit = digits[imgIndex];
            targets[digit] = 1;

            double[] outputs = nn.feedForward(inputs[imgIndex]);
            for (int k = 0; k < 10; k++) {
                errorSum += (targets[k] - outputs[k]) * (targets[k] - outputs[k]);
            }
            nn.backpropagation(targets);

            if ((i + 1) % 2000 == 0) {
                System.out.println("epoch: " + i + ". error: " + errorSum);
                nn.serialize();
            }
        }

    }

    public String getResult () {
        double[] inputs = new double[28 * 28];
        int pos = 0;
        for (int i = 0;i < fieldSize;i++) {
            for (int j = 0;j < fieldSize;j++) {
                inputs[pos++] = ( (double) field[j][i]) / 255.0;
            }
        }
        double[] outputs = nn.feedForward(inputs);
        int maxDigit = 0;
        double maxDigitWeight = -1;
        for (int k = 0; k < 10; k++) {
            if(outputs[k] > maxDigitWeight) {
                maxDigitWeight = outputs[k];
                maxDigit = k;
            }
        }
        return String.valueOf(maxDigit);
    }

    public void setDote (int y, int x) {
        dfsDraw(253, y, x);
    }

    private void dfsDraw (int energy, int y, int x) {
        if (energy < 150) {
            return;
        }
        if (y < 0 || x < 0 || x >= fieldSize || y >= fieldSize) {
            return;
        }
        if (field[y][x] >= energy) {
            return;
        }

        field[y][x] = energy;

        dfsDraw(energy / 7 * 5, y + 1, x);
        dfsDraw(energy / 7 * 5, y - 1, x);
        dfsDraw(energy / 7 * 5, y, x + 1);
        dfsDraw(energy / 7 * 5, y, x - 1);
    }

    public static void print (double[] inputs, int result) {
        for (int i = 0;i < inputs.length;i++) {
            if (inputs[i] > 0.1) {
                System.out.print("#");
            } else {
                System.out.print(".");
            }
            if (i % 28 == 0) {
                System.out.println();
            }
        }
        System.out.println("Result = " + result);
    }
}
