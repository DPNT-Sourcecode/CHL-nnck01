package befaster.solutions.CHK;

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
  public String toString() {
    return "ItemCount{" +
        "count=" + count +
        ", item=" + item +
        '}';
  }
}
