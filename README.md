# TestUtils

Utility-Projekt welches standalone Integrationstests in JavaEE Komponenten mit EJB/CDI
Kontext in einem Weld Container inkl. Transaktionsmanagement ermöglicht.

Das Projekt wurde in OpenJDK Java 8 und 11 erfolgreich getestet.

##### Hinweis
Unter Java 17 kommt es derzeit während der Testausführung zu Fehler:<br>
java.lang.NoClassDefFoundError: Could not initialize class org.jboss.classfilewriter.ClassFile