package org.orm;

import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

public class TestEventOrm {
    static {
        Configure.IsWriteConsole=true;
        new Configure(TypeDataBase.SQLITE,"test",null,null);
    }

    @MapTable
    static class MyTable extends Persistent implements IEventOrm{
        int event=0;

        @MapPrimaryKey
        public int id;

        @MapColumnName("myDate")
        public Date date=new Date();

        @Override
        public void beforeUpdate() {
            PrintConsole.print("beforeUpdate");
            this.event=1000;
        }

        @Override
        public void afterUpdate() {
            PrintConsole.print("afterUpdate");
            this.event=this.event+1000;
        }

        @Override
        public void beforeInsert() {
            PrintConsole.print("beforeInsert");
            this.event=100;
        }

        @Override
        public void afterInsert() {
            PrintConsole.print("beforeInsert");
            this.event=this.event+100;
        }

        @Override
        public void beforeDelete() {
            PrintConsole.print("beforeDelete");
            this.event=200;
        }

        @Override
        public void afterDelete() {
            PrintConsole.print("beforeDelete");
            this.event=this.event+200;
        }
    }

    //public Date(int year, int month, int date) {
    //        this(year, month, date, 0, 0, 0);
    //    }
    @Test
    void testDate() throws Exception {

        try (ISession session = Configure.getSession()) {
            session.dropTableIfExists(MyTable.class);
            session.createTable(MyTable.class);
            MyTable myTable = new MyTable();
            session.insert(myTable);
            assert myTable.event == 200;
            assert myTable.isPersistent==true;
            session.update(myTable);
            assert myTable.event == 2000;
            assert myTable.isPersistent==true;
            session.delete(myTable);
            assert myTable.event == 400;


        }

    }
}
