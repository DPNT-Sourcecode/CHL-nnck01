package befaster.solutions.CHK;

import java.util.Objects;

public final class ItemsCountWithCost {
  public final char item;
  public final int count;
  public final int cost;

  private ItemsCountWithCost(char item, int count, int cost) {
    this.item = item;
    this.count = count;
    this.cost = cost;
  }

  public ItemCount toItemsCount() {
    return ItemCount.by(item, count);
  }

  public ItemsCountWithCost times(int times) {
    return ItemsCountWithCost.by(item, count * times, cost * times);
  }

  public static ItemsCountWithCost by(char item, int count, int cost) {
    return new ItemsCountWithCost(item, count, cost);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ItemsCountWithCost)) return false;
    ItemsCountWithCost that = (ItemsCountWithCost) o;
    return item == that.item &&
        count == that.count &&
        cost == that.cost;
  }

  @Override
  public int hashCode() {
    return Objects.hash(item, count, cost);
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
