package unitech.unicorn.sql.search;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class FilterGroup {
    private List<Filter> filters;
    private List<Object> values;

    public FilterGroup() {
        filters = new LinkedList<>();
        values = new LinkedList<>();
    }

    public FilterGroup(Filter... filter) {
        filters = new LinkedList<>();
        values = new LinkedList<>();
        this.filters.addAll(Arrays.asList(filter));
    }

    public List<Filter> getFilters() {
        return new LinkedList<>(filters);
    }

    public FilterGroup addFilter(Filter... filter) {
        this.filters.addAll(Arrays.asList(filter));
        return this;
    }

    public List<Object> getValues() {
        return new LinkedList<>(values);
    }

    @Override
    public String toString() {
        StringBuilder filterGroup = new StringBuilder();
        int increment = 0;

        if (!filters.isEmpty() && filters.size() >= 2) {
            filterGroup.append("(");
        }

        for (Filter filter : filters) {
            if (increment > 0) {
                filterGroup
                        .append(" ")
                        .append(SearchCriteria.OPERATORS.OR.toString())
                        .append(" ");
            }
            filterGroup.append(filter.toString());
            values.add(filter.getValue());

            increment++;
        }

        if (!filters.isEmpty() && filters.size() >= 2) {
            filterGroup.append(")");
        }

        return filterGroup.toString();
    }
}