package org.orm;

import java.sql.Connection;
import java.util.List;

 class CommandFactoryUpdate<T>  {

   public String create(T item, CacheMetaData<?> metaData, List<Object> parameters, TypeDataBase typeDataBase, Connection connections) {
       StringBuilder keyvaluebuilder = new StringBuilder();




       for (ItemField o : metaData.listColumn) {
           if(o.notInsert){
               continue;
           }
           try {
               String s=UtilsCommand.getString(o.field.get(item), o,parameters,typeDataBase,connections);
               keyvaluebuilder.append(o.columnName).append(" = ").append(s).append(", ");
           } catch (IllegalAccessException e) {
               throw new RuntimeException(e);
           }

       }

       String value = keyvaluebuilder.substring(0,keyvaluebuilder.length()-2);

           return "UPDATE "+metaData.tableName+" SET "+value;

   }
}
