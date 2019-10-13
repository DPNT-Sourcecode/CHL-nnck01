package befaster.solutions.CHK;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class CheckoutSolutionTest {
  private final CheckoutSolution checkoutSolution = new CheckoutSolution();

  @Test
  public void shouldReturnMinusOneForInvalidInput() {
    assertThat(checkoutSolution.checkout(null), is(-1));
  }

  @Test
  public void shouldReturnZeroForEmptyInput() {
    //    - {"method":"checkout","params":[""],"id":"CHL_R1_002"}, expected: 0, got: -1
    assertThat(checkoutSolution.checkout(""), is(0));
    assertThat(checkoutSolution.checkout("a"), is(-1));
    assertThat(checkoutSolution.checkout("-"), is(-1));
    //  id = CHL_R1_009, req = checkout("ABCa"), resp = 4
    //  id = CHL_R1_010, req = checkout("AxA"), resp = 3
    assertThat(checkoutSolution.checkout("ABCa"), is(-1));
    assertThat(checkoutSolution.checkout("AxA"), is(-1));
  }

  @Test
  public void shouldReturnItemCost__IfTableContainsItemAndNoSpecialOfferApplicable() {
    //    - {"method":"checkout","params":["A"],"id":"CHL_R1_003"}, expected: 50, got: 1
    //    - {"method":"checkout","params":["B"],"id":"CHL_R1_004"}, expected: 30, got: 1
    //  id = CHL_R1_013, req = checkout("AA"), resp = 2
    assertThat(checkoutSolution.checkout("A"), is(50));
    assertThat(checkoutSolution.checkout("AA"), is(100));
    assertThat(checkoutSolution.checkout("B"), is(30));
    assertThat(checkoutSolution.checkout("C"), is(20));
    assertThat(checkoutSolution.checkout("D"), is(15));
    assertThat(checkoutSolution.checkout("E"), is(40));
  }

  @Test
  public void shouldSumItemsCosts() {
    //  id = CHL_R1_011, req = checkout("ABCD"), resp = 4
    assertThat(checkoutSolution.checkout("ABCD"), is(115));
  }

  @Test
  public void shouldReturnItemCostWithSpecialOffer__IfApplicable() {
    //  id = CHL_R1_014, req = checkout("AAA"), resp = 3
    //  id = CHL_R1_019, req = checkout("BB"), resp = 2
    //  id = CHL_R1_017, req = checkout("AAAAAA"), resp = 6
    //  id = CHL_R1_021, req = checkout("BBBB"), resp = 4
    assertThat(checkoutSolution.checkout("AAA"), is(130));
    assertThat(checkoutSolution.checkout("AAAAAA"), is(250));
    assertThat(checkoutSolution.checkout("BB"), is(45));
    assertThat(checkoutSolution.checkout("BBBB"), is(90));
  }

  @Test
  public void shouldSumSpecialOfferAndUsualCost__IfApplicable() {
    assertThat(checkoutSolution.checkout("AAAA"), is(180));
    assertThat(checkoutSolution.checkout("AAAAA"), is(200));
    assertThat(checkoutSolution.checkout("BBB"), is(75));
    assertThat(checkoutSolution.checkout("AAABB"), is(175));
  }

  @Test
  public void shouldNotDependOnItemsOrder() {
    //{"method":"checkout","params":["ABCDABCD"],"id":"CHL_R1_022"}, expected: 215, got: 230
    //{"method":"checkout","params":["BABDDCAC"],"id":"CHL_R1_023"}, expected: 215, got: 230
    //{"method":"checkout","params":["ABCDCBAABCABBAAA"],"id":"CHL_R1_001"}, expected: 505, got: 540//    assertThat(checkoutSolution.checkout("ABCDABCD"), is(180));
    assertThat(checkoutSolution.checkout("ABCDABCD"), is(215));
    assertThat(checkoutSolution.checkout("BABDDCAC"), is(215));
    assertThat(checkoutSolution.checkout("ABCDCBAABCABBAAA"), is(495));
  }

  @Test
  public void shouldHandleSpecialOffersWithExtraItems() {
    //todo (assumption): free B affect minus to total
    assertThat(checkoutSolution.checkout("EE"), is(80));
    assertThat(checkoutSolution.checkout("EEEEBB"), is(160));
    assertThat(checkoutSolution.checkout("BEBEEE"), is(160));
    assertThat(checkoutSolution.checkout("FF"), is(20));
    assertThat(checkoutSolution.checkout("FFF"), is(20));
    assertThat(checkoutSolution.checkout("FFFFFF"), is(40));
  }

  @Test
  public void shouldCombineOffersStartingFromTheBest() {
    assertThat(checkoutSolution.checkout("AAAAAA"), is(250));
    assertThat(checkoutSolution.checkout("AAAAAAAA"), is(330));
  }
}




