# Transpo-Server
[![Build Status](https://travis-ci.com/dellisd/transpo-server.svg?token=MVYEeLdes785X4q9tDBW&branch=master)](https://travis-ci.com/dellisd/transpo-server)

This is the backend server component of Route 613 written in Kotlin using [Ktor](https://ktor.io/). More accurately, 
this is a rewrite of the existing C# API but in Kotlin.

## Development setup
### Submodules
Since this project relies on a submodule ([transpo-shared](https://github.com/dellisd/transpo-shared)) you must ensure 
that the submodules are also cloned when cloning this repository. The easiest way is to simply clone this repository 
by using the `--recursive` flag. e.g.`git clone --recursive https://github.com/dellisd/transpo-server.git`

### Application Keys
In order for the backend server to communicate with OC Transpo's realtime API service the server requires an App ID and 
an API key. These keys can be configured in the `keys.properties` file located in the server module. An example 
configuration is provided in the `example.keys.properties` file. Because these keys are unique to each user, the 
`keys.properties` file should _never_ be included in the repository and git commits. As such, a local copy of the 
`keys.properties` file must be manually created each time this project is set up.

Once configured, the application keys can be accessed in code through the 
[`Keys`](server/src/main/kotlin/ca/llamabagel/transpo/server/Keys.kt) object.