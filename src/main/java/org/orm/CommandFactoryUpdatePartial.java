package org.orm;

import java.sql.Connection;
import java.util.List;


 class CommandFactoryUpdatePartial {
    private final Connection conn;
    private final List<Object> params;
    private final CacheMetaData metaData;
    private final PairColumnValue pairColumns;
     private final TypeDataBase typeDataBase;

     CommandFactoryUpdatePartial(Connection conn, List<Object> params, CacheMetaData metaData, PairColumnValue pairColumns,TypeDataBase typeDataBase) {
        super();
        this.conn = conn;
        this.params = params;
        this.metaData = metaData;
        this.pairColumns = pairColumns;
         this.typeDataBase = typeDataBase;
     }
    private  static ItemField getItemField(List<ItemField> list,String name){
        for (ItemField itemField : list) {
            if (itemField.columnNameRaw.equals(name)) {
                return itemField;
            }
        }
        return null;
    }
    String getSql() {

        StringBuilder builder=new StringBuilder("UPDATE "+metaData.tableName+" SET ");
        pairColumns.objectMap.forEach((s, o) -> {
            ItemField itemField=getItemField(metaData.listColumn,s);
            if(itemField==null){
                throw new RuntimeException("!!!Column with name: "+s+" not found in table:"+metaData.tableName);
            }
            if(!itemField.notInsert){
                builder.append(itemField.columnName).append(" = ").append(CommandFactoryInsetBulk.getString(o,itemField,params,conn,typeDataBase)).append(",");
            }

        });
        return builder.substring(0,builder.length()-1);
    }

}
