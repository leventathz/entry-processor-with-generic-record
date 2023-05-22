# Entry Processor with GenericRecord

## Problem definition
JVM class loader can load a class dynamically, post start. However, replacing a class with a new version in a distributed system comes with challenges.
Hazelcast projects may need to load classes during the lifetime of a cluster for various use cases.
Domain objects is an example yet there would be executors, map loaders, pipelines and of course entry-processors.
Hazelcast provides facilities to load classes while the cluster is running. Please check these topics:

- [User Code Deployment](https://docs.hazelcast.com/hazelcast/latest/clusters/deploying-code-on-member)
- [Client Code Deployment](https://docs.hazelcast.com/hazelcast/latest/clusters/deploying-code-from-clients)

As projects evolve, domain object classes and processing of data will require amendments.
Hazelcast offers a feature called "generic record (`GenericRecord`)" which can access an object without necessarily having the class of that object in its class path. 
This project demonstrates use of `GenericRecord`s that can be used as part of an entry processor.
As of writing this, Hazelcast is working on improving the experience of replacing active classes with newer versions for other uses cases than domain objects.

This demo presents two sub-projects `Generator` and `ClientWithNoClue`. 
That is to clarify that the class definition of domain object `Person` merely exists in one project/classpath. 

The `Generator` simply generates some random `Person`s and puts them into an `Imap`.

`ClientWithNoCLue` is completely decoupled from the `Generator`. It does not have the `Person` class in its classpath.
It invokes an entry processor from a Hazelcast client and uses `GenericRecord`s to handle a `Person`
during the execution of the entry processor.
`BoostPoors` is a tiny client application. It connects to the Hazelcast cluster as a client and runs a compassionate entry processor
that donates cash into `Person`s with low balances.

That demonstrates an entry process that is totally unaware of a domain class can be developed and run. 

## Steps to run

- You will have two projects in different folders: 
  - Generator
  - ClientWithNoCLue

- Start a Hazelcast node with User Code Deployment enabled (disabled by default):

`HZ_USERCODEDEPLOYMENT_ENABLED=true HZ_USERCODEDEPLOYMENT_CLASSCACHEMODE=OFF hz start`

- Go Generator's folder and run it:

`./gradlew run`
(or 
`./gradlew.bat run` for Windows)

This will generate a hundred `Person`s with randomised balances up to 10.

- Go to `ClientWithNoClue`'s folder and run the dump to see the generated `Person`s:

`./gradlew run`

- Run an alternative main to run the entry processor that donates a tenner to poor people (`Person`s with balance < 5).
Note that `GenericRecord` is used with 2 other lambdas. One with the processor and another one with the predicate.

`./gradlew  run -DmainClass=BoostPoors`

And run the dump again to observe some of the Persons now have balances exceeding 10.0  
