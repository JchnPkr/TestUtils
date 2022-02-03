
package de.testutils.itest.demo.dao;

import java.util.List;

import de.testutils.itest.demo.entity.DemoEntity;

public interface IDemoDAO {

	/**
	 * Erzeugt einen neuen Demo Eintrag.
	 *
	 * @param value
	 *                  der Wert für das demoValue Feld
	 * @return DemoEntity die angelegte Entitaet
	 */
	public DemoEntity create(String value);

	/**
	 * Erzeugt einen neuen Demo Eintrag.
	 *
	 * @param entity
	 *               	Entität
	 * @return DemoEntity die angelegte Entitaet
	 */
	public DemoEntity create(DemoEntity entity);
	
	/**
	 * Findet Einträge die den Uebergebenen Wert enthalten.
	 *
	 * @param value
	 *                  Wert fuer das Feld demoValue
	 * @return die gefundenen Entitaeten
	 */
	public List<DemoEntity> findByValue(String value);

	/**
	 * Findet Einträge an Hand des primary key.
	 * 
	 * @param pk
	 *               primary key
	 * @return entity
	 */
	public DemoEntity find(long pk);

	/**
	 * Aktualisiert die übergebene Entität.
	 * 
	 * @param entity
	 *               zu aktualisierende Entität
	 * @return entity
	 */
	public DemoEntity update(DemoEntity entity);
}
