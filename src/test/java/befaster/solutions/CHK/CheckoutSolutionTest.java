package befaster.solutions.CHK;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class CheckoutSolutionTest {
  private final CheckoutSolution checkoutSolution = new CheckoutSolution();

  @Test
  public void shouldReturnMinusOneForInvalidInput() {
    assertThat(checkoutSolution.checkout(null), is(-1));
  }

  @Test
  public void shouldReturnZeroForEmptyInput() {
    //id = CHL_R1_002, req = checkout(""), resp = -1
    assertThat(checkoutSolution.checkout(""), is(0));
  }

//  id = CHL_R1_003, req = checkout("A"), resp = 1
//  id = CHL_R1_004, req = checkout("B"), resp = 1
//  id = CHL_R1_005, req = checkout("C"), resp = 1
//  id = CHL_R1_006, req = checkout("D"), resp = 1
//  id = CHL_R1_007, req = checkout("a"), resp = 1
//  id = CHL_R1_008, req = checkout("-"), resp = 1
//  id = CHL_R1_009, req = checkout("ABCa"), resp = 4
//  id = CHL_R1_010, req = checkout("AxA"), resp = 3
//  id = CHL_R1_011, req = checkout("ABCD"), resp = 4
//  id = CHL_R1_012, req = checkout("A"), resp = 1
//  id = CHL_R1_013, req = checkout("AA"), resp = 2
//  id = CHL_R1_014, req = checkout("AAA"), resp = 3
//  id = CHL_R1_015, req = checkout("AAAA"), resp = 4
//  id = CHL_R1_016, req = checkout("AAAAA"), resp = 5
//  id = CHL_R1_017, req = checkout("AAAAAA"), resp = 6
//  id = CHL_R1_018, req = checkout("B"), resp = 1
//  id = CHL_R1_019, req = checkout("BB"), resp = 2
//  id = CHL_R1_020, req = checkout("BBB"), resp = 3
//  id = CHL_R1_021, req = checkout("BBBB"), resp = 4
//  id = CHL_R1_022, req = checkout("ABCDABCD"), resp = 8
//  id = CHL_R1_023, req = checkout("BABDDCAC"), resp = 8
//  id = CHL_R1_024, req = checkout("AAABB"), resp = 5
//  id = CHL_R1_001, req = checkout("ABCDCBAABCABBAAA"), resp = 16
  @Test
  public void shouldReturnItemCost__IfTableContainsItem() {
    assertThat(checkoutSolution.checkout("A"), is(50));
    assertThat(checkoutSolution.checkout("A"), is(50));
  }
}


