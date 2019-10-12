package befaster.solutions.CHK;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class CheckoutSolution {
  public Integer checkout(String skus) {
      if (isBlank(skus)) return -1;

      return skus.length();
  }
}

