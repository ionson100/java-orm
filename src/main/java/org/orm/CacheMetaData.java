package org.orm;





import java.util.List;
import java.util.Objects;

class CacheMetaData<T> {

    public List<ItemField> listColumn = null;
    public ItemField keyColumn = null;
    String tableName = null;
    String tableNameRaw = null;
    boolean isPersistent;
    String typeMysql="";

    String appendCreateTable = null;
    boolean isIAction = false;
    List<String>  stringListColumnName;

    boolean isTableReadOnly;
    private String[] listSelectColumns = null;
    String sessionKey=Utils.KEY_DEFAULT_SESSION;

    boolean isFreeClass;


    public List<String> getListColumnName(){
        List<String> listSelectColumns = new java.util.ArrayList<>();
        for (ItemField itemField : listColumn) {
            if(itemField.columnName!=null){
                listSelectColumns.add(itemField.columnName);
            }
        }
        return listSelectColumns;

    }





    public CacheMetaData(Class<T> aClass,TypeDataBase dataBase) {
        SetClass(aClass,dataBase);
    }

    private void SetClass(Class<?> tClass,TypeDataBase typeDataBase) {


        isRecursiveSubclassOf(tClass,typeDataBase);
        if(tableName!=null){
            if(tableName.isEmpty()){
                throw new RuntimeException("The class does not contain a table name annotation (@MapTableName or MapTable);" +
                        " such a class cannot be used in ORM.type:"+tClass.getName());
            }

            keyColumn = AnnotationOrm.getKeyColumnItemField(tClass,typeDataBase);

            if(keyColumn==null||keyColumn.columnName==null||keyColumn.columnName.isEmpty()){
                throw new RuntimeException("There was a problem defining the primary key, or you did not specify a field with an annotation, " +
                        "or you tried to specify an empty value in it. type: "+tClass.getName());
            }
            listColumn = AnnotationOrm.getListItemFieldColumn(tClass,typeDataBase);

            //PrintConsole.print("\"---ORM WARNING---\",\"Your class is missing fields to associate with table fields.(@MapColumn or @MapColumnName)  type:\"+tClass.getName())");


            if (tClass.isAnnotationPresent(MapTableReadOnly.class)) {
                isTableReadOnly=true;
            }

            MapTableSessionKey key = tClass.getAnnotation(MapTableSessionKey.class);
            if(key!=null){
                sessionKey=key.value();
            }





            int count=listColumn.size() + 1;
            listSelectColumns = new String[count];

            for (int i = 0; i < listColumn.size(); i++) {
                listSelectColumns[i] = listColumn.get(i).columnName;
            }
            listSelectColumns[listColumn.size()] = keyColumn.columnName;
            listSelectColumns[0] = keyColumn.columnName;
            if(!listColumn.isEmpty()){
                listSelectColumns[listSelectColumns.length - 1] = listColumn.get(0).columnName;
            }


        }else {
            isFreeClass=true;
            isRecursiveSubclassOfFree(tClass);
            listColumn = AnnotationOrm.getListItemFieldColumnFreeNew(tClass);
            tableName =tClass.getName();
            int index=tableName.lastIndexOf(".");
            if(index!=-1){
                tableName= Utils.clearStringTrim(tableName.substring(index+1),typeDataBase);
            }else{
                tableName= Utils.clearStringTrim(tableName,typeDataBase);
            }
            listSelectColumns = new String[listColumn.size()];
            for (int i = 0; i < listColumn.size(); i++) {
                listSelectColumns[i] = listColumn.get(i).columnName;
            }
            tableNameRaw=Utils.clearStringTrimRaw(tableName);
        }

    }
    String getSelectColumns(){

        StringBuilder stringBuilder=new StringBuilder(listSelectColumns.length);
        for (int i = 0; i < listSelectColumns.length; i++) {
            stringBuilder.append(listSelectColumns[i]);
            if(i<listSelectColumns.length-1){
                stringBuilder.append(", ");
            }
        }


        return stringBuilder.toString();

    }
    public String[] getStringSelect() {
        return listSelectColumns;

    }

    public  void isRecursiveSubclassOf( Class<?> parentClass,TypeDataBase typeDataBase) {

        String nameCore=parentClass.getName();
        Class<?> currentClass = parentClass;
        while (currentClass != null) {
            if (currentClass.equals(Persistent.class)) {
                isPersistent=true;
            }

            if ((currentClass.isAnnotationPresent(MapTableName.class)||currentClass.isAnnotationPresent(MapTable.class))&&tableName==null) {
                final MapTableName fName = currentClass.getAnnotation(MapTableName.class);
                if(typeDataBase==TypeDataBase.MYSQL){
                    var tm = currentClass.getAnnotation(MapTableTypeMySql.class);
                    if(tm!=null){
                        typeMysql=tm.value();
                    }else {
                        typeMysql = "ENGINE = InnoDB";
                    }
                }
                if(fName !=null){
                    tableName = Utils.clearStringTrim(fName.value(),typeDataBase);
                }else{
                    String name=currentClass.getName();
                    int index=name.lastIndexOf(".");
                    if(index!=-1){
                        tableName= Utils.clearStringTrim(name.substring(index+1),typeDataBase);
                    }else{
                        tableName= Utils.clearStringTrim(name,typeDataBase);
                    }
                }
                tableNameRaw=Utils.clearStringTrimRaw(tableName);
            }



            if (currentClass.isAnnotationPresent(MapAppendCommandCreateTable.class)) {
                if(appendCreateTable==null){
                    appendCreateTable="";
                }
                appendCreateTable = new StringBuilder().append(appendCreateTable).append(Objects.requireNonNull(currentClass.getAnnotation(MapAppendCommandCreateTable.class)).value()).append(System.lineSeparator()).toString();
            }
            if(!isIAction){
                isIAction=IEventOrm.class.isAssignableFrom(currentClass);
            }


            currentClass = currentClass.getSuperclass();
        }

    }

    public  void isRecursiveSubclassOfFree( Class<?> parentClass) {

        String nameCore=parentClass.getName();
        Class<?> currentClass = parentClass;
        while (currentClass != null) {
            if (currentClass.equals(Persistent.class)) {
                isPersistent=true;
            }

            if(!isIAction){
                isIAction=IEventOrm.class.isAssignableFrom(currentClass);
            }
            currentClass = currentClass.getSuperclass();
        }

    }
}

