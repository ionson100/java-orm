package org.orm;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TestFluent {
    static {
        Configure.IsWriteConsole = true;
        new Configure(TypeDataBase.SQLITE, "test", null, null);
    }

    @MapTableName("t22")
    static class MyTable {
        @MapPrimaryKey
        public int id;
        @MapColumn
        public String name;
        @MapColumn
        public int age;

        @MapColumnName("myDate")
        public LocalDateTime date = LocalDateTime.now();
    }

    @Test
    void test() throws Exception {
        Object t;
        try (ISession session = Configure.getSession()) {
            session.query(MyTable.class).dropTableIfExists();
            session.query(MyTable.class).createTableIfNotExists();
            List<MyTable> list = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                MyTable myTable = new MyTable();
                myTable.name = "name" + i;
                myTable.age = i;
                list.add(myTable);
            }
            session.insertBulk(list);
            var l = session.query(MyTable.class).toList();
            assert l.size() == 10;
            l = session.query(MyTable.class).where("age>?", 1).toList();
            assert l.size() == 8;
            l = session.query(MyTable.class)
                    .where("age>?", 1)
                    .where("name =? ", "name4")
                    .orderBy("name")
                    .toList();
            assert l.size() == 1;
            t = session.query(MyTable.class).count();
        }
        assert (int) t == 10;

    }

    @Test
    void testAsync() throws Exception {

        try (ISession session = Configure.getSession()) {
            session.query(MyTable.class).dropTableIfExists();
            session.query(MyTable.class).createTableIfNotExists();
            List<MyTable> list = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                MyTable myTable = new MyTable();
                myTable.name = "name" + i;
                myTable.age = i;
                list.add(myTable);
            }
            session.insertBulk(list);

        }
        List<MyTable> l =new ArrayList<>();
        Configure.getSessionAuto().query(MyTable.class).toListAsync().thenAccept(myTables -> {
            for (MyTable myTable : myTables) {
                l.add(myTable);
            }
        });
        System.out.println("**************************"+l.size()+"***********************");

    }




    @Test
    public void  TestList() throws Exception {
        java.util.concurrent.CompletableFuture<List<MyTable>> f;
        try (ISession session = Configure.getSession()) {
            extracted(session);
            List<MyTable> l =new ArrayList<>();
            session.query(MyTable.class).toListAsync().thenAccept(myTables -> {
                for (MyTable myTable : myTables) {
                    l.add(myTable);
                }
            });
            assert(l.size()==0);
            Thread.sleep(100);
            assert(l.size()==10);
        }

    }
    @Test
    public void  TestSelect() throws Exception {
        java.util.concurrent.CompletableFuture<List<MyTable>> f;
        try (ISession session = Configure.getSession()) {
            extracted(session);
            List<Object> l =new ArrayList<>();
            session.query(MyTable.class)
                    .where("age>?", -1)
                    .where("name not null ")
                    .orderBy("name")
                    .selectAsync("name").thenAccept(objects -> {
                for (Object object : objects) {
                    PrintConsole.print(object.toString());
                    l.add(objects);
                }
            });
            assert(l.size()==0);
            Thread.sleep(100);
            assert(l.size()==10);
        }

    }
    @Test
    public void  TestSelectExpession() throws Exception {
        java.util.concurrent.CompletableFuture<List<MyTable>> f;
        try (ISession session = Configure.getSession()) {
            extracted(session);
            List<Object> l =new ArrayList<>();
            session.query(MyTable.class)
                    .where("age>?", -1)
                    .where("name not null ")
                    .orderBy("name")
                    .selectExpressionAsync("age*10").thenAccept(objects -> {
                        for (Object object : objects) {
                            PrintConsole.print(object.toString());
                            l.add(objects);
                        }
                    });
            assert(l.size()==0);
            Thread.sleep(100);
            assert(l.size()==10);
        }

    }
    @Test
    public void TestSelectDistinct() throws Exception {
        java.util.concurrent.CompletableFuture<List<MyTable>> f;
        try (ISession session = Configure.getSession()) {
            extracted(session);
            List<Object> l =new ArrayList<>();
            session.query(MyTable.class)
                    .where("age>?", -1)
                    .where("name not null ")
                    .orderBy("name")
                    .distinctByAsync("name").thenAccept(objects -> {
                        for (Object object : objects) {
                            PrintConsole.print(object.toString());
                            l.add(objects);
                        }
                    });
            assert(l.size()==0);
            Thread.sleep(100);
            assert(l.size()==10);
        }

    }
    @Test
    public void TestSelectGroupBy() throws Exception {
        java.util.concurrent.CompletableFuture<List<MyTable>> f;
        try (ISession session = Configure.getSession()) {
            extracted(session);
            List<Object> l =new ArrayList<>();
            session.query(MyTable.class)
                    .where("age>?", -1)
                    .where("name not null ")
                    .orderBy("name")
                    .groupByAsync("name").thenAccept(objects -> {
                        objects.forEach((k,v)->{
                            for (MyTable object : v) {
                                PrintConsole.print(object.age + " " +object.name);
                                l.add(object.age);
                            }

                        });
                    });
            assert(l.size()==0);
            Thread.sleep(100);
            assert(l.size()==10);
        }

    }

    @Test
    public void  TestFirst() throws Exception {
        java.util.concurrent.CompletableFuture<List<MyTable>> f;
        try (ISession session = Configure.getSession()) {
            extracted(session);
            List<Object> l =new ArrayList<>();
            session.query(MyTable.class)
                    .where("age>?", -1)
                    .where("name not null ")
                    .orderBy("name")
                    .firstOrDefaultAsync().thenAccept(objects -> {
                        l.add(objects);
                    });
            assert(l.size()==0);
            Thread.sleep(100);
            assert(l.size()==1);
        }

    }
    @Test
    public void  TestSingle() throws Exception {
        java.util.concurrent.CompletableFuture<List<MyTable>> f;
        try (ISession session = Configure.getSession()) {
            extracted(session);
            List<Object> l =new ArrayList<>();
            session.query(MyTable.class)
                    .where("age=?", 2)
                    .where("name not null ")
                    .orderBy("name")
                    .firstOrDefaultAsync().thenAccept(objects -> {
                        l.add(objects);
                    });
            assert(l.size()==0);
            Thread.sleep(100);
            assert(l.size()==1);
        }

    }

    private static void extracted(ISession session) throws Exception {
        session.query(MyTable.class).dropTableIfExists();
        session.query(MyTable.class).createTableIfNotExists();
        for (int i = 0; i < 10; i++) {
            MyTable myTable = new MyTable();
            myTable.age = i;
            myTable.name = "name" + i;
            session.insert(myTable);
        }
    }

}
