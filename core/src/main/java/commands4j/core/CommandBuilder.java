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
import commands4j.core.config.CommandFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;

public final class CommandBuilder {

    private String name;
    private CommandExecutor executor;
    private Set<CommandFilter> filters;
    @Nullable private ArgumentFactory argumentFactory;
    private Set<String> parentCommandNames;
    private Set<String> subCommandNames;

    public CommandBuilder(final Command command) {
        name = command.getName();
        executor = command.getExecutor();
        filters = command.getFilters();
        argumentFactory = command.getArgumentFactory().orElse(null);
        parentCommandNames = command.getParentCommandNames();
        subCommandNames = command.getSubCommandNames();
    }

    public CommandBuilder(final String name, final CommandExecutor executor) {
        this.name = name;
        this.executor = executor;
        filters = Collections.emptySet();
        argumentFactory = null;
        parentCommandNames = Collections.emptySet();
        subCommandNames = Collections.emptySet();
    }

    public String getName() {
        return name;
    }

    public CommandBuilder setName(final String name) {
        this.name = name;
        return this;
    }

    public CommandExecutor getExecutor() {
        return executor;
    }

    public CommandBuilder setExecutor(final CommandExecutor executor) {
        this.executor = executor;
        return this;
    }

    public Set<CommandFilter> getFilters() {
        return Collections.unmodifiableSet(filters);
    }

    public CommandBuilder setFilters(final Iterable<? extends CommandFilter> filters) {
        this.filters = StreamSupport.stream(filters.spliterator(), false).collect(Collectors.toSet());
        return this;
    }

    public CommandBuilder setFilters(final CommandFilter...filters) {
        return setFilters(Arrays.asList(filters));
    }

    public Optional<ArgumentFactory> getArgumentFactory() {
        return Optional.ofNullable(argumentFactory);
    }

    public CommandBuilder setArgumentFactory(@Nullable final ArgumentFactory argumentFactory) {
        this.argumentFactory = argumentFactory;
        return this;
    }

    public Set<String> getParentCommandNames() {
        return Collections.unmodifiableSet(parentCommandNames);
    }

    public CommandBuilder setParentCommandNames(final Iterable<String> parentCommandNames) {
        this.parentCommandNames = StreamSupport.stream(parentCommandNames.spliterator(), false).collect(Collectors.toSet());
        return this;
    }

    public CommandBuilder setParentCommandNames(final String...parentCommandNames) {
        return setParentCommandNames(Arrays.asList(parentCommandNames));
    }

    public Set<String> getSubCommandNames() {
        return Collections.unmodifiableSet(subCommandNames);
    }

    public CommandBuilder setSubCommandNames(final Iterable<String> subCommandNames) {
        this.subCommandNames = StreamSupport.stream(subCommandNames.spliterator(), false).collect(Collectors.toSet());
        return this;
    }

    public CommandBuilder setSubCommandNames(final String...subCommandNames) {
        return setSubCommandNames(Arrays.asList(subCommandNames));
    }

    public Command build() {
        return new Command(name, executor, filters, argumentFactory, parentCommandNames, subCommandNames);
    }
}
