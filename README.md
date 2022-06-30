# DevelopmentBooks

This code was written with JDK 1.8 and maven 3.8.5

For run the application you just need to run the class DevelopmentBooksApplication.

In the folder resources you can find :

- application.properties : it's the configuration of the db. <br/>You can access to the console for the h2 db via : http://localhost:8080/h2-console


- data.sql : this script is execute AFTER the creation of the schema by hibernate (use entities from create all tables).


- DevelopmentBooks.postman_collection.json : you can import this file in postman. He contains all services with example for call them.
