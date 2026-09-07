## Prompt para Claude

Actúa como un desarrollador Java experto. Analiza el archivo
`database/schema_red_social.sql` del proyecto y genera o completa:

1. Los POJOs/entidades JPA correspondientes a las tablas, relaciones, columnas,
   tipos de datos, restricciones y claves definidas en el esquema.
2. Los repositories Spring Data JPA correspondientes a cada entidad.

Antes de crear o modificar archivos, revisa:

- `microservicios/dnetwork-java/pom.xml`, para conocer las dependencias y la
  configuración del microservicio.
- `microservicios/dnetwork-java/src/main/java/co/edu/usbcali/dnetwork_java/domain`
- `microservicios/dnetwork-java/src/main/java/co/edu/usbcali/dnetwork_java/repository`

Conserva las clases y los repositories que ya existan. Genera únicamente los
que hagan falta y no dupliques ni reemplaces código sin necesidad. Si una clase
o un repository existente representa una tabla del esquema, verifica que sea
coherente con el SQL y realiza solo los ajustes estrictamente necesarios.

Guarda cada entidad en un archivo Java independiente dentro de:

`microservicios/dnetwork-java/src/main/java/co/edu/usbcali/dnetwork_java/domain`

Usa el package:

```java
package co.edu.usbcali.dnetwork_java.domain;
```

Guarda cada repository en un archivo Java independiente dentro de:

`microservicios/dnetwork-java/src/main/java/co/edu/usbcali/dnetwork_java/repository`

Usa el package:

```java
package co.edu.usbcali.dnetwork_java.repository;
```

Requisitos para las entidades:

- Mapea correctamente los tipos SQL a tipos Java apropiados.
- Representa las claves primarias, columnas anulables y relaciones según el
  esquema.
- Usa las anotaciones, convenciones, Lombok y tipos de fecha ya adoptados por
  el proyecto; no introduzcas dependencias nuevas sin necesidad.
- Mantén nombres de clases y archivos en `PascalCase`, haciendo que coincidan
  exactamente.
- Mantén encapsulamiento y los constructores, getters, setters, `equals`,
  `hashCode` y `toString` que sean consistentes con el estilo existente.

Requisitos para los repositories:

- Cada entidad debe tener un repository con nombre
  `<Entidad>Repository`, salvo que ya exista uno equivalente.
- Cada repository debe ser una interfaz en el package indicado y extender
  `JpaRepository<Entidad, TipoDeId>`.
- Usa el tipo real de la clave primaria de la entidad. Si la tabla tiene una
  clave primaria compuesta, conserva o crea la representación JPA adecuada y
  usa ese tipo como segundo parámetro del `JpaRepository`.
- Incluye `@Repository` cuando sea consistente con los repositories existentes.
- No inventes métodos de consulta, tablas, campos ni relaciones que no estén
  definidos en el esquema o que no sean necesarios para que compile el
  repository.

No modifiques archivos fuera de:

- `microservicios/dnetwork-java/src/main/java/co/edu/usbcali/dnetwork_java/domain`
- `microservicios/dnetwork-java/src/main/java/co/edu/usbcali/dnetwork_java/repository`

Primero inspecciona el esquema, el `pom.xml`, las entidades y los repositories
existentes; después implementa los cambios necesarios. Al finalizar, informa
cuáles entidades y repositories ya existían, cuáles fueron creados o
actualizados y cualquier ambigüedad encontrada en el esquema.
