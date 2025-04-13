Simple CRUD application, which partially implements a logic of movements of two chess pieces: Rook and Bishop.

Solution details:

1. The application implements only two chess pieces: Rook and Bishop.
2. ID of a new piece is assigned by an application. The application requires a game id, but please treat it as an upsert value
   so just use any string value. It treats each game as a separate game.
3. You can remove a piece by id, then the piece can not be modified or moved over the board.
4. The application exposes Swagger as an Api documentation.
5. The application publishes kafka events: New piece, piece removed, piece moved. Each action has assigned unique. It stores
   each successful action in db and then a process it and push on Kafka. It uses a game id as a kafka message key - it
   provides an order in context of single game.
6. Application stores state in memory, using dummy implementation on top of List/Map. I did my best effort to keep it
   thread/fiber safe but probably not all operations are atomic.

## Run

### Set up docker containers

`docker-compose -f docker-compose/docker-compose.yaml up -d`

It starts kafka broker along necessary containers.

### Run application

Directory `/bin` contains two bash scripts:

- `run_application.sh` - Run Http application on 8080 port.
- `run_client.sh` - Run Kafka Client.

## Application configuration

Configuration via `application.conf` files in modules.

## Swagger

Application provides documentation of the Rest Api as Swagger. You can find it under the url `/docs` i.e:
`localhost:8080/docs`.

TODO:

- Use real db instead of dummy memory implementation.
- Separate objects for each layer.
- Improve Swagger documentation because it does not show what value is expected, for example in case of a row or a
  column types. I use the Iron library first time because refined types are not supported by Scala 3.
