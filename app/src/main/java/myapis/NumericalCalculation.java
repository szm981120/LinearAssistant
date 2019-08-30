package myapis;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class NumericalCalculation {

  public NumericalCalculation() {

  }

  public boolean isNumerical(String s) {
    Pattern pattern = Pattern.compile("[0-9|\\s|\\n|\\.|-]*");
    return pattern.matcher(s).matches();
  }

  public double bigDecimalAdd(double d1, double d2, int accuracy) {
    BigDecimal bd1 = BigDecimal.valueOf(d1);
    BigDecimal bd2 = BigDecimal.valueOf(d2);
    return bd1.add(bd2).setScale(accuracy, BigDecimal.ROUND_HALF_UP).doubleValue();
  }

  public double bigDecimalSub(double d1, double d2, int accuracy) {
    BigDecimal bd1 = BigDecimal.valueOf(d1);
    BigDecimal bd2 = BigDecimal.valueOf(d2);
    return bd1.subtract(bd2).setScale(accuracy, BigDecimal.ROUND_HALF_UP).doubleValue();
  }

  public double bigDecimalMul(double d1, double d2, int accuracy) {
    BigDecimal bd1 = BigDecimal.valueOf(d1);
    BigDecimal bd2 = BigDecimal.valueOf(d2);
    return bd1.multiply(bd2).setScale(accuracy, BigDecimal.ROUND_HALF_UP).doubleValue();
  }

  public double bigDecimalDiv(double d1, double d2, int accuracy) {
    BigDecimal bd1 = BigDecimal.valueOf(d1);
    BigDecimal bd2 = BigDecimal.valueOf(d2);
    return bd1.divide(bd2, accuracy, BigDecimal.ROUND_HALF_UP).doubleValue();
  }
}
