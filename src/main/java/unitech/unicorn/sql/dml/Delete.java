package unitech.unicorn.sql.dml;

import unitech.unicorn.sql.AbstractCommand;
import unitech.unicorn.sql.search.SearchCriteria;

import java.sql.PreparedStatement;

public class Delete extends AbstractCommand {
    private static final String QUERY = "DELETE FROM ::table::";
    private SearchCriteria criteria;
    private String query;

    private Delete(String query) {
        this(query, null);
    }

    private Delete(String query, SearchCriteria criteria) {
        this.query = query;
        this.criteria = criteria;
    }

    @Override
    public String toString() {
        return query;
    }

    public static SetTableName builder() {
        return new Builder();
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

    public interface SetTableName {
        AddSearchCriteria setTableName(String tableName);
    }

    public interface AddSearchCriteria {
        Delete build();
        Delete build(SearchCriteria criteria);
    }

    public static class Builder implements SetTableName, AddSearchCriteria {
        private String tableName;

        @Override
        public AddSearchCriteria setTableName(String tableName) {
            this.tableName = tableName;
            return this;
        }

        @Override
        public Delete build() {
            String query = Delete.QUERY;
            query = query.replace("::table::", tableName);

            return new Delete(query);
        }

        @Override
        public Delete build(SearchCriteria criteria) {
            String query = Delete.QUERY;
            query = query.replace("::table::", tableName);

            String criteriaQuery = criteria.toString();
            query += " WHERE " + criteriaQuery;

            return new Delete(query, criteria);
        }
    }
}
