package org.orm;

import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNull;

class TestOther {

    static {
        Configure.IsWriteConsole=true;
        new Configure(TypeDataBase.SQLITE,"test",null,null);
    }

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



    }
    @Test
    void testShort() throws Exception {
        ISession session = Configure.getSession();
        session.dropTableIfExists(MyShort.class);
        session.createTable(MyShort.class);
        MyShort myShort = new MyShort();
        myShort.aShort = 1;
        session.save(myShort);
        MyShort o=session.firstOrDefault(MyShort.class,null);
        assert o.aShort==1;
        session.updateRows(MyShort.class,new PairColumnValue().put("aShortb",123),null);
        o=session.firstOrDefault(MyShort.class,null);
        o.aShortb2=222;

        assert o.aShortb==123;
        session.save(o);
        o=session.firstOrDefault(MyShort.class,null);
        assert o.aShortb==123;
    }



}

