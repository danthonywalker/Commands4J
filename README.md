# Commands4J
The Command API for Discord4J!

## Adding Commands4J as a Dependency
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.Discord4J-Addons:Commands4J:VERSION'
}
```

## Getting Started with Core
```java
DiscordClient client = new ClientBuilder(token).build();
CommandRegistry registry = new CommandRegistry(client.getEventDispatcher());

// When a command is executed, take the CommandContext, get the event, and respond with "Pong!".
// then() is called because CommandExecutor requires a Mono<Void> return, which then() simply provides.
CommandExecutor executor = context -> context.getEvent().getChannel().createMessage(spec -> spec.setContent("Pong!")).then();

// All main commands MUST have an ArgumentFactory. Every time a message is received, all main commands (thus all
// ArgumentFactory instances) are executed first. ArgumentFactory requires a Mono<List<String>> return so wrap the
// content of the message in Mono.just(); then check if the message starts with our arbitrary prefix ("!" is our case).
// Then remove it from the message and then split the message by spaces, that way, our arguments are determined by the
// spaces. If the Mono returns empty (or the list it conaints), then our command won't be considered for execution.
ArgumentFactory factory = (command, event) -> Mono.just(event.getMessage().getContent())
    .filter(content -> content.startsWith("!"))
    .map(content -> content.replaceFirst("!", ""))
    .map(content -> Arrays.asList(content.split(" ")));

// CommandLimiter expects a Mono<Boolean> so we wrap this logic in Mono.just(). If the Mono returns true, then the
// command WON'T be executed. So here we are simply checking for commands that AREN'T "ping". Coupled with the
// ArgumentFactory logic from earlier, this means we are looking for messages that say "!ping".
CommandLimiter limiter = context -> Mono.just(!context.getArgument().equals("ping"));

Command command = new CommandBuilder("MyCommand", executor).setArgumentFactory(factory).setLimiters(limiter).build();
registry.addCommand(command);
```
TODO: SubCommands and Parent Commands

## Getting Started with Util
TODO
