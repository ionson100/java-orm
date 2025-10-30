package org.orm;


import java.sql.Connection;
import java.util.Date;
import java.util.List;

 class UtilsCommand {
    private UtilsCommand(){}
    static String getString(Object o, ItemField field, List<Object> objectList, TypeDataBase typeDataBase, Connection connections) {

        String typeName = field.typeName;
        field.field.setAccessible(true);

        switch (typeName) {

            case "Date": {
                if (o == null) {
                    return "null";
                } else {
                    switch (typeDataBase) {

                        case SQLITE: {
                            long ld = ((Date) o).getTime();
                            return String.valueOf(ld);
                        }
                        case POSTGRESQL:
                        case MYSQL: {
                            return "'" + UtilsHelper.dateToStringForSQLite((Date) o) + "'";
                        }
                        default: {
                            throw new RuntimeException("Not implemented");
                        }
                    }

                }
            }
            case "LocalDateTime": {
                if (o == null) {
                    return "null";
                } else {
                    objectList.add(o);
                    return Utils.paramsChar(typeDataBase);
                }
            }

            case "BigDecimal": {
                if (o == null) {
                    return "null";
                } else {
                    return o.toString();
                }
            }
            case "UUID": {
                if (o == null) {
                    return "null";
                } else {
                    return String.format("'%s'", o);
                }

            }
            case "String": {
                if (o == null) {
                    return "null";
                } else {
                    objectList.add(o);

                    return Utils.paramsChar(typeDataBase);
                }
            }
            case "boolean": {
                if (o == null) {
                    throw new RuntimeException("Поле: " + field.columnName + " не может быть null");
                } else {
                    switch (typeDataBase) {
                        case SQLITE: {
                            if ((Boolean) o) {
                                return "1";
                            } else {
                                return "0";
                            }
                        }
                        case MYSQL:
                        case POSTGRESQL: {
                            if ((Boolean) o) {
                                return "true";
                            } else {
                                return "false";
                            }
                        }
                        default: {
                            throw new RuntimeException("Not implemented");
                        }
                    }
                }
            }
            case "Boolean": {
                if (o == null) {
                    return "null";
                } else {
                    switch (typeDataBase) {
                        case SQLITE: {
                            if ((Boolean) o) {
                                return "1";
                            } else {
                                return "0";
                            }
                        }
                        case MYSQL:
                        case POSTGRESQL: {
                            if ((Boolean) o) {
                                return "true";
                            } else {
                                return "false";
                            }
                        }
                        default: {
                            throw new RuntimeException("Not implemented");
                        }
                    }
                }
            }
            case "byte":
            case "int":
            case "long":
            case "float":
            case "double":
            case "short": {
                if (o == null) {
                    throw new RuntimeException("Поле: " + field.columnName + " не может быть null");
                } else {
                    return String.valueOf(o);
                }
            }
            case "Byte":
            case "Integer":
            case "Float":
            case "Double":
            case "Long":
            case "Short": {
                if (o == null) {
                    return "null";
                } else {
                    return String.valueOf(o);
                }
            }

            case "[SB": {
                if (o == null) {
                    return "null";
                } else {
                    byte[] s = UtilsHelper.serializeByte(o);
                    objectList.add(s);

                    return Utils.paramsChar(typeDataBase);
                }
            }
            case "[SJ": {
                if (o == null) {
                    return "null";
                } else {
                    String s = UtilsHelper.serializeJson(o);
                    objectList.add(s);

                    return Utils.paramsChar(typeDataBase);
                }
            }
            //o,objectList,connections[0]
            default: {
                IAppenderWorker IA = Configure.getAppenderWorker(typeName);
                if (IA != null) {
                   return  IA.toBase(o, objectList, connections);
                } else {
                    if (o == null) {
                        return "null";
                    } else {
                        byte[] s = UtilsHelper.serializeByte(o);
                        objectList.add(s);
                        return Utils.paramsChar(typeDataBase);
                    }
                }
            }
        }
    }
}
