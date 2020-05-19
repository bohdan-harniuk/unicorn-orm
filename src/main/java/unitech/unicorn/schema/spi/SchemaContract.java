package unitech.unicorn.schema.spi;

import com.sun.jdi.InvalidTypeException;
import unitech.unicorn.schema.dto.ColumnObject;

import java.util.List;

public interface SchemaContract {
    String getTableName();

    boolean isExistenceChecked();

    // TODO: make it db driver independent.
    boolean isWithRowId();

    List<ColumnObject> getColumnsObjects() throws InvalidTypeException;
}
