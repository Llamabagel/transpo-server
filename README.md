# Transpo-Server
[![Build Status](https://travis-ci.com/Llamabagel/transpo-server.svg?branch=master)](https://travis-ci.com/dellisd/transpo-server)
[![Docker Pulls](https://img.shields.io/docker/pulls/llamabagel/transpo-server.svg)](https://hub.docker.com/r/llamabagel/transpo-server)

This is the backend server component of Route 613 written in Kotlin using [Ktor](https://ktor.io/). More accurately, 
this is a rewrite of the existing C# API but in Kotlin.

## Development setup
### Application Keys
In order for the backend server to communicate with OC Transpo's realtime API service the server requires an App ID and 
an API key. These keys can be configured in the `keys.properties` file located in the server module. An example 
configuration is provided in the `example.keys.properties` file. Because these keys are unique to each user, the 
`keys.properties` file should _never_ be included in the repository and git commits. As such, a local copy of the 
`keys.properties` file must be manually created each time this project is set up.

Once configured, the application keys can be accessed in code through the 
[`Keys`](server/src/main/kotlin/ca/llamabagel/transpo/server/Keys.kt) object.

## Deployment
The server is published as a Docker image on the Docker hub.
```shell script
docker pull llamabagel/transpo-server
``` 

To run the server using Docker, there are a few things that must be set up.
#### Configuration
You must create a `config` directory that will be mounted as a volume for the Docker image. This is where you put the `keys.properties` and `config.properties` files.
You can use the example files provided in the repository as a template.

#### Data Packages
You will also need to create a directory to store the data packages that are uploaded using the `package-data` tool. This directory will also be mounted as a volume.

#### Docker Compose
To run the image as a service, create the following `docker-compose.yml` file somewhere:
```yaml
version: "3"
services:
  web:
    image: llamabagel/transpo-server:latest
    deploy:
      replicas: 1
      resources:
        limits:
          cpus: "1"
          memory: 512M
      restart_policy:
        condition: on-failure
    ports:
      - "8080:8080"
    networks:
      - webnet
    volumes:
      - <your-config-folder>:/app/config
      - <your-packages-folder>:/app/packages
networks:
  webnet:
```

Replace `<your-config-folder>` with the path to the Configuration directory that you created before. Do the same for `<your-packages-folder>` but with the packages directory that you created.
You can change replication, and resource values as needed.

To run deploy the service (in a swarm), run the stack deploy command.
```shell script
docker stack deploy -c docker-compose.yml <service-name>
```

You should be able to access the service on port `8080`.