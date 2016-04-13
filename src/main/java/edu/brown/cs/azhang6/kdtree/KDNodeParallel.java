package edu.brown.cs.azhang6.kdtree;

import edu.brown.cs.azhang6.dimension.Dimensional;
import java.util.ArrayList;
import java.util.List;

public class KDNodeParallel<T extends Dimensional> extends KDNode<T> {

    /**
     * Constructs a new {@code KDNode} with the given elements, index of
     * coordinate to split, and level to begin multithreading.
     *
     * @param elements nonempty list of elements
     * @param coordinate index of coordinate to split
     * @param parallelLevel level to begin multithreading
     * @throws IllegalArgumentException if list of elements is empty
     */
    public KDNodeParallel(List<T> elements, int coordinate, int parallelLevel) {
        super();
        this.coordinate = coordinate;
        List<T> elementsCopy = new ArrayList<>(elements);

        // Find median coordinate, or estimate the median if list is long
        int size = elementsCopy.size();
        if (size == 0) {
            throw new IllegalArgumentException("Constructing empty KDNode");
        }
        if (size <= RANDOM_MEDIAN) {
            this.split = median(elementsCopy);
        } else {
            List<T> sample = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                sample.add(elementsCopy.get((int) (Math.random() * size)));
            }
            this.split = median(sample);
        }

        // Put each element in either the left or right subtree
        List<T> leftList = new ArrayList<>();
        List<T> rightList = new ArrayList<>();
        for (T element : elementsCopy) {
            if (element.getCoordinate(coordinate) < split) {
                leftList.add(element);
            } else {
                rightList.add(element);
            }
        }

        // The subtrees are either nodes or leaves, based on size
        int numDimensions = elementsCopy.get(0).numDimensions();
        Thread leftThread = null;
        Thread rightThread = null;
        if (leftList.size() <= KDLeaf.MAX_COUNT) {
            left = new KDLeaf<>(leftList);
        } else {
            // Here, we check if we should start a new thread
            if (parallelLevel == 0) {
                leftThread = new Thread(() -> {
                    left = new KDNode<>(leftList, (coordinate + 1) % numDimensions);
                });
                leftThread.start();
            } else {
                left = new KDNodeParallel<>(
                    leftList, (coordinate + 1) % numDimensions, parallelLevel - 1);
            }
        }
        if (rightList.size() <= KDLeaf.MAX_COUNT) {
            right = new KDLeaf<>(rightList);
        } else {
            if (parallelLevel == 0) {
                rightThread = new Thread(() -> {
                    right = new KDNode<>(rightList, (coordinate + 1) % numDimensions);
                });
                rightThread.start();
            } else {
                right = new KDNodeParallel<>(
                    rightList, (coordinate + 1) % numDimensions, parallelLevel - 1);
            }
        }
        // Wait for any threads we started to finish
        if (leftThread != null) {
            try {
                leftThread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if (rightThread != null) {
            try {
                rightThread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
