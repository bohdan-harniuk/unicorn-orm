package unitech.unicorn.schema.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Integer {
    INTEGER_SIZES size() default INTEGER_SIZES.INT;

    int width() default 10;

    public enum INTEGER_SIZES {
        TINYINT(-128, 127),
        SMALLINT(-32_768, 32_767),
        MEDIUMINT(-8_388_608, 8_388_607),
        INT(-2_147_483_648, 2_147_483_647);

        private int min;
        private int max;

        INTEGER_SIZES(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public int getMin() {
            return min;
        }

        public int getMax() {
            return max;
        }
    }
}
