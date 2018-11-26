/**
 * 
 */
package eu.europeana.vocs.wikidata;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 17 Jun 2016
 */
public class LoadDatabaseWithWikidata
{
 // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
    static final String DB_URL = "jdbc:mysql://localhost/wikidata";

    //  Database credentials
    static final String USER = "root";
    static final String PASS = "admin";

    public static final void main(String[] args) throws Throwable
    {
        Class.forName("com.mysql.jdbc.Driver");

      //STEP 3: Open a connection
        System.out.println("Connecting to database...");
        Connection conn = DriverManager.getConnection(DB_URL,USER,PASS);
    }
}
