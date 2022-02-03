
package de.testutils.itest.helper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;

import org.hibernate.cfg.Environment;

import de.testutils.itest.config.TransactionalConnectionProvider;
import de.testutils.itest.enumeration.TestDbMode;

public final class PropertiesHelper {
  private static final String MODE = "testDb.mode";
  private static final URL APPCONFIGPATH =
      Thread.currentThread().getContextClassLoader().getResource("test.properties");
  private static final Properties APPPROPS = new Properties();
  private static final Map<Object, Object> PROPERTIES = new Properties();
  private static PropertiesHelper INSTANCE;

  private PropertiesHelper() throws FileNotFoundException, IOException, URISyntaxException {
    if (APPPROPS.isEmpty()) {
      APPPROPS.load(Files.newBufferedReader(Paths.get(APPCONFIGPATH.toURI()), StandardCharsets.UTF_8));

      PROPERTIES.put(MODE, APPPROPS.get(MODE));
      PROPERTIES.put(Environment.CONNECTION_PROVIDER, TransactionalConnectionProvider.class);

      if (((String) APPPROPS.get(MODE)).equals(TestDbMode.DEFAULT.getValue())) {
        if ((((String) APPPROPS.get("testDb.url")).isEmpty()
            || ((String) APPPROPS.get("testDb.user")).isEmpty()
            || ((String) APPPROPS.get("testDb.password")).isEmpty())) {
          PROPERTIES.put("javax.persistence.jdbc.url", APPPROPS.get("testDb.default.url"));
          PROPERTIES.put("javax.persistence.jdbc.user", APPPROPS.get("testDb.default.user"));
          PROPERTIES.put("javax.persistence.jdbc.password", APPPROPS.get("testDb.default.password"));
        } else {
          PROPERTIES.put("javax.persistence.jdbc.url", APPPROPS.get("testDb.url"));
          PROPERTIES.put("javax.persistence.jdbc.user", APPPROPS.get("testDb.user"));
          PROPERTIES.put("javax.persistence.jdbc.password", APPPROPS.get("testDb.password"));
        }

        PROPERTIES.put("testDb.datasource", APPPROPS.get("testDb.default.datasource"));
        PROPERTIES.put("testdb.pu", "integrationtest-default-pu");
        PROPERTIES.put("javax.persistence.jdbc.driver", APPPROPS.get("testDb.default.driver"));
        PROPERTIES.put("hibernate.dialect", APPPROPS.get("testDb.default.dialect"));
      } else if (((String) APPPROPS.get(MODE)).equals(TestDbMode.INMEMORY.getValue())) {
        PROPERTIES.put("javax.persistence.jdbc.url", APPPROPS.get("testDb.inMemory.url"));
        PROPERTIES.put("javax.persistence.jdbc.user", APPPROPS.get("testDb.inMemory.user"));
        PROPERTIES.put("javax.persistence.jdbc.password", APPPROPS.get("testDb.inMemory.password"));
        PROPERTIES.put("javax.persistence.jdbc.driver", APPPROPS.get("testDb.inMemory.driver"));
        PROPERTIES.put("hibernate.dialect", APPPROPS.get("testDb.inMemory.dialect"));
        PROPERTIES.put("hibernate.hbm2ddl.import_files", APPPROPS.get("testDb.inMemory.import"));
        PROPERTIES.put("hibernate.hbm2ddl.auto", APPPROPS.get("testDb.inMemory.hbm2ddl"));
        PROPERTIES.put("hibernate.flushMode", "ALWAYS");
        PROPERTIES.put("testDb.datasource", APPPROPS.get("testDb.inMemory.datasource"));
        PROPERTIES.put("testdb.pu", "integrationtest-inMemory-pu");
      }
    }
  }

  private static void init() throws FileNotFoundException, IOException, URISyntaxException {
    if (INSTANCE == null) {
      INSTANCE = new PropertiesHelper();
    }
  }

  public static Map<Object, Object> getProperties() throws FileNotFoundException, IOException, URISyntaxException {
    init();
    return PropertiesHelper.PROPERTIES;
  }
}
