
package de.testutils.itest.demo.dao.impl;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import javax.inject.Inject;

import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import de.testutils.itest.BaseClassIT;
import de.testutils.itest.demo.entity.DemoEntity;
import de.testutils.itest.demo.wrapper.DemoDAOWrapper;
import de.testutils.itest.enumeration.TestDbMode;
import de.testutils.itest.extension.junit.TestTransaction;
import de.testutils.itest.helper.PropertiesHelper;

/** Einfacher Beispieltest mit echten Instanzen, lesend und schreibend */

@AddBeanClasses({ DemoDAO.class, DemoDAOWrapper.class })
class DemoDAOIT extends BaseClassIT {
  @Inject
  private DemoDAO sut;

  @Test
  void findDemoTest() {
    // existenten PK vorausgesetzt. Initial vorhanden da aus test-data-import.sql importiert.
    final long pk = 1L;

    DemoEntity result = sut.find(pk);

    assertNotNull(result);
  }

  @Nested
  @DisplayName("Run transaction dependent test with extension annotation")
  class TransactionAnnotationTest {
    static final String VALUE = "createValidStatusByAnnotation";

    @Order(1)
    @TestTransaction
    void createEntityByTestAnnotationTest() {
      DemoEntity result = sut.create(VALUE);

      assertNotNull(result);
    }

    @Order(2)
    @Test
    void findRecentEntityTest() throws Exception {
      // debug feature um während in memory Tests die DB begutachten zu können.
      // Einfach breakpoint setzen und nach Test start warten bis browser Fenster aufgeht.

      // startH2Server();

      List<DemoEntity> results = sut.findByValue(VALUE);

      String testDbMode = (String) PropertiesHelper.getProperties().get("testDb.mode");

      if (testDbMode.equals(TestDbMode.INMEMORY.getValue())) {
        // An dieser Stelle sollte kein Eintrag gefunden werden, da die in memory DB zwischen
        // den Testläufen 1 und 2 neu aufgezogen wurde.
        assertTrue(results.isEmpty(), "Eintrag gefunden");
      } else {
        // Gegen eine echte DB würde der Eintrag gefunden werden, da dort dauerhaft persistiert
        assertAll(
            () -> assertFalse(results.isEmpty(), "keinen Eintrag gefunden"),
            () -> assertEquals(1, results.size(), "nicht genau einen Eintrag gefunden"));
      }
    }
  }

  @Nested
  @DisplayName("Run transaction dependent test with transaction interceptor")
  class TransactionInterceptorTest {
    static final String VALUE = "createEntityByInterceptor";

    @Inject
    DemoDAOWrapper wrapper;

    @Test
    void findRecentEntityTest() throws Exception {
      // startH2Server(); // debug

      createEntityByInterceptor();

      List<DemoEntity> results = sut.findByValue(VALUE);

      assertAll(
          () -> assertFalse(results.isEmpty(), "keinen Eintrag gefunden"),
          () -> assertEquals(1, results.size(), "nicht genau einen Eintrag gefunden"));
    }

    private void createEntityByInterceptor() {
      wrapper.createWithTestTransaction(VALUE);
    }
  }

  @Nested
  @DisplayName("Run transaction dependent test with functional call of super class transaction method")
  class TransactionMethodTest {
    static final String VALUE = "createValidStatusByFunctionalCall";

    @Test
    void findRecentEntityTest() throws Exception {
      createEntityByFunctionalCall();

      List<DemoEntity> results = sut.findByValue(VALUE);

      assertAll(
          () -> assertFalse(results.isEmpty(), "keinen Eintrag gefunden"),
          () -> assertEquals(1, results.size(), "nicht genau einen Eintrag gefunden"));
    }

    private void createEntityByFunctionalCall() throws Exception {
      DemoEntity demoEntity = new DemoEntity();
      demoEntity.setDemoValue(VALUE);

      runInTransaction((Object[] arr) -> sut.create((DemoEntity) arr[0]),
          new Object[] { demoEntity });
    }
  }
}
