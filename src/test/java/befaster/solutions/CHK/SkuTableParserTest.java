package befaster.solutions.CHK;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;

public class SkuTableParserTest {

  private SkuTableParser skuTableParser = new SkuTableParser();

  @Test(expected = IllegalArgumentException.class)
  void shouldFailfIfNull() {
    skuTableParser.parse(null);
  }

  @Test(expected = IllegalArgumentException.class)
  void shouldFailIfEmpty() {
    skuTableParser.parse("");
  }

  @Test(expected = IllegalArgumentException.class)
  void shouldFailIfNewLinesLessThan4() {
    skuTableParser.parse("\n\n");
  }

  @Test
  void shouldTrimHeaderAndFooter() {
    assertThat(skuTableParser.parse(
        "+------+-------+------------------------+\n" +
            "| Item | Price | Special offers         |\n" +
            "+------+-------+------------------------+\n" +
            "+------+-------+------------------------+"
    ), hasSize(0));
  }

  @Test
  void shouldParseItemWithoutSpecialOffer() {
    assertThat(skuTableParser.parse(
        "+------+-------+------------------------+\n" +
            "| Item | Price | Special offers         |\n" +
            "+------+-------+------------------------+\n" +
            "| C    | 20    |                        |\n" +
            "+------+-------+------------------------+"
    ), hasItem(new Offer.UsualCost(ItemCount.by('C', 1), 20)));
  }

}


