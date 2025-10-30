package org.orm;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestPk {
    static {
        Configure.IsWriteConsole = true;
        new Configure(TypeDataBase.SQLITE, "test", null, null);
    }

    @MapTableName("t22")
    static class MyTable {
        @MapPrimaryKey
        public UUID id = UUID.randomUUID();
        @MapColumn
        public String name;

        @MapColumnIndex
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
