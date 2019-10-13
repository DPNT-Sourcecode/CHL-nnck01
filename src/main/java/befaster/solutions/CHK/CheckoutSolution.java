package befaster.solutions.CHK;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckoutSolution {

  private final List<Offer> allOffers = new SkuTableParser()
      .parse(
          "+------+-------+------------------------+\n" +
          "| Item | Price | Special offers         |\n" +
          "+------+-------+------------------------+\n" +
          "| A    | 50    | 3A for 130, 5A for 200 |\n" +
          "| B    | 30    | 2B for 45              |\n" +
          "| C    | 20    |                        |\n" +
          "| D    | 15    |                        |\n" +
          "| E    | 40    | 2E get one B free      |\n" +
          "| F    | 10    | 2F get one F free      |\n" +
          "| G    | 20    |                        |\n" +
          "| H    | 10    | 5H for 45, 10H for 80  |\n" +
          "| I    | 35    |                        |\n" +
          "| J    | 60    |                        |\n" +
          "| K    | 80    | 2K for 150             |\n" +
          "| L    | 90    |                        |\n" +
          "| M    | 15    |                        |\n" +
          "| N    | 40    | 3N get one M free      |\n" +
          "| O    | 10    |                        |\n" +
          "| P    | 50    | 5P for 200             |\n" +
          "| Q    | 30    | 3Q for 80              |\n" +
          "| R    | 50    | 3R get one Q free      |\n" +
          "| S    | 30    |                        |\n" +
          "| T    | 20    |                        |\n" +
          "| U    | 40    | 3U get one U free      |\n" +
          "| V    | 50    | 2V for 90, 3V for 130  |\n" +
          "| W    | 20    |                        |\n" +
          "| X    | 90    |                        |\n" +
          "| Y    | 10    |                        |\n" +
          "| Z    | 50    |                        |\n" +
          "+------+-------+------------------------+"
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

