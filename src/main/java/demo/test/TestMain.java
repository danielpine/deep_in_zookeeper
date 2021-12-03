package demo.test;

import com.google.common.base.Preconditions;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class TestMain {
    @CanIgnoreReturnValue
    public static void main(String[] args) {
        System.out.println(BigDecimal.valueOf(23.39556).setScale(2, RoundingMode.HALF_UP));
        BigDecimal.valueOf(23.39556).divide(BigDecimal.TEN);
        pow();
    }
    @CanIgnoreReturnValue
    public static BigDecimal pow(){
        return BigDecimal.TEN;
    }
}

