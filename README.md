# sagittarius
An injection framework for internal bytecode manipulations in the bukkit/spigot framework. Like an archer this plugin aims to snipe classes out of the normal spigot / bukkit context.

## Usage

sagittarius is a launch wrapper for the actual spigot/bukkit jar, and links to it. We recommend you use a launch script like `java -jar injector-1.0-SNAPSHOT.jar`
injector will automatically link to the bukkit / spigot main class (as long as it's on the classpath) and start the server once it's done with its manipulation.
