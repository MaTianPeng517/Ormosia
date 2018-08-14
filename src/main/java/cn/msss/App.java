package cn.msss;

import cn.msss.entity.Users;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        //1.连接数据库的四要素
        String driver="com.mysql.jdbc.Driver";
        String url="jdbc:mysql://localhost:3306/texts";
        String userName="MTP";
        String password="root";

        //2.创建JDBC的 API
        Connection con=null;
        PreparedStatement ps=null;
        ResultSet rs=null;

        //3.通过反射的方式创建实例
        /**
         * 1.创建一个Object对象
         */
        Object obje=null;
        try {
            obje=Class.forName("cn.msss.entity.Users").newInstance();
            //2.加载驱动
            Class.forName(driver);
            try {
                //3.创建连接
                con = DriverManager.getConnection(url, userName, password);
                //4.sql语句
                String sql="SELECT `id`,`name`,`sex`,`age` FROM `student` WHERE `id`=?";
                //5.给参数赋值
                ps=con.prepareStatement(sql);
                ps.setInt(1,1);
                //6.执行sql
                rs=ps.executeQuery();
                //7.遍历结果集
                if(rs.next()){
                    //1.获取元数据
                    ResultSetMetaData metaData = rs.getMetaData();
                    System.out.println("多少元数据"+metaData.getColumnCount());
                    //2.获取元数据的列数
                    int count = metaData.getColumnCount();
                    for (int i=1;i<=count;i++){
                        //1.获取指定列名
                        String columnName = metaData.getColumnName(i);
                        System.out.println("列名"+columnName);
                        //2.获取实体类中的属性名称，就是setXxx()
                        String mc=changeName(columnName);
                        //3..获取字段在数据库中的类型
                        String typeName = metaData.getColumnTypeName(i);
                        //判断获取数据库int类型
                        if (typeName.equalsIgnoreCase("int")){
                            try {
                                obje.getClass().getMethod(mc,Integer.class).invoke(obje,rs.getInt(columnName));
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            } catch (NoSuchMethodException e) {
                                e.printStackTrace();
                            }
                            //判断获取数据库varchar类型
                        }else if (typeName.equalsIgnoreCase("varchar")){
                            try {
                                obje.getClass().getMethod(mc,String.class).invoke(obje,rs.getString(columnName));
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            } catch (NoSuchMethodException e) {
                                e.printStackTrace();
                            }
                            //判断获取数据库char类型
                        }else if (typeName.equalsIgnoreCase("varchar")){
                            try {
                                obje.getClass().getMethod(mc,char.class).invoke(obje,rs.getString(columnName));
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            } catch (NoSuchMethodException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
                    //释放资源
                try {
                    if (rs!=null){
                    rs.close();
                    }
                    if (ps!=null){
                        ps.close();
                    }
                    if (con!=null){
                        con.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
            }
        }
        //最后一步输出实体类信息
        System.out.println((Users)obje);
    }

    /**
     *
     * @param columnName
     * @return
     */
    private static String changeName(String columnName) {

        return "set"+columnName.substring(0,1).toUpperCase()+columnName.substring(1);
    }
}
