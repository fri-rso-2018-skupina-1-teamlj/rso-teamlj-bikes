# RSO: Bikes microservice

## Prerequisites

```bash
docker run -d --name pg-bikes -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=bike -p 5434:5432 postgres:latest
```

Local run (warning: debugger needs to be attached):
```
java -agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=y -jar api/target/bikes-api-1.0.0-SNAPSHOT.jar
```

```
docker build -t bikes:1.0 .
docker run -p 8082:8082 bikes:1.0
to change network host: docker run -p 8082:8082 --net=host bikes:1.0
```
