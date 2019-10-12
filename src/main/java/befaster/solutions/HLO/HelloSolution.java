package befaster.solutions.HLO;

import org.apache.commons.lang3.StringUtils;

import static org.apache.commons.lang3.StringUtils.defaultString;

public class HelloSolution {
    public String hello(String friendName) {
        return "Hello, " + defaultString(friendName) + "!";
    }
}
