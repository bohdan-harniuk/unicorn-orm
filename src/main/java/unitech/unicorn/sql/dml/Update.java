package unitech.unicorn.sql.dml;

import unitech.unicorn.sql.search.SearchCriteria;

import java.sql.PreparedStatement;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Update {
    private static final String QUERY = "UPDATE ::table:: SET ::value:map:: WHERE ::criteria::";
    private static final String VALUE_REPLACEMENT = "?";
    private SearchCriteria criteria;
    private List<Object> bindQueue;
    private String query;

    private Update(String query, List<Object> bindQueue, SearchCriteria criteria) {
        this.query = query;
        this.bindQueue = bindQueue;
        this.criteria = criteria;

        this.bindQueue.addAll(criteria.getBindQueue());
    }

    @Override
    public String toString() {
        return query;
    }

    // TODO: refactor this. To much duplicates.
    public void fillStatement(PreparedStatement smtp) throws Exception {
        int increment = 1;

        if (bindQueue.isEmpty()) {
            throw new Exception("There are no values to prepare in statement");
        }

        if (criteria.getBindQueue().isEmpty()) {
            throw new Exception("There are no search criteria values to identify target record.");
        }

        for (Object value : bindQueue) {
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

    public static SetTableNameInterface builder() {
        return new Builder();
    }

    public interface SetTableNameInterface {
        SetValueInterface setTableName(String tableName);
    }

    public interface SetValueInterface {
        AddSearchCriteriaInterface setValue(String columnName, Object value);
        AddSearchCriteriaInterface addValueMap(Map<String, Object> columnValueMap);
    }

    public interface AddSearchCriteriaInterface {
        AddSearchCriteriaInterface setValue(String columnName, Object value);
        BuildInterface addSearchCriteria(SearchCriteria criteria);
    }

    public interface BuildInterface {
        Update build();
    }

    public static class Builder implements
            SetTableNameInterface, SetValueInterface, AddSearchCriteriaInterface, BuildInterface {
        private String tableName;
        private Map<String, Object> valueMap;
        private SearchCriteria searchCriteria;
        private List<Object> bindQueue;

        public Builder() {
            valueMap = new LinkedHashMap<>();
            bindQueue = new LinkedList<>();
        }

        @Override
        public SetValueInterface setTableName(String tableName) {
            this.tableName = tableName;
            return this;
        }

        @Override
        public AddSearchCriteriaInterface setValue(String columnName, Object value) {
            valueMap.put(columnName, value);
            return this;
        }

        @Override
        public AddSearchCriteriaInterface addValueMap(Map<String, Object> columnValueMap) {
            valueMap.putAll(columnValueMap);
            return this;
        }

        @Override
        public BuildInterface addSearchCriteria(SearchCriteria criteria) {
            searchCriteria = criteria;
            return this;
        }

        @Override
        public Update build() {
            String query = Update.QUERY;
            query = query.replace("::table::", tableName);

            List<String> columnValueMapList = new LinkedList<>();

            for (Map.Entry<String, Object> map : valueMap.entrySet()) {
                String value = map.getKey() + " = " + VALUE_REPLACEMENT;
                columnValueMapList.add(value);
                bindQueue.add(map.getValue());
            }

            query = query.replace("::value:map::", String.join(", ", columnValueMapList));
            query = query.replace("::criteria::", searchCriteria.toString());

            return new Update(query, bindQueue, searchCriteria);
        }
    }
}
