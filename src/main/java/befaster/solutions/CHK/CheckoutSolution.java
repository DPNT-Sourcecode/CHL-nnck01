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

//  private final Map<Character, List<Offer>> offers = ImmutableMap.of(
//      'A', ImmutableList.of(
//          DiscountOffer.by(LetterCount.by('A', 5), 200),
//          DiscountOffer.by(LetterCount.by('A', 3), 130)
////          UsualCost.by('A', 50)
//      ),
//      'B', ImmutableList.of(
//          DiscountOffer.by(LetterCount.by('B', 2), 45)
////          UsualCost.by('B', 30)
//      ),
//      'C', ImmutableList.of(
////          UsualCost.by('C', 20)
//      ),
//      'D', ImmutableList.of(
////          UsualCost.by('D', 15)
//      ),
//      'E', ImmutableList.of(
//          ExtraItemOffer.by(
//              LetterCountWithCost.by('E', 2, 80),
//              LetterCountWithCost.by('B', 1, usualCost.get('B')))
////          UsualCost.by('E', 40)
//      )
//  );

  private final List<Offer> allOffers = ImmutableList.of(
      ExtraItemOffer.by(
          LetterCountWithCost.by('E', 2, 80),
          LetterCountWithCost.by('B', 1, 0)
      ),
      DiscountOffer.by(LetterCount.by('A', 5), 200),
      DiscountOffer.by(LetterCount.by('A', 3), 130),
      DiscountOffer.by(LetterCount.by('B', 2), 45),
      UsualCost.by(LetterCount.by('A', 1), 50),
      UsualCost.by(LetterCount.by('B', 1), 30),
      UsualCost.by(LetterCount.by('C', 1), 20),
      UsualCost.by(LetterCount.by('D', 1), 15),
      UsualCost.by(LetterCount.by('E', 1), 40)
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
    LettersWithAmount current = LettersWithAmount.by(0, lettersCount);
    for (final Offer offer : allOffers) current = offer.applyTo(current);

//    for (final Map.Entry<Character, Integer> letterToCount : lettersCount.entrySet()) {
//      final Character letter = letterToCount.getKey();
//      final Integer count = letterToCount.getValue();
//      final LetterCountWithCost withUsualCost = LetterCountWithCost.by(letter, count, count * usualCost.get(letter));
//
//      final List<Offer> specials = offers.getOrDefault(letter, emptyList());
//
//      LetterCountWithCost current = withUsualCost;
//      for (final Offer offer : specials) current = offer.applyTo(current);
//
//      total += current.cost;
//    }

    return current.amount;
  }

  private static boolean validLetter(char c) {
    return 'A' <= c && c <= 'Z';
  }

  interface Offer {
    LetterCountWithCost toLetterCountWithCost();

    LettersWithAmount applyTo(LettersWithAmount lettersWithAmount);

    Offer times(int times);
  }

  private static final class UsualCost implements Offer {
    final LetterCount letterCount;
    final int cost;

    public UsualCost(LetterCount letterCount, int cost) {
      this.letterCount = letterCount;
      this.cost = cost;
    }

    static UsualCost by(LetterCount letter, int cost) {
      return new UsualCost(letter, cost);
    }

    @Override
    public LetterCountWithCost toLetterCountWithCost() {
      return LetterCountWithCost.by(letterCount.letter, letterCount.count, cost);
    }

    @Override
    public LettersWithAmount applyTo(LettersWithAmount lettersWithAmount) {
      final Integer count = lettersWithAmount.lettersCount.getOrDefault(letterCount.letter, 0);
      if (count == 0) return lettersWithAmount;

      return lettersWithAmount.minus(times(count).toLetterCountWithCost());
    }

    @Override
    public Offer times(int times) {
      return UsualCost.by(letterCount.times(times), cost * times);
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
          LetterCount.by(letterCount.letter, letterCount.count * times),
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

  private static final class ExtraItemOffer implements Offer {
    final LetterCountWithCost letterCountWithCost;
    final LetterCountWithCost extraItems;

    private ExtraItemOffer(LetterCountWithCost letterCountWithCost, LetterCountWithCost extraItems) {
      this.letterCountWithCost = letterCountWithCost;
      this.extraItems = extraItems;
    }

    static ExtraItemOffer by(LetterCountWithCost letterCount, LetterCountWithCost extraItems) {
      return new ExtraItemOffer(letterCount, extraItems);
    }

    public LetterCountWithCost toLetterCountWithCost() {
      return LetterCountWithCost.by(letterCountWithCost.letter, letterCountWithCost.count, letterCountWithCost.cost);
    }

    @Override
    public ExtraItemOffer times(int times) {
      return ExtraItemOffer.by(
          LetterCountWithCost.by(letterCountWithCost.letter, letterCountWithCost.count * times, letterCountWithCost.cost * times),
          LetterCountWithCost.by(
              extraItems.letter,
              extraItems.count * times,
              extraItems.cost * times
          )
      );
    }

    @Override
    public LettersWithAmount applyTo(LettersWithAmount lettersWithAmount) {
      final Integer count = lettersWithAmount.lettersCount.getOrDefault(letterCountWithCost.letter, 0);
      if (count == null || count < letterCountWithCost.count) return lettersWithAmount;

      int times = count / letterCountWithCost.count;

      final HashMap<Character, Integer> newLettersCount = new HashMap<>(lettersWithAmount.lettersCount);
      newLettersCount.computeIfPresent(extraItems.letter, (ignored, oldCount) -> oldCount - extraItems.count * times);
      newLettersCount.computeIfPresent(letterCountWithCost.letter, (ignored, oldCount) -> oldCount - letterCountWithCost.count * times);
      return LettersWithAmount.by(lettersWithAmount.amount + letterCountWithCost.cost * times, newLettersCount);
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

      final Map<Character, Integer> newLettersCount = new HashMap<>(lettersCount);
      newLettersCount.put(letter, count - letterCount);
      return new LettersWithAmount(amount + letterCountWithCost.cost, newLettersCount);
    }

    static LettersWithAmount by(int amount, Map<Character, Integer> lettersCount) {
      return new LettersWithAmount(amount, lettersCount);
    }
  }

  private static final class LetterCount {
    final int count;
    final char letter;

    private LetterCount(char letter, int count) {
      this.count = count;
      this.letter = letter;
    }

    LetterCount times(int times) {
      return LetterCount.by(letter, count * times);
    }

    static LetterCount by(char letter, int count) {
      return new LetterCount(letter, count);
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





