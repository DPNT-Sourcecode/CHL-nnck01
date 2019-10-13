package befaster.solutions.CHK;

import java.util.Objects;

public final class ItemCount {
  public final char item;
  public final int count;

  public ItemCount(char item, int count) {
    this.count = count;
    this.item = item;
  }

  ItemCount times(int times) {
    return ItemCount.by(item, count * times);
  }

  ItemCount plus(int count) {
    return ItemCount.by(item, this.count + count);
  }

  public static ItemCount by(char item, int count) {
    return new ItemCount(item, count);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ItemCount)) return false;
    ItemCount itemCount = (ItemCount) o;
    return item == itemCount.item &&
        count == itemCount.count;
  }

  @Override
  public int hashCode() {
    return Objects.hash(item, count);
  }

  @Override
  public String toString() {
    return "ItemCount{" +
        "count=" + count +
        ", item=" + item +
        '}';
  }
}
