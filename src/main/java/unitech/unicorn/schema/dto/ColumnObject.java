package unitech.unicorn.schema.dto;

public class ColumnObject {
    private String name;
    private String type;
    private boolean nullable = true;
    private boolean primary = false;
    private boolean unique = false;
    private boolean autoincrement = false;
    private String defaultValue;
    private String check;

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public void setAutoincrement(boolean autoincrement) {
        this.autoincrement = autoincrement;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setCheck(String check) {
        this.check = check;
    }

    @Override
    public String toString() {
        String query = name + " " + type;

        if (!nullable) {
            query += " NOT NULL";
        }

        if (unique) {
            query += " UNIQUE";
        }


        if (primary) {
            query += " PRIMARY KEY";
        }

        if (autoincrement) {
            query += " AUTOINCREMENT";
        }

        if (defaultValue != null && !defaultValue.isEmpty()) {
            query += " DEFAULT " + defaultValue;
        }

        if (check != null && !check.isEmpty()) {
            query += " CHECK (" + check + ")";
        }

        return query;
    }
}
