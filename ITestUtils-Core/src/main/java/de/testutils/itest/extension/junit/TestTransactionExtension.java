
package de.testutils.itest.extension.junit;

import java.lang.reflect.Method;

import javax.transaction.UserTransaction;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import org.junit.platform.commons.util.AnnotationUtils;

public class TestTransactionExtension implements InvocationInterceptor {
  private UserTransaction ut;

  public TestTransactionExtension(UserTransaction ut) {
    this.ut = ut;
  }

  public TestTransactionExtension() {
    // This constructor is intentionally empty. Nothing special is needed here.
  }

  @Override
  public void interceptTestMethod(Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext,
      ExtensionContext extensionContext) throws Throwable {
    if (AnnotationUtils.isAnnotated(invocationContext.getExecutable(), TestTransaction.class)) {
      ut.begin();
      invocation.proceed();
      ut.commit();
    } else {
      invocation.proceed();
    }
  }

  /**
   * Zur initialisierung der Abhängigkeiten zu nutzen.
   *
   * @param ut
   *          Die Transaktion in der Gekapselt werden soll
   * @return Ergebnis der Testmethode
   */
  public TestTransactionExtension init(UserTransaction ut) {
    this.ut = ut;

    return this;
  }
}
