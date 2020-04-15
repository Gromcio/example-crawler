# CRAWLER EXAMPLE SERVICE

Example crawler service with some basic multithreading

### How to build, test and run. 

Project requires java8 and maven installed

1. First install and build the project using `mvn install` or `mvnw install` if you have wrapper configured for the project
2. To test application run command `mvn test`
3. To build jar call respectively `mvn package`
4. To start program call `java -jar target/crawler-0.0.1-SNAPSHOT.jar https://wiprodigital.com/ --crawler.threads=10` 

Command supports additional optional flag --crawler.visits-limit which will change amount of threads used internally by crawler, by default uses 10 threads

### Project status

I'v wanted to create quite simple web crawler which at least uses some basic threading. There's a few things which could be done better,
like adding option to limit pages visited, change page processing to include what errors might have occurred, sizes on resources and more detailed information.
We could add option to define with a flag desired output format as well. For bigger changes, few tweaks to make it into a bean which could be used both
for web, server and command line app.