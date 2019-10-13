package befaster.solutions.CHK;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;

public class CheckoutSolution {

  private final Map<Character, Integer> usualCost = ImmutableMap.of(
      'A', 50,
      'B', 30,
      'C', 20,
      'D', 15,
      'E', 40
  );

//  private final Map<Character, List<LetterCountWithCost>> skus = ImmutableMap.of(
//      'A', ImmutableList.of(
//          LetterCountWithCost.by('A', 5, 200),
//          LetterCountWithCost.by('A', 3, 130),
//          LetterCountWithCost.by('A', 1, 50)
//      ),
//      'B', ImmutableList.of(
//          LetterCountWithCost.by('B', 1, 30)
//      ),
//      'C', ImmutableList.of(
//          LetterCountWithCost.by('C', 1, 20)
//      ),
//      'D', ImmutableList.of(
//          LetterCountWithCost.by('D', 1, 15)
//      ),
//      'E', ImmutableList.of(
//          LetterCountWithCost.by('E', 1, 40)
//      )
//  );

  private final Map<Character, List<Offer>> offers = ImmutableMap.of(
      'A', ImmutableList.of(
          DiscountOffer.by(LetterCount.by(5, 'A'), 50),
          DiscountOffer.by(LetterCount.by(3, 'A'), 20)
//          UsualCost.by('A', 50)
      ),
      'B', ImmutableList.of(
          DiscountOffer.by(LetterCount.by(2, 'B'), 15)
//          UsualCost.by('B', 30)
      ),
      'C', ImmutableList.of(
//          UsualCost.by('C', 20)
      ),
      'D', ImmutableList.of(
//          UsualCost.by('D', 15)
      ),
      'E', ImmutableList.of(
          ExtraItemOffer.by(LetterCount.by(2, 'E'), LetterCountWithCost.by('B', 1, usualCost.get('B')))
//          UsualCost.by('E', 40)
      )
  );

  public Integer checkout(String skus) {
    if (skus == null) return -1;

    final char[] letters = skus.toCharArray();

    final Map<Character, Integer> lettersCount = new HashMap<>();
    for (char letter : letters) {
      if (!validLetter(letter)) return -1;

      lettersCount.compute(letter, (l, count) -> {
        if (count == null) return 1;

        return count + 1;
      });
    }

    int total = 0;
    for (final Map.Entry<Character, Integer> letterToCount : lettersCount.entrySet()) {
      final Character letter = letterToCount.getKey();
      final Integer count = letterToCount.getValue();
      final LetterCountWithCost withUsualCost = LetterCountWithCost.by(letter, count, count * usualCost.get(letter));

      final List<Offer> specials = offers.getOrDefault(letter, emptyList());

      LetterCountWithCost current = withUsualCost;
      for (final Offer offer : specials) current = offer.applyTo(current);

      total += current.cost;
    }

    return total;
  }

  private static boolean validLetter(char c) {
    return 'A' <= c && c <= 'Z';
  }

  interface Offer {
    LetterCountWithCost toLetterCountWithCost();

    /**
     * Applies special offer to LetterCountWithCost.
     *
     * @param letterCountWithCost - current
     * @return cost is reduced by discount multiplied number of times applicable. Letter count is reduced accordingly.
     */
    LetterCountWithCost applyTo(LetterCountWithCost letterCountWithCost);

    LettersWithAmount applyTo(LettersWithAmount lettersWithAmount);

    Offer times(int times);
  }

  private static final class UsualCost implements Offer {
    final Character letter;
    final int cost;

    public UsualCost(Character letter, int cost) {
      this.letter = letter;
      this.cost = cost;
    }

    static UsualCost by(Character letter, int cost) {
      return new UsualCost(letter, cost);
    }

    @Override
    public LetterCountWithCost toLetterCountWithCost() {
      return LetterCountWithCost.by(letter, 1, cost);
    }

    @Override
    public LetterCountWithCost applyTo(LetterCountWithCost other) {
      if (letter != other.letter) return other;

      int times = other.count;
      return LetterCountWithCost.by(letter, 0, other.cost + times * cost);
    }

    @Override
    public LettersWithAmount applyTo(LettersWithAmount lettersWithAmount) {
      return null;
    }

    @Override
    public Offer times(int times) {
      return null;
    }
  }

  private static final class DiscountOffer implements Offer {
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
      return LetterCountWithCost.by(letterCount.letter, letterCount.count, discount);
    }

    @Override
    public DiscountOffer times(int times) {
      return DiscountOffer.by(
          LetterCount.by(letterCount.count * times, letterCount.letter),
          discount * times
      );
    }

    @Override
    public LetterCountWithCost applyTo(LetterCountWithCost letterCountWithCost) {
      if (letterCount.letter != letterCountWithCost.letter || letterCount.count > letterCountWithCost.count)
        return letterCountWithCost;

      int times = letterCountWithCost.count / letterCount.count;
      return LetterCountWithCost.by(
          letterCountWithCost.letter,
          letterCountWithCost.count % letterCount.count,
          letterCountWithCost.cost - discount * times
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

  private static final class ExtraItemOffer implements Offer {
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
      return LetterCountWithCost.by(letterCount.letter, letterCount.count, extraItems.cost);
    }

    @Override
    public ExtraItemOffer times(int times) {
      return ExtraItemOffer.by(
          LetterCount.by(letterCount.count * times, letterCount.letter),
          LetterCountWithCost.by(extraItems.letter, extraItems.count * times, extraItems.cost * times)
      );
    }

    @Override
    public LetterCountWithCost applyTo(LetterCountWithCost letterCountWithCost) {
      if (letterCount.letter != letterCountWithCost.letter || letterCount.count > letterCountWithCost.count)
        return letterCountWithCost;

      int times = letterCountWithCost.count / letterCount.count;
      return LetterCountWithCost.by(
          letterCountWithCost.letter,
          letterCountWithCost.count % letterCount.count,
          letterCountWithCost.cost - extraItems.cost * times
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
      final char letter = letterCountWithCost.letter;
      final int letterCount = letterCountWithCost.count;

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
    final int count;
    final char letter;
    final int cost;

    private LetterCountWithCost(char letter, int count, int cost) {
      this.letter = letter;
      this.count = count;
      this.cost = cost;
    }

    private static LetterCountWithCost by(char letter, int count, int cost) {
      return new LetterCountWithCost(letter, count, cost);
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

  private static class Item {
    final Character name;

    private Item(Character name) {
      this.name = name;
    }
  }
}
