
package de.testutils.itest;

import java.sql.SQLException;
import java.util.function.Function;

import javax.inject.Inject;
import javax.transaction.UserTransaction;

import com.arjuna.ats.internal.jta.transaction.arjunacore.TransactionManagerImple;
import com.arjuna.ats.jta.cdi.TransactionExtension;
import com.arjuna.ats.jta.cdi.transactional.TransactionalInterceptorMandatory;
import com.arjuna.ats.jta.cdi.transactional.TransactionalInterceptorNever;
import com.arjuna.ats.jta.cdi.transactional.TransactionalInterceptorNotSupported;
import com.arjuna.ats.jta.cdi.transactional.TransactionalInterceptorRequired;
import com.arjuna.ats.jta.cdi.transactional.TransactionalInterceptorRequiresNew;
import com.arjuna.ats.jta.cdi.transactional.TransactionalInterceptorSupports;
import com.arjuna.ats.jta.utils.JNDIManager;

import org.apache.logging.log4j.Logger;
import org.h2.tools.Server;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.AddEnabledInterceptors;
import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.jnp.server.NamingBeanImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;

import de.testutils.itest.config.CdiProducer;
import de.testutils.itest.config.TransactionalConnectionProvider;
import de.testutils.itest.enumeration.TestDbMode;
import de.testutils.itest.extension.cdi.TransactionAttributeExtension;
import de.testutils.itest.extension.junit.TestTransactionExtension;
import de.testutils.itest.helper.PropertiesHelper;
import de.testutils.itest.interceptor.TestTransactionInterceptor;

/**
 * Basisklasse für Integrationstests.
 * Stellt Weld Configuration und producer Abhängigkeiten zu verfügung
 * und ermöglicht vor den Tests einen H2 Server und Browser zur manuellen
 * Überprüfung des DB Inhaltes zu starten.
 */
@EnableAutoWeld
@AddBeanClasses({ CdiProducer.class, com.arjuna.ats.jta.UserTransaction.class, TransactionManagerImple.class })
@AddExtensions({ TransactionExtension.class, TransactionAttributeExtension.class })
@AddEnabledInterceptors({ TestTransactionInterceptor.class, TransactionalInterceptorRequiresNew.class,
    TransactionalInterceptorMandatory.class, TransactionalInterceptorNever.class,
    TransactionalInterceptorNotSupported.class, TransactionalInterceptorRequired.class,
    TransactionalInterceptorSupports.class })
public abstract class BaseClassIT {
  @Inject
  private Logger log;

  @Inject
  private UserTransaction ut;

  @RegisterExtension
  TestTransactionExtension testTransactionExtension = new TestTransactionExtension();

  private static String testDbMode;
  private Server h2Server;
  private static NamingBeanImpl NAMING_BEAN;

  @BeforeAll
  protected static void init() throws Exception {
    testDbMode = (String) PropertiesHelper.getProperties().get("testDb.mode");
    NAMING_BEAN = new NamingBeanImpl();
    NAMING_BEAN.start();

    JNDIManager.bindJTAImplementation();
    TransactionalConnectionProvider.bindDataSource();
  }

  @AfterAll
  protected static void tearDown() {
    NAMING_BEAN.stop();
  }

  @BeforeEach
  void setup() {
    testTransactionExtension.init(ut);
  }

  @AfterEach
  protected void cleanUp() {
    if (h2Server != null) {
      h2Server.stop();
    }
  }

  protected <T, R> Object runInTransaction(Function<T, R> func, T param) throws Exception {
    try {
      ut.begin();
      R result = func.apply(param);
      ut.commit();

      return result;
    } catch (Exception ex) {
      ut.rollback();
      throw ex;
    }
  }

  protected void startH2Server() {
    if (testDbMode.equals(TestDbMode.INMEMORY.getValue())) {
      try {
        h2Server = Server.createTcpServer().start();
        org.h2.tools.Console.main("-web", "-browser");
      } catch (SQLException ex) {
        log.warn(ex.getMessage());
      }
    } else {
      log.warn("H2 Server not available in default mode. Please run tests in inMemory mode for this option.");
    }
  }
}
