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

import discord4j.core.event.domain.message.MessageCreateEvent;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public final class CommandContext {

    private final Command command;
    private final String argument;
    private final List<String> arguments;
    private final MessageCreateEvent event;
    private final AtomicReference<Object> state;

    CommandContext(final Command command, final String argument, final List<String> arguments,
                   final MessageCreateEvent event, final AtomicReference<Object> state) {

        this.command = command;
        this.argument = argument;
        this.arguments = arguments;
        this.event = event;
        this.state = state;
    }

    public Command getCommand() {
        return command;
    }

    public String getArgument() {
        return argument;
    }

    public List<String> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    public MessageCreateEvent getEvent() {
        return event;
    }

    @SuppressWarnings("unchecked")
    public <T> AtomicReference<T> getState() {
        return (AtomicReference<T>) state;
    }
}
