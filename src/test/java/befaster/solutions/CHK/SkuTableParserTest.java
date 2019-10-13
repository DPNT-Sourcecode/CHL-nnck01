package befaster.solutions.CHK;

import org.junit.Test;

import java.util.List;

import static befaster.solutions.CHK.Offer.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;

public class SkuTableParserTest {

  private SkuTableParser skuTableParser = new SkuTableParser();

  @Test(expected = IllegalArgumentException.class)
  public void shouldFailfIfNull() {
    skuTableParser.parse(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldFailIfEmpty() {
    skuTableParser.parse("");
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldFailIfNewLinesLessThan4() {
    skuTableParser.parse("\n\n");
  }

  @Test
  public void shouldTrimHeaderAndFooter() {
    assertThat(skuTableParser.parse(
        "+------+-------+------------------------+\n" +
            "| Item | Price | Special offers         |\n" +
            "+------+-------+------------------------+\n" +
            "+------+-------+------------------------+"
    ), hasSize(0));
  }

  @Test
  public void shouldParseItemWithoutSpecialOffer() {
    final List<Offer> offers = skuTableParser.parse(
        "+------+-------+------------------------+\n" +
            "| Item | Price | Special offers         |\n" +
            "+------+-------+------------------------+\n" +
            "| C    | 20    |                        |\n" +
            "+------+-------+------------------------+"
    );
    assertThat(offers, hasItem(UsualCost.by(ItemCount.by('C', 1), 20)));
  }

  @Test
  public void shouldParseItemWithDiscountOffer() {
    final List<Offer> offers = skuTableParser.parse(
        "+------+-------+------------------------+\n" +
            "| Item | Price | Special offers         |\n" +
            "+------+-------+------------------------+\n" +
            "| B    | 30    | 2B for 45              |\n" +
            "+------+-------+------------------------+"
    );
    assertThat(offers, hasItem(UsualCost.by(ItemCount.by('C', 1), 20)));
  }

}



