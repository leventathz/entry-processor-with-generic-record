# ClientWithNoClue 
This is a simple demo that works with a companion project `Generator`

JVM class loader can load a class post start. However, it cannot unload a class or replace with a new version.
Hazelcast cluster may need "dynamic" classes during the lifetime of a cluster for various use cases.
Domain objects is an example yet there would be executors, map loaders, pipelines and of course entry-processors.

As a project evolves, domain object classes and processing of data will require amendments.
Because of the class loader's design, a developer cannot simply  
This project demonstrates use of `GenericRecord`s that can be used as part of an entry processor.
In this scenario, a `Generator` project runs with a domain class `Person` in its classpath.
The `Generator` simply generates some random `Person`s and puts them into an `Imap`.

`ClientWithNoCLue` is a completely decoupled project from the `Generator`. It does not have the `Person` class in its classpath.
It runs an entry processor from a Hazelcast client and uses `GenericRecord`s to handle a `Person`
during the execution of an entry processor.
`BoostPoors` is a tiny client application. It connects to the cluster as a client and runs a compassionate entry processor
that donates cash into `Person`s with low balances.

# Steps

- Extract two projects into different folders.

- Start a Hazelcast node:

`HZ_USERCODEDEPLOYMENT_ENABLED=true HZ_USERCODEDEPLOYMENT_CLASSCACHEMODE=OFF hz start`

- Go Generator's folder and run it:

`./gradlew  run`
(or 
`./gradlew.bat  run` for Windows)

- Go ClientWithNoClue's folder and run the dump to see the generated `Person`s:

`./gradlew  run`

Run an alternative main to run the entry processor that donates a tenner to poors

`./gradlew  run -DmainClass=BoostPoors`

And run the dump again to see dome of the Persons now have balances exceeding 10.0  

