package org.orm;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Date;

class TestOtherPostgres {

    static {
        Configure.IsWriteConsole=true;
        Configure.addConfigure("pg",TypeDataBase.POSTGRESQL,"localhost:5432/test","user","postgres");
    }

    @MapTableSessionKey("pg")
    @MapTable
    static class MyShort extends Persistent{
        @MapPrimaryKey
        public int id;

        @MapColumn
        public short  aShort;

        @MapColumn
        public Short aShortb;
        @MapColumn
        public Short aShortb2;

        @MapColumn
        public Date date;

        @MapColumn
        public LocalDate localDate;



    }
    @Test
    void testShort() throws Exception {
        ISession session = Configure.getSession("pg");
        session.dropTableIfExists(MyShort.class);
        session.createTable(MyShort.class);
        MyShort myShort = new MyShort();
        myShort.aShort = 1;
        myShort.date=new Date();
        myShort.localDate=LocalDate.now();
        session.save(myShort);
        MyShort o=session.firstOrDefault(MyShort.class,null);
        assert o.aShort==1;
        session.updateRows(MyShort.class,new PairColumnValue().put("aShortb",123),null);
        o=session.firstOrDefault(MyShort.class,null);
        o.aShortb2=222;
        assert o.date.toString().equals(myShort.date.toString());

        assert o.aShortb==123;
        session.save(o);
        o=session.firstOrDefault(MyShort.class,null);
        assert o.aShortb==123;
    }



}

