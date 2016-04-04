package edu.brown.cs.azhang6.pair;

import java.util.Objects;

/**
 * An immutable unordered pair.
 *
 * @author aaronzhang
 * @param <T> type of object in pair
 */
public class UnorderedPair<T> extends Pair<T, T> {

  /**
   * New unordered pair with given elements.
   *
   * @param s element
   * @param t element
   */
  public UnorderedPair(T s, T t) {
    super(s, t);
  }

  /**
   * Returns an element not equal to the argument, or any element if both
   * elements equal the argument.
   *
   * @param o object
   * @return element not equal to argument, or any element if both equal
   */
  public T not(Object o) {
    if (!s().equals(o)) {
      return s();
    }
    return t();
  }

  /**
   * Equality between this unordered pair and another object. If the other
   * object is an unordered pair, checks if this pair and the other pair contain
   * the same elements; order is irrelevant.
   *
   * @param o another object
   * @return whether this object is equal to other object
   */
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof UnorderedPair)) {
      return false;
    }
    UnorderedPair other = (UnorderedPair) o;
    return this.s().equals(other.s()) && this.t().equals(other.t())
        || this.s().equals(other.t()) && this.t().equals(other.s());
  }

  /**
   * @return hash code
   */
  @Override
  public int hashCode() {
    return Math.min(Objects.hash(s(), t()), Objects.hash(t(), s()));
  }

  /**
   * @return string representation
   */
  @Override
  public String toString() {
    return String.format("unordered pair with %s and %s", s(), t());
  }
}
