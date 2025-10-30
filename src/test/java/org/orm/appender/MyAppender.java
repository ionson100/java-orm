package org.orm.appender;

import com.google.gson.Gson;
import org.orm.IAppenderWorker;
import org.postgresql.util.PGobject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class MyAppender implements IAppenderWorker {

    @Override
    public String toBase(Object o, List<Object> params, Connection connection) {
        MyUser2 myUser2 = (MyUser2) o;
        PGobject jsonObject = new PGobject();
        jsonObject.setType("json");
        try {
            jsonObject.setValue(new Gson().toJson(myUser2, MyUser2.class));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        params.add(jsonObject);
        return "?";
    }

    @Override
    public Object fromBase(ResultSet resultSet, int index) {

        try {
            String  json = resultSet.getString(index);
            MyUser2 myUser2 = new Gson().fromJson(json, MyUser2.class);
            return myUser2;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
