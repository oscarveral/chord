# Proyecto AppMusic.

Proyecto de desarrollo de software de una aplicación que imitaría un servicio de 
streaming de música.

## Servicio de persistencia.

Para poder crear y utilizar la base de datos del servicio de persistencia es 
necesario que la aplicación ServidorPersistenciaH2.jar del directorio 
/persistencia/server se encuentre en ejecución.

Para ello sólo es necesario abrir una terminal en dicho directorio y ejecutar:

```bash
java -jar ServidorPersistenciaH2.jar
```

La interacción con este servicio de persistencia requerirá del uso de un driver
específico que deberemos instalar en el repositorio local de Maven antes de 
poder ejecutar esta aplicación.

Se deberá ejecutar el siguiente comando desde /persistencia/driver para instalar 
el driver.

```bash
mvn install:install‐file "-Dfile=DriverPersistencia.jar" "-DpomFile=driverPersistencia‐2.0.pom"
```

Una vez instalado se podrá se indica su uso en este proyecto añadiendolo al POM
utilizando el siguiente fragmento de texto.

```xml
<dependency>
  <groupId>umu.tds</groupId>
  <artifactId>driverPersistencia</artifactId>
  <version>2.0</version>
</dependency>
```

## Componente JavaBeam Luz.

Como requisito para esta aplicación, se encuentra el uso de un componente 
JavaBeam proporcionado por los profesores de la asignatura. Dicho componente
es un pulsador proporcionado mediante la librería /componentes/Luz.jar. Será 
necesario instalarla en el repositorio local de Maven.

Para realizar la instalación se deberá ejecutar el siguiente comando 
desde /persistencia/driver de forma que se pueda instalar el componente 
JavaBeam Luz.

```bash
mvn install:install-file -Dfile="Luz.jar" -DgroupId="umu.tds" -DartifactId="luz" -Dversion="1.0" -Dpackaging="jar" -DgeneratePom=true
```

Una vez instalado se podrá se indica su uso en este proyecto añadiendolo al POM
utilizando el siguiente fragmento de texto.

```xml
<dependency>
	<groupId>umu.tds</groupId>
	<artifactId>luz</artifactId>
	<version>1.0</version>
</dependency>
```

## Componente JavaBeam CargadorCanciones.

Como requisito adicional, se debe utilizar un componente Java Beam de desarrollo
propio para realizar cargas de lotes de canciones. Véase el proyecto "component"
en la raiz del repositorio donde se implementa dicho componente y 
se ofrecen las instrucciones necesarias para su instalación y uso mediante
Maven.

Una vez el componente se encuentra instalado en el repositorio local de Maven, 
podrá se utilizado agregando al POM el siguiente fragmento de texto.

```xml
<dependency>
	<groupId>umu.tds</groupId>
	<artifactId>component</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</dependency>
```

## Autor.

* Óscar Vera López.