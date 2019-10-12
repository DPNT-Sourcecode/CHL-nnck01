package befaster.solutions.HLO;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class HelloSolutionTest {
  private HelloSolution helloSolution = new HelloSolution();

  @Test
  public void shouldReturnHelloWorldToCraftsman() {
    assertThat(helloSolution.hello("Craftsman"), is("Hello, World!"));
  }
  @Test
  public void shouldReturnHelloWorldToMtX() {
    assertThat(helloSolution.hello("Mr. X"), is("Hello, World!"));
  }
}


