# Toy
Toy RESTful API

Problem: account transfer

---

To run:
* clone
* cd Toy
* mvn clean verify
* java -jar target/Toy-1.0-SNAPSHOT.jar

---

Design hiccups and sneezes:
* persistence layer mixed with Domain Model
* Domain Model exceptions text published to the Outside
* internal entity identifiers published to the Outside
* REST HAEOTAS not followed

Implementation hiccups and sneezes:
* no integration tests with Underflow
    - need to check how malformed requests are treated
    - need check how internal server errors are treated