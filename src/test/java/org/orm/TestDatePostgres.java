package org.orm;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class TestDatePostgres {
    static {
        Configure.IsWriteConsole=true;
        Configure.addConfigure("pg",TypeDataBase.POSTGRESQL,"localhost:5432/test","user","postgres");
    }

    @MapTableSessionKey("pg")
    @MapTable
    static class MyTable{
        @MapPrimaryKey
        public int id;

        @MapColumnName("myDate")
        @MapColumnType("TIMESTAMP WITH TIME ZONE")
        public Date date=new Date();
    }

    //public Date(int year, int month, int date) {
    //        this(year, month, date, 0, 0, 0);
    //    }
    @Test
    void testDate() throws Exception {
        LocalDateTime localDateTime=LocalDateTime.of(2020,1,1,12,12,12);
        List<MyTable> myTable;
        try (ISession session = Configure.getSession("pg")) {
            session.dropTableIfExists(MyTable.class);
            session.createTable(MyTable.class);
            {
                MyTable myTable1 = new MyTable();
                myTable1.date = UtilsHelper.addingDate(new Date(), -1, AddingDate.Days);
                session.insertBulk(myTable1);
            }
            {
                MyTable myTable2 = new MyTable();
                myTable2.date = new Date();
                session.insertBulk(myTable2);
            }
            {
                MyTable myTable3 = new MyTable();
                myTable3.date = UtilsHelper.addingDate(new Date(), 1, AddingDate.Days);
                session.insertBulk(myTable3);
            }
            Date date = UtilsHelper.addingDate(new Date(), 5, AddingDate.Days);
            myTable = session.getList(MyTable.class, " \"myDate\" < ?", date);
            assert myTable.size() == 3;

            date = UtilsHelper.addingDate(new Date(), -5, AddingDate.Days);
            myTable = session.getList(MyTable.class, " \"myDate\" < ?", date);
            assert myTable.isEmpty();

            myTable = session.getList(MyTable.class, " \"myDate\" between ? and ? ", UtilsHelper.atStartOfDay(new Date()), UtilsHelper.atEndOfDay(new Date()));
        }
        assert myTable.size()==1;
    }
}
