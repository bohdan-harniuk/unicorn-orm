package unitech.unicorn;

import unitech.unicorn.data.AbstractModel;
import unitech.unicorn.exception.AlreadyExistsException;
import unitech.unicorn.sql.dml.Delete;
import unitech.unicorn.sql.dml.Insert;
import unitech.unicorn.sql.dml.Select;
import unitech.unicorn.sql.dml.Update;
import unitech.unicorn.sql.search.Filter;
import unitech.unicorn.sql.search.SearchCriteria;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.List;
import java.util.Map;

public class EntityManager {
    private Connection connection;

    protected <T extends AbstractModel> boolean update(T object) {
        try {
            open();

            Map<String, Object> changedDataMap = object.getChangedDataMap();

            if (changedDataMap != null) {
                SearchCriteria identifyByIdCriteria = new SearchCriteria();
                identifyByIdCriteria.addFilter(
                        new Filter(
                                object.getPrimaryKeyName(),
                                object.getId(),
                                SearchCriteria.CONDITIONS.EQUAL_TO
                        )
                );

                Update query = Update.builder()
                        .setTableName(object.getTableName())
                        .addValueMap(changedDataMap)
                        .addSearchCriteria(identifyByIdCriteria)
                        .build();

                PreparedStatement smtp = connection.prepareStatement(query.toString());
                query.fillStatement(smtp);

                int affectedRows = smtp.executeUpdate();

                if (affectedRows == 1) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }

        return false;
    }

    public <T extends AbstractModel> boolean save(T object) {
        if (object.getId() != null) {
            return update(object);
        }
        try {
            open();

            AbstractModel.DataObject dataObject = object.getData();

            Insert query = Insert.builder()
                    .setTableName(object.getTableName())
                    .setColumns(dataObject.getData())
                    .addValues(dataObject.getValues())
                    .build();

            PreparedStatement smtp = connection.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS);
            query.fillStatement(smtp);

            int affectedRows = smtp.executeUpdate();

            if (affectedRows == 1) {
                ResultSet generatedKeys = smtp.getGeneratedKeys();

                if (generatedKeys.next()) {
                    object.setId(generatedKeys.getInt(1));
                }

                return true;
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 19) {
                throw new AlreadyExistsException(e.getMessage());
            } else {
                e.printStackTrace();
            }
        } finally {
            close();
        }

        return false;
    }

    public <T extends AbstractModel> boolean delete(T object) {
        try {
            open();

            if (object.getId() == null) {
                throw new NullPointerException(
                        String.format("Identity field `%s` is null", object.getPrimaryKeyName())
                );
            }

            AbstractModel.DataObject dataObject = object.getData();

            SearchCriteria identifyByIdCriteria = new SearchCriteria();
            identifyByIdCriteria.addFilter(
                    new Filter(
                            object.getPrimaryKeyName(),
                            object.getId(),
                            SearchCriteria.CONDITIONS.EQUAL_TO
                    )
            );

            Delete query = Delete.builder()
                    .setTableName(object.getTableName())
                    .build(identifyByIdCriteria);

            PreparedStatement smtp = connection.prepareStatement(query.toString());
            query.fillStatement(smtp);

            int affectedRows = smtp.executeUpdate();

            if (affectedRows == 1) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }

        return false;
    }

    public <T extends AbstractModel> T findOne(Class<T> type, int id) {
        try {
            open();

            T object = (T) type.newInstance();
            String tableName = object.getTableName();

            SearchCriteria getByIdCriteria = new SearchCriteria();
            getByIdCriteria.addFilter(
                    new Filter(object.getPrimaryKeyName(), id, SearchCriteria.CONDITIONS.EQUAL_TO)
            );

            Select query = Select.builder()
                    .setTableName(tableName)
                    .build(getByIdCriteria);

            PreparedStatement smtp = connection.prepareStatement(query.toString());
            query.fillStatement(smtp);

            ResultSet resultSet = smtp.executeQuery();

            fillObject(resultSet, object);

            return object;
        } catch (InstantiationException | IllegalAccessException exception) {
            System.out.println("Cannot instantiate class: " + exception.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }

        return null;
    }

    private <T extends AbstractModel> void fillObject(ResultSet rows, T object) {
        Method addLoadedValueMethod = null;
        boolean addLoadedValueMethodAccessibility = false;

        try {
            ResultSetMetaData metadata = rows.getMetaData();
            List<String> columns = object.getColumns();

            addLoadedValueMethod = AbstractModel.class.getDeclaredMethod("addLoadedValue", String.class, Object.class);
            addLoadedValueMethodAccessibility = addLoadedValueMethod.isAccessible();
            addLoadedValueMethod.setAccessible(true);

            if (rows.next()) {
                for (String column : columns) {
                    int pos = rows.findColumn(column);
                    String type = metadata.getColumnTypeName(pos);

                    switch (type) {
                        case "INTEGER":
                            object.setFieldByColumnName(column, rows.getInt(pos));
                            addLoadedValueMethod.invoke(object, column, rows.getInt(pos));
                            break;
                        case "BOOLEAN":
                            object.setFieldByColumnName(column, rows.getBoolean(pos));
                            addLoadedValueMethod.invoke(object, column, rows.getBoolean(pos));
                            break;
                        case "FLOAT":
                        case "REAL":
                            object.setFieldByColumnName(column, rows.getDouble(pos));
                            addLoadedValueMethod.invoke(object, column, rows.getDouble(pos));
                            break;
                        case "TEXT":
                            object.setFieldByColumnName(column, rows.getString(pos));
                            addLoadedValueMethod.invoke(object, column, rows.getString(pos));
                            break;
                        default:
                            break;
                    }

                }
            } else {
                throw new SQLException("We cannot find the object");
            }
        } catch (SQLException | NoSuchMethodException exception) {
            System.out.println("Something went wrong while filling object: " + exception.getMessage());
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        } finally {
            if (addLoadedValueMethod != null) {
                addLoadedValueMethod.setAccessible(addLoadedValueMethodAccessibility);
            }
        }
    }

    private void open() {
        connection = ConnectionManger.getConnection();
    }

    private void close() {
        ConnectionManger.releaseConnection(connection);
    }
}
