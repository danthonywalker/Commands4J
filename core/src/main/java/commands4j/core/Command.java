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

import commands4j.core.config.ArgumentFactory;
import commands4j.core.config.CommandExecutor;
import commands4j.core.config.CommandLimiter;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

public final class Command {

    private final String name;
    private final CommandExecutor executor;
    private final Set<CommandLimiter> limiters;
    @Nullable private final ArgumentFactory argumentFactory;
    private final Set<String> parentCommandNames;
    private final Set<String> subCommandNames;
    private final Set<Command> parentCommands;
    private final Set<Command> subCommands;

    Command(final String name, final CommandExecutor executor, final Set<CommandLimiter> limiters,
            @Nullable final ArgumentFactory argumentFactory, final Set<String> parentCommandNames,
            final Set<String> subCommandNames) {

        this.name = name;
        this.executor = executor;
        this.limiters = limiters;
        this.argumentFactory = argumentFactory;
        this.parentCommandNames = parentCommandNames;
        this.subCommandNames = subCommandNames;
        parentCommands = ConcurrentHashMap.newKeySet();
        subCommands = ConcurrentHashMap.newKeySet();
    }

    public String getName() {
        return name;
    }

    public CommandExecutor getExecutor() {
        return executor;
    }

    public Set<CommandLimiter> getLimiters() {
        return Collections.unmodifiableSet(limiters);
    }

    public Optional<ArgumentFactory> getArgumentFactory() {
        return Optional.ofNullable(argumentFactory);
    }

    Set<String> getParentCommandNames() {
        return parentCommandNames;
    }

    Set<String> getSubCommandNames() {
        return subCommandNames;
    }

    public Set<Command> getParentCommands() {
        return Collections.unmodifiableSet(parentCommands);
    }

    public Set<Command> getSubCommands() {
        return Collections.unmodifiableSet(subCommands);
    }

    Set<Command> getRawParentCommands() {
        return parentCommands;
    }

    Set<Command> getRawSubCommands() {
        return subCommands;
    }

    @Override
    public boolean equals(final Object obj) {
        return (obj instanceof Command) && ((Command) obj).name.equals(name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "Command(" + name + ")";
    }
}
