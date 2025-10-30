package org.orm;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.jetbrains.annotations.NotNull;

import org.jetbrains.annotations.Nullable;
import org.sqlite.JDBC;

import java.lang.reflect.Field;
import java.sql.*;
import java.text.MessageFormat;
import java.util.*;
import java.util.Date;

import static org.orm.Utils.getStringListSqlCreateTable;
import static org.orm.UtilsCompound.builderInstance;
import static org.orm.UtilsCompound.extractedSwitchSelect;


/**
 * Session Factory
 */
public class Configure implements ISession {

    private static final Map<String,FactoryConnect> mapFactory= new HashMap<>();
    private String dbMysql;

    private static final Map<String, IAppenderWorker> mapAppenderWorker= new HashMap<>();

    /**
     * Add appender IAppenderWorker
     * @param key key name
     * @param value IAppenderWorker object
     */
    public static void addAppender(String key, IAppenderWorker value) {
        mapAppenderWorker.put(key,value);
    }

    /**
     * Delete all  appender IAppenderWorker
     *
     */
    public static void clearAppender() {
        mapAppenderWorker.clear();
    }

    /**
     * Get appender IAppenderWorker
     * @param key name
     * @return IAppenderWorker
     */
    public static @Nullable IAppenderWorker getAppenderWorker(String key) {
        if(mapAppenderWorker.containsKey(key)){
            return mapAppenderWorker.get(key);
        }else{
            return null;
        }
    }

    void closeAuto(){
        if(isAutoClose){
            try {
                if(!this.connection.isClosed())
                     this.connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Adds a connection to an existing one
     * @param key - key session
     * @param dataBase - type database
     * @param connectString - connection string
     * @param user - user
     * @param password   - password
     *
     * <pre>
     * {@code
     * // pre start orm
     *  Configure.IsWriteConsole=true;
     *  Configure.addConfigure("pg",TypeDataBase.POSTGRESQL,"localhost:5432/test","postgres","postgres")
     *  Configure.addConfigure("my",TypeDataBase.MYSQL,"localhost:3306/test","root","12345");
     *
     *  //start orm, connection default to sqlite
     *   new Configure(TypeDataBase.SQLITE,"test",null,null);
     *
     *     @MapTableName("tFromSqlie")
     *     class MyTable1{
     *
     *         @MapPrimaryKey
     *         public int id;
     *
     *         @MapColumn
     *         public String name;
     *     }
     *
     *     @MapTableName("tFromPostgres")
     *     @MapTableSessionKey("pg")
     *     class MyTable2{
     *
     *         @MapPrimaryKey
     *         public int id;
     *
     *         @MapColumn
     *         public String name;
     *     }
     *
     *     @MapTableName("tFromMySql")
     *     @MapTableTypeMySql("ENGINE=InnoDB DEFAULT CHARSET=utf8mb4")
     *     @MapTableSessionKey("my")
     *     class MyTable3{
     *
     *         @MapPrimaryKey
     *         public int id;
     *
     *         @MapColumn
     *         public String name;
     *     }
     *     initBases();
     *     ISession session = Configure.getSession();
     *     Table<MyTable1> table1 = session.query(MyTable1.class).toList(); //from sqlite table
     *
     *     ISession session2 = Configure.getSession("pg");
     *     Table<MyTable2> table1 = session2.query(MyTable2.class).toList(); //from postgres table
     *
     *     ISession session3 = Configure.getSession("my");
     *     Table<MyTable3> table1 = session3.query(MyTable3.class).toList(); //from mysql table
     * }
     * </pre>
     */
    public static void addConfigure(String key, TypeDataBase dataBase, String connectString, String user, String password){
        if(!mapFactory.containsKey(key)){
            initConfig(key,dataBase,connectString,user,password);
        }

    }


    private String sessionKey=Utils.KEY_DEFAULT_SESSION;

    private  HikariDataSource dataSource;
    private   TypeDataBase typeDataBase = TypeDataBase.SQLITE;

    /**
     * Command to write all database queries to the console
     * <pre>
     * {@code
     *  Configure.IsWriteConsole=true;
     *  new Configure(TypeDataBase.SQLITE, "test", null, null);
     *  }
     * </pre>
     */
    public static boolean IsWriteConsole;
    //private static String CON_STR = "jdbc:sqlite:";

    static {
        try {
            JDBC jdb = new JDBC();
            DriverManager.registerDriver(jdb);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Connection connection;



    private boolean isAutoClose;


    /**
     * Getting configuration, session factories
     * @param dataBase - type database
     * @param connectString - connection string
     * @param user - user
     * @param password - password
     * <pre>
     * {@code
     *  Configure.IsWriteConsole=true;
     *  Configure.addConfigure("pg",TypeDataBase.POSTGRESQL,"localhost:5432/test","postgres","postgres")
     *  Configure.addConfigure("my",TypeDataBase.MYSQL,"localhost:3306/test","root","12345");
     *  new Configure(TypeDataBase.SQLITE, "test", null, null);
     *  }
     * </pre>
     *
      *
     */
    public Configure(TypeDataBase dataBase,String connectString,String user, String password) {
        if (mapFactory.containsKey(Utils.KEY_DEFAULT_SESSION)) return;
        initConfig(Utils.KEY_DEFAULT_SESSION,dataBase, connectString, user, password);

    }

    private static void initConfig(String key,TypeDataBase dataBase, String connectString, String user, String password) {
        FactoryConnect factoryConnect=new FactoryConnect();
        factoryConnect.typeDataBase=dataBase;
        switch (dataBase) {


            case POSTGRESQL:{

                String connectCore ="jdbc:postgresql://"+ connectString;
                HikariConfig config = getHikariConfig(connectCore, user, password);
                factoryConnect.dataSourcePostgres = new HikariDataSource(config);
                break;
            }
            case MYSQL:{
                String connectCore ="jdbc:mysql://"+ connectString;
                HikariConfig config = getHikariConfigMysql(connectCore, user, password);
                factoryConnect.dataSourceMySql = new HikariDataSource(config);
                break;

            }
            case SQLITE:{
                String connectCore ="jdbc:sqlite:"+ connectString;
                HikariConfig config = getHikariConfigSqLite(connectCore);
                factoryConnect.dataSourceSqlite = new HikariDataSource(config);
                break;

            }

            default:{
                throw new RuntimeException("Not implemented");
            }

        }
        mapFactory.put(key,factoryConnect);

        PrintConsole.print("Connection to SQLite database successful!");
    }
    private static @NotNull HikariConfig getHikariConfigMysql(String connectString,String user, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(connectString); // URL базы данных
        config.setUsername(user);                                     // Имя пользователя
        config.setPassword(password);                                // Пароль
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");              // Класс драйвера PostgreSQL
        config.setMaximumPoolSize(ConfigureConnectionPool.getMaximumPoolSize());
        config.addDataSourceProperty("cachePrepStmts", ConfigureConnectionPool.getCachePrepStmts());         // Кеширование подготовленных запросов
        config.addDataSourceProperty("prepStmtCacheSize", ConfigureConnectionPool.getPrepStmtCacheSize());       // Размер кеша
        config.addDataSourceProperty("prepStmtCacheSqlLimit", ConfigureConnectionPool.getPrepStmtCacheSqlLimit());   // Длина SQL-лимита
        return config;
    }

    private static @NotNull HikariConfig getHikariConfig(String connectString,String user, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(connectString); // URL базы данных
        config.setMaximumPoolSize(ConfigureConnectionPool.getMaximumPoolSize());
        config.setUsername(user);                                     // Имя пользователя
        config.setPassword(password);                                // Пароль
        config.setDriverClassName("org.postgresql.Driver");              // Класс драйвера PostgreSQL
        config.addDataSourceProperty("cachePrepStmts", ConfigureConnectionPool.getCachePrepStmts());         // Кеширование подготовленных запросов
        config.addDataSourceProperty("prepStmtCacheSize", ConfigureConnectionPool.getPrepStmtCacheSize());       // Размер кеша
        config.addDataSourceProperty("prepStmtCacheSqlLimit", ConfigureConnectionPool.getPrepStmtCacheSqlLimit());   // Длина SQL-лимита
        return config;
    }

    private static @NotNull HikariConfig getHikariConfigSqLite(String connectString) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(connectString); // Укажите путь к файлу базы данных
        config.setMaximumPoolSize(ConfigureConnectionPool.getMaximumPoolSize()); // Установите максимальный размер пула
//        config.setConnectionTimeout(ConfigureSqlite.getConnectionTimeout()); // Тайм-аут ожидания соединения
//        config.setIdleTimeout(ConfigureSqlite.getIdleTimeout()); // Тайм-аут простоя
//        config.setMaxLifetime(ConfigureSqlite.getMaxLifetime()); // Максимальное время жизни соединения   // Длина SQL-лимита
        return config;
    }



    private Configure(boolean isAutoClose) {

        this.isAutoClose = isAutoClose;

    }

    /**
     * Getting the default session
     * @return ISession object
     */
    public static @NotNull ISession getSession() {

        FactoryConnect factoryConnect=Configure.mapFactory.get(Utils.KEY_DEFAULT_SESSION);
        if(factoryConnect==null){
            throw new RuntimeException("Connection object not found");
        }
        Configure configure=new Configure(false);
        initSession(configure, factoryConnect);
        return configure;
    }

    /**
     * Get a Session object for one operation with the database  by key
     * @param key - key session
     * @return ISession object
     * <pre>
     * {@code
     * // pre start orm
     *  Configure.IsWriteConsole=true;
     *  Configure.addConfigure("pg",TypeDataBase.POSTGRESQL,"localhost:5432/test","postgres","postgres")
     *  Configure.addConfigure("my",TypeDataBase.MYSQL,"localhost:3306/test","root","12345");
     *
     *  //start orm, connection default to sqlite
     *   new Configure(TypeDataBase.SQLITE,"test",null,null);
     *
     *     @MapTableName("tFromSqlie")
     *     class MyTable1{
     *
     *         @MapPrimaryKey
     *         public int id;
     *
     *         @MapColumn
     *         public String name;
     *     }
     *
     *     @MapTableName("tFromPostgres")
     *     @MapTableSessionKey("pg")
     *     class MyTable2{
     *
     *         @MapPrimaryKey
     *         public int id;
     *
     *         @MapColumn
     *         public String name;
     *     }
     *
     *     @MapTableName("tFromMySql")
     *     @MapTableTypeMySql("ENGINE=InnoDB DEFAULT CHARSET=utf8mb4")
     *     @MapTableSessionKey("my")
     *     class MyTable3{
     *
     *         @MapPrimaryKey
     *         public int id;
     *
     *         @MapColumn
     *         public String name;
     *     }
     *     initBases();
     *     ISession session = Configure.getSession();
     *     Table<MyTable1> table1 = session.query(MyTable1.class).toList(); //from sqlite table
     *
     *     ISession session2 = Configure.getSession("pg");
     *     Table<MyTable2> table1 = session2.query(MyTable2.class).toList(); //from postgres table
     *
     *     ISession session3 = Configure.getSession("my");
     *     Table<MyTable3> table1 = session3.query(MyTable3.class).toList(); //from mysql table
     * }
     * </pre>
     */
    public static @NotNull ISession getSession(String key) {

        FactoryConnect factoryConnect=Configure.mapFactory.get(key);
        if(factoryConnect==null){
            throw new RuntimeException("Не найден объект соединения");
        }
        Configure configure=new Configure(false);
        configure.sessionKey=key;
        initSession(configure, factoryConnect);
        return configure;
    }

    private static void initSession(Configure configure, FactoryConnect factoryConnect) {

        configure.typeDataBase= factoryConnect.typeDataBase;
        switch (configure.typeDataBase) {
            case SQLITE: {
                try {
                    configure.dataSource= factoryConnect.dataSourceSqlite;
                    configure.connection= configure.dataSource.getConnection();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;
            }
            case POSTGRESQL:{
                try {
                    configure.dataSource= factoryConnect.dataSourcePostgres;
                    configure.connection= configure.dataSource.getConnection();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;

            }
            case MYSQL:{
                try {
                    configure.dataSource= factoryConnect.dataSourceMySql;
                    configure.connection= configure.dataSource.getConnection();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;

            }
            default:{
                throw new RuntimeException("Not implemented");
            }
        }
    }

    /**
     * Obtaining a session for one operation with the database, after the operation, the connection will be automatically closed
     * @return ISession object
     */
    public static ISession getSessionAuto() {

        FactoryConnect factoryConnect=Configure.mapFactory.get(Utils.KEY_DEFAULT_SESSION);
        if(factoryConnect==null){
            throw new RuntimeException("Connection object not found");
        }
        Configure configure=new Configure(true);
        configure.sessionKey=Utils.KEY_DEFAULT_SESSION;
        initSession(configure, factoryConnect);
        return configure;
    }

    /**
     * Obtaining a session for one operation with the database by key, after the operation, the connection will be automatically closed
     * @param key - key session
     * @return  ISession object
     */
    public static @NotNull ISession getSessionAuto(String key) {

        FactoryConnect factoryConnect=Configure.mapFactory.get(key);
        if(factoryConnect==null){
            throw new RuntimeException("Connection object not found");
        }
        Configure configure=new Configure(true);
        configure.sessionKey=key;
        initSession(configure, factoryConnect);
        return configure;
    }



    private static <T> @NotNull List<List<T>> partition1(Collection<T> members) {
        List<List<T>> res = new ArrayList<>();
        List<T> internal = new ArrayList<>();
        for (T member : members) {
            internal.add(member);
            if (internal.size() == 500) {
                res.add(internal);
                internal = new ArrayList<>();
            }
        }
        if (!internal.isEmpty()) {
            res.add(internal);
        }
        return res;
    }


    @Override
    public Object executeScalar(String sql, Object... parameters) {

        PrintConsole.print(sql);
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            parametrise(parameters, statement);

            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getObject(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            closeAuto();
        }

    }

    @Override
    public void execSQL(String sql, Object... parameters) {

        PrintConsole.print(sql,parameters);
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            parametrise(parameters, statement);
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        finally {
            closeAuto();
        }
    }
    private void execSQLInner(String sql, Object... parameters) {

        PrintConsole.print(sql,parameters);
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            parametrise(parameters, statement);
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void beginTransaction() throws SQLException {
        if(this.isAutoClose){
            throw new RuntimeException("A transaction cannot be used in a single-action session.(setSessionAuto)");
        }
        connection.setAutoCommit(false);
    }

    @Override
    public void commitTransaction() throws SQLException {
        if(this.isAutoClose){
            throw new RuntimeException("A transaction cannot be used in a single-action session.(setSessionAuto)");
        }
        connection.commit();
        connection.setAutoCommit(true);
    }

    @Override
    public void rollbackTransaction() throws SQLException {
        if(this.isAutoClose){
            throw new RuntimeException("A transaction cannot be used in a single-action session.(setSessionAuto)");
        }
        connection.rollback();
        connection.setAutoCommit(true);
    }

    private CacheMetaData<?> initCacheMetaData(Class<?> aClass){
        CacheMetaData<?> metaData=CacheDictionary.getCacheMetaData(aClass,typeDataBase);
        if(!sessionKey.equals(metaData.sessionKey)){
            throw new RuntimeException("You are not allowed to use this type: "+aClass.getName()+", this type is from another database." +
                    System.lineSeparator()+"It does not have the @MapSessionKey(nameKey) annotation.");
        }
        return metaData;
    }

    @Override
    public <T> String getTableName(Class<T> aClass) {
        try {
            CacheMetaData<?> metaData = initCacheMetaData(aClass);
            return metaData.tableName;
        }finally {
            closeAuto();
        }

    }

    @Override
    public <T> boolean tableExists(Class<T> aClass) {
        CacheMetaData<?> metaData = initCacheMetaData(aClass);
        String sql;
        switch (this.typeDataBase) {
            case SQLITE: {
                sql = "SELECT name FROM sqlite_master WHERE type='table' AND name= " + metaData.tableName+";";
              break;
            }
            case POSTGRESQL:{
                sql= "SELECT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = '"+metaData.tableNameRaw+"');";
                break;
            }
            case MYSQL:{
                if(this.dbMysql==null){
                    this.dbMysql= (String)executeScalar("SELECT DATABASE()");
                }

                sql= "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = '"+dbMysql+"' AND table_name = '"+metaData.tableNameRaw+"';";
                break;
            }
            default:{
                throw new RuntimeException("Not implemented");
            }
        }
        PrintConsole.print(sql);
        Object o = executeScalar(sql);
        switch (this.typeDataBase) {
            case SQLITE: {
                return o != null;
            }
            case POSTGRESQL:{
                if(o!=null){
                    return (boolean)o;
                }
            }
            case MYSQL:{
                return (long)o>0;
            }
            default:{
                throw new RuntimeException("Not implemented");
            }
        }


    }

    @Override
    public void dropTableIfExists(String tableName) {
        if (tableName.trim().isEmpty()) {
            throw new RuntimeException("Missing table name in parameter");
        }
        String sql = "DROP TABLE IF EXISTS " + tableName+";";
        execSQL(sql);

    }

    @Override
    public <T> void dropTableIfExists(Class<T> aClass) {
        CacheMetaData<?> metaData = initCacheMetaData(aClass);
        checkingUsageType(metaData, "dropTableIfExists", "aClass");

        if (metaData.isTableReadOnly) {
            throw new RuntimeException("You can only extract data from the table. (@MapTableReadOnly)");
        }
        String sql = "DROP TABLE IF EXISTS " + metaData.tableName+";";
        execSQL(sql);

    }

    @Override
    public <T> void createTable(Class<T> aClass) throws Exception {

        createTableInner(aClass, "");
    }

    @Override
    public <T> void createTableIfNotExists(Class<T> aClass) throws Exception {
        createTableInner(aClass, "IF NOT EXISTS");
    }

    @Override
    public <T> void insert(T item) {

        CacheMetaData<?> metaData = initCacheMetaData(item.getClass());
        checkingUsageType(metaData, "insert", "item");
        if (metaData.isTableReadOnly) {
            throw new RuntimeException("You can only extract data from the table. (@MapTableReadOnly)");
        }
        if (metaData.isPersistent) {
            Persistent per = ((Persistent) item);
            if (per.isPersistent) {
                throw new RuntimeException("You are trying to insert an object into the database that was previously retrieved from the database, which is not very logical.");
            }
        }

        if (metaData.isIAction) {
            ((IEventOrm) item).beforeInsert();
        }
        List<Object> params = new ArrayList<>();
        String sql = new CommandFactoryInsert<T>(this.connection).create(item, metaData, params,this.typeDataBase);
        PrintConsole.print(sql);



        try (PreparedStatement statement = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            int i = 0;
            parametrise(params.toArray(), statement);

            try {
                statement.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if(!metaData.keyColumn.isAssigned){
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {

                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        if (!metaData.keyColumn.isAssigned) {
                            metaData.keyColumn.field.set(item, id);
                        }
                    }
                }
            }
            if (metaData.isPersistent) {
                ((Persistent) item).isPersistent = true;
            }
            if (metaData.isIAction) {
                ((IEventOrm) item).afterInsert();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            closeAuto();
        }
    }

    @Override
    public <T> void insertBulk(List<T> tList) {
        if (tList.isEmpty()) {
            throw new ArithmeticException("The list is Empty");
        }
        tList.forEach(t -> {
            if (t == null) {
                throw new ArithmeticException("The list must not contain empty objects as null");
            }

        });
        CacheMetaData<?> metaData =initCacheMetaData(tList.getFirst().getClass());


        checkingUsageType(metaData, "insetBulk", "item of lis");
        if (metaData.isTableReadOnly) {
            throw new RuntimeException("You can only extract data from the table. (@MapTableReadOnly)");
        }
        tList.forEach(t -> {
            if (metaData.isPersistent) {
                if (((Persistent) t).isPersistent) {
                    throw new RuntimeException("Your list contains a persistent object that was previously retrieved from the database.");
                }
            }

        });

        List<List<T>> sd = partition1(tList);
        for (List<T> ts : sd) {
            CommandFactoryInsetBulk<T> s = new CommandFactoryInsetBulk<>(metaData,this.connection,typeDataBase);
            for (T t : ts) {
                s.add(t);
            }
            String sql = s.getSql();
            if (sql != null) {
                try {
                    Object[] param = s.getParamsObjectList().toArray();
                    execSQL(sql, param);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        if (metaData.isPersistent) {
            tList.forEach(t -> ((Persistent) t).isPersistent = true);
        }
    }

    @SafeVarargs
    @Override
    public final <T> void insertBulk(T... object) {
        List<T> list = Arrays.asList(object);
        insertBulk(list);
    }
    private String whereBuilder(String where) {
        if (where == null || where.trim().isEmpty()) {
            where = "";
        } else {

            where = " WHERE " + where;

        }
        return where;
    }

    @Override
    public <T> List<T> getList(Class<T> aClass, String where, Object... parameters) {
        CacheMetaData<?> metaData = initCacheMetaData(aClass);
        checkingUsageType(metaData, "getList", "aClass");
        where = whereBuilder(where);
        String sql = String.format("SELECT %s FROM %s %s;", String.join(",", metaData.getStringSelect()), metaData.tableName, where);
        PrintConsole.print(sql,parameters);
        List<T> list;
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            parametrise(parameters, statement);
            try (ResultSet resultSet = statement.executeQuery()) {
                list = new ArrayList<>();
                while (resultSet.next()) {
                    T instance = aClass.getDeclaredConstructor().newInstance();
                    builderInstance(metaData, resultSet, instance,this.typeDataBase);
                    if (metaData.isPersistent) {
                        ((Persistent) instance).isPersistent = true;
                    }

                    list.add(instance);
                }
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            closeAuto();
        }
    }

    private  void parametrise(Object[] parameters, PreparedStatement statement) throws SQLException {
        int i = 0;
        if (parameters != null) {
            for (Object object : parameters) {
                if(object instanceof Date){
                    switch (this.typeDataBase) {
                        case SQLITE: {
                            statement.setLong(++i, ((Date) object).getTime());
                            continue;
                        }
                        case POSTGRESQL:{
                            statement.setTimestamp(++i,  new java.sql.Timestamp(((Date)object ).getTime()));
                            continue;
                        }
                        case MYSQL:{
                            statement.setObject(++i, UtilsHelper.dateToStringForSQLite((Date)object ));
                            continue;
                        }

                        default:{
                            statement.setLong(++i, ((Date) object).getTime());
                        }

                    }

                }else if(object instanceof UUID){
                    switch (this.typeDataBase){
                        case SQLITE, MYSQL:{
                            statement.setString(++i, object.toString());
                            continue;
                        }
                        case POSTGRESQL:{
                            statement.setObject(++i, object);
                            continue;
                        }
                        default:{
                            throw new RuntimeException("Not implemented");
                        }
                    }
                }else {
                    statement.setObject(++i, object);
                }

            }
        }
    }


    @Override
    public <T> int update(T item) {

        return update(item, null);


    }

    @Override
    public <T> int update(T item, String appendWhere, Object... parameters) {

        CacheMetaData<?> metaData = initCacheMetaData(item.getClass());
        checkingUsageType(metaData, "update", "item");
        if (metaData.isTableReadOnly) {
            throw new RuntimeException("You can only extract data from the table. (@MapTableReadOnly)");
        }
        if (metaData.isPersistent) {
            if (!((Persistent) item).isPersistent) {
                throw new RuntimeException("You are trying to update a non-persistent object that is not in the database.");
            }
        }

        int res;

        if (metaData.isIAction) {
            ((IEventOrm) item).beforeUpdate();
        }


        List<Object> params = new ArrayList<>();

        if (appendWhere == null || appendWhere.trim().isEmpty()) {
            appendWhere = "";
        } else {
            appendWhere = " AND " + appendWhere;
        }

        try {
            String sql = new CommandFactoryUpdate<T>().create(item, metaData, params,this.typeDataBase,this.connection);
            sql = sql + " where " + metaData.keyColumn.columnName + " = ? " + appendWhere + ";";
            Object o = metaData.keyColumn.field.get(item);
            params.add(o);
            params.addAll(Arrays.asList(parameters));

            PrintConsole.print(sql,parameters);
            try (PreparedStatement statement = this.connection.prepareStatement(sql)) {

                parametrise(params.toArray(), statement);
                res = statement.executeUpdate();
                if (metaData.isIAction) {
                    ((IEventOrm) item).afterUpdate();
                }

                return res;

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            closeAuto();
        }
    }

    @Override
    public <T> int updateRows(Class<T> aClass, PairColumnValue columnValues, String where, Object... parameters) {
        CacheMetaData<?> metaData = initCacheMetaData(aClass);
        checkingUsageType(metaData,"updateRows","aClass");
        if(metaData.isTableReadOnly){
            throw new RuntimeException("You can only extract data from the table. (@MapTableReadOnly)");
        }
        where = whereBuilder(where);

        int res;
        List<Object> params = new ArrayList<>();
        String sql=new CommandFactoryUpdatePartial(this.connection,params,metaData,columnValues,typeDataBase).getSql();
        sql=sql+" "+where+";";
        Collections.addAll(params, parameters);
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            parametrise(params.toArray(), statement);

            res = statement.executeUpdate();

            return res;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    @Override
    public <T> T single(Class<T> aClass, String where, Object... parameters) throws Exception {
        T t = singleOrDefault(aClass, where, parameters);
        if (t == null) {
            throw new Exception("No data found");
        }
        return t;
    }

    @Override
    public <T> T singleOrDefault(Class<T> aClass, String where, Object... parameters) {
        CacheMetaData<?> metaData = initCacheMetaData(aClass);
        checkingUsageType(metaData, "firstOrDefault", "aClass");
        where = whereBuilder(where);
        String sql = String.format("SELECT %s FROM %s %s %s;", String.join(",", metaData.getStringSelect()), metaData.tableName, where, "LIMIT 2");


        List<T> list;
        PrintConsole.print(sql,parameters);
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            parametrise(parameters, statement);
            try (ResultSet resultSet = statement.executeQuery()) {

                list = new ArrayList<>();
                while (resultSet.next()) {
                    T instance = aClass.getDeclaredConstructor().newInstance();
                    builderInstance(metaData, resultSet, instance,this.typeDataBase);
                    if(metaData.isPersistent){
                        ((Persistent)instance).isPersistent=true;
                    }
                    list.add(instance);
                }
            }
            if (list.size() == 1) {
                return list.getFirst();
            }
            return null;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            closeAuto();
        }
    }

    @Override
    public <T> int delete(T item) {
        CacheMetaData<?> metaData = initCacheMetaData(item.getClass());
        checkingUsageType(metaData, "delete", "item");
        if (metaData.isTableReadOnly) {
            throw new RuntimeException("You can only extract data from the table. (@MapTableReadOnly)");
        }
        if (metaData.isPersistent) {
            Persistent per = ((Persistent) item);
            if (!per.isPersistent) {
                throw new RuntimeException("You cannot delete the object because it was not retrieved from the database.");
            }
        }


        int res;
        List<Object> params = new ArrayList<>();
        try {

            Object key;

            Field field = metaData.keyColumn.field;
            field.setAccessible(true);
            key = checkFieldValue(field, item);

            if (metaData.isIAction) {
                ((IEventOrm) item).beforeDelete();
            }
            String sql = "DELETE FROM " + metaData.tableName + " WHERE " + metaData.keyColumn.columnName + " = ?;";
            params.add(key);
            PrintConsole.print(sql,params);

            try (PreparedStatement statement = this.connection.prepareStatement(sql)) {

                parametrise(params.toArray(), statement);
                statement.execute();
                res = statement.getUpdateCount();
            }finally {
                closeAuto();
            }

            if (metaData.isIAction) {
                ((IEventOrm) item).afterDelete();
            }
            return res;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public <T> int deleteRows(Class<T> aClass, String where, Object... parameters) {
        CacheMetaData<?> metaData = initCacheMetaData(aClass);
        checkingUsageType(metaData, "deleteRows", "aClass");
        if (metaData.isTableReadOnly) {
            throw new RuntimeException("You can only extract data from the table. (@MapTableReadOnly)");
        }
        String tableName = metaData.tableName;
        if (tableName == null || tableName.trim().isEmpty()) return 0;
        int res;
        try {
            where = whereBuilder(where);
            String sql = "DELETE FROM " + tableName + where+";";
            PrintConsole.print(sql,parameters);
            try (PreparedStatement statement = this.connection.prepareStatement(sql)) {

                int i = 0;
                for (Object param : parameters) {
                    statement.setObject(++i, param);
                }
                statement.execute();
                res = statement.getUpdateCount();
                return res;
            }finally {
                closeAuto();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T,D> D count(Class<T> aClass) {
        return  count(aClass, null);
    }


    @Override
    public <T,D> D count(Class<T> aClass, String where, Object... parameters) {
        CacheMetaData<?> metaData = initCacheMetaData(aClass);
        checkingUsageType(metaData, "count", "aClass");


        where = whereBuilder(where);
        String sql = MessageFormat.format("SELECT COUNT(*) FROM {0} {1};", metaData.tableName, where);
        PrintConsole.print(sql,parameters);
        return (D) executeScalar(sql, parameters);
    }

    @Override
    public <T> boolean any(Class<T> aClass) {
        return any(aClass, null);
    }

    @Override
    public <T> boolean any(Class<T> aClass, String where, Object... parameters) {
        CacheMetaData<?> metaData = initCacheMetaData(aClass);
        checkingUsageType(metaData, "any", "aClass");
        if (where == null || where.trim().isEmpty()) {
            where = "";
        } else {
            where = " WHERE " + where;
        }
        String sql = MessageFormat.format("SELECT EXISTS ( select * from {0}  {1});", metaData.tableName, where);
        PrintConsole.print(sql,parameters);
        Object o = executeScalar(sql, parameters);
        return switch (this.typeDataBase) {
            case MYSQL -> (long) o == 1;
            case SQLITE -> (int) o == 1;
            case POSTGRESQL -> (boolean) o;
        };


    }

    @Override
    public <T> T firstOrDefault(Class<T> aClass, String where, Object... parameters) {
        CacheMetaData<?> metaData = initCacheMetaData(aClass);
        checkingUsageType(metaData, "firstOrDefault", "aClass");
        where = whereBuilder(where);
        String sql = String.format("SELECT %s FROM %s %s %s;", String.join(",", metaData.getStringSelect()), metaData.tableName, where, "LIMIT 1");


        List<T> list;
        PrintConsole.print(sql,parameters);
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            parametrise(parameters, statement);
            try (ResultSet resultSet = statement.executeQuery()) {

                list = new ArrayList<>();
                while (resultSet.next()) {
                    T instance = aClass.getDeclaredConstructor().newInstance();
                    builderInstance(metaData, resultSet, instance,typeDataBase);
                    if(metaData.isPersistent){
                        ((Persistent)instance).isPersistent=true;
                    }
                    list.add(instance);
                }
            }
            if (list.isEmpty()) {
                return null;
            }
            return list.getFirst();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            closeAuto();
        }


    }

    @Override
    public <T> T first(Class<T> aClass, String where, Object... parameters) throws Exception {
        T t = firstOrDefault(aClass, where, parameters);
        if (t == null) {
            throw new Exception("!!!The sample did not yield any results.");
        }
        return t;
    }

    @Override
    public <T, D> List<D> getListSelect(Class<T> aClass, String columnName, String where, Object... parameters) {
        List<D> list;
        CacheMetaData<?> metaData = initCacheMetaData(aClass);


        checkingUsageType(metaData, "getListSelect", "aClass");
        where = whereBuilder(where);
        ItemField itemField = getItemField(columnName, metaData);

        String sql;
        switch (this.typeDataBase) {
            case POSTGRESQL:
            case SQLITE: {
                sql = MessageFormat.format("SELECT \"{0}\" FROM {1} {2};", columnName, metaData.tableName, where);
                break;
            }
            case MYSQL: {
                sql = MessageFormat.format("SELECT `{0}` FROM {1} {2};", columnName, metaData.tableName, where);
                break;
            }
            default: {
                throw new RuntimeException("Not implemented");
            }
        }

        PrintConsole.print(sql,parameters);
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            int i = 0;
            parametrise(parameters, statement);
            try (ResultSet resultSet = statement.executeQuery()) {

                list = new ArrayList<>();
                while (resultSet.next()) {


                    Object o = extractedSwitchSelect(resultSet, itemField, itemField.field, 1,typeDataBase);
                    D d = (D) o;
                    list.add(d);
                }
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            closeAuto();
        }


    }

    private static @NotNull ItemField getItemField(String columnName, CacheMetaData<?> metaData) {
        ItemField itemField = null;
        if (metaData.keyColumn.columnNameRaw.equals(columnName)) {
            itemField = metaData.keyColumn;
        } else {
            for (ItemField itemField1 : metaData.listColumn) {
                if (itemField1.columnNameRaw.equals(columnName)) {
                    itemField = itemField1;
                    break;
                }
            }
        }
        if (itemField == null) {
            throw new RuntimeException("Column name: " + columnName + " is not found in the table: " + metaData.tableName);
        }
        return itemField;
    }

    @Override
    public <T> Map<Object, List<T>> groupBy(Class<T> aClass, String columnName, String where, Object... parameters) {
        if (columnName.isEmpty()) {
            throw new ArithmeticException("Parameter columnName, empty or is null,");
        }

        CacheMetaData<?> metaData = initCacheMetaData(aClass);
        checkingUsageType(metaData, "groupBy", "aClass");
        boolean isthis;
        isthis = isIsthis(columnName, metaData);
        for (int i = 0; i < metaData.listColumn.size(); i++) {
            ItemField field = metaData.listColumn.get(i);
            if (field.columnNameRaw.equals(columnName)) {
                isthis = true;
                break;
            }
        }
        if (!isthis) {
            throw new RuntimeException("The column with the name " + columnName + " was not found in the table " + metaData.tableName + ". " +
                    "Perhaps you meant the name of the primary key, which is prohibited.");
        }
        where = whereBuilder(where);


        String sql = String.format("SELECT %s FROM %s %s;", String.join(",", metaData.getStringSelect()), metaData.tableName, where);
        Map<Object, List<T>> map;
        PrintConsole.print(sql,parameters);

        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            parametrise(parameters, statement);
            try (ResultSet cursor = statement.executeQuery()) {

                map = new HashMap<>();
                int columnIndex = -1;

                while (cursor.next()) {
                    if (columnIndex == -1) {
                        columnIndex = cursor.findColumn(columnName);
                    }
                    Object key = cursor.getObject(columnIndex);

                    T instance = aClass.getDeclaredConstructor().newInstance();
                    if (metaData.isPersistent) {
                        ((Persistent) instance).isPersistent = true;
                    }
                    builderInstance(metaData, cursor, instance,this.typeDataBase);
                    if(metaData.isPersistent){
                        ((Persistent)instance).isPersistent=true;
                    }
                    if (map.containsKey(key)) {
                        List<T> maplist = map.get(key);
                        if (maplist == null) {
                            throw new RuntimeException("list is null");
                        }
                        maplist.add(instance);
                    } else {
                        List<T> tList = new ArrayList<>();
                        tList.add(instance);
                        map.put(key, tList);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            closeAuto();
        }

        return map;
    }


     ResultSet execSQLRaw(String sql, Object... parameters) {
        ResultSet resultSet;
        try {
            PrintConsole.print(sql,parameters);
            PreparedStatement statement = this.connection.prepareStatement(sql);
            parametrise(parameters, statement);
            resultSet = statement.executeQuery();
            statement.executeQuery();

            return resultSet;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public <T> List<Object> distinctBy(Class<T> aClass, String columnName, String where, Object... parameters) {
        if (columnName.isEmpty()) {
            throw new ArithmeticException("Parameter columnName, empty or is null,");
        }
        CacheMetaData<?> metaData = initCacheMetaData(aClass);
        checkingUsageType(metaData, "distinctBy", "aClass");
        boolean isthis;
        isthis = isIsthis(columnName, metaData);
        if (!isthis) {
            throw new RuntimeException("The column with the name " + columnName + " was not found in the table " + metaData.tableName + ". " +
                    "Perhaps you meant the name of the primary key, which is prohibited.");
        }
        where = whereBuilder(where);


        String sql = String.format("SELECT DISTINCT  %s FROM %s %s;", columnName, metaData.tableName, where);

        List<Object> objectList;
        PrintConsole.print(sql,parameters);
        ItemField itemField = getItemField(columnName, metaData);

        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            parametrise(parameters, statement);
            int keyColumnIndex;
            try (ResultSet resultSet = statement.executeQuery()) {

                keyColumnIndex = resultSet.findColumn(columnName);
                if(keyColumnIndex==-1){
                    throw new RuntimeException("Column not found "+columnName+" in table "+metaData.tableName);
                }
                objectList = new ArrayList<>();
                while (resultSet.next()) {
                    Object key = extractedSwitchSelect(resultSet, itemField, itemField.field, keyColumnIndex,this.typeDataBase);
                    objectList.add(key);
                }
            }
            statement.executeQuery();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            closeAuto();
        }
        return objectList;

    }

    private static boolean isIsthis(String columnName, CacheMetaData<?> metaData) {
        for (int i = 0; i < metaData.listColumn.size(); i++) {
            ItemField field = metaData.listColumn.get(i);
            if (field.columnNameRaw.equals(columnName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean IsAlive() {
        try {
            return this.connection.isClosed();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> int save(T item) {
        CacheMetaData<?> metaData = initCacheMetaData(item.getClass());
        checkingUsageType(metaData,"save","item");
        if(!metaData.isPersistent){
            throw new RuntimeException("An object of type "+item.getClass()+" does not inherit the class Persistent");
        }
        var per=((Persistent)item).isPersistent;
        if(!per){
            insert(item);
            return 1;
        }
        return update(item);
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }

    @Override
    public String getSessionKey() {
        return this.sessionKey;
    }

    @Override
    public <T> IQueryable<T> query(Class<T> aClass) {
        return new ScopedValue<T>(this, aClass);
    }

    @Override
    public <T> void iterator(Class<T> aClass, ITask<T> task, String where, Object... parameters) {
        CacheMetaData<?> metaData = initCacheMetaData(aClass);
        checkingUsageType(metaData, "getList", "aClass");
        where = whereBuilder(where);
        String sql = String.format("SELECT %s FROM %s %s;", String.join(",", metaData.getStringSelect()), metaData.tableName, where);
        PrintConsole.print(sql,parameters);

        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            parametrise(parameters, statement);
            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    T instance = aClass.getDeclaredConstructor().newInstance();
                    builderInstance(metaData, resultSet, instance,this.typeDataBase);
                    if (metaData.isPersistent) {
                        ((Persistent) instance).isPersistent = true;
                    }
                    task.invoke(instance);


                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            closeAuto();
        }
    }

    @Override
    public <T> void iteratorFree(Class<T> aClass, String sql, ITask<T> task, Object... parameters) {
        CacheMetaData<?> metaData = initCacheMetaData(aClass);

        PrintConsole.print(sql);
        List<T> list = new ArrayList<>();

        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            parametrise(parameters, statement);
            try (ResultSet cursor = statement.executeQuery()) {
                while (cursor.next()) {
                    T instance = aClass.getDeclaredConstructor().newInstance();
                    builderInstance(metaData, cursor, instance,this.typeDataBase);
                    if (metaData.isPersistent) {
                        ((Persistent) instance).isPersistent = true;
                    }
                    task.invoke(instance);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            closeAuto();
        }
    }

    @Override
    public <T> List<T> getListFree(Class<T> aClass, String sql, Object... parameters) {
        CacheMetaData<?> metaData = initCacheMetaData(aClass);
        PrintConsole.print(sql);
        List<T> list = new ArrayList<>();
        List<T> tList= new ArrayList<>();
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            parametrise(parameters, statement);
            try (ResultSet cursor = statement.executeQuery()) {


                while (cursor.next()) {
                    T instance = aClass.getDeclaredConstructor().newInstance();
                    builderInstance(metaData, cursor, instance,this.typeDataBase);
                    if (metaData.isPersistent) {
                        ((Persistent) instance).isPersistent = true;
                    }
                    tList.add(instance);
                }
            }
            return tList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            closeAuto();
        }
    }

    @Override
    public <T> List<Object> selectExpression(Class<T> aClass, String expression, String where, Object... parameters) {
        CacheMetaData<?> metaData = initCacheMetaData(aClass);
        where = whereBuilder(where);

        String sql = MessageFormat.format("SELECT {0} FROM {1} {2};", expression, metaData.tableName, where);
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            parametrise(parameters, statement);
            List<Object> list = new ArrayList<>();
            try (ResultSet cursor = statement.executeQuery()) {


                while (cursor.next()) {

                    list.add(cursor.getObject(1));
                }
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            closeAuto();
        }
    }

    @Override
    public <T> T getByPrimaryKey(Class<T> aClass, Object primaryKey) {
        CacheMetaData<?> metaData = initCacheMetaData(aClass);
        String where = MessageFormat.format("{0} = ?", metaData.keyColumn.columnName);
        return singleOrDefault(aClass, where, primaryKey);
    }


    private static Object checkFieldValue(Field field, Object item) {
        try {
            Object sd = field.get(item);
            if (sd == null) {
                throw new RuntimeException("Field name: " + field.getName() +
                        "object type: " + item.getClass().getName() + " is null");
            }
            return sd;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void checkingUsageType(CacheMetaData<?> metaData, String methodName, String parameterName) {
        if (metaData.isFreeClass) {
            throw new RuntimeException(MessageFormat.format("In the update {0}, in the {1} parameter, a type must be used whose class has annotated markup.", methodName, parameterName));
        }
    }

    private <T> void createTableInner(Class<T> aClass, String ifNotExist) throws Exception {
        CacheMetaData<?> metaData = initCacheMetaData(aClass);

        if (metaData.isTableReadOnly) {
            throw new RuntimeException("You can only extract data from the table. (@MapTableReadOnly)");
        }
        if (metaData.keyColumn == null) {
            String msg = aClass.getName() + ": Primary key field is missing.";

            throw new Exception(msg);
        }

        List<String> sqlList = getStringListSqlCreateTable(ifNotExist, metaData,this.typeDataBase);
        getStringAppend(metaData, sqlList);
        StringBuilder sqlBuilder=new StringBuilder();
        for (String rowSql : sqlList) {
            execSQLInner(rowSql);
        }



    }

    private  <T> void getStringAppend(CacheMetaData<T> data, List<String> sqlList) {
        String tb = Utils.clearStringTrimRaw(data.tableName);
        if (data.appendCreateTable != null) {
            sqlList.add(data.appendCreateTable);
        }
        for (ItemField f : data.listColumn) {
            if (!f.isIndex) continue;
            String fName = tb + "_" + f.columnNameRaw;
            switch (this.typeDataBase) {
                case SQLITE: {

                        sqlList.add("CREATE INDEX IF NOT EXISTS " + fName + " ON " + data.tableName + " (" + f.columnName + ");");
                        continue;
                    }
                    case POSTGRESQL: {
                        sqlList.add("CREATE INDEX " + fName+ " ON " + data.tableNameRaw + " (" + f.columnNameRaw + ");");
                        continue;
                    }
                    case MYSQL: {
                        //throw new RuntimeException("Not implemented");
                        if(f.typeName.equals("String")||f.columnName.equals("UUID")||f.columnName.equals("BigDecimal")) {
                            sqlList.add("CREATE FULLTEXT INDEX  " + fName + " ON " + data.tableName + " (" + f.columnNameRaw + "(20));");
                        }else {
                            sqlList.add("CREATE INDEX " + fName + " ON " + data.tableName + " (" + f.columnName + "); \n");
                        }
                        continue;
                    }
                default:{
                    throw new RuntimeException("Not implemented");
                }
            }
            //sqlList.add("CREATE INDEX IF NOT EXISTS " + tb + "_" + f.columnNameRaw + " ON " + data.tableName + " (" + f.columnName + ");");
        }

    }

    @Override
    public void close() throws Exception {
        this.connection.close();
    }
}

