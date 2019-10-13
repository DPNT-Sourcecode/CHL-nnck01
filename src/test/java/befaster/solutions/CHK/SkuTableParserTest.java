package befaster.solutions.CHK;

import org.junit.Test;

import java.util.List;

import static befaster.solutions.CHK.Offer.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

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
    assertThat(offers, hasItem(DiscountOffer.by(ItemCount.by('B', 2), 45)));
  }

  @Test
  public void shouldParseItemWithExtraItem() {
    final List<Offer> offers = skuTableParser.parse(
        "+------+-------+------------------------+\n" +
            "| Item | Price | Special offers         |\n" +
            "+------+-------+------------------------+\n" +
            "| N    | 40    | 3N get one M free      |\n" +
            "+------+-------+------------------------+"
    );
    assertThat(offers.get(0), is(ExtraItemOffer.by(
        ItemsCountWithCost.by('N', 3, 120),
        ItemsCountWithCost.by('M', 1, 0)
    )));
  }

  @Test
  public void shouldParseItemWithFreeItem() {
    final List<Offer> offers = skuTableParser.parse(
        "+------+-------+------------------------+\n" +
            "| Item | Price | Special offers         |\n" +
            "+------+-------+------------------------+\n" +
            "| U    | 40    | 3U get one U free      |\n" +
            "+------+-------+------------------------+"
    );
    assertThat(offers.get(0), is(
        FreeItemOffer.by(
            ItemsCountWithCost.by('U', 3, 120),
            1
        )
    ));
  }

  @Test
  public void shouldParseMultipleItems() {
    final List<Offer> offers = skuTableParser.parse(
        "+------+-------+------------------------+\n" +
            "| Item | Price | Special offers         |\n" +
            "+------+-------+------------------------+\n" +
            "| V    | 50    | 2V for 90, 3V for 130  |\n" +
            "+------+-------+------------------------+"
    );
    assertThat(offers.get(0), is(
        DiscountOffer.by(
            ItemCount.by('V', 2),
            90
        )
    ));
    assertThat(offers.get(1), is(
        DiscountOffer.by(
            ItemCount.by('V', 3),
            130
        )
    ));
  }

}



