package unitech.unicorn.sql.ddl.table;

import unitech.unicorn.sql.AbstractCommand;

public class Drop extends AbstractCommand {
    private static final String QUERY = "DROP TABLE ::EXISTENCE:CHECK:: ::table::";
    private String query;

    private Drop(String query) {
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
        BuildInterface setExistenceChecked(boolean isExistenceChecked);
    }

    public interface BuildInterface {
        Drop build();
    }

    public static class Builder implements
            SetTableNameInterface, SetExistenceCheckedInterface, BuildInterface {
        private String tableName;
        private boolean checkExistence;

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
        public Drop build() {
            String query = Drop.QUERY;
            query = query.replace("::table::", tableName);
            query = query.replace("::EXISTENCE:CHECK::", checkExistence ? "IF EXISTS" : "");

            return new Drop(query);
        }
    }
}
