package unitech.unicorn.schema.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Text {
    TEXT_SIZES size() default TEXT_SIZES.TINYTEXT;

    public enum TEXT_SIZES {
        TINYTEXT(255),
        TEXT(65_535),
        MEDIUMTEXT(16_777_215),
        LONGTEXT(4_294_967_295L);

        private long size;

        TEXT_SIZES(long size) {
            this.size = size;
        }

        public long getSize() {
            return size;
        }
    }
}
