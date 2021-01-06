package com.lemon.util;

import com.lemon.common.Constants;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class JDBCUtils {

    /**
     * 获取数据库连接对象
     * @return
     */
    public static Connection getConnection() {
        //定义数据库连接
        //Oracle：jdbc:oracle:thin:@localhost:1521:DBName
        //SqlServer：jdbc:microsoft:sqlserver://localhost:1433; DatabaseName=DBName
        //MySql：jdbc:mysql://localhost:3306/DBName
        //futureloan：数据库名
        String url = Constants.URL;
        String username = Constants.USERNAME;
        String password = Constants.PASSWORD;
        //定义数据库连接对象
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, username,password);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }


    /**
     * 数据库增、删、改操作
     * @param sql
     */
    public static int updata(String sql) {
        //1. 获取数据库连接对象
        Connection conn = getConnection();
        //2. 数据库操作
        QueryRunner queryRunner = new QueryRunner();
        int count = 0;
        try {
            count = queryRunner.update(conn, sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        //3. 关闭数据库连接
        try {
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return count;
    }

    /**
     * 查询所有的结果集
     * @param sql
     * @return List<Map<String, Object>>
     */
    public static List<Map<String, Object>> queryAll(String sql) {
        //1. 获取数据库连接对象
        Connection conn = getConnection();
        //2. 数据库操作
        QueryRunner queryRunner = new QueryRunner();

        try {
            //第一个参数：数据库连接对象 第二个参数：sql语句 第三个参数：接收查询结果
            List<Map<String, Object>> result = queryRunner.query(conn, sql, new MapListHandler());
            return result;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }


    /**
     * 查询结果集中的第一条数据
     * @param sql
     * @return
     */
    public static Map<String, Object> queryOne(String sql) {
        //1. 获取数据库连接对象
        Connection conn = getConnection();
        //2. 数据库操作
        QueryRunner queryRunner = new QueryRunner();

        try {
            //第一个参数：数据库连接对象 第二个参数：sql语句 第三个参数：接收查询结果
            Map<String, Object> result = queryRunner.query(conn, sql, new MapHandler());
            return result;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    /**
     * 查询结果集中的单个数据
     * 如果返回的结果集不只是一个数据，会取第一个数据
     * @param sql
     * @return
     */
    public static Object querySingle(String sql) {
        //1. 获取数据库连接对象
        Connection conn = getConnection();
        //2. 数据库操作
        QueryRunner queryRunner = new QueryRunner();

        try {
            //第一个参数：数据库连接对象 第二个参数：sql语句 第三个参数：接收查询结果
            Object result = queryRunner.query(conn, sql, new ScalarHandler<Object>());
            return result;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }
}
