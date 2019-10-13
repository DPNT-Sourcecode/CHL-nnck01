package befaster.solutions.CHK;

import com.google.common.collect.ImmutableList;

import java.util.Objects;

interface Offer {
  ItemsCountWithCost toItemsCountWithCost();

  ItemsWithAmount applyTo(ItemsWithAmount itemsWithAmount);

  Offer times(int times);

  final class UsualCost implements Offer, Comparable<UsualCost>{
    public final ItemCount letteCount;
    public final int cost;

    private UsualCost(ItemCount letteCount, int cost) {
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

    @Override
    public int compareTo(UsualCost o) {
      return 0;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof UsualCost)) return false;
      UsualCost usualCost = (UsualCost) o;
      return cost == usualCost.cost &&
          Objects.equals(letteCount, usualCost.letteCount);
    }

    @Override
    public int hashCode() {
      return Objects.hash(letteCount, cost);
    }

    @Override
    public String toString() {
      return "UsualCost{" +
          "letteCount=" + letteCount +
          ", cost=" + cost +
          '}';
    }
  }

  final class DiscountOffer implements Offer, Comparable<DiscountOffer> {
    public final ItemCount itemsCount;
    public final int price;

    private DiscountOffer(ItemCount itemsCount, int price) {
      this.itemsCount = itemsCount;
      this.price = price;
    }

    @Override
    public ItemsCountWithCost toItemsCountWithCost() {
      return ItemsCountWithCost.by(itemsCount.item, itemsCount.count, price);
    }

    @Override
    public DiscountOffer times(int times) {
      return DiscountOffer.by(
          ItemCount.by(itemsCount.item, itemsCount.count * times),
          price * times
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

    @Override
    public int compareTo(DiscountOffer other) {
      if (itemsCount.item != other.itemsCount.item) return itemsCount.item - other.itemsCount.item;

      return itemsCount.count - other.itemsCount.count;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof DiscountOffer)) return false;
      DiscountOffer that = (DiscountOffer) o;
      return price == that.price &&
          Objects.equals(itemsCount, that.itemsCount);
    }

    @Override
    public int hashCode() {
      return Objects.hash(itemsCount, price);
    }

    @Override
    public String toString() {
      return "DiscountOffer{" +
          "itemsCount=" + itemsCount +
          ", discount=" + price +
          '}';
    }
  }

  final class ExtraItemOffer implements Offer, Comparable<ExtraItemOffer> {
    final ItemsCountWithCost itemsCountWithCost;
    final ItemsCountWithCost extraItemsWithCost;

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

    @Override
    public int compareTo(ExtraItemOffer other) {
      if (itemsCountWithCost.item != other.itemsCountWithCost.item)
        return itemsCountWithCost.item - other.itemsCountWithCost.item;

      return itemsCountWithCost.count - other.itemsCountWithCost.count;
    }

    public static ExtraItemOffer by(ItemsCountWithCost itemsCountWithCost, ItemsCountWithCost extraItems) {
      return new ExtraItemOffer(itemsCountWithCost, extraItems);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof ExtraItemOffer)) return false;
      ExtraItemOffer that = (ExtraItemOffer) o;
      return Objects.equals(itemsCountWithCost, that.itemsCountWithCost) &&
          Objects.equals(extraItemsWithCost, that.extraItemsWithCost);
    }

    @Override
    public int hashCode() {
      return Objects.hash(itemsCountWithCost, extraItemsWithCost);
    }

    @Override
    public String toString() {
      return "ExtraItemOffer{" +
          "itemsCountWithCost=" + itemsCountWithCost +
          ", extraItemsWithCost=" + extraItemsWithCost +
          '}';
    }
  }

  final class FreeItemOffer implements Offer, Comparable<FreeItemOffer> {
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

    @Override
    public int compareTo(FreeItemOffer other) {
      if (itemCountWithCost.item != other.itemCountWithCost.item)
        return itemCountWithCost.item - other.itemCountWithCost.item;

      return itemCountWithCost.count - other.itemCountWithCost.count;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof FreeItemOffer)) return false;
      FreeItemOffer that = (FreeItemOffer) o;
      return freeCount == that.freeCount &&
          Objects.equals(itemCountWithCost, that.itemCountWithCost);
    }

    @Override
    public int hashCode() {
      return Objects.hash(itemCountWithCost, freeCount);
    }

    @Override
    public String toString() {
      return "FreeItemOffer{" +
          "itemCountWithCost=" + itemCountWithCost +
          ", freeCount=" + freeCount +
          '}';
    }
  }
}




