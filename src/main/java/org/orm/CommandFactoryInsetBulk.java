package org.orm;




import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

class CommandFactoryInsetBulk<F> {
    final private StringBuilder sql = new StringBuilder();
    final private List<Object> objectList = new ArrayList<>();
    private int it = 0;
    private final CacheMetaData<?> metaData;
    private final Connection connection;
    private final TypeDataBase typeDataBase;


    CommandFactoryInsetBulk(CacheMetaData<?> data, Connection connection, TypeDataBase typeDataBase) {
        metaData = data;
        this.connection = connection;
        this.typeDataBase = typeDataBase;
        sql.append(" INSERT INTO ");
        sql.append(metaData.tableName).append(" (");
        List<ItemField> itemFields = new ArrayList<>(metaData.listColumn);
        if (metaData.keyColumn.isAssigned) {
            itemFields.add(0, metaData.keyColumn);
        }
        for (ItemField f : itemFields) {
            if (!f.notInsert) {
                sql.append(f.columnName);
                sql.append(", ");
            }

        }
        int start = sql.lastIndexOf(",");
        sql.delete(start, sql.length());
        sql.append(") VALUES ").append(System.lineSeparator());

    }


    public void add(F o) {
        it++;
        sql.append("(");
        List<ItemField> itemFields = new ArrayList<>(metaData.listColumn);
        if (metaData.keyColumn.isAssigned) {
            itemFields.add(0, metaData.keyColumn);
        }
        for (ItemField f : itemFields) {
            f.field.setAccessible(true);

            try {
                if (!f.notInsert) {
                    Object value = f.field.get(o);
                    String v = getString(value, f, objectList,connection,typeDataBase);
                    sql.append(v);
                    sql.append(", ");
                }


            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        int start = sql.lastIndexOf(",");
        sql.delete(start, sql.length());
        sql.append(") ,").append(System.lineSeparator());
    }

    String getSql() {
        if (it == 0) {
            return null;
        } else {
            return sql.substring(0, sql.toString().lastIndexOf(",")).trim()+";";
        }

    }

    List<Object> getParamsObjectList() {

        return objectList;
    }

    public static String getString(Object o,  ItemField field, List<Object> objectList,Connection connection,TypeDataBase typeDataBase) {


         return UtilsCommand.getString(o, field, objectList,typeDataBase,connection);



    }
}
