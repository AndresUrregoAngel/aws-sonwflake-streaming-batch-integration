package lambdaroot;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import lambdaroot.snowSchema;


public class snowflakedb {


    public static String snowUser = System.getenv("snowuser");
    public static String snowPass = System.getenv("snowpass");
    public static String snowVwh = System.getenv("snowvwh");
    public static String snowDB = System.getenv("snowdb");
    public static String snowDBSchema = System.getenv("snowschema");
    public static String snowDBTable = System.getenv("snowtable");
    public static String snowAccount = System.getenv("snowaccount");



    public void insertNewRecords (snowSchema record) throws  SQLException{

        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        String insertcmd = "insert into \""+snowDB+"\".\""+snowDBSchema+"\".\""+snowDBTable+"\" values " +
                "("+record.sensorId+","+record.currentTemperature+", \'"+record.status+"\' )";

        System.out.println("execution the statement: "+insertcmd);
        statement.executeQuery(insertcmd);
        System.out.println("the record : "+ record.toString()+" has been inserted");

        statement.close();
        connection.close();
    }



    private static Connection getConnection()
            throws SQLException {

        //TODO: Create a standard snowflake connection

        // build connection properties
        Properties properties = new Properties();
        properties.put("user", snowUser);         // replace "" with your user name
        properties.put("password", snowPass);     // replace "" with your password
        properties.put("warehouse", snowVwh);    // replace "" with target warehouse name
        properties.put("db", snowDB);           // replace "" with target database name
        properties.put("schema",snowDBSchema);       // replace "" with target schema name

        String connectStr = "jdbc:snowflake://"+snowAccount+".snowflakecomputing.com";

        return DriverManager.getConnection(connectStr, properties);
    }






    }
