Un pequeño ejemplo de como utilizar el SDK de POS Integrado de Java en una aplicacion de escritorio.

Utiliza JavaFX version 8, sobre Java 8.

Para compilar, 

```maven
mvn package
```

Para ejecutar

```sh
$ java -jar target/jfx8-app-jar-with-dependencies.jar
```

Para utilizar el SDK del POS es necesario el archivo Transbank.dll, o Transbank.dylib del SDK de C. Esta es la librería nativa del POS integrado de Transbank, y la versión de Java la utiliza.

Para que la libreria de Java pueda encontrar la librería nativa, utiliza una variable de ambiente llamada NATIVE_TRANSBANK_WRAP que debe apuntar al archivo de esta variable.

Por ejemplo en MacOS se debe correr el comando export en el mismo Shell en que se ejecutará el programa que utiliza la librería.

```sh
export NATIVE_TRANSBANK_WRAP=/usr/local/lib/libTransbankWrap.dylib
```

Además recuerda agregar al PATH la ruta de java

```sh
export PATH="/Library/Java/JavaVirtualMachines/jdk1.8.0_251.jdk/Contents/Home/bin:$PATH"
```

en Windows, se debe correr este comando en la ventana de Command.com antes de ejecutar el programa que utiliza la librería.

setx NATIVE_TRANSBANK_WRAP=c:\TransbankLib\TransbankWrap.dll
