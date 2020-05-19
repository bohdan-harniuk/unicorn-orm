package unitech.unicorn.sql.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SortOrder {
    private List<String> fields;
    private SearchCriteria.SORT_ORDER sortOrder;
    private static final SearchCriteria.SORT_ORDER DEFAULT_ORDER = SearchCriteria.SORT_ORDER.ASC;
    public static final String SORT_ORDER_KEYWORD = "ORDER BY";

    public SortOrder(String... fields) {
        this.fields = new ArrayList<>(Arrays.asList(fields));
        this.sortOrder = DEFAULT_ORDER;
    }

    public SortOrder desc() {
        sortOrder = SearchCriteria.SORT_ORDER.DESC;
        return this;
    }

    public SortOrder asc() {
        sortOrder = SearchCriteria.SORT_ORDER.ASC;
        return this;
    }

    @Override
    public String toString() {
        return String.join(", ", fields) + " " + sortOrder.toString();
    }
}