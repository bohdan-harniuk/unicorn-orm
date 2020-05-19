package unitech.unicorn.sql.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SearchCriteria {
    private List<Filter> filters;
    private List<FilterGroup> filterGroups;
    private List<SortOrder> orders;
    private List<Object> bindQueue;
    private String query;

    static final String VALUE_REPLACEMENT = "?";

    public enum CONDITIONS {
        EQUAL_TO("="),
        LIKE("LIKE"),
        NOT_EQUAL_TO("<>"),
        GREATER_THAN(">"),
        LESS_THAN("<"),
        GREATER_THAN_OR_EQUAL_TO(">="),
        LESS_THAN_OR_EQUAL_TO("<=");

        private final String sign;

        CONDITIONS(String sign) {
            this.sign = sign;
        }

        public String getSign() {
            return sign;
        }
    }

    public enum OPERATORS {
        AND,
        IN,
        LIKE,
        NOT,
        OR
    }

    public enum SORT_ORDER {
        ASC,
        DESC
    }

    public SearchCriteria() {
        filters = new LinkedList<>();
        filterGroups = new LinkedList<>();
        orders = new ArrayList<>();
        bindQueue = new LinkedList<>();
    }

    public List<Object> getBindQueue() {
        return new LinkedList<>(bindQueue);
    }

    public void addFilterGroup(FilterGroup... filterGroup) {
        filterGroups.addAll(Arrays.asList(filterGroup));
    }

    public void addFilter(Filter... filter) {
        filters.addAll(Arrays.asList(filter));
    }

    public void addSortOrder(SortOrder... order) {
        orders.addAll(Arrays.asList(order));
    }

    public String build() {
        StringBuilder criteria = new StringBuilder();
        int increment = 0;

        for (FilterGroup group : this.filterGroups) {
            if (increment > 0) {
                criteria.append(" ")
                        .append(OPERATORS.AND)
                        .append(" ");
            }
            criteria.append(group.toString());
            bindQueue.addAll(group.getValues());

            increment++;
        }

        for (Filter filter : filters) {
            if (increment > 0) {
                criteria.append(" ")
                        .append(OPERATORS.AND)
                        .append(" ");
            }
            criteria.append(filter.toString());
            bindQueue.add(filter.getValue());
            increment++;
        }

        if (!orders.isEmpty()) {
            String[] sortOrders = new String[orders.size()];
            int sortOrderIndex = 0;

            for (SortOrder sortOrder : orders) {
                sortOrders[sortOrderIndex++] = sortOrder.toString();
            }

            criteria
                    .append(" ")
                    .append(SortOrder.SORT_ORDER_KEYWORD)
                    .append(" ")
                    .append(String.join(", ", sortOrders));
        }

        query = criteria.toString();

        return query;
    }

    @Override
    public String toString() {
        if (query == null) {
            return build();
        }

        return query;
    }
}
