package unitech.unicorn.schema;

import com.sun.jdi.InvalidTypeException;
import unitech.unicorn.ConnectionManger;
import unitech.unicorn.exception.SchemaAccessException;
import unitech.unicorn.schema.dto.ColumnObject;
import unitech.unicorn.schema.spi.SchemaContract;
import unitech.unicorn.sql.ddl.table.Create;
import unitech.unicorn.sql.ddl.table.Drop;
import unitech.unicorn.sql.ddl.table.Truncate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class SchemaManager {
    private Connection connection;
    private SchemaContract contractor;

    public SchemaManager(SchemaContract contractor) {
        this.contractor = contractor;
    }

    public void createTable() throws InvalidTypeException {
        String tableName = contractor.getTableName();
        boolean isExistenceChecked = contractor.isExistenceChecked();
        boolean withRowId = contractor.isWithRowId();
        List<ColumnObject> columns = contractor.getColumnsObjects();

        Create query = Create.builder()
                .setTableName(tableName)
                .setExistenceChecked(isExistenceChecked)
                .setColumns(columns)
                .setWithRowId(withRowId)
                .build();

        try {
            open();
            PreparedStatement smtp = connection.prepareStatement(query.toString());
            smtp.execute();
        } catch (SQLException e) {
            throw new SchemaAccessException(
                    String.format("Cannot create the table `%s`. Error was: %s", tableName, e.getMessage())
            );
        } finally {
            close();
        }
    }

    public void dropTable() {
        String tableName = contractor.getTableName();
        boolean isExistenceChecked = contractor.isExistenceChecked();

        Drop query = Drop.builder()
                .setTableName(tableName)
                .setExistenceChecked(isExistenceChecked)
                .build();

        try {
            open();
            PreparedStatement smtp = connection.prepareStatement(query.toString());
            smtp.execute();
        } catch (SQLException e) {
            throw new SchemaAccessException(
                    String.format("Cannot delete the table `%s`. Error was: %s", tableName, e.getMessage())
            );
        } finally {
            close();
        }
    }

    public int truncateTable() {
        String tableName = contractor.getTableName();

        Truncate query = Truncate.builder()
                .setTableName(tableName)
                .build();

        try {
            open();
            PreparedStatement smtp = connection.prepareStatement(query.toString());

            return smtp.executeUpdate();
        } catch (SQLException e) {
            throw new SchemaAccessException(
                    String.format("Cannot truncate the table `%s`. Error was: %s", tableName, e.getMessage())
            );
        } finally {
            close();
        }
    }

    private void open() {
        connection = ConnectionManger.getConnection();
    }

    private void close() {
        ConnectionManger.releaseConnection(connection);
    }
}
