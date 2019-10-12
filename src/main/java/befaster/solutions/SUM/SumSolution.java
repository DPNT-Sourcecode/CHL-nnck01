package befaster.solutions.SUM;

@SuppressWarnings("unused")
public final class SumSolution {

  /**
   * Returns sums of two integers, each of the in [0, 100].
   * Extension points: custom exceptions.
   *
   * @param x
   * @param y
   * @return x + y
   */
  public final int compute(int x, int y) {
    if (x < 0 || y < 0 || x > 100 || y > 100)
      throw new IllegalArgumentException();

    return x + y;
  }

}



