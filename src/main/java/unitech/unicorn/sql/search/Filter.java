package unitech.unicorn.sql.search;

public class Filter {
    private String field;
    private SearchCriteria.CONDITIONS condition;
    private SearchCriteria.OPERATORS operator;
    private Object value;

    public Filter(String field, Object value, SearchCriteria.CONDITIONS condition) {
        this.field = field;
        this.value = value;
        this.condition = condition;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        // TODO: use ? before smtp will be filled.
        return String.join(" ", new String[]{
                field,
                condition.getSign(),
                SearchCriteria.VALUE_REPLACEMENT
        });
    }
}