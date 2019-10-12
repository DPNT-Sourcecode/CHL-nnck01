package befaster.solutions.TST;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class OneTest {
    private One one;

    @Before
    public void name() {
        one = new One();
    }

    @Test
    public void run() {
        assertThat(one.apply(), equalTo(1));
    }
}