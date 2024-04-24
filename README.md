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
desde /persistencia/driver para instalar el componente JavaBeam Luz.

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

## Carga de canciones desde internet.

Se implementa un componente que habilita la carga de canciones desde la red
mediante su especificación en un fichero XML. El fichero XML y su esquema
necesario se encuentran bajo el directorio /xml de este proyecto. Se utiliza
Internet Archive como repositorio de canciones que se pueden descargar.

```xml
<cancion titulo="In the Court of the Crimson King">
		<URL><![CDATA[https://ia800102.us.archive.org/7/items/cd_in-the-court-of-the-crimson-king_king-crimson/disc1/05.%20King%20Crimson%20-%20The%20Court%20of%20the%20Crimson%20King%20%28including%20The%20Return%20of%20the%20Fire%20Witch%20and%20The%20Dance%20of%20the%20Puppets%29_sample.mp3]]></URL>
		<estilo>Rock-sinfonico</estilo>
		<interprete>King Crimson </interprete>
</cancion>
```
Véase el fichero del directorio /xml para para obtener más ejemplos.

## Autor.

* Óscar Vera López.