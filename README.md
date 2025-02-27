# ShareD20

### Overview
App for sharing dice rolls on your mobile devices around a table at an in-person RPG.

Final project will have:
- different roles for DM and players, with different visibility permissions
- ability to share dice with different levels of visibility, including DM only visibility
- create a session and join it with a code

### Usage
Currently only configured to run on localhost

Steps to run:

- Clone the repository 
- Create a file in the directory `server` called `.env` and put inside the following:
```
POSTGRES_URL=jdbc:postgresql://localhost:5432/d20_database
POSTGRES_USER=[your postgres username]
POSTGRES_PASSWORD=[your postgres password]
POSTGRES_DB=d20_database
```

- run `./gradlew server:run` to start the localhost server
(use `./gradlew server:runTempDatabase` to run using an in-memory temp database)
- To run a desktop client, use `./gradlew composeApp:run`
- To run a browser client, use `./gradlew composeApp:wasmJsBrowserDevelopmentRun`, and then open http://localhost:8080/ in your browser
- To run an Android client, open the repository in Android studio, open the emulator, and run `./gradlew gradle composeApp:installDebug` 