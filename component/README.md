# Proyecto AppMusic.

Desarrollo de un componente Java Beam para permitir la carga de lotes de 
canciones a partir de ficheros XML.

## Carga de canciones desde internet.

Se implementa un componente que habilita la carga de canciones desde la red
mediante su especificación en un fichero XML. El fichero XML y su esquema
necesario se encuentran bajo el directorio /xml de este proyecto. 
Se utiliza Internet Archive como repositorio de canciones que se pueden descargar.

```xml
<cancion titulo="In the Court of the Crimson King">
		<URL><![CDATA[https://ia800102.us.archive.org/7/items/cd_in-the-court-of-the-crimson-king_king-crimson/disc1/05.%20King%20Crimson%20-%20The%20Court%20of%20the%20Crimson%20King%20%28including%20The%20Return%20of%20the%20Fire%20Witch%20and%20The%20Dance%20of%20the%20Puppets%29_sample.mp3]]></URL>
		<estilo>Rock-sinfonico</estilo>
		<interprete>King Crimson </interprete>
</cancion>
```
Véase el fichero del directorio /xml para para obtener más ejemplos.

## Instalación.

Para instalar este componente en el repositorio local de Maven es suficiente con
ejecutar el siguiente comando.

```bash
mvn install
```

Si se prefiere realizar una instalación manual, es posible generar un fichero 
.jar y después isntalar manualmente el fichero generado en el repositorio local.
Usualmente, se podrá encontrar el fichero generado en el directorio /target en 
este proyecto.

```bash
mvn package
mvn install:install‐file "-Dfile=$(RUTA AL FICHERO GENERADO)"
```

## Autor

* Óscar Vera López.