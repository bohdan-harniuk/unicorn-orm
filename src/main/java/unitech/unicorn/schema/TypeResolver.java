package unitech.unicorn.schema;

import com.sun.jdi.InvalidTypeException;
import unitech.unicorn.exception.EntityAnnotatingException;
import unitech.unicorn.schema.annotation.Integer;
import unitech.unicorn.schema.annotation.Text;

import java.lang.reflect.Field;

public class TypeResolver {
    public static String resolve(Field field, Text.TEXT_SIZES size) throws InvalidTypeException {
        Class<?> classType = field.getType();
        String type = null;

        if (classType != String.class) {
            throw new InvalidTypeException(
                    String.format(
                            "Cannot resolve type. Only `%s` type can be resolved to sql Text type",
                            String.class.getTypeName()
                    )
            );
        }

        if (size == Text.TEXT_SIZES.TINYTEXT) {
            type = Text.TEXT_SIZES.TINYTEXT.toString();
        } else if (size == Text.TEXT_SIZES.TEXT) {
            type = Text.TEXT_SIZES.TEXT.toString();
        } else if (size == Text.TEXT_SIZES.MEDIUMTEXT) {
            type = Text.TEXT_SIZES.MEDIUMTEXT.toString();
        } else if (size == Text.TEXT_SIZES.LONGTEXT) {
            type = Text.TEXT_SIZES.LONGTEXT.toString();
        }

        return type;
    }

    public static String resolve(Field field, Integer.INTEGER_SIZES size, int width) throws InvalidTypeException {
        Class<?> classType = field.getType();
        String type = null;

        if (classType != Integer.class || classType.getName().equals("int")) {
            throw new InvalidTypeException(
                    String.format(
                            "Cannot resolve type. Only `%s` or `int` primitive " +
                                    "type can be resolved to sql integer types",
                            Integer.class.getTypeName()
                    )
            );
        }

        if (size == Integer.INTEGER_SIZES.TINYINT) {
            type = Integer.INTEGER_SIZES.TINYINT.toString();
        } else if (size == Integer.INTEGER_SIZES.SMALLINT) {
            type = Integer.INTEGER_SIZES.SMALLINT.toString();
        } else if (size == Integer.INTEGER_SIZES.MEDIUMINT) {
            type = Integer.INTEGER_SIZES.MEDIUMINT.toString();
        } else if (size == Integer.INTEGER_SIZES.INT) {
            type = Integer.INTEGER_SIZES.INT.toString();
        }

        if (type == null) {
            throw new EntityAnnotatingException("Cannot resolve integer type for entity");
        }

        type += "(" + width + ")";

        return type;
    }

    public static String resolve(Field field, int size, int scale) throws InvalidTypeException {
        Class<?> classType = field.getType();
        String type = null;

        if (classType == String.class) {
            if (size <= 65_535) {
                type = "VARCHAR";
                type += "(" + size + ")";
            } else if (size <= Text.TEXT_SIZES.MEDIUMTEXT.getSize()) {
                type = "MEDIUMTEXT";
            } else if (size <= Text.TEXT_SIZES.LONGTEXT.getSize()) {
                type = "LONGTEXT";
            }
        } else if (classType.getName().equals("int") || classType == java.lang.Integer.class) {
            if (size <= 255) {
                type = "INT(" + size + ")";
            } else {
                throw new InvalidTypeException("The maximum width of integer values in SQL lang is 255");
            }
        } else if (classType.getName().equals("float") ||
                classType == Float.class ||
                classType.getName().equals("double") ||
                classType == Double.class) {
            type = "DECIMAL(" + size + ", " + scale + ")";
        } else if (classType.getName().equals("boolean") || classType == Boolean.class) {
            type = "BOOLEAN";
        }

        // TODO: resolve datetime types.

        return type;
    }

    public static String resolve(Field field) throws InvalidTypeException {
        Class<?> classType = field.getType();
        int defaultSize = 0;
        int defaultScale = 0;

        if (classType == String.class) {
            defaultSize = 255;
        } else if (classType.getName().equals("int") || classType == java.lang.Integer.class) {
            defaultSize = 10;
        } else if (classType.getName().equals("float") ||
                classType == Float.class ||
                classType.getName().equals("double") ||
                classType == Double.class) {
            defaultSize = 10;
            defaultScale = 2;
        }

        return resolve(field, defaultSize, defaultScale);
    }
}
