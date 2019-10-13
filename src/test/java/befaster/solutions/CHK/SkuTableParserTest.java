package befaster.solutions.CHK;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class SkuTableParserTest {

  private SkuTableParser skuTableParser = new SkuTableParser();

  @Test
  void shouldTrimHeaderAndFooter() {
    assertThat(skuTableParser.parse("+------+-------+------------------------+\n" +
        "| Item | Price | Special offers         |\n" +
        "+------+-------+------------------------+" +
        "+------+-------+------------------------+"), hasSize(0));
  }

  @Test
  void shouldParseItemWithoutSpecialOffer() {
    assertThat(skuTableParser.parse())
  }

}

