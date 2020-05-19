package unitech.unicorn.sql.dml;

import unitech.unicorn.sql.AbstractCommand;
import unitech.unicorn.sql.search.SearchCriteria;

import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Select extends AbstractCommand {
    private static final String QUERY = "SELECT ::columns:: FROM ::table::";
    private SearchCriteria criteria;
    private String query;

    private Select(String query) {
        this(query, null);
    }

    private Select(String query, SearchCriteria criteria) {
        this.query = query;
        this.criteria = criteria;
    }

    @Override
    public String toString() {
        return query;
    }

    public void fillStatement(PreparedStatement smtp) throws Exception {
        int increment = 1;

        if (criteria == null) {
            throw new Exception("Search Criteria is not specified.");
        }

        if (criteria.getBindQueue().isEmpty()) {
            throw new Exception("There are no values to prepare in statement.");
        }

        for (Object value : criteria.getBindQueue()) {
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

    public static SetTableName builder() {
        return new Builder();
    }

    public interface SetTableName {
        AddSearchCriteria setTableName(String tableName);
    }

    public interface AddSearchCriteria {
        AddSearchCriteria addColumns(String... column);
        AddSearchCriteria addColumns(List<String> column);
        Select build();
        Select build(SearchCriteria criteria);
    }


    public static class Builder implements SetTableName, AddSearchCriteria {
        private String tableName;
        private List<String> columns;

        public Builder() {
            this.columns = new LinkedList<>();
        }

        @Override
        public AddSearchCriteria setTableName(String tableName) {
            this.tableName = tableName;
            return this;
        }

        @Override
        public AddSearchCriteria addColumns(String... column) {
            this.columns.addAll(Arrays.asList(column));
            return this;
        }

        @Override
        public AddSearchCriteria addColumns(List<String> column) {
            this.columns.addAll(column);
            return this;
        }

        private String innerBuild() {
            String query = Select.QUERY;
            query = query.replace("::table::", tableName);
            String columns = "*";

            if (!this.columns.isEmpty()) {
                columns = String.join(", ", this.columns);
            }

            query = query.replace("::columns::", columns);

            return query;
        }

        @Override
        public Select build() {
            String query = innerBuild();

            return new Select(query);
        }

        @Override
        public Select build(SearchCriteria criteria) {
            String query = innerBuild();

            String criteriaQuery = criteria.toString();
            query += " WHERE " + criteriaQuery;

            return new Select(query, criteria);
        }
    }
}
