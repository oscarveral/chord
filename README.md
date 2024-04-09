# Proyecto AppMusic.

Proyecto de desarrollo de software de una aplicación que imitaría un servicio de 
streaming de música.

## Servicio de persistencia.

Para poder ccrear y utilizar la base de datos del servicio de persistencia es 
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

## Autor.

* Óscar Vera López.