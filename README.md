# Transpo-Server
This is the backend server component of Route 613 written in Kotlin using [Ktor](http://ktor.io/).

## Development Setup
### Application Keys
In order for the backend server to communicate with OC Transpo's realtime API service the server requires an App ID and 
an API key. These keys can be configured in the `keys.properties` file located in the server module. An example 
configuration is provided in the `example.keys.properties` file. Because these keys are unique to each user, the 
`keys.properties` file should _never_ be included in the repository and git commits. As such, a local copy of the 
`keys.properties` file must be manually created each time this project is set up.