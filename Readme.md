# Program to search for text in .pdf files

### Deploy JAR:
Compile and create .jars (Maven should be installed):
```
mvn clean install
```
### pdfSearch-1.0-SNAPSHOT-jar-with-dependencies.jar 
this jar is ready to be copied and run using: 
```
javaw -jar pdfSearch-1.0-SNAPSHOT-jar-with-dependencies.jar -Xmx3000m
```
**-Xmx3000m** is used to overwrite default max RAM allowed.
It is possible that 3GB is also not enough sometimes (depends on the PDF file).
If program throws "OutOfMemory" errors, then this value 
should be increased.

To see errors output use:
```
java -jar pdfSearch-1.0-SNAPSHOT-jar-with-dependencies.jar -Xmx3000m
```
### pdfSearch-1.0-SNAPSHOT.jar
this jar is lightweight and should be always copied together with libraries, those can be found under **target / lib /..**
lib folder should be in the same folder where .jar file is

Run this .jar the same way as before:
```
javaw -jar pdfSearch-1.0-SNAPSHOT.jar -Xmx3000m
```