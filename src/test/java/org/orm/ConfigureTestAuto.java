package org.orm;

import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class ConfigureTestAuto {

    static {
        Configure.IsWriteConsole=true; new Configure(TypeDataBase.SQLITE,"test",null,null);
    }

    @MapTable
    static class MyTable{
        @MapPrimaryKey
        public int id;
        @MapColumnIndex
        @MapColumn
        public String name;
        @MapColumn
        public List<String> stringList=new ArrayList<>();


    }

    @Test
    void getSession() {
        try (ISession session = Configure.getSession()) {
            assert session != null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
//    @Test
//    void getMetaData() {
//        CacheMetaData metaData =  CacheDictionary.getCacheMetaData(MyTable.class);
//        String s=metaData.keyColumn.columnName;
//        assert s!=null;
//        assert metaData != null;
//
//        System.out.println(metaData.keyColumn.columnName);
//    }
    @Test
    void getTableName(){
        String name=Configure.getSession().getTableName(MyTable.class);
        assert name!=null;
        System.out.println(name);
    }
    @Test
    void getTableExist() throws Exception {
        boolean res;
     
            Configure.getSessionAuto().dropTableIfExists(MyTable.class);
            res = Configure.getSessionAuto().tableExists(MyTable.class);
        
        assert !res;
        System.out.println(res);
    }
    @Test
    void getTableCreate() throws Exception {
        boolean res;
    
            Configure.getSessionAuto().dropTableIfExists(MyTable.class);
            Configure.getSessionAuto().createTable(MyTable.class);
            res = Configure.getSessionAuto().tableExists(MyTable.class);
       
        assert res;

    }
 @Test
    void getTableCreateIfNotExist() throws Exception {
     boolean res;
     
         Configure.getSessionAuto().dropTableIfExists(MyTable.class);
         Configure.getSessionAuto().createTableIfNotExists(MyTable.class);
         res = Configure.getSessionAuto().tableExists(MyTable.class);
    
     assert res;

    }

    @Test
    void insert() throws Exception {
        MyTable myTable;
        List<MyTable> list;
     
            Configure.getSessionAuto().dropTableIfExists(MyTable.class);
            Configure.getSessionAuto().createTable(MyTable.class);
            myTable = new MyTable();
            myTable.name = "123";
            myTable.stringList.add("123");
            Configure.getSessionAuto().insert(myTable);
            list = Configure.getSessionAuto().getList(MyTable.class, null);
        
        assert list.size()==1;
        assert myTable.id>0;

    }
    @Test
    void getTableInsertBulk() throws Exception {
      
            Configure.getSessionAuto().dropTableIfExists(MyTable.class);
            Configure.getSessionAuto().createTable(MyTable.class);
            MyTable myTable = new MyTable();
            myTable.name = "123";
            Configure.getSessionAuto().insertBulk(myTable);
       


    }
    @Test
    void getList() throws Exception {
        List<MyTable> list;
      
            Configure.getSessionAuto().dropTableIfExists(MyTable.class);
            Configure.getSessionAuto().createTable(MyTable.class);
            MyTable myTable = new MyTable();
            myTable.name = "123";
            myTable.stringList.add("123");
            Configure.getSessionAuto().insertBulk(myTable);
            list = Configure.getSessionAuto().getList(MyTable.class, null);
        
        assert list.size()==1;

    }

    @Test
    void update() throws Exception {
        List<MyTable> list;
       
            Configure.getSessionAuto().dropTableIfExists(MyTable.class);
            Configure.getSessionAuto().createTable(MyTable.class);
            MyTable myTable = new MyTable();
            myTable.name = "123";
            myTable.stringList.add("123");
            Configure.getSessionAuto().insertBulk(myTable);
            list = Configure.getSessionAuto().getList(MyTable.class, null);
            list.get(0).name = "1234";
            Configure.getSessionAuto().update(list.get(0));
            list = Configure.getSessionAuto().getList(MyTable.class, null);
      
        assert list.get(0).name.equals("1234");


    }

    @Test
    void delete() throws Exception {
        List<MyTable> list;
      
            Configure.getSessionAuto().dropTableIfExists(MyTable.class);
            Configure.getSessionAuto().createTable(MyTable.class);
            MyTable myTable = new MyTable();
            myTable.name = "123";
            myTable.stringList.add("123");
            Configure.getSessionAuto().insertBulk(myTable);
            list = Configure.getSessionAuto().getList(MyTable.class, null);

            assert list.size() == 1;
            Configure.getSessionAuto().delete(list.get(0));
            list = Configure.getSessionAuto().getList(MyTable.class, null);
       
        assert list.size()==0;


    }

    @Test
    void deleteRows() throws Exception {
        List<MyTable> list;
      
            Configure.getSessionAuto().dropTableIfExists(MyTable.class);
            Configure.getSessionAuto().createTable(MyTable.class);
            MyTable myTable = new MyTable();
            myTable.name = "123";
            myTable.stringList.add("123");
            Configure.getSessionAuto().insertBulk(myTable);
            list = Configure.getSessionAuto().getList(MyTable.class, null);
            assert list.size() == 1;
            Configure.getSessionAuto().deleteRows(MyTable.class, "id=?", list.get(0).id);
            list = Configure.getSessionAuto().getList(MyTable.class, null);
            assert list.size()==0;
      



    }
    @Test
    void count() throws Exception {
        int count;
    
            Configure.getSessionAuto().dropTableIfExists(MyTable.class);
            Configure.getSessionAuto().createTable(MyTable.class);
            MyTable myTable = new MyTable();
            myTable.name = "123";
            myTable.stringList.add("123");
            Configure.getSessionAuto().insertBulk(myTable);
            count = Configure.getSessionAuto().count(MyTable.class);
        
        assert count==1;
    }
    @Test
    void countWhere() throws Exception {
        int count;
        
            Configure.getSessionAuto().dropTableIfExists(MyTable.class);
            Configure.getSessionAuto().createTable(MyTable.class);
            MyTable myTable = new MyTable();
            myTable.name = "123";
            myTable.stringList.add("123");
            Configure.getSessionAuto().insertBulk(myTable);
            count = Configure.getSessionAuto().count(MyTable.class, "name=?", myTable.name);
       
        assert count==1;
    }

    @Test
    void any() throws Exception {
        boolean any;
       
            Configure.getSessionAuto().dropTableIfExists(MyTable.class);
            Configure.getSessionAuto().createTable(MyTable.class);
            MyTable myTable = new MyTable();
            myTable.name = "123";
            myTable.stringList.add("123");
            Configure.getSessionAuto().insertBulk(myTable);
            any = Configure.getSessionAuto().any(MyTable.class, "name=?", myTable.name);
       
        assert any;
    }

    @Test
    void any2() throws Exception {
        boolean any;
        
            Configure.getSessionAuto().dropTableIfExists(MyTable.class);
            Configure.getSessionAuto().createTable(MyTable.class);
            MyTable myTable = new MyTable();
            myTable.name = "123";
            myTable.stringList.add("123");
            Configure.getSessionAuto().insertBulk(myTable);
            any = Configure.getSessionAuto().any(MyTable.class, null);
        
        assert any;
    }
    @Test
    void firestOrDefault() throws Exception {
        MyTable table;
    
            Configure.getSessionAuto().dropTableIfExists(MyTable.class);
            Configure.getSessionAuto().createTable(MyTable.class);
            MyTable myTable = new MyTable();
            myTable.name = "123";
            myTable.stringList.add("123");
            Configure.getSessionAuto().insertBulk(myTable);
            table = Configure.getSessionAuto().firstOrDefault(MyTable.class, "name=?", myTable.name);
      
        assert table!=null;
    }
    @Test
    void firestOrDefault1() throws Exception {
        MyTable table;

            Configure.getSessionAuto().dropTableIfExists(MyTable.class);
            Configure.getSessionAuto().createTable(MyTable.class);
            MyTable myTable = new MyTable();
            myTable.name = "123";
            myTable.stringList.add("123");
            Configure.getSessionAuto().insertBulk(myTable);
            table = Configure.getSessionAuto().firstOrDefault(MyTable.class, null);

        assert table!=null;
    }

    @Test
    void getListSelect() throws Exception {
        List<Integer> list;

            Configure.getSessionAuto().dropTableIfExists(MyTable.class);
            Configure.getSessionAuto().createTable(MyTable.class);
            MyTable myTable = new MyTable();
            myTable.name = "123";
            myTable.stringList.add("123");
            Configure.getSessionAuto().insertBulk(myTable);
            list = Configure.getSessionAuto().getListSelect(MyTable.class, "stringList", null);

        assert list.size()==1;
    }

    @Test
    void groupBy() throws Exception {
        Map<Object, List<MyTable>> map;

            Configure.getSessionAuto().dropTableIfExists(MyTable.class);
            Configure.getSessionAuto().createTable(MyTable.class);
            MyTable myTable = new MyTable();
            myTable.name = "123";
            myTable.stringList.add("123");
            Configure.getSessionAuto().insertBulk(myTable);
            map = Configure.getSessionAuto().groupBy(MyTable.class, "name", null);

        for (Map.Entry<Object, List<MyTable>> entry : map.entrySet()) {
            for (MyTable table : entry.getValue()) {
                assert table.id>0;
                System.out.println(table.id);
            }
            return;
        }
        assert false;

    }



    @Test
    void autoClose() throws Exception {

        Configure.getSessionAuto().dropTableIfExists(MyTable.class);
        Configure.getSessionAuto().createTableIfNotExists(MyTable.class);
        boolean alive=Configure.getSessionAuto().IsAlive();
        if(alive==true){
            throw new Exception();
        }


        for (int i = 0; i < 10; i++) {
            MyTable myTable = new MyTable();
            myTable.name = "123";
            myTable.stringList.add("123");
            Configure.getSessionAuto().insertBulk(myTable);
        }
    }

}

