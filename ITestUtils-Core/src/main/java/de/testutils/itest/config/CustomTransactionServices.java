
package de.testutils.itest.config;

import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import com.arjuna.ats.jta.common.jtaPropertyManager;

import org.jboss.weld.transaction.spi.TransactionServices;

public class CustomTransactionServices implements TransactionServices {
  // private static Logger LOG = LogManager.getLogger();

  @Override
  public void cleanup() {
    // nothing to clean up
  }

  @Override
  public void registerSynchronization(Synchronization synchronizedObserver) {
    jtaPropertyManager.getJTAEnvironmentBean()
        .getTransactionSynchronizationRegistry()
        .registerInterposedSynchronization(synchronizedObserver);

    // try {
    // com.arjuna.ats.jta.TransactionManager.transactionManager().getTransaction()
    // .registerSynchronization(synchronizedObserver);
    // } catch (SystemException | IllegalStateException | RollbackException e) {
    // throw new IllegalStateException("Cannot register synchronization observer " + synchronizedObserver
    // + " to the available transaction", (Throwable) e);
    // }
  }

  @Override
  public boolean isTransactionActive() {
    try {
      return com.arjuna.ats.jta.UserTransaction.userTransaction().getStatus() == Status.STATUS_ACTIVE;
    } catch (SystemException e) {
      throw new RuntimeException(e);
    }

    // try {
    // int status = com.arjuna.ats.jta.TransactionManager.transactionManager().getStatus();
    // switch (status) {
    // case Status.STATUS_ACTIVE:
    // case Status.STATUS_COMMITTING:
    // case Status.STATUS_MARKED_ROLLBACK:
    // case Status.STATUS_PREPARED:
    // case Status.STATUS_PREPARING:
    // case Status.STATUS_ROLLING_BACK:
    // return true;
    // default:
    // return false;
    // }
    // } catch (SystemException se) {
    // LOG.error("Cannot obtain the status of the transaction", se);
    // return false;
    // }
  }

  @Override
  public UserTransaction getUserTransaction() {
    return com.arjuna.ats.jta.UserTransaction.userTransaction();
  }
}