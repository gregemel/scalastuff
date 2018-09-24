# scalastuff
#### Exploring Scala and Akka

purpose: This project includes a very basic demonstration of using akka streams to read, parse, and record data from a flat, comma separated values (csv), file.

* Main - creates and runs the csv importer

* StreamCsvToRepository - streams data from flat file to database

* TemperatureRepository - stubbed database repo

* StreamCsvToRepositorySpec - simple demonstration to test a flow

This is a very simple example that demonstrates a working akka stream.  It is not meant to exemplify best practices, just something that works.

# Akka Streams

Akka streams work to move data from source to sink via flow steps.

* source -> flow step 1 -> flow step 2 -> flow step n -> sink

This example:
* source = csv file with temperature readings
* transform = flow step that transforms a file row to a Temperature type
* sink = database repository


