package befaster.solutions.CHK;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckoutSolution {

  private final Map<Character, Integer> usualCost = ImmutableMap.of(
          'A', 50,
          'B', 30,
          'C', 20,
          'D', 15,
          'E', 40
  );

  private final Map<Character, CountToCost> specialOffers = ImmutableMap.of(
          'A', CountToCost.by(5, 200),
//          'A', CountToCost.by(3, 130),
          'B', CountToCost.by(2, 45)
  );

  private final Map<Character, List<SpecialOffer>> offers = ImmutableMap.of(
          'A', ImmutableList.of(
                  DiscountOffer.by(LetterCount.by(3, 'A'), 20),
                  DiscountOffer.by(LetterCount.by(5, 'A'), 50)
          )
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

  interface SpecialOffer {
    LetterCountWithCost toLetterCountWithCost();

    LettersWithAmount applyTo(LettersWithAmount lettersWithAmount);
  }

  private static final class DiscountOffer implements SpecialOffer {
    final LetterCount letterCount;
    final int discount;

    private DiscountOffer(LetterCount letterCount, int discount) {
      this.letterCount = letterCount;
      this.discount = discount;
    }

    static DiscountOffer by(LetterCount letterCount, int discount) {
      return new DiscountOffer(letterCount, discount);
    }

    @Override
    public LetterCountWithCost toLetterCountWithCost() {
      return LetterCountWithCost.by(letterCount, discount);
    }

    public DiscountOffer times(int times) {
      return DiscountOffer.by(
              LetterCount.by(letterCount.count * times, letterCount.letter),
              discount * times
      );
    }

    @Override
    public LettersWithAmount applyTo(LettersWithAmount lettersWithAmount) {
      final Integer count = lettersWithAmount.lettersCount.get(letterCount.letter);
      if (count == null || count < letterCount.count) return lettersWithAmount;

      int times = count / letterCount.count;
      return lettersWithAmount.minus(times(times).toLetterCountWithCost());
    }
  }

  private static final class ExtraItemOffer implements SpecialOffer {
    final LetterCount letterCount;
    final LetterCountWithCost extraItems;

    private ExtraItemOffer(LetterCount letterCount, LetterCountWithCost extraItems) {
      this.letterCount = letterCount;
      this.extraItems = extraItems;
    }

    static ExtraItemOffer by(LetterCount letterCount, LetterCountWithCost extraItems) {
      return new ExtraItemOffer(letterCount, extraItems);
    }

    public LetterCountWithCost toLetterCountWithCost() {
      return LetterCountWithCost.by(letterCount, extraItems.cost);
    }

    public ExtraItemOffer times(int times) {
      return ExtraItemOffer.by(
              LetterCount.by(letterCount.count * times, letterCount.letter),
              LetterCountWithCost.by(extraItems.letterCount, extraItems.cost * times)
      );
    }

    @Override
    public LettersWithAmount applyTo(LettersWithAmount lettersWithAmount) {
      final Integer count = lettersWithAmount.lettersCount.get(letterCount.letter);
      if (count == null || count < letterCount.count) return lettersWithAmount;

      int times = count / letterCount.count;
      return lettersWithAmount.minus(times(times).toLetterCountWithCost());
    }
  }

  private static final class LettersWithAmount {
    final int amount;
    final Map<Character, Integer> lettersCount;

    private LettersWithAmount(int amount, Map<Character, Integer> lettersCount) {
      this.amount = amount;
      this.lettersCount = lettersCount;
    }

    LettersWithAmount minus(LetterCountWithCost letterCountWithCost) {
      final char letter = letterCountWithCost.letterCount.letter;
      final int letterCount = letterCountWithCost.letterCount.count;

      final Integer count = lettersCount.getOrDefault(letter, 0);
      if (count < letterCount)
        throw new IllegalArgumentException("Can not subtract LetterCountWithCost: " + letterCountWithCost);

      final Map<Character, Integer> newLettersCount = ImmutableMap.<Character, Integer>builder()
              .putAll(lettersCount)
              .put(letter, count - letterCount)
              .build();

      return new LettersWithAmount(amount - letterCountWithCost.cost, newLettersCount);
    }

    static LettersWithAmount by(int amount, Map<Character, Integer> lettersCount) {
      return new LettersWithAmount(amount, lettersCount);
    }
  }

  private static final class LetterCount {
    final int count;
    final char letter;

    private LetterCount(int count, char letter) {
      this.count = count;
      this.letter = letter;
    }

    static LetterCount by(int count, char letter) {
      return new LetterCount(count, letter);
    }
  }

  private static final class LetterCountWithCost {
    final LetterCount letterCount;
    final int cost;

    private LetterCountWithCost(LetterCount letterCount, int cost) {
      this.letterCount = letterCount;
      this.cost = cost;
    }

    private static LetterCountWithCost by(LetterCount letterCount, int cost) {
      return new LetterCountWithCost(letterCount, cost);
    }
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


