package org.orm;

import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class ConfigureTest {

    static {
        Configure.IsWriteConsole=true;
        new Configure(TypeDataBase.SQLITE,"test",null,null);
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
//        CacheMetaData metaData = CacheDictionary.getCacheMetaData(MyTable.class);
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
        try (ISession session = Configure.getSession()) {
            session.dropTableIfExists(MyTable.class);
            res = session.tableExists(MyTable.class);
        }
        assert !res;
        System.out.println(res);
    }
    @Test
    void getTableCreate() throws Exception {
        boolean res;
        try (ISession session = Configure.getSession()) {
            session.dropTableIfExists(MyTable.class);
            session.createTable(MyTable.class);
            res = session.tableExists(MyTable.class);
        }
        assert res;

    }
 @Test
    void getTableCreateIfNotExist() throws Exception {
     boolean res;
     try (ISession session = Configure.getSession()) {
         session.dropTableIfExists(MyTable.class);
         session.createTableIfNotExists(MyTable.class);
         res = session.tableExists(MyTable.class);
     }
     assert res;

    }

    @Test
    void insert() throws Exception {
        MyTable myTable;
        List<MyTable> list;
        try (ISession session = Configure.getSession()) {
            session.dropTableIfExists(MyTable.class);
            session.createTable(MyTable.class);
            myTable = new MyTable();
            myTable.name = "123";
            myTable.stringList.add("123");
            session.insert(myTable);
            list = session.getList(MyTable.class, null);
        }
        assert list.size()==1;
        assert myTable.id>0;

    }
    @Test
    void getTableInsertBulk() throws Exception {
        try (ISession session = Configure.getSession()) {
            session.dropTableIfExists(MyTable.class);
            session.createTable(MyTable.class);
            MyTable myTable = new MyTable();
            myTable.name = "123";
            session.insertBulk(myTable);
        }


    }
    @Test
    void getList() throws Exception {
        List<MyTable> list;
        try (ISession session = Configure.getSession()) {
            session.dropTableIfExists(MyTable.class);
            session.createTable(MyTable.class);
            MyTable myTable = new MyTable();
            myTable.name = "123";
            myTable.stringList.add("123");
            session.insertBulk(myTable);
            list = session.getList(MyTable.class, null);
        }
        assert list.size()==1;

    }

    @Test
    void update() throws Exception {
        List<MyTable> list;
        try (ISession session = Configure.getSession()) {
            session.dropTableIfExists(MyTable.class);
            session.createTable(MyTable.class);
            MyTable myTable = new MyTable();
            myTable.name = "123";
            myTable.stringList.add("123");
            session.insertBulk(myTable);
            list = session.getList(MyTable.class, null);
            list.get(0).name = "1234";
            session.update(list.get(0));
            list = session.getList(MyTable.class, null);
        }
        assert list.get(0).name.equals("1234");


    }

    @Test
    void delete() throws Exception {
        List<MyTable> list;
        try (ISession session = Configure.getSession()) {
            session.dropTableIfExists(MyTable.class);
            session.createTable(MyTable.class);
            MyTable myTable = new MyTable();
            myTable.name = "123";
            myTable.stringList.add("123");
            session.insertBulk(myTable);
            list = session.getList(MyTable.class, null);

            assert list.size() == 1;
            session.delete(list.get(0));
            list = session.getList(MyTable.class, null);
        }
        assert list.size()==0;


    }

    @Test
    void deleteRows() throws Exception {
        List<MyTable> list;
        try (ISession session = Configure.getSession()) {
            session.dropTableIfExists(MyTable.class);
            session.createTable(MyTable.class);
            MyTable myTable = new MyTable();
            myTable.name = "123";
            myTable.stringList.add("123");
            session.insertBulk(myTable);
            list = session.getList(MyTable.class, null);
            assert list.size() == 1;
            session.deleteRows(MyTable.class, "id=?", list.get(0).id);
            list = session.getList(MyTable.class, null);
            assert list.size()==0;
        }



    }
    @Test
    void count() throws Exception {
        int count;
        try (ISession session = Configure.getSession()) {
            session.dropTableIfExists(MyTable.class);
            session.createTable(MyTable.class);
            MyTable myTable = new MyTable();
            myTable.name = "123";
            myTable.stringList.add("123");
            session.insertBulk(myTable);
            count = session.count(MyTable.class);
        }
        assert count==1;
    }
    @Test
    void countWhere() throws Exception {
        int count;
        try (ISession session = Configure.getSession()) {
            session.dropTableIfExists(MyTable.class);
            session.createTable(MyTable.class);
            MyTable myTable = new MyTable();
            myTable.name = "123";
            myTable.stringList.add("123");
            session.insertBulk(myTable);
            count = session.count(MyTable.class, "name=?", myTable.name);
        }
        assert count==1;
    }

    @Test
    void any() throws Exception {
        boolean any;
        try (ISession session = Configure.getSession()) {
            session.dropTableIfExists(MyTable.class);
            session.createTable(MyTable.class);
            MyTable myTable = new MyTable();
            myTable.name = "123";
            myTable.stringList.add("123");
            session.insertBulk(myTable);
            any = session.any(MyTable.class, "name=?", myTable.name);
        }
        assert any;
    }

    @Test
    void any2() throws Exception {
        boolean any;
        try (ISession session = Configure.getSession()) {
            session.dropTableIfExists(MyTable.class);
            session.createTable(MyTable.class);
            MyTable myTable = new MyTable();
            myTable.name = "123";
            myTable.stringList.add("123");
            session.insertBulk(myTable);
            any = session.any(MyTable.class, null);
        }
        assert any;
    }
    @Test
    void firestOrDefault() throws Exception {
        MyTable table;
        try (ISession session = Configure.getSession()) {
            session.dropTableIfExists(MyTable.class);
            session.createTable(MyTable.class);
            MyTable myTable = new MyTable();
            myTable.name = "123";
            myTable.stringList.add("123");
            session.insertBulk(myTable);
            table = session.firstOrDefault(MyTable.class, "name=?", myTable.name);
        }
        assert table!=null;
    }
    @Test
    void firestOrDefault1() throws Exception {
        MyTable table;
        try (ISession session = Configure.getSession()) {
            session.dropTableIfExists(MyTable.class);
            session.createTable(MyTable.class);
            MyTable myTable = new MyTable();
            myTable.name = "123";
            myTable.stringList.add("123");
            session.insertBulk(myTable);
            table = session.firstOrDefault(MyTable.class, null);
        }
        assert table!=null;
    }

    @Test
    void getListSelect() throws Exception {
        List<Integer> list;
        try (ISession session = Configure.getSession()) {
            session.dropTableIfExists(MyTable.class);
            session.createTable(MyTable.class);
            MyTable myTable = new MyTable();
            myTable.name = "123";
            myTable.stringList.add("123");
            session.insertBulk(myTable);
            list = session.getListSelect(MyTable.class, "stringList", null);
        }
        assert list.size()==1;
    }

    @Test
    void groupBy() throws Exception {
        Map<Object, List<MyTable>> map;
        try (ISession session = Configure.getSession()) {
            session.dropTableIfExists(MyTable.class);
            session.createTable(MyTable.class);
            MyTable myTable = new MyTable();
            myTable.name = "123";
            myTable.stringList.add("123");
            session.insertBulk(myTable);
            map = session.groupBy(MyTable.class, "name", null);
        }
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

    @Test
    void transaction() throws Exception {

        try (ISession session = Configure.getSession()) {
            session.dropTableIfExists(MyTable.class);
            session.createTable(MyTable.class);
            session.beginTransaction();
            MyTable myTable = new MyTable();
            myTable.name = "123";
            myTable.stringList.add("123");
            session.insertBulk(myTable);
            session.rollbackTransaction();
            List<MyTable> list=Configure.getSessionAuto().getList(MyTable.class,null);
            assert  list.size()==0;

        }
    }
    @Test
    void transaction1() throws Exception {

        try (ISession session = Configure.getSession()) {
            session.dropTableIfExists(MyTable.class);
            session.createTable(MyTable.class);
            session.beginTransaction();
            MyTable myTable = new MyTable();
            myTable.name = "123";
            myTable.stringList.add("123");
            session.insertBulk(myTable);
            session.commitTransaction();
            List<MyTable> list=Configure.getSessionAuto().getList(MyTable.class,null);
            assert  list.size()==1;

        }
    }

    @Test
    void transaction2() throws Exception {

        try (ISession session = Configure.getSession()) {
            session.dropTableIfExists(MyTable.class);
            session.createTable(MyTable.class);
            session.beginTransaction();
            MyTable myTable = new MyTable();
            myTable.name = "123";
            myTable.stringList.add("123");
            session.insertBulk(myTable);

            List<MyTable> list=Configure.getSessionAuto().getList(MyTable.class,null);
            assert  list.size()==0;
            session.commitTransaction();
            list=Configure.getSessionAuto().getList(MyTable.class,null);
            assert  list.size()==1;

        }
    }


}

