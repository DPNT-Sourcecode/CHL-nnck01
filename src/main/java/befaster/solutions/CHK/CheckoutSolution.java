package befaster.solutions.CHK;

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

public class CheckoutSolution {

  private final Map<Character, Integer> usualCost = ImmutableMap.of(
          'A', 50,
          'B', 30,
          'C', 20,
          'D', 15
  );

  private final Map<Character, CountToCost> specialOffers = ImmutableMap.of(
          'A', CountToCost.by(3, 130),
          'B', CountToCost.by(2, 45)
  );

  public Integer checkout(String skus) {
    if (skus == null) return -1;

    final char[] letters = skus.toCharArray();

    int total = 0;
    char currentLetter = '_';
    int currentLettersCount = 0;

    final Map<Character, Integer> lettersCount = new HashMap<>();
    for (char letter : letters) {
      if (!validLetter(letter)) return -1;

      lettersCount.compute(letter, (l, count) -> {
        if (count == null) return 1;

        return count + 1;
      });
    }

    for (final Map.Entry<Character, Integer> letterToCount : lettersCount.entrySet()) {
      final CountToCost countReminderToSpecialOffersSum = specialOffersSum(letterToCount.getKey(), letterToCount.getValue());
      total += countReminderToSpecialOffersSum.cost + usualOffersSum(letterToCount.getKey(), countReminderToSpecialOffersSum.count);
    }

    return total;
  }

  private static boolean validLetter(char c) {
    return 'A' <= c && c <= 'Z';
  }

  private CountToCost specialOffersSum(char letter, int lettersCount) {
    final CountToCost countToCost = specialOffers.get(letter);
    if (countToCost == null) return CountToCost.by(lettersCount, 0);

    final int specialOfferCost = (lettersCount / countToCost.count) * countToCost.cost;
    return CountToCost.by(lettersCount % countToCost.count, specialOfferCost);
  }

  private int usualOffersSum(char letter, int lettersCount) {
    return usualCost.getOrDefault(letter, 0) * lettersCount;
  }

  private static final class CountToCost {
    final int count;
    final int cost;

    private CountToCost(int count, int cost) {
      this.count = count;
      this.cost = cost;
    }

    static CountToCost by(int count, int cost) {
      return new CountToCost(count, cost);
    }
  }
}
