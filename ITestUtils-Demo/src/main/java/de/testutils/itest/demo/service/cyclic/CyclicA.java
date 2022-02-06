package de.testutils.itest.demo.service.cyclic;

import javax.inject.Inject;

public class CyclicA {
	@Inject
	private CyclicB cyclicB;

	public String getFromCyclicB() {
		return cyclicB.getStuff();
	}

	public String getStuff() {
		return "stuff from CyclicA";
	}
}
