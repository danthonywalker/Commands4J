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
package commands4j.core.config;

import commands4j.core.Command;
import discord4j.core.event.domain.message.MessageCreateEvent;
import java.util.List;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface ArgumentFactory {

    Mono<List<String>> getArguments(Command command, MessageCreateEvent event);
}
