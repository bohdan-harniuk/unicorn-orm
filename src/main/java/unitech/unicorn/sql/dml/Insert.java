package unitech.unicorn.sql.dml;

import unitech.unicorn.sql.AbstractCommand;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Insert extends AbstractCommand {
    private static final String QUERY = "INSERT INTO ::table:: (::columns::) VALUES ::value:query::";
    private static final String VALUE_QUERY = "(::values::)";

    private String query;
    private List<List<Object>> values;

    private Insert(String query, List<List<Object>> values) {
        this.query = query;
        this.values = values;
    }

    @Override
    public String toString() {
        return query;
    }

    public void fillStatement(PreparedStatement smtp) throws SQLException {
        int increment = 1;

        for (List<Object> values : this.values) {
            for (Object value : values) {
                if (value instanceof Integer) {
                    smtp.setInt(increment, (int) value);
                }
                if (value instanceof Double) {
                    smtp.setDouble(increment, (double) value);
                }
                if (value instanceof String) {
                    smtp.setString(increment, (String) value);
                }
                if (value instanceof Boolean) {
                    smtp.setBoolean(increment, (boolean) value);
                }

                increment++;
            }
        }
    }

    private static String joinColumnNames(List<String> columns) {
        return String.join(", ", columns);
    }

    private static String joinValues(List<List<Object>> valuesList) {
        StringBuilder valuesQuery = new StringBuilder();

        for (List<Object> values : valuesList) {
            StringBuilder valueQuery = new StringBuilder();
            if (valuesQuery.length() > 0) {
                valuesQuery.append(", ");
            }

            for (Object value : values) {
                if (valueQuery.length() > 0) {
                    valueQuery.append(", ");
                }
                valueQuery.append("?");
//                valueQuery.append(value);
            }

            String valueString = VALUE_QUERY.replace("::values::", valueQuery.toString());
            valuesQuery.append(valueString);
        }

        return valuesQuery.toString();
    }

    public static SetTableName builder() {
        return new Builder();
    }

    public interface SetTableName {
        SetColumns setTableName(String tableName);
    }

    public interface SetColumns {
        AddValues setColumns(String... columns);
        AddValues setColumns(List<String> columns);
    }

    public interface AddValues {
        AddValues addValues(Object... values);
        AddValues addValues(List<Object> values);
        Insert build();
    }

    public static class Builder implements SetTableName, SetColumns, AddValues {
        private String tableName;
        private List<String> columns;
        private List<List<Object>> values;

        Builder() {
            columns = new ArrayList<>();
            values = new ArrayList<>();
        }

        @Override
        public Insert build() {
            String query = Insert.QUERY;

            query = query
                    .replace("::table::", tableName)
                    .replace("::columns::", joinColumnNames(columns))
                    .replace("::value:query::", joinValues(values));

            return new Insert(query, values);
        }

        @Override
        public SetColumns setTableName(String tableName) {
            this.tableName = tableName;
            return this;
        }

        @Override
        public AddValues setColumns(String... columns) {
            this.columns.addAll(new LinkedList<>(Arrays.asList(columns)));

            return this;
        }

        @Override
        public AddValues setColumns(List<String> columns) {
            this.columns.addAll(new LinkedList<>(columns));

            return this;
        }

        @Override
        public AddValues addValues(Object... values) {
            this.values.add(new LinkedList<>(Arrays.asList(values)));
            return this;
        }

        @Override
        public AddValues addValues(List<Object> values) {
            this.values.add(new LinkedList<>(values));
            return this;
        }
    }
}
