
package de.testutils.itest.demo.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import de.testutils.itest.BaseClassIT;
import de.testutils.itest.demo.dao.impl.DemoDAO;
import de.testutils.itest.demo.entity.DemoEntity;

/** Beispieltest mit echter Instanz und Mock eines injizierten Feldes. */

@AddBeanClasses({ DemoServiceWithDependency.class, DemoDAO.class })
@ExtendWith(MockitoExtension.class)
class DemoServiceWithDependecyIT extends BaseClassIT {
  @Inject
  @InjectMocks
  private DemoServiceWithDependency sut;

  @Produces
  @ApplicationScoped // hat ohne scope einen Fehler WELD-001443 pseudo scoped bean has circular dependencies zur Folge
  @Mock
  private RandomDependencyService dependency;

  @Inject
  @Spy
  private DemoDAO dao;

  @Test
  void injectedMockTest() {
    // Eintrag existiert durch import
    final long pk = 1L;
    final String value = "value";
    final String expectedValue = "mocked stuff done";

    // darf laut CDI Standart nicht null zurück liefern, da das Proxyobjekt des Service aus dem Container Kontext
    // null nicht weiterleiten kann.
    when(dependency.doStuff(any(String.class))).thenReturn(expectedValue);

    DemoEntity result = sut.modifiyValueAndUpdateEntity(pk, value);

    verify(dao).update(any(DemoEntity.class));
    assertAll(
        () -> assertEquals(pk, result.getId()),
        () -> assertEquals(expectedValue, result.getDemoValue()));

    DemoEntity updateResultFromDB = dao.find(pk);
    assertEquals(expectedValue, updateResultFromDB.getDemoValue());
  }
}
