package edu.brown.cs.azhang6.pair;

import java.util.Objects;

/**
 * An immutable ordered pair.
 *
 * @author aaronzhang
 * @param <S> type of first object
 * @param <T> type of second object
 */
public class OrderedPair<S, T> extends Pair<S, T> {

  /**
   * New ordered pair with given elements.
   *
   * @param s first element
   * @param t second element
   */
  public OrderedPair(S s, T t) {
    super(s, t);
  }

  /**
   * @return first element
   */
  public S first() {
    return s();
  }

  /**
   * @return second element
   */
  public T second() {
    return t();
  }

  /**
   * Equality between this ordered pair and another object. If the other object
   * is an ordered pair, checks if this pair and the other pair contain the same
   * elements in the same order.
   *
   * @param o another object
   * @return whether this object is equal to other object
   */
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof OrderedPair)) {
      return false;
    }
    OrderedPair other = (OrderedPair) o;
    return this.s().equals(other.s()) && this.t().equals(other.t());
  }

  /**
   * @return hash code
   */
  @Override
  public int hashCode() {
    return Objects.hash(s(), t());
  }

  /**
   * @return string representation
   */
  @Override
  public String toString() {
    return String.format("ordered pair with %s and %s", s(), t());
  }
}
