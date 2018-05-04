/*
 * This file is part of Commands4J.
 *
 * Commands4J is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Commands4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Commands4J.  If not, see <http://www.gnu.org/licenses/>.
 */
package commands4j.core;

import commands4j.core.config.CommandFilter;
import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.message.MessageCreateEvent;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;

public final class CommandRegistry {

    private static final Logger LOGGER = Loggers.getLogger(CommandRegistry.class);

    private static void linkCommands(final Command parentCommand, final Command subCommand) {
        final boolean addedParentCommand = subCommand.getRawParentCommands().add(parentCommand);
        LOGGER.trace("Added Status of Parent Command, {}, for {}: {}", parentCommand, subCommand, addedParentCommand);
        final boolean addedSubCommand = parentCommand.getRawSubCommands().add(subCommand);
        LOGGER.trace("Added Status of SubCommand, {}, for {}, {}", subCommand, parentCommand, addedSubCommand);
    }

    private final Map<String, Command> commands;
    private final Set<Command> mainCommands;

    public CommandRegistry(final EventDispatcher eventDispatcher) {
        mainCommands = ConcurrentHashMap.newKeySet();
        commands = new ConcurrentHashMap<>();

        eventDispatcher.on(MessageCreateEvent.class).flatMap(this::apply).subscribe();
    }

    public Map<String, Command> getCommands() {
        return Collections.unmodifiableMap(commands);
    }

    public Set<Command> getMainCommands() {
        return Collections.unmodifiableSet(mainCommands);
    }

    public synchronized boolean removeCommand(final Command command) {
        final boolean removedCommand = commands.remove(command.getName()) != null;
        LOGGER.info("Removal Status of Command, {}: {}", command, removedCommand);

        final boolean removedMainCommand = mainCommands.remove(command);
        LOGGER.trace("Removal Status of Main Command, {}: {}", command, removedMainCommand);

        command.getRawParentCommands().removeIf(parentCommand -> {
            final boolean removedSubCommand = parentCommand.getRawSubCommands().remove(command);
            LOGGER.trace("Removal Status of SubCommand, {}, for {}: {}", command, parentCommand, removedSubCommand);
            return removedSubCommand;
        });

        command.getRawSubCommands().removeIf(subCommand -> {
            final boolean removedParentCommand = subCommand.getRawParentCommands().remove(command);
            LOGGER.trace("Removal Status of Parent Command, {}. for {}: {}", command, subCommand, removedParentCommand);
            return removedParentCommand;
        });

        return removedCommand;
    }

    public synchronized boolean addCommand(final Command command) {
        final boolean addedCommand = commands.putIfAbsent(command.getName(), command) == null;
        LOGGER.info("Added Status of Command, {}: {}", command, addedCommand);

        command.getArgumentFactory()
            .map(ignored -> mainCommands.add(command))
            .ifPresent(status -> LOGGER.trace("Added Status of Main Command, {}: {}", command, status));

        commands.values().stream()
            .filter(parentCommand -> parentCommand.getSubCommandNames().contains(command.getName()))
            .forEach(parentCommand -> linkCommands(parentCommand, command));

        commands.values().stream()
            .filter(subCommand -> subCommand.getParentCommandNames().contains(command.getName()))
            .forEach(subCommand -> linkCommands(command, subCommand));

        command.getParentCommandNames().stream()
            .map(commands::get)
            .filter(Objects::nonNull)
            .forEach(parentCommand -> linkCommands(parentCommand, command));

        command.getSubCommandNames().stream()
            .map(commands::get)
            .filter(Objects::nonNull)
            .forEach(subCommand -> linkCommands(command, subCommand));

        return addedCommand;
    }

    private Flux<Void> apply(final MessageCreateEvent t) {
        return Flux.fromIterable(mainCommands)
            .flatMap(command -> command.getArgumentFactory()
                .orElseThrow(IllegalStateException::new)
                .getArguments(command, t)
                .filter(arguments -> !arguments.isEmpty())
                .map(args -> new CommandContext(command, args.get(0), args.subList(1, args.size()), t,
                    new AtomicReference<>()))) // Executed on the first argument it created for itself
            .flatMap(this::apply);
    }

    private Mono<Void> apply(final CommandContext t) {
        return Flux.fromIterable(t.getCommand().getFilters())
            .filterWhen(commandFilter -> commandFilter.filter(t))
            // If no CommandFilters return true, execute, then cast safely on the empty Mono
            .switchIfEmpty(t.getCommand().getExecutor().execute(t).cast(CommandFilter.class))
            .then(Flux.fromIterable(t.getCommand().getSubCommands())
                .flatMap(command -> Mono.just(t.getArguments())
                    .filter(arguments -> !arguments.isEmpty())
                    .map(args -> new CommandContext(command, args.get(0), args.subList(1, args.size()), t.getEvent(),
                        t.getState()))) // SubCommands are potentially executed on the next argument (if it exists)
                .flatMap(this::apply)
                .ignoreElements());
    }
}
