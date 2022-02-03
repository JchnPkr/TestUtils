
package de.testutils.itest.demo.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "DEMO")
public class DemoEntity implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @SequenceGenerator(name = "DEMO_ID_GENERATOR", sequenceName = "SEQ_DEMO", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DEMO_ID_GENERATOR")
  @Column(name = "ID", unique = true, nullable = false)
  private long id;

  @Column(name = "DEMO_VALUE")
  private String demoValue;

  public DemoEntity() {}

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getDemoValue() {
    return demoValue;
  }

  public void setDemoValue(String demoValue) {
    this.demoValue = demoValue;
  }
}