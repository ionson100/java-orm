package org.orm;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

class Utils {

    public final static String KEY_DEFAULT_SESSION="default";
    private Utils(){}











    public static List<String> getStringListSqlCreateTable(String ifNotExist, CacheMetaData<?> data,TypeDataBase typeDataBase) {
        StringBuilder sb = new StringBuilder("CREATE TABLE " + ifNotExist + " " + data.tableName + " (" + System.lineSeparator());

        StringBuilder foreignKey = new StringBuilder();

        sb.append(data.keyColumn.columnName).append(" ");
        sb.append(pizdaticusKey(data.keyColumn,typeDataBase));

        if(typeDataBase == TypeDataBase.MYSQL){
            if(!data.keyColumn.isAssigned){
                sb.append(" AUTO_INCREMENT, ").append(System.lineSeparator());
            }else{
                sb.append(" DEFAULT (UUID()), ").append(System.lineSeparator());
            }


        }else{
            sb.append(" PRIMARY KEY, ").append(System.lineSeparator());
        }

        for (ItemField f : data.listColumn) {
            sb.append(f.columnName);
            sb.append(pizdaticusField(f,typeDataBase));
            sb.append(System.lineSeparator());
            if (f.foreignKey != null) {
                foreignKey.append(",").append(f.foreignKey).append(System.lineSeparator());
            }
        }
        String s = sb.toString().trim();
        String ss = s.substring(0, s.length() - 1);
        String fringeKey = "";
        if (!foreignKey.isEmpty()) {
            fringeKey = fringeKey + System.lineSeparator() + foreignKey;
        }
        if(typeDataBase == TypeDataBase.MYSQL) {

           ss=ss+","+System.lineSeparator()+"primary key ("+data.keyColumn.columnName+")";
            String sql = ss + fringeKey +System.lineSeparator()+ ")"+data.typeMysql+";";
        }
        String sql = ss + fringeKey +System.lineSeparator()+ ")"+data.typeMysql+";";

        //ENGINE=InnoDB DEFAULT CHARSET=utf8mb4


        sb.delete(0, sb.length() - 1);
        List<String> sqlList = new ArrayList<>();
        sqlList.add(sql);
        return sqlList;
    }



    public static String pizdaticusKey(ItemField field,TypeDataBase typeDataBase) {

        switch (field.typeName) {
            case "float":
            case "Float":
            case "Double":
            case "double": {
                return " REAL ";
            }
            case "int":
            case "Integer":
            case "short":
            case "Short":
            case "byte":
            case "Byte": {
                return switch (typeDataBase) {
                    case SQLITE -> " INTEGER ";
                    case POSTGRESQL -> " SERIAL ";//NOT NULL
                    case MYSQL -> " INT NOT NULL ";
                };

            }
            case "long":
            case "Long":{
                return switch (typeDataBase) {
                    case SQLITE -> " INTEGER ";
                    case POSTGRESQL -> " BIGINT ";
                    case MYSQL -> " BIGINT ";
                };

            }
            case "boolean":
            case "Boolean": {
                return " BOOL ";
            }
            case "UUID":{
                return switch (typeDataBase) {
                    case SQLITE -> " TEXT ";
                    case POSTGRESQL -> " UUID ";
                    case MYSQL -> " VARCHAR(36) ";
                };

            }
            default: {
                return " TEXT ";
            }

        }
    }
    public static String paramsChar(TypeDataBase typeDataBase) {
        return switch (typeDataBase) {
            case SQLITE, POSTGRESQL -> "?";
            default -> "?";
        };

    }

    public static String pizdaticusField(ItemField field,TypeDataBase typeDataBase) {

        if (field.columnType != null) {
            return " " + field.columnType + ", ";
        }
        if(field.typeName.equals("[SJ")){
            return " TEXT, ";
        }
            switch (field.typeName) {
                case "UUID":{
                    return switch (typeDataBase) {
                        case SQLITE -> " TEXT, ";
                        case POSTGRESQL -> " UUID, ";
                        case MYSQL ->" VARCHAR(36), ";
                    };
                }
                case "[SJ":
                case "String":

                case "BigDecimal": {
                    return switch (typeDataBase) {
                        case SQLITE -> " TEXT, ";
                        case POSTGRESQL -> " VARCHAR(256) DEFAULT NULL, ";
                        case MYSQL ->" VARCHAR(256), ";
                    };
                }
                case "float":
                case "double": {
                    return switch (typeDataBase) {
                        case SQLITE -> " REAL DEFAULT 0, ";
                        case POSTGRESQL -> " FLOAT(25) DEFAULT 0, ";
                        default -> throw new RuntimeException("Not implemented");
                    };


                }
                case "long":
                case "short":
                case "byte":
                case "int": {
                    return " INTEGER DEFAULT 0 , ";
                }

                case "Float":
                case "Double":{
                    return switch (typeDataBase) {
                        case SQLITE -> " REAL DEFAULT NULL, ";
                        case POSTGRESQL -> " FLOAT(25) DEFAULT NULL, ";
                        default -> throw new RuntimeException("Not implemented");
                    };
                }

                case "Byte":
                case "Long":
                case "Short":
                case "Integer":{
                    return " INTEGER DEFAULT NULL , ";
                }
                case "Boolean":{
                    return switch (typeDataBase) {
                        case SQLITE -> " BOOL DEFAULT NULL, ";
                        case POSTGRESQL -> " BOOLEAN DEFAULT NULL, ";
                        default -> throw new RuntimeException("Not implemented");
                    };
                }
                case "boolean": {
                    return switch (typeDataBase) {
                        case SQLITE, MYSQL -> " BOOL DEFAULT 0, ";
                        case POSTGRESQL -> " BOOLEAN DEFAULT FALSE, ";


                    };
                }
                case "Date", "LocalDateTime": {
                    return switch (typeDataBase) {
                        case SQLITE,MYSQL -> " DATETIME, ";
                        case POSTGRESQL -> " TIMESTAMP, ";
                    };
                }
                case "[SB":
                default: {
                    return switch (typeDataBase) {
                        case SQLITE,MYSQL  -> " BLOB, ";
                        case POSTGRESQL -> " BYTEA, ";
                    };
                }

            }


    }





    public static String trimStart(String str, Character... c) {
        if (str == null) return null;
        str = str.trim();
        if (str.isEmpty()) return str;

        for (int i = 0; i < str.length(); i++) {
            if (ContainsArray(c, str.charAt(i))) {
                return str.substring(i);
            }
        }
        return "";
    }

    public static String trimEnd(String str, Character... c) {
        if (str == null) return null;
        str = str.trim();
        if (str.isEmpty()) return str;
        StringBuilder builder = new StringBuilder();
        for (int i = str.length() - 1; i > 0; i--) {
            if (ContainsArray(c, str.charAt(i))) {
                builder.append(str, 0, i + 1);
                break;
            }
        }
        return builder.toString();
    }

    public static String trimAll(String str, Character... c) {
        String s = trimStart(str, c);
        return trimEnd(s, c);
    }

    private static <T> boolean ContainsArray(T[] t, T d) {
        for (T w : t) {

            if (w.equals(d)) {
                return false;
            }
        }
        return true;
    }

    public static String clearStringTrim(String str,TypeDataBase typeDataBase) {
        switch (typeDataBase){
            case SQLITE:{
                return "\"" + trimAll(str, ' ', '`', '[', ']', '\'') + "\"";
            }
            case POSTGRESQL:{
                return ("\"" + trimAll(str, ' ', '`', '[', ']', '\'') + "\"").replace("$", "_");
            }
            case MYSQL:{
                return ("`" + trimAll(str, ' ', '`', '[', ']', '\'') + "`").replace("$", "_");
            }
            default:{
                throw new RuntimeException("Not implemented");
            }
        }


    }






    public static String clearStringTrimRaw(String str) {
        return trimAll(str, '"',' ', '`', '[', ']', '\'');
    }




    public static String getTypeName(Field f) {

        String res = f.getType().getName();
        switch (res) {
            case "java.util.List":
            case "java.util.Set":
            case "java.util.Map": {
                return "[SB";
            }


            default:{
                if(res.startsWith("[")){
                    return "[SB";
                }
                int index = res.lastIndexOf(".");
                if (index != -1) {
                    return res.substring(index + 1);
                } else {
                    return res;
                }
            }
        }


    }
}

