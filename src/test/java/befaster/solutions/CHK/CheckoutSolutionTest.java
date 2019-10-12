package befaster.solutions.CHK;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class CheckoutSolutionTest {
  private final CheckoutSolution checkoutSolution = new CheckoutSolution();

  @Test
  public void shouldReturnMinusOneForInvalidInput() {
    assertThat(checkoutSolution.checkout(null), is(-1));
    assertThat(checkoutSolution.checkout(""), is(-1));
    assertThat(checkoutSolution.checkout("         "), is(-1));
  }
}

