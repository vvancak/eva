package evolution.binPacking;

import evolution.Population;
import evolution.RandomNumberGenerator;
import evolution.individuals.Individual;
import evolution.individuals.IntegerIndividual;
import evolution.operators.Operator;

import java.util.*;

/**
 * A mutation for integer encoded individuals. Goes through the indivudal and generates new value from the
 * valid interval for each of the positions with a given probability.
 *
 * @author Martin Pilat
 */
public class HighLowMutationOperator implements Operator {

    double mutationProbability;
    double geneChangeProbability;
    Vector<Double> weights;
    int binCount;

    RandomNumberGenerator rng = RandomNumberGenerator.getInstance();

    /**
     * Constructor, sets the probabilities
     *
     * @param mutationProbability   the probability of mutating an individual
     * @param geneChangeProbability the probability of changing a given gene in the mutated individual
     */

    public HighLowMutationOperator(double mutationProbability, double geneChangeProbability, int binCount, Vector<Double> weights) {
        this.mutationProbability = mutationProbability;
        this.geneChangeProbability = geneChangeProbability;
        this.weights = weights;
        this.binCount = binCount;
    }

    public void operate(Population parents, Population offspring) {

        int size = parents.getPopulationSize();


        for (int i = 0; i < size; i++) {

            IntegerIndividual p1 = (IntegerIndividual) parents.get(i);


            IntegerIndividual o1 = (IntegerIndividual) p1.clone();

            if (rng.nextDouble() < mutationProbability) {
                swapBinItem(o1);
            }

            offspring.add(o1);
        }
    }

    private int[] getBinWeights(Individual ind) {

        int[] binWeights = new int[binCount];

        int[] bins = ((IntegerIndividual) ind).toIntArray();

        for (int i = 0; i < bins.length; i++) {

            binWeights[bins[i]] += weights.get(i);
        }

        return binWeights;
    }

    private Iterator<Map.Entry<Integer,Integer>> getOrderedBinWeights(IntegerIndividual ind) {
        Map<Integer, Integer> indexedBinWeights = new HashMap<>();

        int[] binWeights = getBinWeights(ind);
        for (int i = 0; i < binWeights.length; ++i) {
            indexedBinWeights.put(i,binWeights[i]);
        }

        return indexedBinWeights
                .entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getValue))
                .iterator();
    }

    private void swapBinItem(IntegerIndividual individual) {
        int swapPart = binCount / 10;

        Vector<Integer> head = new Vector<>(swapPart);
        Vector<Integer> tail = new Vector<>(swapPart);

        int i = 0;
        Iterator<Map.Entry<Integer,Integer>> orderedBinWeights = getOrderedBinWeights(individual);
        while (orderedBinWeights.hasNext()) {
            Integer nextBin = orderedBinWeights.next().getKey();

            if (i < swapPart) head.add(nextBin);
            if (i >= binCount - swapPart) tail.add(nextBin);
            ++i;
        }

        int highIndex = -1;
        int lowIndex = -1;

        // get items for swap
        while (highIndex < 0 || lowIndex < 0) {

            // select a random item
            int item = RandomNumberGenerator.getInstance().nextInt(individual.length());
            int bin = (int) individual.get(item);

            // check if in heavy bins
            if (highIndex < 0 && head.contains(bin)) {
                highIndex = item;
            }

            // check if in light bins
            if (lowIndex < 0 && tail.contains(bin)) {
                lowIndex = item;
            }

        }

        // swap one item from heavy bins with one from the low bins
        Object highItem = individual.get(highIndex);
        Object lowItem = individual.get(lowIndex);

        individual.set(highIndex, lowItem);
        individual.set(lowIndex, highItem);
    }
}
