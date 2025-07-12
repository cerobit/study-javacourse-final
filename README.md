# Proyecto Base Implementando Clean Architecture

# for this study project i used the following resources:

# Podman and MongoDB and RabbitMQ
## Run MongoDB with Podman

```sh
podman  machine start
```

```sh
podman start  mongo
```
#Optional clean start
#podman stop  mongo
#podman rm  mongo
#podman run --name mongo -d -p 27017:27017 -v $(pwd)/mongodb-data:/data/db mongo:latest

```sh
podman start  rabbitmq-stack
```

#List
```sh
curl -X GET "http://localhost:8080/box" -H "Accept: application/json"
```

```sh
curl -X POST "http://localhost:8080/box" \
  -H "Content-Type: application/json" \
  -d '{
    "id": "123",
    "name": "Sample Box",
    "status": "OPENED",
    "openingAmount": 1000.00,
    "closingAmount": 1500.00,
    "openedAt": "2024-06-01T10:00:00",
    "closedAt": "",
    "currentBalance": 500.00
  }'
```
#Close Box
```sh
curl -X POST "http://localhost:8080/box/close/123" \
  -H "Content-Type: application/json"
```

#Reopen Box
```sh
curl -X POST "http://localhost:8080/box/reopen/123" \
  -H "Content-Type: application/json"
```

```sh
curl --location --request PATCH 'http://localhost:8080/box/123' \
--header 'Content-Type: application/json' \
--data-raw '{
    "name": "New Partial Box Name"
}'
```

```sh
curl -X PUT "http://localhost:8080/box/123" \
  -H "Content-Type: application/json" \
  -d '{"id":"123","name":"Updated Box","status":"CLOSED","openedAt":"2024-06-01T10:00:00Z","closedAt":"2024-06-02T18:00:00Z","closingAmount":1500,"currentBalance":0}'
```

```sh
curl -X DELETE "http://localhost:8080/box/123"
```

#Optional clean start
#podman stop  rabbitmq-stack
#podman rm  rabbitmq-stack
#podman run --tls-verify=false -d --hostname my-rabbit --name rabbitmq-stack -p 5672:5672 -p 15672:15672 rabbitmq:3-management


## Antes de Iniciar

Empezaremos por explicar los diferentes componentes del proyectos y partiremos de los componentes externos, continuando con los componentes core de negocio (dominio) y por último el inicio y configuración de la aplicación.

Lee el artículo [Clean Architecture — Aislando los detalles](https://medium.com/bancolombia-tech/clean-architecture-aislando-los-detalles-4f9530f35d7a)

# Arquitectura

![Clean Architecture](https://miro.medium.com/max/1400/1*ZdlHz8B0-qu9Y-QO3AXR_w.png)

## Domain

Es el módulo más interno de la arquitectura, pertenece a la capa del dominio y encapsula la lógica y reglas del negocio mediante modelos y entidades del dominio.

## Usecases

Este módulo gradle perteneciente a la capa del dominio, implementa los casos de uso del sistema, define lógica de aplicación y reacciona a las invocaciones desde el módulo de entry points, orquestando los flujos hacia el módulo de entities.

## Infrastructure

### Helpers

En el apartado de helpers tendremos utilidades generales para los Driven Adapters y Entry Points.

Estas utilidades no están arraigadas a objetos concretos, se realiza el uso de generics para modelar comportamientos
genéricos de los diferentes objetos de persistencia que puedan existir, este tipo de implementaciones se realizan
basadas en el patrón de diseño [Unit of Work y Repository](https://medium.com/@krzychukosobudzki/repository-design-pattern-bc490b256006)

Estas clases no puede existir solas y debe heredarse su compartimiento en los **Driven Adapters**

### Driven Adapters

Los driven adapter representan implementaciones externas a nuestro sistema, como lo son conexiones a servicios rest,
soap, bases de datos, lectura de archivos planos, y en concreto cualquier origen y fuente de datos con la que debamos
interactuar.

### Entry Points

Los entry points representan los puntos de entrada de la aplicación o el inicio de los flujos de negocio.

## Application

Este módulo es el más externo de la arquitectura, es el encargado de ensamblar los distintos módulos, resolver las dependencias y crear los beans de los casos de use (UseCases) de forma automática, inyectando en éstos instancias concretas de las dependencias declaradas. Además inicia la aplicación (es el único módulo del proyecto donde encontraremos la función “public static void main(String[] args)”.

**Los beans de los casos de uso se disponibilizan automaticamente gracias a un '@ComponentScan' ubicado en esta capa.**

