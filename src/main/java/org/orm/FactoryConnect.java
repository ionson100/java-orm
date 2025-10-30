package org.orm;

import com.zaxxer.hikari.HikariDataSource;

 class FactoryConnect {
    public  HikariDataSource dataSourcePostgres;
    public  HikariDataSource dataSourceMySql;
    public  HikariDataSource dataSourceSqlite;
    public TypeDataBase typeDataBase = TypeDataBase.SQLITE;

}
