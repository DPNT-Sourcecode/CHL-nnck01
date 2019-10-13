package befaster.solutions.CHK;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;

public class CheckoutSolution {

  private final List<Offer> allOffers = ImmutableList.of(
      ExtraItemOffer.by(
          LetterCountWithCost.by('E', 2, 80),
          LetterCountWithCost.by('B', 1, 0)
      ),
      ExtraItemOffer.by(
          LetterCountWithCost.by('F', 2, 20),
          LetterCountWithCost.by('F', 1, 0)
      ),
      DiscountOffer.by(LetterCount.by('A', 5), 200),
      DiscountOffer.by(LetterCount.by('A', 3), 130),
      DiscountOffer.by(LetterCount.by('B', 2), 45),
      UsualCost.by(LetterCount.by('A', 1), 50),
      UsualCost.by(LetterCount.by('B', 1), 30),
      UsualCost.by(LetterCount.by('C', 1), 20),
      UsualCost.by(LetterCount.by('D', 1), 15),
      UsualCost.by(LetterCount.by('E', 1), 40),
      UsualCost.by(LetterCount.by('F', 1), 10)
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

      return lettersWithAmount
          .minus(
              ImmutableList.of(
                extraItems.toLetterCount().times(times),
                letterCountWithCost.toLetterCount().times(times)
              )
          )
          .plus(letterCountWithCost.cost * times);
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

    LettersWithAmount minus(Map<Character, Integer> minusLettersCount) {
      final Map<Character, Integer> newLettersCount = new HashMap<>(lettersCount);
      minusLettersCount
          .forEach((letter, count) -> newLettersCount.compute(letter, (ignored, oldCount) -> {
            if (oldCount == null) return 0;
            if (oldCount - count < 0)
              throw new IllegalArgumentException("Can not subtract " + LetterCount.by(letter, count));

            return oldCount - count;
          }));

      return LettersWithAmount.by(amount, newLettersCount);
    }

    LettersWithAmount minus(Collection<LetterCount> minusLetters) {
      final Map<Character, Integer> lettersToCount = minusLetters
          .stream()
          .collect(groupingBy(letterCount -> letterCount.letter, summingInt(lettersCount -> lettersCount.count)));
      return minus(lettersCount);
    }

    LettersWithAmount minus(int amount) {
      return LettersWithAmount.by(this.amount - amount, lettersCount);
    }

    LettersWithAmount plus(int amount) {
      return LettersWithAmount.by(this.amount + amount, lettersCount);
    }

    LettersWithAmount minus(LettersWithAmount lettersWithAmount) {
      return minus(lettersWithAmount.lettersCount)
          .minus(amount);
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

    @Override
    public String toString() {
      return "LetterCount{" +
          "count=" + count +
          ", letter=" + letter +
          '}';
    }
  }

  private static final class LetterCountWithCost {
    final char letter;
    final int count;
    final int cost;

    private LetterCountWithCost(char letter, int count, int cost) {
      this.letter = letter;
      this.count = count;
      this.cost = cost;
    }

    LetterCount toLetterCount() {
      return LetterCount.by(letter, count);
    }

    LetterCountWithCost times(int times) {
      return LetterCountWithCost.by(letter, count * times, cost * times);
    }

    private static LetterCountWithCost by(char letter, int count, int cost) {
      return new LetterCountWithCost(letter, count, cost);
    }

    @Override
    public String toString() {
      return "LetterCountWithCost{" +
          "letter=" + letter +
          ", count=" + count +
          ", cost=" + cost +
          '}';
    }
  }

  private static class Item {
    final Character name;

    private Item(Character name) {
      this.name = name;
    }
  }
}



