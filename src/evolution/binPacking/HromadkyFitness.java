package evolution.binPacking;

import evolution.FitnessFunction;
import evolution.individuals.Individual;
import evolution.individuals.IntegerIndividual;

import java.util.Vector;

import static java.lang.Integer.MAX_VALUE;

public class HromadkyFitness implements FitnessFunction {

    Vector<Double> weights;
    int K;

    public HromadkyFitness(Vector<Double> weights, int K) {
        this.weights = weights;
        this.K = K;
    }

    private double avg(double[] fields) {
        double sum = 0;
        for (int i = 0; i < fields.length; ++i) {
            sum += fields[i];
        }
        return (sum / fields.length);
    }

    public int[] getBinWeights(Individual ind) {

        int[] binWeights = new int[K];

        int[] bins = ((IntegerIndividual) ind).toIntArray();

        for (int i = 0; i < bins.length; i++) {

            binWeights[bins[i]] += weights.get(i);
        }

        return binWeights;

    }

    @Override
    public double evaluate(Individual ind) {

        int[] binWeights = getBinWeights(ind);

        double min = MAX_VALUE;
        double max = Integer.MIN_VALUE;
        for (int i = 0; i < K; i++) {
            if (binWeights[i] < min) {
                min = binWeights[i];
            }
            if (binWeights[i] > max) {
                max = binWeights[i];
            }
        }

        ind.setObjectiveValue(max - min);    // tohle doporucuji zachovat
        //return 1.0/(max-min);

        //EX
        double[] x = new double[K];
        for (int i = 0; i < binWeights.length; ++i) {
            x[i] = binWeights[i];
        }
        double ex = avg(x);

        // (X-EX)^2
        double[] x_ex = new double[K];
        for (int i = 0; i < binWeights.length; ++i) {
            double tmp = binWeights[i] - ex;
            x_ex[i] = tmp * tmp;
        }

        // E(X-EX)^2
        double varx = avg(x_ex);
        return Integer.MAX_VALUE /( 1.0 + varx);
    }
}