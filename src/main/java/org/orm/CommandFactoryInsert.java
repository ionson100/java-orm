package org.orm;

import java.sql.Connection;
import java.util.List;

class CommandFactoryInsert<T>  {

    private final Connection connection;

    public CommandFactoryInsert(Connection connection) {
        this.connection = connection;
    }
    public String create(T item, CacheMetaData<?> metaData, List<Object> parameters,TypeDataBase typeDataBase)  {

        StringBuilder keyBuilder = new StringBuilder();
        StringBuilder valueBuilder = new StringBuilder();

        if(metaData.keyColumn.isAssigned){
            keyBuilder.append(metaData.keyColumn.columnName).append(", ");
            valueBuilder.append(Utils.paramsChar(typeDataBase)).append(", ");
            try {
                parameters.add(metaData.keyColumn.field.get(item));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        for (ItemField o : metaData.listColumn) {
            if(o.notInsert){
                continue;
            }
            keyBuilder.append(o.columnName).append(", ");
            try {
                valueBuilder.append(UtilsCommand.getString(o.field.get(item), o,parameters,typeDataBase,connection)).append(", ");
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        String key = keyBuilder.isEmpty() ?"":keyBuilder.substring(0,keyBuilder.length()-2);
        String value = valueBuilder.isEmpty() ?"":valueBuilder.substring(0,valueBuilder.length()-2);
        return "INSERT INTO " + metaData.tableName + "("+key+") VALUES ("+value+")";

    }
}
