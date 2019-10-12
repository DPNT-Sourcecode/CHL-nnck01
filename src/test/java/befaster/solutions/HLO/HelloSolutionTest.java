package befaster.solutions.HLO;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class HelloSolutionTest {
  private HelloSolution helloSolution = new HelloSolution();

  @Test
  public void shouldReturnHelloDespiteTheInput() {
    assertThat(helloSolution.hello(null), is("hello"));
    assertThat(helloSolution.hello(""), is("hello"));
  }
}

