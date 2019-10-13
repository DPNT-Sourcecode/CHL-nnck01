package befaster.solutions.CHK;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;

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
        .map(s -> s.split("|"))
        .map(this::parseOffer)
        .collect(toList());
  }

  private Offer parseOffer(String[] params) {
    return null;
  }
}
