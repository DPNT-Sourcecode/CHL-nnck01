package befaster.solutions.CHK;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
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
        .collect(toList());
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
          if (line.contains("for")) {
            return parseDiscountOffer(item, line);
          }

          if (line.contains("get one")) {
            return parseExtraOrFreeOffer(item, usualOffer.cost, line);
          }

          return null;
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
}



