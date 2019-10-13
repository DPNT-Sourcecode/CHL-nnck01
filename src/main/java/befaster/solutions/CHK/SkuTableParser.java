package befaster.solutions.CHK;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static befaster.solutions.CHK.Offer.*;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trim;
import static org.apache.commons.lang3.math.NumberUtils.createInteger;

public class SkuTableParser {

  public List<Offer> parse(String skuTable) {
    if (isBlank(skuTable)) throw new IllegalArgumentException("Invalid table format");

    final List<String> lines = Arrays.asList(skuTable.split("\n"));
    if (lines.size() < 3) throw new IllegalArgumentException("Invalid table length");

    if (!(
        lines.get(0).equals("+------+-------+------------------------+")
            && lines.get(1).equals("| Item | Price | Special offers         |")
            && lines.get(2).equals("+------+-------+------------------------+")
    )) throw new IllegalArgumentException("Invalid table header");

    if (!lines.get(lines.size() - 1).equals("+------+-------+------------------------+"))
      throw new IllegalArgumentException("Invalid table footer");

    return lines.subList(3, lines.size() - 1)
        .stream()
        .map(s -> s.split("\\|"))
        .flatMap(params -> parseOffer(params).stream())
        .sorted(offerComparator())
        .collect(toList());
  }

  private Comparator<Offer> offerComparator() {
    return (offer1, offer2) -> {

      final boolean isFree1 = offer1 instanceof FreeItemOffer;
      final boolean isFree2 = offer2 instanceof FreeItemOffer;
      if (isFree1 || isFree2) {
        if (isFree1 && isFree2) {
          return ((FreeItemOffer) offer1).compareTo((FreeItemOffer) offer2);
        }

        return isFree2 ? 1 : -1;
      }

      final boolean isExtra1 = offer1 instanceof ExtraItemOffer;
      final boolean isExtra2 = offer2 instanceof ExtraItemOffer;
      if (isExtra1 || isExtra2) {
        if (isExtra1 && isExtra2) {
          return ((ExtraItemOffer) offer1).compareTo((ExtraItemOffer) offer2);
        }

        return isExtra2 ? 1 : -1;
      }

      final boolean isDiscount1 = offer1 instanceof DiscountOffer;
      final boolean isDiscount2 = offer2 instanceof DiscountOffer;
      if (isDiscount1 || isDiscount2) {
        if (isDiscount1 && isDiscount2) {
          return ((DiscountOffer) offer1).compareTo((DiscountOffer) offer2);
        }

        return isDiscount2 ? 1 : -1;
      }

      return ((UsualCost) offer1).compareTo((UsualCost) offer2);
    };
  }

  private List<Offer> parseOffer(String[] params) {
    final char item = trim(params[1]).charAt(0);
    final int price = createInteger(trim(params[2]));

    final String specialOffersLine = trim(params[3]);
    final UsualCost usualOffer = UsualCost.by(ItemCount.by(item, 1), price);
    if (specialOffersLine.isEmpty()) return ImmutableList.of(usualOffer);

    final List<Offer> specialOffers = stream(specialOffersLine.split(","))
        .map(StringUtils::trim)
        .map(line -> {
          if (line.contains("for")) return parseDiscountOffer(item, line);
          else return parseExtraOrFreeOffer(item, usualOffer.cost, line);
        })
        .collect(toList());

    return ImmutableList.<Offer>builder()
        .addAll(specialOffers)
        .add(usualOffer)
        .build();
  }

  private DiscountOffer parseDiscountOffer(char item, String line) {
    final String[] discountLine = line.split("for");
    final String count = discountLine[0].trim();
    final String specialPrice = discountLine[1].trim();
    return DiscountOffer.by(ItemCount.by(item, createInteger(count.substring(0, count.length() - 1))), createInteger(specialPrice));
  }

  private Offer parseExtraOrFreeOffer(char item, int price, String line) {
    final String[] offerLine = line.split("get one");
    final String countLine = offerLine[0].trim();
    final int count = createInteger(countLine.substring(0, countLine.length() - 1));
    final char freeItem = offerLine[1].trim().charAt(0);

    if (item == freeItem) return FreeItemOffer.by(
        ItemsCountWithCost.by(item, count, count * price),
        1
    );

    return ExtraItemOffer.by(
        ItemsCountWithCost.by(item, count, count * price),
        ItemsCountWithCost.by(freeItem, 1, 0)
    );
  }

  public static void main(String[] args) {
    System.out.println(new SkuTableParser()
        .parse("+------+-------+------------------------+\n" +
            "| Item | Price | Special offers         |\n" +
            "+------+-------+------------------------+\n" +
            "| A    | 50    | 3A for 130, 5A for 200 |\n" +
            "| B    | 30    | 2B for 45              |\n" +
            "| C    | 20    |                        |\n" +
            "| D    | 15    |                        |\n" +
            "| E    | 40    | 2E get one B free      |\n" +
            "| F    | 10    | 2F get one F free      |\n" +
            "| G    | 20    |                        |\n" +
            "| H    | 10    | 5H for 45, 10H for 80  |\n" +
            "| I    | 35    |                        |\n" +
            "| J    | 60    |                        |\n" +
            "| K    | 80    | 2K for 150             |\n" +
            "| L    | 90    |                        |\n" +
            "| M    | 15    |                        |\n" +
            "| N    | 40    | 3N get one M free      |\n" +
            "| O    | 10    |                        |\n" +
            "| P    | 50    | 5P for 200             |\n" +
            "| Q    | 30    | 3Q for 80              |\n" +
            "| R    | 50    | 3R get one Q free      |\n" +
            "| S    | 30    |                        |\n" +
            "| T    | 20    |                        |\n" +
            "| U    | 40    | 3U get one U free      |\n" +
            "| V    | 50    | 2V for 90, 3V for 130  |\n" +
            "| W    | 20    |                        |\n" +
            "| X    | 90    |                        |\n" +
            "| Y    | 10    |                        |\n" +
            "| Z    | 50    |                        |\n" +
            "+------+-------+------------------------+"));
  }

}

