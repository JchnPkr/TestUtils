
package de.testutils.itest.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.arjuna.ats.jdbc.TransactionalDriver;

import org.h2.jdbcx.JdbcDataSource;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.service.UnknownUnwrapTypeException;

import de.testutils.itest.helper.PropertiesHelper;
import oracle.jdbc.xa.client.OracleXADataSource;

public class TransactionalConnectionProvider implements ConnectionProvider {
  private static final long serialVersionUID = 1L;
  public static final String DATASOURCE_JNDI = "java:testDS";
  public static String username;
  public static String pass;

  private final TransactionalDriver transactionalDriver;

  public TransactionalConnectionProvider() {
    transactionalDriver = new TransactionalDriver();
  }

  /**
   * Erstellt einen JNDI Eintrag zur dem Testmodus entsprechenden Datasource der jeweiligen DB.
   *
   * @throws SQLException
   *           wenn keine XADatasource erstellt werden konnte
   * @throws IOException
   *           falls Properties nicht auffindbar
   * @throws FileNotFoundException
   *           falls Properties nicht auffindbar
   * @throws URISyntaxException
   *           falls Pfad nicht lesbar
   * @throws ClassNotFoundException
   *           falls DataSource aus properties nicht gefunden wird
   * @throws InstantiationException
   *           falls DataSource aus properties nicht geladen wird
   * @throws IllegalAccessException
   *           falls DataSource aus properties nicht kompatibel
   */
  public static void bindDataSource()
      throws SQLException, FileNotFoundException, IOException, URISyntaxException, ClassNotFoundException,
      IllegalAccessException, InstantiationException {
    username = (String) PropertiesHelper.getProperties().get("javax.persistence.jdbc.user");
    pass = (String) PropertiesHelper.getProperties().get("javax.persistence.jdbc.password");
    String url = (String) PropertiesHelper.getProperties().get("javax.persistence.jdbc.url");
    Object dataSource = Class.forName((String) PropertiesHelper.getProperties().get("testDb.datasource")).newInstance();

    initDatasource(dataSource, url);

    try {
      InitialContext initialContext = new InitialContext();
      initialContext.bind(DATASOURCE_JNDI, dataSource);
    } catch (NamingException e) {
      throw new RuntimeException(e);
    }
  }

  private static void initDatasource(Object dataSource, String url) {
    if (dataSource instanceof OracleXADataSource) {
      ((OracleXADataSource) dataSource).setURL(url);
      ((OracleXADataSource) dataSource).setUser(username);
      ((OracleXADataSource) dataSource).setPassword(pass);
    } else if (dataSource instanceof JdbcDataSource) {
      ((JdbcDataSource) dataSource).setURL(url);
      ((JdbcDataSource) dataSource).setUser(username);
      ((JdbcDataSource) dataSource).setPassword(pass);
    } else {
      throw new IllegalArgumentException("Datasource Type wird nicht unterstützt: " + dataSource.getClass().getName());
    }
  }

  @Override
  public Connection getConnection() throws SQLException {
    Properties properties = new Properties();
    properties.setProperty(TransactionalDriver.userName, username);
    properties.setProperty(TransactionalDriver.password, pass);
    return transactionalDriver.connect("jdbc:arjuna:" + DATASOURCE_JNDI, properties);
  }

  @Override
  public void closeConnection(Connection connection) throws SQLException {
    if (!connection.isClosed()) {
      connection.close();
    }
  }

  @Override
  public boolean supportsAggressiveRelease() {
    return false;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public boolean isUnwrappableAs(Class clazz) {
    return getClass().isAssignableFrom(clazz);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T unwrap(Class<T> clazz) {
    if (isUnwrappableAs(clazz)) {
      return (T) this;
    }

    throw new UnknownUnwrapTypeException(clazz);
  }
}