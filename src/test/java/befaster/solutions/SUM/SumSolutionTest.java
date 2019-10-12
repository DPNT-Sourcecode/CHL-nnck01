package befaster.solutions.SUM;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class SumSolutionTest {

  private SumSolution sum;

  @Before
  public void setUp() {
    sum = new SumSolution();
  }

  @Test
  public void compute_sum() {
    assertThat(sum.compute(1, 1), equalTo(2));
  }

  @Test
  public void shouldSumTwoZerosAndReturnZero() {
    assertThat(sum.compute(0, 0), equalTo(0));
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionIfXIsLessThanZero() {
    sum.compute(-1, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionIfYIsLessThanZero() {
    sum.compute(0, -1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionIfXIsGreaterThan100() {
    sum.compute(101, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionIfYIsGreaterThan100() {
    sum.compute(0, 101);
  }
}


