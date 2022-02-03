
package de.testutils.itest.demo.service;

import javax.inject.Inject;
import javax.transaction.Transactional;

import de.testutils.itest.demo.dao.IDemoDAO;
import de.testutils.itest.demo.entity.DemoEntity;

public class DemoServiceWithDependency {
  @Inject
  private IDemoDAO dao;

  @Inject
  private RandomDependencyService dependency;

  /**
   * Moifiziert einen uebergebenen Wert und passt den mit dem
   * uebergebenen pk aus der DB ermittelten Eintrag an.
   *
   * @param pk
   *          die id des zu modifizierenden Eintrages
   * @param value
   *          der neue Wert
   * @return angepasster Eintrag
   */
  @Transactional
  public DemoEntity modifiyValueAndUpdateEntity(long pk, String value) {
    String modifiedValue = dependency.doStuff(value);

    DemoEntity entity = dao.find(pk);
    entity.setDemoValue(modifiedValue);

    return dao.update(entity);
  }
}
