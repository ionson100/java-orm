package org.orm;

import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNull;

class TestType {

    static {
        Configure.IsWriteConsole=true;
        new Configure(TypeDataBase.SQLITE,"test",null,null);
    }

    @MapTable
    static class MyShort{
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
    void shortF() throws Exception {
        MyShort o;
        try (ISession session = Configure.getSession()) {
            session.dropTableIfExists(MyShort.class);
            session.createTableIfNotExists(MyShort.class);
            {
                MyShort myShort = new MyShort();
                myShort.aShort = 1;
                myShort.aShortb2 = 1;
                myShort.aShortb = null;
                myShort.id = 0;
                session.insert(myShort);
            }
            {
                MyShort myShort = new MyShort();
                myShort.aShort = 1;
                myShort.aShortb2 = 1;
                myShort.aShortb = null;
                myShort.id = 0;
                session.insertBulk(myShort);
            }
            o = session.firstOrDefault(MyShort.class, null);
            session.update(o);
        }

        assert o.aShort==1;
        assert o.aShortb2==1;


    }
    @MapTable
    static class MyInt{
        @MapPrimaryKey
        public int id;
        @MapColumn
        public int anInt;
        @MapColumn
        public Integer anInteger;
        @MapColumn
        public Integer anInteger2;

    }
    @Test
    void intF() throws Exception {
        MyInt o;
        try (ISession session = Configure.getSession()) {
            session.dropTableIfExists(MyInt.class);
            session.createTableIfNotExists(MyInt.class);
            {
                MyInt myInt = new MyInt();
                myInt.anInt = 1;
                myInt.anInteger2 = 1;
                session.insert(myInt);
            }
            {
                MyInt myInt = new MyInt();
                myInt.anInt = 1;
                myInt.anInteger2 = 1;
                session.insertBulk(myInt);
            }
            o = session.firstOrDefault(MyInt.class, null);
            session.update(o);
        }
        assert o.anInt==1;
        assertNull(o.anInteger);
        assert o.anInteger2==1;
    }

    @MapTable
    static class MyLong{
        @MapPrimaryKey
        public int id;
        @MapColumn
        public long aLongM;
        @MapColumn
        public Long aLong;
        @MapColumn
        public Long aLong2;
    }
    @Test
    void longF() throws Exception {
        MyLong o;
        try (ISession session = Configure.getSession()) {
            session.dropTableIfExists(MyLong.class);
            session.createTableIfNotExists(MyLong.class);
            {
                MyLong myLong = new MyLong();
                myLong.aLongM = 1L;
                myLong.aLong2 = 1L;
                session.insert(myLong);
            }
            {
                MyLong myLong = new MyLong();
                myLong.aLongM = 1L;
                myLong.aLong2 = 1L;
                session.insertBulk(myLong);
            }
            o = session.firstOrDefault(MyLong.class, null);
            session.update(o);
        }
        assert o.aLongM==1L;
        assertNull(o.aLong);
        assert o.aLong2==1L;
    }

    @MapTable
    static class MyFloat{
        @MapPrimaryKey
        public int id;
        @MapColumn
        public float aFloat;
        @MapColumn
        public Float aFloat2;
        @MapColumn
        public Float aFloat3;
    }
    @Test
    void floatF() throws Exception {
        MyFloat o;
        try (ISession session = Configure.getSession()) {
            session.dropTableIfExists(MyFloat.class);
            session.createTableIfNotExists(MyFloat.class);
            {
                MyFloat myFloat = new MyFloat();
                myFloat.aFloat = 1;
                myFloat.aFloat2 = 1F;
                session.insert(myFloat);
            }
            {
                MyFloat myFloat = new MyFloat();
                myFloat.aFloat = 1;
                myFloat.aFloat2 = 1F;
                session.insertBulk(myFloat);
            }
            o = session.firstOrDefault(MyFloat.class, null);
            session.update(o);
        }
        assert o.aFloat==1;
        assertNull(o.aFloat3);
        assert o.aFloat2==1F;
    }
    @MapTable
    static class MyDouble{
        @MapPrimaryKey
        public int id;
        @MapColumn
        public double aDouble;
        @MapColumn
        public Double aDouble2;
        @MapColumn
        public Double aDouble3;
    }
    @Test
    void doubleF() throws Exception {
        MyDouble o;
        try (ISession session = Configure.getSession()) {
            session.dropTableIfExists(MyDouble.class);
            session.createTableIfNotExists(MyDouble.class);
            {
                MyDouble myDouble = new MyDouble();
                myDouble.aDouble = 1;
                myDouble.aDouble2 = 1D;
                session.insert(myDouble);
            }
            {
                MyDouble myDouble = new MyDouble();
                myDouble.aDouble = 1;
                myDouble.aDouble2 = 1D;
                session.insertBulk(myDouble);
            }
            o = session.firstOrDefault(MyDouble.class, null);
            session.update(o);
        }
        assert o.aDouble==1;
        assertNull(o.aDouble3);
        assert o.aDouble2==1D;
    }
    @MapTable
    static class MyString{
        @MapPrimaryKey
        public int id;
        @MapColumn
        public String aString;
        @MapColumn
        public String aString2;

    }
    @Test
    void stringF() throws Exception {
        MyString o;
        try (ISession session = Configure.getSession()) {
            session.dropTableIfExists(MyString.class);
            session.createTableIfNotExists(MyString.class);
            {
                MyString myString = new MyString();
                myString.aString = "1";
                session.insert(myString);
            }
            {
                MyString myString = new MyString();
                myString.aString = "1";
                session.insertBulk(myString);
            }
            o = session.firstOrDefault(MyString.class, null);
            session.update(o);
        }
        assert o.aString.equals("1");
        assertNull(o.aString2);
    }
    @MapTable
    static class MyBoolean{
        @MapPrimaryKey
        public int id;
        @MapColumn
        public boolean aBoolean;
        @MapColumn
        public Boolean aBoolean2;
        @MapColumn
        public Boolean aBoolean3;
    }
    @Test
    void booleanF() throws Exception {
        MyBoolean o;
        try (ISession session = Configure.getSession()) {
            session.dropTableIfExists(MyBoolean.class);
            session.createTableIfNotExists(MyBoolean.class);
            {
                MyBoolean myBoolean = new MyBoolean();
                myBoolean.aBoolean = true;
                myBoolean.aBoolean2 = false;
                session.insert(myBoolean);
            }
            {

                MyBoolean myBoolean = new MyBoolean();
                myBoolean.aBoolean = true;
                myBoolean.aBoolean2 = false;
                session.insertBulk(myBoolean);
            }
            o = session.firstOrDefault(MyBoolean.class, null);
            session.update(o);
        }
        assert o.aBoolean==true;
        assertNull(o.aBoolean3);
        assert o.aBoolean2==false;
    }
    static class MyObject implements Serializable {
        public  String name;
    }

    @MapTable
    static class MyObjectTest implements Serializable {
        @MapPrimaryKey
        public int id;

        @MapColumn
        public MyObject object;
        @MapColumn
        public UUID uuid1;
        @MapColumn
        public UUID uuid2;
        @MapColumn
        public BigDecimal bigDecimal1;
        @MapColumn
        public BigDecimal bigDecimal2;
        @MapColumn
        @MapColumnJson
        public MyObject objectJson;
    }
    @Test
    void objectF() throws Exception {
        MyObjectTest o;
        try (ISession session = Configure.getSession()) {
            session.dropTableIfExists(MyObjectTest.class);
            session.createTableIfNotExists(MyObjectTest.class);
            {
                MyObjectTest myObjectTest = new MyObjectTest();
                myObjectTest.object = new MyObject();
                myObjectTest.objectJson = new MyObject();
                myObjectTest.object.name = "1";
                myObjectTest.objectJson.name = "2";
                myObjectTest.uuid1 = UUID.randomUUID();
                //myObjectTest.uuid2=UUID.randomUUID();
                myObjectTest.bigDecimal1 = BigDecimal.valueOf(1);
                session.insert(myObjectTest);
            }
            {
                MyObjectTest myObjectTest = new MyObjectTest();
                myObjectTest.object = new MyObject();
                myObjectTest.objectJson = new MyObject();
                myObjectTest.object.name = "1";
                myObjectTest.objectJson.name = "2";
                myObjectTest.uuid1 = UUID.randomUUID();
                session.insertBulk(myObjectTest);
            }
            o = session.firstOrDefault(MyObjectTest.class, null);
            session.update(o);
        }
        assert o.object.name.equals("1");
        assert  o.objectJson.name.equals("2");

        assertNull(o.bigDecimal2);
    }



}

