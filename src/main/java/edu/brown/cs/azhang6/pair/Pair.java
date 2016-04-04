package edu.brown.cs.azhang6.pair;

/**
 * An immutable pair of elements.
 *
 * @author aaronzhang
 * @param <S> type of one element
 * @param <T> type of other element
 */
public abstract class Pair<S, T> {

  /**
   * Element.
   */
  private final S s;

  /**
   * Element.
   */
  private final T t;

  /**
   * Pair with given elements.
   *
   * @param s element
   * @param t element
   */
  public Pair(S s, T t) {
    this.s = s;
    this.t = t;
  }

  /**
   * @return one element
   */
  public S s() {
    return s;
  }

  /**
   * @return the element not returned by {@link Pair#s()}
   */
  public T t() {
    return t;
  }

  /**
   * Whether the object is in the pair.
   *
   * @param o object
   * @return whether object is in pair
   */
  public boolean contains(Object o) {
    return s.equals(o) || t.equals(o);
  }
}
