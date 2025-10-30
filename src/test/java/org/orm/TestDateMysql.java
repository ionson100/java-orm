package org.orm;

import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

public class TestDateMysql {
    static {
        Configure.IsWriteConsole=true;
        Configure.addConfigure("my",TypeDataBase.MYSQL,"localhost:3306/test","root","12345");
    }

    @MapTableSessionKey("my")
    @MapTable
    static class MyTable{
        @MapPrimaryKey
        public int id;

        @MapColumnName("myDate")
        public Date date=new Date();
    }

    //public Date(int year, int month, int date) {
    //        this(year, month, date, 0, 0, 0);
    //    }
    @Test
    void testDate() throws Exception {
        List<MyTable> myTable;
        try (ISession session = Configure.getSession("my")) {
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
            myTable = session.getList(MyTable.class, " `myDate` < ?", date);
            assert myTable.size() == 3;

            date = UtilsHelper.addingDate(new Date(), -5, AddingDate.Days);
            myTable = session.getList(MyTable.class, " `myDate` < ?", date);
            //assert myTable.isEmpty();

            myTable = session.getList(MyTable.class, " `myDate` between ? and ? ", UtilsHelper.atStartOfDay(new Date()), UtilsHelper.atEndOfDay(new Date()));
        }
        assert myTable.size()==1;
    }
}
