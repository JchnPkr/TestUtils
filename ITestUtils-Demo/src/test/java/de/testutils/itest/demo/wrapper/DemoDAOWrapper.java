
package de.testutils.itest.demo.wrapper;

import javax.ejb.Stateless;
import javax.inject.Inject;

import de.testutils.itest.demo.dao.impl.DemoDAO;
import de.testutils.itest.demo.entity.DemoEntity;
import de.testutils.itest.interceptor.TestTransaction;

/**
 * Diese Klasse dient Demonstrationszwecken der TestTransactionInterceptor Annotation.
 * JUnit5 unterstützt nicht ohne weiteres Interceptoren unmittelbar in Tests.
 */
@Stateless
public class DemoDAOWrapper {
  @Inject
  private DemoDAO dao;

  /**
   * Kapselt den dao Aufruf in einer UserTransaction. Die an der Methode verwendete
   * Annotation ist nur im test package verfuegbar.
   *
   * @param value
   *          Wert des Feldes demoValue
   * @return DemoEntity die angelegte Entitaet
   */
  @TestTransaction
  public DemoEntity createWithTestTransaction(String value) {
    return dao.create(value);
  }
}
