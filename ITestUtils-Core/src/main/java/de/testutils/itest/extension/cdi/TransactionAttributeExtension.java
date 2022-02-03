
package de.testutils.itest.extension.cdi;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.apache.deltaspike.core.util.metadata.AnnotationInstanceProvider;
import org.apache.deltaspike.core.util.metadata.builder.AnnotatedTypeBuilder;

public class TransactionAttributeExtension implements Extension {
  /**
   * Sucht nach TransactionAttribute Annotationen und ersetzt diese durch Transactional
   * Annotationen vor der CDI injection.
   *
   * @param <X>
   *          die Art der Ausprägung
   * @param pat
   *          der Annotierte Typ
   */
  public <X> void processInjectionTarget(@Observes ProcessAnnotatedType<X> pat) {
    if (pat.getAnnotatedType().isAnnotationPresent(Stateless.class)) {
      createTransactionalWrapper(pat);
    }
  }

  private <X> void createTransactionalWrapper(ProcessAnnotatedType<X> pat) {
    final AnnotatedType<X> at = pat.getAnnotatedType();
    AnnotatedTypeBuilder<X> builder = new AnnotatedTypeBuilder<X>().readFromType(at);

    setTransactionalAnnotationOnClass(at, builder);
    setTransactionalAnnotationOnMethods(at, builder);

    pat.setAnnotatedType(builder.create());
  }

  private <X> void setTransactionalAnnotationOnMethods(final AnnotatedType<X> at, AnnotatedTypeBuilder<X> builder) {
    for (AnnotatedMethod<? super X> method : at.getMethods()) {
      if (method.isAnnotationPresent(TransactionAttribute.class)
          && !(method.isAnnotationPresent(Transactional.class))) {
        Transactional transactionalAnnotation =
            getTransactionalAnnotation(method.getAnnotation(TransactionAttribute.class).value());
        builder.addToMethod(method, transactionalAnnotation);
      }
    }
  }

  private <X> void setTransactionalAnnotationOnClass(final AnnotatedType<X> at, AnnotatedTypeBuilder<X> builder) {
    if (at.isAnnotationPresent(TransactionAttribute.class) && !at.isAnnotationPresent(Transactional.class)) {
      Transactional transactionalAnnotation =
          getTransactionalAnnotation(at.getAnnotation(TransactionAttribute.class).value());
      builder.addToClass(transactionalAnnotation);
    }
  }

  private Transactional getTransactionalAnnotation(TransactionAttributeType type) {
    Map<String, TxType> value = new HashMap<String, TxType>();
    String paramName = "value";

    switch (type) {
      case MANDATORY:
        value.put(paramName, TxType.MANDATORY);
        return AnnotationInstanceProvider.of(Transactional.class, value);
      case REQUIRED:
        value.put(paramName, TxType.REQUIRED);
        return AnnotationInstanceProvider.of(Transactional.class, value);
      case REQUIRES_NEW:
        value.put(paramName, TxType.REQUIRES_NEW);
        return AnnotationInstanceProvider.of(Transactional.class, value);
      case SUPPORTS:
        value.put(paramName, TxType.SUPPORTS);
        return AnnotationInstanceProvider.of(Transactional.class, value);
      case NOT_SUPPORTED:
        value.put(paramName, TxType.NOT_SUPPORTED);
        return AnnotationInstanceProvider.of(Transactional.class, value);
      case NEVER:
        value.put(paramName, TxType.NEVER);
        return AnnotationInstanceProvider.of(Transactional.class, value);
      default:
        throw new IllegalArgumentException("TransactionAttributeType nicht zuordnbar: " + type.getClass().getName());
    }
  }
}