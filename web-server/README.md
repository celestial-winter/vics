# web-server

This REST api exposes electoral data (wards, constituencies and electors) to the web-client.

## Dependencies

- Java 8+
- PostgreSQL
- Maven 3+

## Run the application

The component is bundled as a fat jar (including dependencies) and can be run as follows:

```
java -jar target/web-server-0.0.1-SNAPSHOT.jar
```

## Upstream applications

The core database is maintained by an upstream API (PAF).  This application stores users and static data (wards and constituencies).
Any voter data is maintained by PAF and updated through web service calls.

## Developer notes

### Exceptions

Exceptions are not used as control flow in the application.  Exceptions are caught when they are thrown
and propagated up the stack as values, with the aid of the Try class (like functional error handling in Scala).
However, hibernate depends on exceptions for rolling back transactions so exceptions on write operations are not handled in the same manner.
See here for reasons behind this design approach: http://www.joelonsoftware.com/items/2003/10/13.html
