
package de.testutils.itest.interceptor;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.transaction.UserTransaction;

@Interceptor
@TestTransaction
public class TestTransactionInterceptor {
  @Inject
  private UserTransaction ut;

  /**
   * Fängt Methodenaufruf und Kapseln ihn in einer Usertransaction.
   *
   * @param context
   *          Aufruf Kontext
   * @return Ergebnis der Methode
   * @throws Exception
   *           Exception aus Methode oder Transaktion
   */
  @AroundInvoke
  public Object transactional(InvocationContext context) throws Exception {

    try {
      ut.begin();
      Object returnValue = context.proceed();
      ut.commit();

      return returnValue;
    } catch (Exception e) {
      ut.rollback();
      throw e;
    }
  }
}
