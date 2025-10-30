package org.orm;


import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

class UtilsCompound {

    private UtilsCompound() {
    }


    public static void Compound(List<ItemField> listIf, ItemField key, ResultSet cursor, Object o, TypeDataBase typeDataBase) throws Exception {
        for (ItemField str : listIf) {
            int i = cursor.findColumn((String) str.columnNameRaw);
            if (i == -1) continue;
            Field res = str.field;
            res.setAccessible(true);


            extractedSwitch(cursor, o, str, res, i, typeDataBase);
        }
        try {
            Field field = key.field;
            int index = cursor.findColumn((String) key.columnNameRaw);
            field.setAccessible(true);
            switch (key.typeName) {
                case "UUID": {
                    String uuid = cursor.getString(index);
                    field.set(o, UUID.fromString(uuid));
                    break;
                }
                case "int": {
                    field.set(o, cursor.getInt(index));
                    break;
                }
                case "long": {
                    field.set(o, cursor.getLong(index));
                    break;
                }
                case "float": {
                    field.set(o, cursor.getFloat(index));
                    break;
                }
                case "String": {
                    field.set(o, cursor.getString(index));
                    break;
                }
                default: {
                    throw new RuntimeException(" Не могу вставить первичный ключ: " + key.type);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    static void extractedSwitch(ResultSet cursor, Object o, ItemField fieldBase, Field field, int i, TypeDataBase typeDataBase) throws Exception {


        if (cursor.getObject(i) == null) {
            field.set(o, null);
            return;
        }
        switch (fieldBase.typeName) {


            case "int": {

                field.setInt(o, cursor.getInt(i));
                return;
            }
            case "LocalDateTime": {
                String str = cursor.getString(i).replace(" ", "T");
                LocalDateTime localDateTime = LocalDateTime.parse(str);
                field.set(o, localDateTime);
                return;
            }

            case "Date": {
                switch (typeDataBase) {

                    case SQLITE: {
                        Date date = new Date(cursor.getLong(i));
                        field.set(o, date);
                        return;
                    }
                    case POSTGRESQL:
                    case MYSQL: {
                        String str = cursor.getString(i);
                        Date date = UtilsHelper.stringToDate(str);
                        field.set(o, date);
                        return;
                    }
                    default: {
                        throw new RuntimeException("Not implemented");
                    }
                }


            }
            case "UUID": {

                String uuid = cursor.getString(i);
                field.set(o, UUID.fromString(cursor.getString(i)));
                return;
            }
            case "BigDecimal": {

                field.set(o, new BigDecimal(cursor.getString(i)));
                return;
            }
            case "UUId":
            case "String": {

                field.set(o, cursor.getString(i));
                return;
            }
            case "double": {

                field.setDouble(o, cursor.getDouble(i));
                return;
            }
            case "float": {

                field.setFloat(o, cursor.getFloat(i));
                return;
            }
            case "long": {

                long dd = cursor.getLong(i);
                field.setLong(o, dd);
                return;
            }
            case "short": {

                field.setShort(o, cursor.getShort(i));
                return;
            }


            case "byte": {

                Integer myInt = new Integer(cursor.getInt(i));
                field.setByte(o, myInt.byteValue());
                return;
            }
            case "Integer": {
                {
                    Integer ii = cursor.getInt(i);
                    field.set(o, ii);
                }
                return;
            }
            case "Double": {

                Double d = cursor.getDouble(i);
                field.set(o, d);

                return;
            }
            case "Float": {

                Float f = cursor.getFloat(i);
                field.set(o, f);

                return;
            }
            case "Long": {

                Long l = cursor.getLong(i);
                field.set(o, l);

                return;
            }
            case "Short": {

                Short sh = cursor.getShort(i);
                field.set(o, sh);

                return;
            }

            case "boolean": {

                boolean val;
                switch (typeDataBase) {
                    case SQLITE: {
                        val = cursor.getInt(i) != 0;
                        field.set(o, val);
                        return;
                    }
                    case POSTGRESQL: {
                        val = cursor.getBoolean(i);
                        field.set(o, val);
                        return;
                    }
                    default: {
                        throw new RuntimeException("Not implemented");
                    }
                }


            }
            case "Boolean": {
                boolean val;
                switch (typeDataBase) {
                    case SQLITE: {
                        val = cursor.getInt(i) != 0;
                        field.set(o, val);
                        return;
                    }
                    case POSTGRESQL: {
                        val = cursor.getBoolean(i);
                        field.set(o, new Boolean(val));
                        return;
                    }
                    default: {
                        throw new RuntimeException("Not implemented");
                    }
                }

            }


            case "Byte": {

                Integer myInt = new Integer(cursor.getInt(i));
                field.set(o, myInt.byteValue());

                return;
            }


            case "[SB": {

                byte[] b = cursor.getBytes(i);
                //Base64.decode(baseStr, Base64.DEFAULT)
                Object res = UtilsHelper.deserializeByte(b);
                field.set(o, res);
                return;

            }
            case "[SJ": {

                String json = cursor.getString(i);
                Object res = UtilsHelper.deserializeJson(json, field.getType());
                field.set(o, res);
                return;

            }

            default: {
                IAppenderWorker I = Configure.getAppenderWorker(fieldBase.typeName);
                if (I != null) {
                    Object res = I.fromBase(cursor, i);
                    field.set(o, res);
                } else {
                    byte[] b = cursor.getBytes(i);
                    Object res = UtilsHelper.deserializeByte(b);
                    field.set(o, res);
                }
            }
        }


    }


    static void CompoundFree(List<ItemField> listIf, ResultSet resultSet, Object o, TypeDataBase typeDataBase) throws Exception {
        for (ItemField str : listIf) {
            int i = resultSet.findColumn((String) str.columnNameRaw);
            if (i == -1) continue;
            Field res = str.field;
            res.setAccessible(true);
            extractedSwitch(resultSet, o, str, res, i, typeDataBase);

        }
    }

    static void builderInstance(CacheMetaData<?> metaData, ResultSet resultSet, Object instance, TypeDataBase typeDataBase) throws Exception {
        if (metaData.isFreeClass) {
            CompoundFree(metaData.listColumn, resultSet, instance, typeDataBase);
        } else {
            if (metaData.isPersistent) {
                ((Persistent) instance).isPersistent = true;
            }
            Compound(metaData.listColumn, metaData.keyColumn, resultSet, instance, typeDataBase);
        }
    }

    static Object extractedSwitchSelect(ResultSet cursor, ItemField fieldBase, Field field, int i, TypeDataBase typeDataBase) throws Exception {

        if (cursor.getObject(i) == null) {

            return null;
        }
        switch (fieldBase.typeName) {

            case "int":
            case "Integer": {
                return cursor.getInt(i);
            }
            case "LocalDateTime": {
                String str = cursor.getString(i).replace(" ", "T");
                return LocalDateTime.parse(str);
            }
            case "Date": {
                switch (typeDataBase) {

                    case SQLITE: {
                        return new Date(cursor.getLong(i));
                    }
                    case POSTGRESQL:
                    case MYSQL: {
                        String str = cursor.getString(i);
                        return UtilsHelper.stringToDate(str);
                    }
                    default: {
                        throw new RuntimeException("Not implemented");
                    }
                }
            }
            case "UUID": {
                return UUID.fromString(cursor.getString(i));
            }
            case "BigDecimal": {
                return new BigDecimal(cursor.getString(i));
            }
            case "UUId":
            case "String": {
                return cursor.getString(i);
            }
            case "double":
            case "Double": {
                return cursor.getDouble(i);
            }
            case "float":
            case "Float": {
                return cursor.getFloat(i);
            }
            case "long":
            case "Long": {


                return cursor.getLong(i);
            }
            case "short":
            case "Short": {
                return cursor.getShort(i);
            }


            case "byte":
            case "Byte": {
                Integer myInt = new Integer(cursor.getInt(i));
                return myInt.byteValue();
            }
            case "boolean":
            case "Boolean": {
                return cursor.getInt(i) != 0;
            }


            case "[SB": {
                byte[] b = cursor.getBytes(i);
                return UtilsHelper.deserializeByte(b);
            }
            case "[SJ": {

                String json = cursor.getString(i);
                return UtilsHelper.deserializeJson(json, field.getType());

            }

            default: {
                Object I = Configure.getAppenderWorker(fieldBase.typeName);
                if (I != null) {
                    return ((IAppenderWorker) I).fromBase(cursor, i);
                }

                byte[] b = cursor.getBytes(i);
                return UtilsHelper.deserializeByte(b);

            }
        }


    }

}

