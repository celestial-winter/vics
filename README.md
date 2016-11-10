[This project is no longer maintained]

# vics

Voter Intention Collection System. An application to collect voting intentions

## Stack

The UI is written in JS, HTML and CSS using AngularJS (v1.4) as a client side SPA.

The web app serving the client is written in Java. Spring Boot is the web framework.  Data between
the UI and server is REST over HTTP serialized as JSON.  PDFs are generated on the server and 
returned to the client as byte arrays.

A security system is implemented using Spring security and Redis for session state persistence.
The application should be deployed using TLS.

PostgreSQL is used for persistence.

## Build & Test

The build depends on a running redis and postgres (in future we should switch to the embedded versions for easier testing...)
Install redis and ensure the redis server is running locally. A simple approach is to run redis with docker.

Install postgres and create a database locally called ```canvassapp``` with username ```postgres``` and password ```postgres``` then run the database creation script `vics/web-server/src/test/resources/sql/drop-create.sql`

Install maven 3 and Java 8, then test and build the entire project from the root directory as follows

    mvn clean install

This will generate a ```/dist``` folder that contains all artefacts for deployment - minified javascript/html/css, java server as a fat jar and the database installation scripts that contain reference data.

## Project Modules

    * paf-client - client library to call the upstream voter api
    * paf-stub - mocks the upstream voter api. Can be executed from tests and run standalone
    * web-server - the main web application that provides data to the UI and manages users (Java/Spring Boot)
    * web-client - the user interface (AngularJS app)
    * common - utilities and language extensions that can be reused across all modules
    * tools - scripts and tools for managing data and deploying the application
