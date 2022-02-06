package de.testutils.itest.demo.service.cyclic;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.inject.Inject;

import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import de.testutils.itest.BaseClassIT;

@Disabled("Beispieltest, der Zirkuläre Abhängigkeiten aufdeckt und während Initialisierung scheitert.")
@AddBeanClasses({ CyclicA.class, CyclicB.class })
@ExtendWith(MockitoExtension.class)
class CyclicTest extends BaseClassIT {
	@Inject
	private CyclicB cyclicB;

	@Test
	void test() {
		assertNotNull(cyclicB.getStuff());
	}
}