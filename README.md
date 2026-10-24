# Watzon

A Discord bot for the OCE Echo VR community, written in Java with JDA. It looks up players, teams and recent matches from the VRML league data and answers commands in a server.

## Commands

| Command | What it does |
|---|---|
| `watz help` | Lists the commands |
| `watz player` | Player lookup by name |
| `watz team` | Team lookup and roster |
| `watz top` | Team rankings |
| `watz matches` | Recent matches |
| `watz size` | League or community size |
| `watz info` | Bot and league info |

Slash commands are registered too, so `/help` and the rest work where the server supports them.

## Running it

Needs a JDK and a Discord bot token. The token is read from `token.txt` in the working directory, first line only, and `*.txt` is gitignored.

```bash
./gradlew build
./gradlew run
```

The league calls live in `app/src/main/java/vrml/api/`, split into `Player`, `Team`, `Match` and `Bot`. `Multitread.java` is spelled that way in the source and I left it.

## State

Last touched in July 2022. Echo VR's servers were shut down in August 2023, so the league lookups will not return anything now. Kept as a record of the bot rather than something to deploy.

MIT licensed.
