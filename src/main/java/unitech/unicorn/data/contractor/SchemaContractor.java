package unitech.unicorn.data.contractor;

import com.sun.jdi.InvalidTypeException;
import unitech.unicorn.data.AbstractModel;
import unitech.unicorn.exception.EntityAnnotatingException;
import unitech.unicorn.schema.TypeResolver;
import unitech.unicorn.schema.annotation.Integer;
import unitech.unicorn.schema.annotation.*;
import unitech.unicorn.schema.dto.ColumnObject;
import unitech.unicorn.schema.spi.SchemaContract;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SchemaContractor<T extends AbstractModel> implements SchemaContract {
    private Class<T> cls;

    public SchemaContractor(Class<T> cls) {
        this.cls = cls;
    }

    @Override
    public String getTableName() {
        return getTableAnnotation().name();
    }

    @Override
    public boolean isExistenceChecked() {
        return getTableAnnotation().checkExistence();
    }

    @Override
    public boolean isWithRowId() {
        return getTableAnnotation().isWithRowId();
    }

    @Override
    public List<ColumnObject> getColumnsObjects() throws InvalidTypeException {
        List<Field> fieldList = new LinkedList<>(Arrays.asList(AbstractModel.class.getDeclaredFields()));
        List<ColumnObject> columns = new LinkedList<>();

        fieldList.addAll(Arrays.asList(cls.getDeclaredFields()));

        for (Field field : fieldList) {
            Column column = field.getAnnotation(Column.class);

            if (column != null) {
                ColumnObject columnObject = new ColumnObject();
                columnObject.setName(column.name());
                ColumnSize columnSize = field.getAnnotation(ColumnSize.class);
                Text textSize = field.getAnnotation(Text.class);
                Integer integerSize = field.getAnnotation(Integer.class);

                if (columnSize != null && textSize != null) {
                    throw new EntityAnnotatingException("Only one size annotation is allowed");
                }

                if (integerSize != null) {
                    columnObject.setType(TypeResolver.resolve(field, integerSize.size(), integerSize.width()));
                } else if (columnSize != null) {
                    columnObject.setType(TypeResolver.resolve(field, columnSize.size(), columnSize.scale()));
                } else if (textSize != null) {
                    columnObject.setType(TypeResolver.resolve(field, textSize.size()));
                } else {
                    columnObject.setType(TypeResolver.resolve(field));
                }

                PRIMARY primary = field.getAnnotation(PRIMARY.class);

                if (primary != null) {
                    columnObject.setPrimary(true);
                    columnObject.setType("INTEGER");
                    columnObject.setAutoincrement(primary.autoincrement());
                }

                CHECK check = field.getAnnotation(CHECK.class);

                if (check != null) {
                    columnObject.setCheck(check.value());
                }

                columnObject.setNullable(column.nullable());
                columnObject.setUnique(column.unique());
                columnObject.setDefaultValue(column.defaultValue());

                columns.add(columnObject);
            }
        }

        return columns;
    }

    private Table getTableAnnotation() {
        Table tableAnnotation = cls.getAnnotation(Table.class);

        if (tableAnnotation == null) {
            throw new EntityAnnotatingException(String.format("`%s` type annotating is invalid", cls));
        }

        return tableAnnotation;
    }
}
