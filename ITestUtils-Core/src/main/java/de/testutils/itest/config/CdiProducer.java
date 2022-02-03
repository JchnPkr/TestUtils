
package de.testutils.itest.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.testutils.itest.helper.PropertiesHelper;

public class CdiProducer {
  // needed for JPA entity listeners
  @Inject
  private BeanManager beanManager;

  /**
   * Weld bietet keinen eigenen persistence context. EntityManager ist nicht
   * Container managed.
   *
   * @return der zur Testpersitenzunit passende EntityManager
   * @throws IOException
   *           falls Properties nicht auffindbar
   * @throws FileNotFoundException
   *           falls Properties nicht auffindbar
   * @throws URISyntaxException
   *           falls Pfad nicht lesbar
   */
  @Produces
  public EntityManager createEntityManager() throws FileNotFoundException, IOException, URISyntaxException {
    Map<Object, Object> props = PropertiesHelper.getProperties();
    props.put("javax.persistence.bean.manager", beanManager);

    return Persistence
        .createEntityManagerFactory((String) props.get("testdb.pu"), props)
        .createEntityManager();
  }

  /**
   * Erstellt Testlogger.
   *
   * @param injectionPoint
   *          Eben dieser
   * @return der Logger
   */
  @Produces
  public Logger produceLogger(InjectionPoint injectionPoint) {
    return LogManager.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
  }
}