## Prompt para Claude

Actúa como un desarrollador Java experto. Analiza el archivo `database/schema.sql` del proyecto y genera los POJOs correspondientes a las tablas, relaciones, columnas, tipos de datos, restricciones y claves definidas allí.

Antes de crear cualquier archivo, revisa el contenido existente en:

`microservices/autosusbcali/src/main/java/co/edu/usbcali/autosusbcali/domain`

Conserva las clases Java que ya existan y genera únicamente las clases que hagan falta. Si una clase existente representa una tabla del esquema, no la dupliques ni la reemplaces sin necesidad; verifica que sea coherente con el SQL y realiza solo los ajustes estrictamente necesarios para completar su correspondencia.

Guarda cada POJO en un archivo Java independiente dentro de ese directorio, usando nombres de clase en `PascalCase` y nombres de archivo que coincidan exactamente con ellos. Usa el package:

```java
package co.edu.usbcali.autosusbcali.domain;
```

Requisitos:

- Mapea correctamente los tipos SQL a tipos Java apropiados.
- Representa las claves primarias, columnas anulables y relaciones según el esquema.
- Usa una estructura compatible con el proyecto y sus dependencias existentes; antes de elegir anotaciones, revisa el código y la configuración del microservicio.
- Mantén encapsulamiento con atributos privados, constructor(es), getters y setters, y agrega `equals`, `hashCode` y `toString` cuando sea consistente con el estilo actual del proyecto.
- No inventes tablas, campos ni relaciones que no estén en `database/schema.sql`.
- No modifiques archivos fuera de `microservices/autosusbcali/src/main/java/co/edu/usbcali/autosusbcali/domain`.
- Al finalizar, informa cuáles clases ya existían, cuáles fueron creadas o actualizadas y cualquier ambigüedad encontrada en el esquema.

Primero inspecciona el esquema y las clases existentes; después implementa los cambios necesarios.
