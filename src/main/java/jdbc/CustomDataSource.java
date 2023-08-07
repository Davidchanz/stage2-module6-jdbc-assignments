package jdbc;

import javax.sql.DataSource;

import lombok.Getter;
import lombok.Setter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Logger;

@Getter
@Setter
public class CustomDataSource implements DataSource {
    private static volatile CustomDataSource instance;
    private final String driver;
    private final String url;
    private final String name;
    private final String password;
    private CustomConnector connector = new CustomConnector();

    private CustomDataSource(String driver, String url, String password, String name) {
        this.driver = driver;
        this.url = url;
        this.password = password;
        this.name = name;
    }

    public static CustomDataSource getInstance() {
        if(instance == null){
            synchronized (CustomDataSource.class){
                if(instance == null){
                    Properties props = new Properties();
                    try {
                        props.load(CustomDataSource.class.getClassLoader().getResourceAsStream("app.properties"));
                        instance = new CustomDataSource(
                            props.getProperty("postgres.driver"),
                            props.getProperty("postgres.url"),
                            props.getProperty("postgres.password"),
                            props.getProperty("postgres.name")
                        );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return instance;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.connector.getConnection(this.url, this.name, this.password);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return this.connector.getConnection(this.url, username, password);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter printWriter) throws SQLException {

    }

    @Override
    public void setLoginTimeout(int i) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> aClass) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> aClass) throws SQLException {
        return false;
    }
}
