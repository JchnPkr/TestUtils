
package de.testutils.itest.demo.service;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.transaction.Transactional;

import de.testutils.itest.demo.dao.IDemoDAO;
import de.testutils.itest.demo.entity.DemoEntity;

@Stateless // wird für @TransactionAttribute benötigt da EJB feature
public class DemoService {
  @Inject
  private IDemoDAO dao;

  @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
  public DemoEntity createWithTransactionAttribute(String value) {
    return dao.create(value);
  }

  @Transactional
  public DemoEntity createWithTransactional(String value) {
    return dao.create(value);
  }
}
