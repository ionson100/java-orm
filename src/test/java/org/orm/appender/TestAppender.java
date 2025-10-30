package org.orm.appender;


import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.orm.*;

@Disabled("Отключено по причине:")
public class TestAppender {
    static {
        Configure.IsWriteConsole = true;
        Configure.addAppender("MyUser2", new MyAppender());
        new Configure(TypeDataBase.POSTGRESQL, "localhost:5432/test", "user", "postgres");
    }


    @MapTable
    public static class MyTable  extends Persistent  {
        @MapPrimaryKey
        public int id;
        @MapColumnAppenderKey("MyUser2")
        @MapColumn
        @MapColumnType("JSONB")
        public MyUser2 json;
    }
    public static class MyTablePartial {


        public int id;
        public MyUser2 json;
    }
    @Test
    void test() throws Exception {
        ISession session = Configure.getSession();
        session.dropTableIfExists(MyTable.class);
        session.createTable(MyTable.class);
        {
            MyTable myTable = new MyTable();
            myTable.id = 1;
            myTable.json = new MyUser2() ;
            myTable.json.name = "name";
            myTable.json.age = 12;
            session.save(myTable);

        }
        {
            MyTable myTable = new MyTable();
            myTable.id = 1;
            myTable.json = new MyUser2() ;
            myTable.json.name = "name";
            myTable.json.age = 12;
            session.insertBulk(myTable);

        }
        var count = session.query(MyTable.class).count();
        assert (long)count == 2L;
        var list = session.query(MyTable.class).toList();
        assert (list.size() == 2);
        for (var item : list) {
            if (item.id == 1) {
                assert item.json.name.equals("name");
                assert item.json.age == 12;
            }
        }
        for (var item : list) {
            item.json.age=20;
            session.update(item);
        }
        list = session.query(MyTable.class).toList();
        for (var item : list) {
            if (item.id == 1) {
                assert item.json.name.equals("name");
                assert item.json.age == 20;
            }
        }
        session.query(MyTable.class).update("json", new MyUser2()).updateNow();
        list = session.query(MyTable.class).toList();
        for (var item : list) {
            if (item.id == 1) {
                assert item.json.name.equals("one");
                assert item.json.age == 1;
            }
        }
        var listUser=session.query(MyTable.class).select("json");
        String sql="select * from "+session.getTableName(MyTable.class);
        var l=session.getListFree(MyTablePartial.class, sql);
        for (var item : l) {
            if (item.id == 1) {
                assert item.json.name.equals("one");
                assert item.json.age == 1;
            }
        }
        l=session.query(MyTablePartial.class).rawSqlSelect(sql).toList();
        for (var item : l) {
            if (item.id == 1) {
                assert item.json.name.equals("one");
                assert item.json.age == 1;
            }
        }


    }


}

