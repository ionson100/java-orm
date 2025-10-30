package org.orm;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

 class ItemField {
    public Field field;
    public String columnName;
    public String fieldName;
    public Type type;
    public int typeKeyField;
    public Object columnNameRaw;
    public String typeName;
    public String foreignKey;
    public boolean isIndex;
    public boolean notInsert;
    public String columnType;
    public boolean isAssigned;
}
