package unitech.unicorn.sql.ddl.table;

import unitech.unicorn.schema.dto.ColumnObject;
import unitech.unicorn.sql.AbstractCommand;

import java.util.LinkedList;
import java.util.List;

public class Create extends AbstractCommand {
    private static final String QUERY = "CREATE TABLE ::EXISTENCE:CHECK:: ::table:: (::COLUMNS::) ::WITH:ROW:ID::";
    private String query;

    private Create(String query) {
        this.query = query;
    }

    @Override
    public String toString() {
        return query;
    }

    public static SetTableNameInterface builder() {
        return new Builder();
    }

    public interface SetTableNameInterface {
        SetExistenceCheckedInterface setTableName(String tableName);
    }

    public interface SetExistenceCheckedInterface {
        SetColumnsInterface setExistenceChecked(boolean isExistenceChecked);
    }

    public interface SetColumnsInterface {
        SetWithRowIdInterface setColumns(List<ColumnObject> columns);
    }

    public interface SetWithRowIdInterface {
        BuildInterface setWithRowId(boolean isWithRowId);
    }

    public interface BuildInterface {
        Create build();
    }

    public static class Builder implements
            SetTableNameInterface,
            SetExistenceCheckedInterface,
            SetColumnsInterface,
            SetWithRowIdInterface,
            BuildInterface {
        private String tableName;
        private boolean checkExistence;
        private List<ColumnObject> columnObjectList;
        // TODO: make it db driver independent.
        private boolean withRowId;

        @Override
        public Builder setTableName(String tableName) {
            this.tableName = tableName;
            return this;
        }

        @Override
        public Builder setExistenceChecked(boolean isExistenceChecked) {
            this.checkExistence = isExistenceChecked;
            return this;
        }

        @Override
        public SetWithRowIdInterface setColumns(List<ColumnObject> columns) {
            columnObjectList = new LinkedList<>(columns);
            return this;
        }

        @Override
        public Builder setWithRowId(boolean isWithRowId) {
            this.withRowId = isWithRowId;
            return this;
        }

        @Override
        public Create build() {
            String query = Create.QUERY;
            query = query.replace("::table::", tableName);
            query = query.replace("::EXISTENCE:CHECK::", checkExistence ? "IF NOT EXISTS" : "");
            query = query.replace("::WITH:ROW:ID::", withRowId ? "" : "WITHOUT ROWID");

            String[] columnStringArray = new String[columnObjectList.size()];
            int columnIndex = 0;

            for (ColumnObject column : columnObjectList) {
                columnStringArray[columnIndex++] = column.toString();
            }
            String columns = String.join(", ", columnStringArray);
            query = query.replace("::COLUMNS::", columns);

            return new Create(query);
        }
    }
}
