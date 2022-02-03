
package de.testutils.itest.demo.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;

import javax.inject.Inject;

import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.testutils.itest.BaseClassIT;
import de.testutils.itest.demo.dao.impl.DemoDAO;
import de.testutils.itest.demo.entity.DemoEntity;

/** Einfacher Beispieltest mit echten Instanzen, lesend und schreibend */

@AddBeanClasses({ DemoService.class, DemoDAO.class })
class DemoServiceIT extends BaseClassIT {
  @Inject
  private DemoService sut;

  @Inject
  private DemoDAO dao;

  @Nested
  @DisplayName("Run transaction dependent test with transactional annotation")
  class TransactionalAnnotationTest {
    static final String VALUE = "createEntityByTransactional";

    @Test
    void findEntityTest() throws Exception {
      sut.createWithTransactional(VALUE);

      List<DemoEntity> results = dao.findByValue(VALUE);

      assertAll(
          () -> assertFalse(results.isEmpty(), "keinen Eintrag gefunden"),
          () -> assertEquals(1, results.size(), "nicht genau einen Eintrag gefunden"));
    }
  }

  @Nested
  @DisplayName("Run transaction dependent test with TransactionAttribute annotation")
  class TransactionAttributeTest {
    static final String VALUE = "createWithTransactionAttribute";

    @Test
    void findEntityTest() throws Exception {
      sut.createWithTransactionAttribute(VALUE);

      List<DemoEntity> results = dao.findByValue(VALUE);

      assertAll(
          () -> assertFalse(results.isEmpty(), "keinen Eintrag gefunden"),
          () -> assertEquals(1, results.size(), "nicht genau einen Eintrag gefunden"));
    }
  }
}