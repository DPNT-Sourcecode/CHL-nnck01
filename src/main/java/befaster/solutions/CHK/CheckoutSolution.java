package befaster.solutions.CHK;

import com.google.common.collect.ImmutableList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckoutSolution {

  private final List<Offer> allOffers = ImmutableList.of(
      Offer.FreeItemOffer.by(
          ItemsCountWithCost.by('F', 2, 20),
          1
      ),
      Offer.ExtraItemOffer.by(
          ItemsCountWithCost.by('E', 2, 80),
          ItemsCountWithCost.by('B', 1, 0)
      ),
      Offer.DiscountOffer.by(ItemCount.by('A', 5), 200),
      Offer.DiscountOffer.by(ItemCount.by('A', 3), 130),
      Offer.DiscountOffer.by(ItemCount.by('B', 2), 45),
      Offer.UsualCost.by(ItemCount.by('A', 1), 50),
      Offer.UsualCost.by(ItemCount.by('B', 1), 30),
      Offer.UsualCost.by(ItemCount.by('C', 1), 20),
      Offer.UsualCost.by(ItemCount.by('D', 1), 15),
      Offer.UsualCost.by(ItemCount.by('E', 1), 40),
      Offer.UsualCost.by(ItemCount.by('F', 1), 10)
  );

  public Integer checkout(String skus) {
    if (skus == null) return -1;

    final char[] items = skus.toCharArray();

    final Map<Character, Integer> itemsCount = new HashMap<>();
    for (char item : items) {
      if (!validItem(item)) return -1;

      itemsCount.compute(item, (l, count) -> {
        if (count == null) return 1;

        return count + 1;
      });
    }

    ItemsWithAmount current = ItemsWithAmount.by(0, itemsCount);
    for (final Offer offer : allOffers) current = offer.applyTo(current);

    return current.amount;
  }

  private static boolean validItem(char c) {
    return 'A' <= c && c <= 'Z';
  }

}
