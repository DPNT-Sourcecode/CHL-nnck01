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

  interface Offer {
    ItemsCountWithCost toItemsCountWithCost();

    ItemsWithAmount applyTo(ItemsWithAmount itemsWithAmount);

    Offer times(int times);
  }

  private static final class UsualCost implements Offer {
    final ItemCount letteCount;
    final int cost;

    public UsualCost(ItemCount letteCount, int cost) {
      this.letteCount = letteCount;
      this.cost = cost;
    }

    static UsualCost by(ItemCount itemCount, int cost) {
      return new UsualCost(itemCount, cost);
    }

    @Override
    public ItemsCountWithCost toItemsCountWithCost() {
      return ItemsCountWithCost.by(letteCount.item, letteCount.count, cost);
    }

    @Override
    public ItemsWithAmount applyTo(ItemsWithAmount itemsWithAmount) {
      final Integer count = itemsWithAmount.itemsCount.getOrDefault(letteCount.item, 0);
      if (count == 0) return itemsWithAmount;

      return itemsWithAmount.minus(times(count).toItemsCountWithCost());
    }

    @Override
    public Offer times(int times) {
      return UsualCost.by(letteCount.times(times), cost * times);
    }
  }

  private static final class DiscountOffer implements Offer {
    final ItemCount itemsCount;
    final int discount;

    private DiscountOffer(ItemCount itemsCount, int discount) {
      this.itemsCount = itemsCount;
      this.discount = discount;
    }

    static DiscountOffer by(ItemCount itemsCount, int discount) {
      return new DiscountOffer(itemsCount, discount);
    }

    @Override
    public ItemsCountWithCost toItemsCountWithCost() {
      return ItemsCountWithCost.by(itemsCount.item, itemsCount.count, discount);
    }

    @Override
    public DiscountOffer times(int times) {
      return DiscountOffer.by(
          ItemCount.by(itemsCount.item, itemsCount.count * times),
          discount * times
      );
    }

    @Override
    public ItemsWithAmount applyTo(ItemsWithAmount itemsWithAmount) {
      final Integer count = itemsWithAmount.itemsCount.get(itemsCount.item);
      if (count == null || count < itemsCount.count) return itemsWithAmount;

      int times = count / itemsCount.count;
      return itemsWithAmount.minus(times(times).toItemsCountWithCost());
    }
  }

  private static final class ExtraItemOffer implements Offer {
    final ItemsCountWithCost itemsCountWithCost;
    final ItemsCountWithCost extraItemsWithCost;

    private ExtraItemOffer(ItemsCountWithCost itemsCountWithCost, ItemsCountWithCost extraItemsWithCost) {
      this.itemsCountWithCost = itemsCountWithCost;
      this.extraItemsWithCost = extraItemsWithCost;
    }

    static ExtraItemOffer by(ItemsCountWithCost itemsCountWithCost, ItemsCountWithCost extraItems) {
      return new ExtraItemOffer(itemsCountWithCost, extraItems);
    }

    public ItemsCountWithCost toItemsCountWithCost() {
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
    public ItemsWithAmount applyTo(ItemsWithAmount itemsWithAmount) {
      final Integer count = itemsWithAmount.itemsCount.getOrDefault(itemsCountWithCost.item, 0);
      if (count == null || count < itemsCountWithCost.count) return itemsWithAmount;

      int times = count / itemsCountWithCost.count;

      return itemsWithAmount
          .minus(
              ImmutableList.of(
                extraItemsWithCost.toItemsCount().times(times),
                itemsCountWithCost.toItemsCount().times(times)
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

    ItemsWithAmount minus(ItemsCountWithCost itemsCountWithCost) {
      final char item = itemsCountWithCost.item;
      final int itemsCount = itemsCountWithCost.count;

      final Integer count = this.itemsCount.getOrDefault(item, 0);
      if (count < itemsCount)
        throw new IllegalArgumentException("Can not subtract ItemCountWithCost: " + itemsCountWithCost);

      final Map<Character, Integer> newItemsCount = new HashMap<>(this.itemsCount);
      newItemsCount.put(item, count - itemsCount);
      return new ItemsWithAmount(amount + itemsCountWithCost.cost, newItemsCount);
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

    ItemsWithAmount minus(Collection<ItemCount> minusItems) {
      final Map<Character, Integer> itemsCount = minusItems
          .stream()
          .collect(groupingBy(itemCount -> itemCount.item, summingInt(itemCount -> itemCount.count)));
      return minus(itemsCount);
    }

    ItemsWithAmount minus(int amount) {
      return ItemsWithAmount.by(this.amount - amount, itemsCount);
    }

    ItemsWithAmount plus(int amount) {
      return ItemsWithAmount.by(this.amount + amount, itemsCount);
    }

    ItemsWithAmount minus(ItemsWithAmount itemsWithAmount) {
      return minus(itemsWithAmount.itemsCount)
          .minus(amount);
    }

    static ItemsWithAmount by(int amount, Map<Character, Integer> itemsCount) {
      return new ItemsWithAmount(amount, itemsCount);
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
      return "ItemCount{" +
          "count=" + count +
          ", item=" + item +
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

    ItemCount toItemsCount() {
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
      return "ItemCountWithCost{" +
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

