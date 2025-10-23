# Proyecto de ejemplo SDK POS Java

Un proyecto de ejemplo para utilizar el SDK de POS de Transbank para Java en una aplicación de escritorio.

## Tecnologías

Este proyecto utiliza:
*   Java 11
*   JavaFX 17
*   Maven para la gestión de dependencias y construcción.

El `pom.xml` está configurado con perfiles para detectar automáticamente el sistema operativo (macOS, Linux, Windows) y la arquitectura para utilizar los artefactos correctos de JavaFX.

## Requisitos

*   JDK 11 o superior.
*   Maven.
*   Asegúrate de que el comando `java` esté disponible en el PATH de tu sistema.

### Desarrollo

Para ejecutar el proyecto y probarlo junto al [SDK POS Java](https://github.com/TransbankDevelopers/transbank-pos-sdk-java) localmente debe ejecutar las siguientes líneas.

Remplace &lt;DIR&gt; por el directorio donde clono el repositorio y &lt;VERSION&gt; por el que corresponda a la versión clonada según sea el caso, donde &lt;VERSION&gt; dependerá de lo especificado en la etiqueta version del archivo pom.xml, busque la línea con el código similar a &lt;version&gt;X.X.X-SNAPSHOT&lt;/version&gt;.

```sh
mvn compile && mvn package && mvn install:install-file \
   -Dfile=/<DIR>/transbank-pos-sdk-java/target/transbank-sdk-pos-java-<VERSION>.jar \
   -DgroupId=com.github.transbankdevelopers \
   -DartifactId=transbank-sdk-pos-java \
   -Dversion=<VERSION> \
   -Dpackaging=jar \
   -DgeneratePom=true
```

## Compilación

Para compilar el proyecto, ejecuta el siguiente comando en la raíz del directorio:

```sh
mvn package
```

## Ejecución

Una vez compilado, puedes ejecutar la aplicación con el siguiente comando:

```sh
java -jar target/jfx8-app-jar-with-dependencies.jar
```

Una vía alternativa para ejecutar la aplicación sin alterar tu instalación local de Java es utilizando el plugin de JavaFX de Maven:

```sh
mvn javafx:run
```