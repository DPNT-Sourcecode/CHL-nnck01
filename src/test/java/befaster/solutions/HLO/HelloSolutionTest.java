package befaster.solutions.HLO;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class HelloSolutionTest {
  private HelloSolution helloSolution = new HelloSolution();

  @Test
  public void shouldReturnHelloWorldToEmpty() {
    assertThat(helloSolution.hello(""), is("Hello, !"));
  }

  @Test
  public void shouldReturnHelloWorldToNull() {
    assertThat(helloSolution.hello(null), is("Hello, !"));
  }

  @Test
  public void shouldReturnHelloWorldToCraftsman() {
    assertThat(helloSolution.hello("Craftsman"), is("Hello, Craftsman!"));
  }

  @Test
  public void shouldReturnHelloWorldToMtX() {
    assertThat(helloSolution.hello("Mr. X"), is("Hello, Mr. X!"));
  }
}
