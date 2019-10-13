package befaster.solutions.CHK;

import com.google.common.collect.ImmutableList;

interface Offer {
  ItemsCountWithCost toItemsCountWithCost();

  ItemsWithAmount applyTo(ItemsWithAmount itemsWithAmount);

  Offer times(int times);

  final class UsualCost implements Offer {
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

  final class DiscountOffer implements Offer {
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

  final class ExtraItemOffer implements Offer {
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

  final class FreeItemOffer implements Offer {
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
}
