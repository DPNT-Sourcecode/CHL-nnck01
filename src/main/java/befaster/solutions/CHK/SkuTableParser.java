package befaster.solutions.CHK;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.*;

public class SkuTableParser {

  public List<Offer> parse(String skuTable) {
    if (isBlank(skuTable)) throw new IllegalArgumentException("Invalid table format");

    final List<String> lines = Arrays.asList(skuTable.split("\n"));
    if (lines.size() < 3) throw new IllegalArgumentException("Invalid table length");

    if (!lines.get(0).equals("+------+-------+------------------------+") ||
        !lines.get(1).equals("| Item | Price | Special offers         |"))
        throw new IllegalArgumentException("Invalid table header");

    if (!lines.get(1).equals("| Item | Price | Special offers         |"))
        throw new IllegalArgumentException("Invalid table header");
  }
}


