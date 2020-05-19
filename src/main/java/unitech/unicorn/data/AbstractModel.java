package unitech.unicorn.data;

import unitech.unicorn.schema.annotation.Column;
import unitech.unicorn.schema.annotation.PRIMARY;
import unitech.unicorn.schema.annotation.Table;

import java.lang.reflect.Field;
import java.util.*;

public abstract class AbstractModel {
    private String tableName;
    private Map<String, ColumnData> columns = new HashMap<>();
    private DataObject dataObject;
    // TODO: add inspection of changed data.
    private DataObject originData;

    @PRIMARY
    @Column(name = "_id", nullable = false)
    private Integer id = null;

    protected AbstractModel() {
        resolveTableName();
        resolveColumns();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTableName() {
        return tableName;
    }

    public List<String> getColumns() {
        return new ArrayList<>(columns.keySet());
    }

    public String getPrimaryKeyName() throws Exception {
        for (Map.Entry<String, ColumnData> entry : columns.entrySet()) {
            if (isColumnPK(entry.getKey())) {
                return entry.getKey();
            }
        }

        throw new Exception(String.format(
                "Primary key field must be specified in annotations for model class `%s`",
                this.getClass())
        );
    }

    private void resolveTableName() {
        Class<? extends AbstractModel> cls = this.getClass();
        Table table = cls.getAnnotation(Table.class);

        if (table != null) {
            tableName = table.name();
        }
    }

    private void resolveColumns() {
        Class<? extends AbstractModel> cls = this.getClass();
        List<Field> fields = new ArrayList<>(Arrays.asList(AbstractModel.class.getDeclaredFields()));
        fields.addAll(Arrays.asList(cls.getDeclaredFields()));

        for (Field field : fields) {
            if (field.getAnnotation(Column.class) != null) {
                String columnName = field.getAnnotation(Column.class).name();
                columns.put(columnName, new ColumnData(columnName, field));
            }
        }
    }

    public boolean setFieldByColumnName(String columnName, Object value) {
        if (columns.containsKey(columnName)) {
            Field field = columns.get(columnName).getField();
            boolean accessible = field.isAccessible();
//            boolean accessible = field.canAccess(this);
            field.setAccessible(true);

            try {
                field.set(this, value);
            } catch (IllegalAccessException exception) {
                // Skip it.
            } finally {
                field.setAccessible(accessible);
            }

            return true;
        }
        return false;
    }

    protected boolean isColumnPK(String columnName) {
        Field columnField = this.columns.get(columnName).getField();

        return columnField.getAnnotation(PRIMARY.class) != null;
    }

    public DataObject getData() {
        if (dataObject == null) {
            dataObject = new DataObject();
        }

        for (Map.Entry<String, ColumnData> columnDataEntry : this.columns.entrySet()) {
            Field field = columnDataEntry.getValue().getField();
            boolean accessible = field.isAccessible();
            field.setAccessible(true);

            try {
                Object value = field.get(this);
                if (value == null) {
                    continue;
                }
                dataObject.setValue(columnDataEntry.getKey(), field.get(this));
            } catch (IllegalAccessException exception) {
                // Skip it.
            } finally {
                field.setAccessible(accessible);
            }
        }

        return dataObject;
    }

    private void addLoadedValue(String columnName, Object value) {
        if (originData == null) {
            originData = new DataObject();
        }

        originData.setValue(columnName, value);
    }

    public Map<String, Object> getChangedDataMap() {
        Map<String, Object> map = null;
        DataObject currentDataObject = this.getData();

        for (String column : this.getColumns()) {
            if (isColumnDataChanged(column)) {
                if (map == null) {
                    map = new LinkedHashMap<>();
                }

                map.put(column, currentDataObject.getValue(column));
            }
        }

        return map;
    }

    protected boolean isColumnDataChanged(String columnName) {
        boolean changed = false;

        Object columnData = this.dataObject.getValue(columnName);
        Object originData = this.originData.getValue(columnName);

        if (columnData != null && originData != null) {
            if (!columnData.equals(originData)) {
                changed = true;
            }
        }

        return changed;
    }

    public static class DataObject {
        private Map<String, Object> data;

        DataObject() {
            data = new LinkedHashMap<>();
        }

        public void setValue(String column, Object value) {
            if (value instanceof Integer) {
                data.put(column, (Integer) value);
            } else if (value instanceof Double) {
                data.put(column, (Double) value);
            } else if (value instanceof Boolean) {
                data.put(column, (Boolean) value);
            } else if (value instanceof String) {
                data.put(column, (String) value);
            } else {
                throwSetIllegalArgumentException(value);
            }
        }

        public Object getValue(String column) {
            return data.getOrDefault(column, null);
        }

        public List<String> getData() {
            return new LinkedList<>(data.keySet());
        }

        public List<Object> getValues() {
            return new LinkedList<>(data.values());
        }

        public void throwSetIllegalArgumentException(Object o) {
            throw new IllegalArgumentException(
                    String.format("Cannot set value `%s` of type `%s`", o, o.getClass().getName())
            );
        }
    }

    private static class ColumnData {
        private String name;
        private Field field;

        ColumnData(String name, Field field) {
            this.name = name;
            this.field = field;
        }

        String getName() {
            return name;
        }

        Field getField() {
            return field;
        }
    }
}
