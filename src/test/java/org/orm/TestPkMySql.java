package org.orm;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestPkMySql {
    static {
        Configure.IsWriteConsole=true;
        Configure.IsWriteConsole=true;
        Configure.addConfigure("my",TypeDataBase.MYSQL,"localhost:3306/test","root","12345");
        //new Configure(TypeDataBase.MYSQL,"test","root","12345");
    }

    //@MapAppendCommandCreateTable()
    @MapTableSessionKey("my")
    @MapTableName("t22")
    @MapTableTypeMySql("ENGINE=InnoDB DEFAULT CHARSET=utf8mb4")
    static class MyTable {
        @MapPrimaryKey
        public UUID id = UUID.randomUUID();
        @MapColumn
        @MapColumnIndex
        public String name;

        @MapColumn
        public int age;

        @MapColumnName("myDate")
        public LocalDateTime date = LocalDateTime.now();
    }

    @Test
    void test() throws Exception {
        Object t;
        try (ISession session = Configure.getSession("my")) {
            session.query(MyTable.class).dropTableIfExists();
            session.query(MyTable.class).createTableIfNotExists();

            List<MyTable> list = new ArrayList<>();
            for (int i = 0; i < 1; i++) {
                MyTable myTable = new MyTable();
                myTable.name = "name" + i;
                myTable.age = i;
                list.add(myTable);
            }
            session.insertBulk(list);
            var obj = session.query(MyTable.class).getByPrimaryKey(list.get(0).id);
            assert obj != null;

        }


    }




}
