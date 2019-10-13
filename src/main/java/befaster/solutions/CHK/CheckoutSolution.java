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
      FreeItemOffer.by(
          ItemsCountWithCost.by('F', 2, 20),
          1
      ),
      ExtraItemOffer.by(
          ItemsCountWithCost.by('E', 2, 80),
          ItemsCountWithCost.by('B', 1, 0)
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

  public static final class UsualCost implements Offer {
    public final ItemCount letteCount;
    public final int cost;

    public UsualCost(ItemCount letteCount, int cost) {
      this.letteCount = letteCount;
      this.cost = cost;
    }

    public static UsualCost by(ItemCount itemCount, int cost) {
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

  public static final class DiscountOffer implements Offer {
    public final ItemCount itemsCount;
    public final int discount;

    private DiscountOffer(ItemCount itemsCount, int discount) {
      this.itemsCount = itemsCount;
      this.discount = discount;
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

    public static DiscountOffer by(ItemCount itemsCount, int discount) {
      return new DiscountOffer(itemsCount, discount);
    }
  }

  public static final class ExtraItemOffer implements Offer {
    public final ItemsCountWithCost itemsCountWithCost;
    public final ItemsCountWithCost extraItemsWithCost;

    private ExtraItemOffer(ItemsCountWithCost itemsCountWithCost, ItemsCountWithCost extraItemsWithCost) {
      this.itemsCountWithCost = itemsCountWithCost;
      this.extraItemsWithCost = extraItemsWithCost;
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

    public static ExtraItemOffer by(ItemsCountWithCost itemsCountWithCost, ItemsCountWithCost extraItems) {
      return new ExtraItemOffer(itemsCountWithCost, extraItems);
    }
  }

  public static final class FreeItemOffer implements Offer {
    public final ItemsCountWithCost itemCountWithCost;
    public final int freeCount;

    private FreeItemOffer(ItemsCountWithCost itemCountWithCost, int freeCount) {
      this.itemCountWithCost = itemCountWithCost;
      this.freeCount = freeCount;
    }

    public ItemsCountWithCost toItemsCountWithCost() {
      return ItemsCountWithCost.by(itemCountWithCost.item, itemCountWithCost.count, itemCountWithCost.cost);
    }

    @Override
    public FreeItemOffer times(int times) {
      return FreeItemOffer.by(
          itemCountWithCost.times(times),
          freeCount * times
      );
    }

    public static FreeItemOffer by(ItemsCountWithCost itemsCountWithCost, int freeCount) {
      return new FreeItemOffer(itemsCountWithCost, freeCount);
    }

    @Override
    public ItemsWithAmount applyTo(ItemsWithAmount itemsWithAmount) {
      final int totalItems = itemCountWithCost.count + freeCount;
      final Integer count = itemsWithAmount.itemsCount.getOrDefault(itemCountWithCost.item, 0);
      if (count == null || count < totalItems) return itemsWithAmount;

      int times = count / totalItems;

      return itemsWithAmount
          .minus(
              ImmutableList.of(
                  itemCountWithCost.toItemsCount().times(times).plus(freeCount * times)
              )
          )
          .plus(itemCountWithCost.cost * times);
    }
  }

  public static final class ItemsWithAmount {
    public final int amount;
    public final Map<Character, Integer> itemsCount;

    public ItemsWithAmount(int amount, Map<Character, Integer> itemsCount) {
      this.amount = amount;
      this.itemsCount = itemsCount;
    }

    public ItemsWithAmount minus(ItemsCountWithCost itemsCountWithCost) {
      final char item = itemsCountWithCost.item;
      final int itemsCount = itemsCountWithCost.count;

      final Integer count = this.itemsCount.getOrDefault(item, 0);
      if (count < itemsCount)
        throw new IllegalArgumentException("Can not subtract ItemCountWithCost: " + itemsCountWithCost);

      final Map<Character, Integer> newItemsCount = new HashMap<>(this.itemsCount);
      newItemsCount.put(item, count - itemsCount);
      return new ItemsWithAmount(amount + itemsCountWithCost.cost, newItemsCount);
    }

    public ItemsWithAmount minus(Map<Character, Integer> minusItemsCount) {
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

    public ItemsWithAmount minus(Collection<ItemCount> minusItems) {
      final Map<Character, Integer> itemsCount = minusItems
          .stream()
          .collect(groupingBy(itemCount -> itemCount.item, summingInt(itemCount -> itemCount.count)));
      return minus(itemsCount);
    }

    public ItemsWithAmount minus(int amount) {
      return ItemsWithAmount.by(this.amount - amount, itemsCount);
    }

    public ItemsWithAmount plus(int amount) {
      return ItemsWithAmount.by(this.amount + amount, itemsCount);
    }

    public ItemsWithAmount minus(ItemsWithAmount itemsWithAmount) {
      return minus(itemsWithAmount.itemsCount)
          .minus(amount);
    }

    public static ItemsWithAmount by(int amount, Map<Character, Integer> itemsCount) {
      return new ItemsWithAmount(amount, itemsCount);
    }
  }

}


