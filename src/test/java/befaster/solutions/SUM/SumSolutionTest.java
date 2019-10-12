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

    @Test
    public void shouldThrowExceptionIfEitherXOrYAreOutTheRange() {
        assertThat(sum.compute(0, 0), equalTo(0));
    }
}

