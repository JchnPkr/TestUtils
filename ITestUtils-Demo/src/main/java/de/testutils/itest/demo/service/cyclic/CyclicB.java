package de.testutils.itest.demo.service.cyclic;

import javax.inject.Inject;

public class CyclicB {
	@Inject
	private CyclicA cyclicA;

	public String getFromCyclicA() {
		return cyclicA.getFromCyclicB();
	}

	public String getStuff() {
		return "stuff from CyclicB";
	}
}
