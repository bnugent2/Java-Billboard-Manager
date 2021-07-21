package UnitTests;

import Server.databaseCommands;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Properties;

class TestdatabaseCommands {
    @Test
    public Properties testReadProperties()throws IOException{
        return null;
    }
    @Test
    public Connection testGetConnection() throws SQLException{
        //Properties props =
        //Connection con = databaseCommands.getConnection(db_props);
        return null;
    }

    public interface DatabaseIF{
        public void open();
        public void getConnection(Properties db_props);
        public void close();
    }


}
/*
class DatabaseDummy implements TestdatabaseCommands.DatabaseIF {
    public void open(){
    }
    public ArrayList<String> getNames() throws Exception{
        return new ArrayList<String>();
    }
    public void close(){

    }
}

 */
