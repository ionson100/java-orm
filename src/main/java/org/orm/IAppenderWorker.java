package org.orm;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;


/**
 * Allows you to plan your database storage strategy
 */
public interface IAppenderWorker {


    /**
     * Get the string value for insertion into the database
     * @param o  Object field value
     * @param params List of parameters for insertion into the database
     * @param connection    jdbc connection
     * @return string value for insertion into the database
     */
    String toBase(Object o, List<Object> params, Connection connection);

    /** get the field of object from the result set
     * @param resultSet     ResultSet
     * @param index         index of the field in the result set
     * @return Object field value from the result set
     */
    Object fromBase(ResultSet resultSet, int index);
}
