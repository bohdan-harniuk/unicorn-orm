package unitech.unicorn.sql.ddl.table;

import unitech.unicorn.sql.AbstractCommand;

public class Truncate extends AbstractCommand {
    private static final String QUERY = "DELETE FROM ::table::";
    private String query;

    private Truncate(String query) {
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
        BuildInterface setTableName(String tableName);
    }

    public interface BuildInterface {
        Truncate build();
    }

    public static class Builder implements SetTableNameInterface, BuildInterface {
        private String tableName;

        @Override
        public Builder setTableName(String tableName) {
            this.tableName = tableName;
            return this;
        }

        @Override
        public Truncate build() {
            String query = Truncate.QUERY;
            query = query.replace("::table::", tableName);

            return new Truncate(query);
        }
    }
}
