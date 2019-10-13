package befaster.solutions.CHK;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;

public final class ItemsWithAmount {
  public final int amount;
  public final Map<Character, Integer> itemsCount;

  private ItemsWithAmount(int amount, Map<Character, Integer> itemsCount) {
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
