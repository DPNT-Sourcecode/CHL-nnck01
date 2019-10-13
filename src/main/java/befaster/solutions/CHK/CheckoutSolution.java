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
          ItemsCountWithCost.by('E', 2, 80),
          ItemsCountWithCost.by('B', 1, 0)
      ),
      ExtraItemOffer.by(
          ItemsCountWithCost.by('F', 2, 20),
          ItemsCountWithCost.by('F', 1, 0)
      ),
      DiscountOffer.by(ItemCount.by('A', 5), 200),
      DiscountOffer.by(ItemCount.by('A', 3), 130),
      DiscountOffer.by(ItemCount.by('B', 2), 45),
      UsualCost.by(ItemCount.by('A', 1), 50),
      UsualCost.by(ItemCount.by('B', 1), 30),
      UsualCost.by(ItemCount.by('C', 1), 20),
      UsualCost.by(ItemCount.by('D', 1), 15),
      UsualCost.by(ItemCount.by('E', 1), 40),
      UsualCost.by(ItemCount.by('F', 1), 10)
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
    ItemsWithAmount current = ItemsWithAmount.by(0, lettersCount);
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
    ItemsCountWithCost toLetterCountWithCost();

    ItemsWithAmount applyTo(ItemsWithAmount lettersWithAmount);

    Offer times(int times);
  }

  private static final class UsualCost implements Offer {
    final ItemCount letterCount;
    final int cost;

    public UsualCost(ItemCount letterCount, int cost) {
      this.letterCount = letterCount;
      this.cost = cost;
    }

    static UsualCost by(ItemCount letter, int cost) {
      return new UsualCost(letter, cost);
    }

    @Override
    public ItemsCountWithCost toLetterCountWithCost() {
      return ItemsCountWithCost.by(letterCount.item, letterCount.count, cost);
    }

    @Override
    public ItemsWithAmount applyTo(ItemsWithAmount lettersWithAmount) {
      final Integer count = lettersWithAmount.itemsCount.getOrDefault(letterCount.item, 0);
      if (count == 0) return lettersWithAmount;

      return lettersWithAmount.minus(times(count).toLetterCountWithCost());
    }

    @Override
    public Offer times(int times) {
      return UsualCost.by(letterCount.times(times), cost * times);
    }
  }

  private static final class DiscountOffer implements Offer {
    final ItemCount letterCount;
    final int discount;

    private DiscountOffer(ItemCount letterCount, int discount) {
      this.letterCount = letterCount;
      this.discount = discount;
    }

    static DiscountOffer by(ItemCount letterCount, int discount) {
      return new DiscountOffer(letterCount, discount);
    }

    @Override
    public ItemsCountWithCost toLetterCountWithCost() {
      return ItemsCountWithCost.by(letterCount.item, letterCount.count, discount);
    }

    @Override
    public DiscountOffer times(int times) {
      return DiscountOffer.by(
          ItemCount.by(letterCount.item, letterCount.count * times),
          discount * times
      );
    }

    @Override
    public ItemsWithAmount applyTo(ItemsWithAmount lettersWithAmount) {
      final Integer count = lettersWithAmount.itemsCount.get(letterCount.item);
      if (count == null || count < letterCount.count) return lettersWithAmount;

      int times = count / letterCount.count;
      return lettersWithAmount.minus(times(times).toLetterCountWithCost());
    }
  }

  private static final class ExtraItemOffer implements Offer {
    final ItemsCountWithCost itemsCountWithCost;
    final ItemsCountWithCost extraItemsWithCost;

    private ExtraItemOffer(ItemsCountWithCost itemsCountWithCost, ItemsCountWithCost extraItemsWithCost) {
      this.itemsCountWithCost = itemsCountWithCost;
      this.extraItemsWithCost = extraItemsWithCost;
    }

    static ExtraItemOffer by(ItemsCountWithCost letterCount, ItemsCountWithCost extraItems) {
      return new ExtraItemOffer(letterCount, extraItems);
    }

    public ItemsCountWithCost toLetterCountWithCost() {
      return ItemsCountWithCost.by(itemsCountWithCost.item, itemsCountWithCost.count, itemsCountWithCost.cost);
    }

    @Override
    public ExtraItemOffer times(int times) {
      return ExtraItemOffer.by(
          itemsCountWithCost.times(times),
          extraItemsWithCost.times(times)
      );
    }

    @Override
    public ItemsWithAmount applyTo(ItemsWithAmount lettersWithAmount) {
      final Integer count = lettersWithAmount.itemsCount.getOrDefault(itemsCountWithCost.item, 0);
      if (count == null || count < itemsCountWithCost.count) return lettersWithAmount;

      int times = count / itemsCountWithCost.count;

      return lettersWithAmount
          .minus(
              ImmutableList.of(
                extraItemsWithCost.toLetterCount().times(times),
                itemsCountWithCost.toLetterCount().times(times)
              )
          )
          .plus(itemsCountWithCost.cost * times);
    }
  }

  private static final class ItemsWithAmount {
    final int amount;
    final Map<Character, Integer> itemsCount;

    private ItemsWithAmount(int amount, Map<Character, Integer> itemsCount) {
      this.amount = amount;
      this.itemsCount = itemsCount;
    }

    ItemsWithAmount minus(ItemsCountWithCost letterCountWithCost) {
      final char item = letterCountWithCost.item;
      final int letterCount = letterCountWithCost.count;

      final Integer count = itemsCount.getOrDefault(item, 0);
      if (count < letterCount)
        throw new IllegalArgumentException("Can not subtract LetterCountWithCost: " + letterCountWithCost);

      final Map<Character, Integer> newLettersCount = new HashMap<>(itemsCount);
      newLettersCount.put(item, count - letterCount);
      return new ItemsWithAmount(amount + letterCountWithCost.cost, newLettersCount);
    }

    ItemsWithAmount minus(Map<Character, Integer> minusItemsCount) {
      final Map<Character, Integer> newItemsCount = new HashMap<>(itemsCount);
      minusItemsCount
          .forEach((item, count) -> newItemsCount.compute(item, (ignored, oldCount) -> {
            if (oldCount == null) return 0;
            if (oldCount - count < 0)
              throw new IllegalArgumentException("Can not subtract " + ItemCount.by(item, count));

            return oldCount - count;
          }));

      return ItemsWithAmount.by(amount, newItemsCount);
    }

    ItemsWithAmount minus(Collection<ItemCount> minusLetters) {
      final Map<Character, Integer> itemsCount = minusLetters
          .stream()
          .collect(groupingBy(letterCount -> letterCount.item, summingInt(lettersCount -> lettersCount.count)));
      return minus(itemsCount);
    }

    ItemsWithAmount minus(int amount) {
      return ItemsWithAmount.by(this.amount - amount, itemsCount);
    }

    ItemsWithAmount plus(int amount) {
      return ItemsWithAmount.by(this.amount + amount, itemsCount);
    }

    ItemsWithAmount minus(ItemsWithAmount lettersWithAmount) {
      return minus(lettersWithAmount.itemsCount)
          .minus(amount);
    }

    static ItemsWithAmount by(int amount, Map<Character, Integer> lettersCount) {
      return new ItemsWithAmount(amount, lettersCount);
    }
  }

  private static final class ItemCount {
    final char item;
    final int count;

    private ItemCount(char item, int count) {
      this.count = count;
      this.item = item;
    }

    ItemCount times(int times) {
      return ItemCount.by(item, count * times);
    }

    static ItemCount by(char item, int count) {
      return new ItemCount(item, count);
    }

    @Override
    public String toString() {
      return "LetterCount{" +
          "count=" + count +
          ", letter=" + item +
          '}';
    }
  }

  private static final class ItemsCountWithCost {
    final char item;
    final int count;
    final int cost;

    private ItemsCountWithCost(char item, int count, int cost) {
      this.item = item;
      this.count = count;
      this.cost = cost;
    }

    ItemCount toLetterCount() {
      return ItemCount.by(item, count);
    }

    ItemsCountWithCost times(int times) {
      return ItemsCountWithCost.by(item, count * times, cost * times);
    }

    private static ItemsCountWithCost by(char item, int count, int cost) {
      return new ItemsCountWithCost(item, count, cost);
    }

    @Override
    public String toString() {
      return "LetterCountWithCost{" +
          "item=" + item +
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
