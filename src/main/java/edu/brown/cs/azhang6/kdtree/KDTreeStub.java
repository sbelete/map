package edu.brown.cs.azhang6.kdtree;

import edu.brown.cs.azhang6.dimension.Dimensional;
import edu.brown.cs.azhang6.dimension.DimensionalDistance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.Predicate;

/**
 * Stub k-d tree. Stores all elements in a list.
 *
 * @author aaronzhang
 * @param <T> a dimensional type
 */
class KDTreeStub<T extends Dimensional> implements KDVertex<T> {

    /**
     * List of elements.
     */
    private final ArrayList<T> elements = new ArrayList<>();

    /**
     * Creates a new {@code KDTreeStub} with the given elements.
     *
     * @param elements list of elements
     * @throws NullPointerException if list is null
     */
    KDTreeStub(List<T> elements) {
        if (elements == null) {
            throw new NullPointerException(
                "Creating KDTreeStub with null list");
        }
        this.elements.addAll(elements);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return elements.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(T element) {
        return elements.contains(element);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void nearestNeighbors(Dimensional d, int n, Predicate<T> ignore,
        List<DimensionalDistance<T>> current) {
        // Check validity of arguments
        if (d == null) {
            throw new NullPointerException(
                "Calling nearestNeighbors() with null dimensional");
        }
        if (current == null) {
            throw new NullPointerException(
                "Calling nearestNeighbors() with null list");
        }
        if (n < 0) {
            throw new IllegalArgumentException(
                "number of nearest neighbors must be nonnegative");
        }
        if (n == 0) {
            return;
        }

        // Nearest neighbors in this vertex (not in the list argument)
        PriorityQueue<DimensionalDistance<T>> nearestInVertex
            = new PriorityQueue<>();
        for (T element : elements) {
            if (ignore == null || !ignore.test(element)) {
                nearestInVertex.add(new DimensionalDistance<>(
                    element, element.distanceTo(d)));
            }
        }
        // Add up to n elements from the priority queue to the list argument
        for (int i = 0; i < n && !nearestInVertex.isEmpty(); i++) {
            nearestInVertex.poll().insertInto(current);
        }
        // Keep the first n elements in the list argument
        while (current.size() > n) {
            current.remove(n);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void withinRadius(Dimensional d, double r, Predicate<T> ignore,
        List<DimensionalDistance<T>> current) {
        // Check validity of arguments
        if (d == null) {
            throw new NullPointerException(
                "Calling withinRadius() with null dimensional");
        }
        if (current == null) {
            throw new NullPointerException(
                "Calling withinRadius() with null list");
        }
        if (r < 0) {
            throw new IllegalArgumentException(
                "radius must be a non-negative decimal");
        }

        for (T element : elements) {
            if (ignore == null || !ignore.test(element)) {
                double distance = element.distanceTo(d);
                if (distance <= r) {
                    current.add(new DimensionalDistance<>(element, distance));
                }
            }
        }
    }

    /**
     * Returns an unmodifiable view of the list of elements in this tree.
     *
     * @return elements in this tree
     */
    List<T> getElements() {
        return Collections.unmodifiableList(elements);
    }

    /**
     * @return string representation of this {@code KDTreeStub}
     */
    @Override
    public String toString() {
        return "KDTreeStub returning correct query results";
    }

    @Override
    public double getMin() {
        throw new UnsupportedOperationException("Not supported since this is a stub");
    }

    @Override
    public double getMax() {
        throw new UnsupportedOperationException("Not supported since this is a stub");
    }
}

/**
 * Contains an incorrect implementation of nearest neighbors and radius search.
 * Does not override the abstract methods in {@link KDVertex} implemented by
 * {@link KDTreeStub}; instead overrides the default methods. Subclasses of this
 * type should override {@link #modify(List)} to modify the correct list
 * returned by each of the queries.
 *
 * @param <T> a dimensional type
 */
abstract class Incorrect<T extends Dimensional> extends KDTreeStub<T> {

    /**
     * Creates a new incorrect k-d tree implementation with the given elements.
     *
     * @param elements list of elements
     */
    Incorrect(List<T> elements) {
        super(elements);
    }

    /**
     * Modifies the correct list returned by a query and/or throws an exception.
     *
     * @param correct correct list
     */
    abstract void modify(List<DimensionalDistance<T>> correct);

    /**
     * Overriden to modify the correct list of nearest neighbors.
     */
    @Override
    public List<DimensionalDistance<T>> nearestNeighbors(Dimensional d,
        int n, Predicate<T> ignore) {
        List<DimensionalDistance<T>> correct
            = super.nearestNeighbors(d, n, ignore);
        modify(correct);
        return correct;
    }

    /**
     * Overriden to modify the correct list of elements found.
     */
    @Override
    public List<DimensionalDistance<T>> withinRadius(Dimensional d,
        double r, Predicate<T> ignore) {
        List<DimensionalDistance<T>> correct
            = super.withinRadius(d, r, ignore);
        modify(correct);
        return correct;
    }

    /**
     * @return string representation of this incorrect stub
     */
    @Override
    public String toString() {
        return "KDTreeStub returning incorrect query results";
    }
}

/**
 * Returns an empty list for a query.
 *
 * @param <T> a dimensional type
 */
class IncorrectEmpty<T extends Dimensional> extends Incorrect<T> {

    /**
     * New incorrect implementation with given elements.
     *
     * @param elements list of elements
     */
    IncorrectEmpty(List<T> elements) {
        super(elements);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void modify(List<DimensionalDistance<T>> correct) {
        correct.clear();
    }
}

/**
 * Omits an element in the correct list returned by a query.
 *
 * @param <T> a dimensional type
 */
class IncorrectOmitsElement<T extends Dimensional> extends Incorrect<T> {

    /**
     * New incorrect implementation with given elements.
     *
     * @param elements list of elements
     */
    IncorrectOmitsElement(List<T> elements) {
        super(elements);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void modify(List<DimensionalDistance<T>> correct) {
        if (!correct.isEmpty()) {
            correct.remove((int) (Math.random() * correct.size()));
        }
    }
}

/**
 * Duplicates first element in the correct list returned by a query.
 *
 * @param <T> a dimensional type
 */
class IncorrectDuplicatesFirst<T extends Dimensional> extends Incorrect<T> {

    /**
     * New incorrect implementation with given elements.
     *
     * @param elements list of elements
     */
    IncorrectDuplicatesFirst(List<T> elements) {
        super(elements);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void modify(List<DimensionalDistance<T>> correct) {
        if (!correct.isEmpty()) {
            correct.add(0, correct.get(0));
        }
    }
}

/**
 * Swaps two elements in the correct list returned by a query.
 *
 * @param <T> a dimensional type
 */
class IncorrectSwaps<T extends Dimensional> extends Incorrect<T> {

    /**
     * New incorrect implementation with given elements.
     *
     * @param elements list of elements
     */
    IncorrectSwaps(List<T> elements) {
        super(elements);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void modify(List<DimensionalDistance<T>> correct) {
        if (!correct.isEmpty()) {
            int index1 = (int) (Math.random() * correct.size());
            int index2 = (int) (Math.random() * correct.size());
            Collections.swap(correct, index1, index2);
        }
    }
}

/**
 * Reverses the correct list returned by a query.
 *
 * @param <T> a dimensional type
 */
class IncorrectReverses<T extends Dimensional> extends Incorrect<T> {

    /**
     * New incorrect implementation with given elements.
     *
     * @param elements list of elements
     */
    IncorrectReverses(List<T> elements) {
        super(elements);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void modify(List<DimensionalDistance<T>> correct) {
        Collections.reverse(correct);
    }
}

/**
 * Modifies one of the distances in the correct list returned by a query.
 *
 * @param <T> a dimensional type
 */
class IncorrectModifiesDistance<T extends Dimensional>
    extends Incorrect<T> {

    /**
     * New incorrect implementation with given elements.
     *
     * @param elements list of elements
     */
    IncorrectModifiesDistance(List<T> elements) {
        super(elements);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void modify(List<DimensionalDistance<T>> correct) {
        if (!correct.isEmpty()) {
            int index = (int) (Math.random() * correct.size());
            DimensionalDistance<T> element = correct.get(index);
            DimensionalDistance<T> newElement
                = new DimensionalDistance<>(element.getDimensional(),
                    element.getDistance() + Math.random());
            correct.set(index, newElement);
        }
    }
}
