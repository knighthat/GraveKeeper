/*
 * Copyright (c) 2023. Knight Hat
 * All rights reserved.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 *  and associated documentation files (the "Software"), to deal in the Software without restriction,
 *  including without limitation the rights to use, copy, modify, merge, publish, distribute,sublicense, and/or sell copies of the Software,
 *   and to permit persons to whom the Software is furnished to do so,
 *   subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 *  DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 *  ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.knighthat.plugin.command;

import me.knighthat.api.command.conditions.PlayerCommand;
import me.knighthat.api.command.conditions.Preconditions;
import me.knighthat.api.command.permission.PermissionCommand;
import me.knighthat.api.command.tabcomplete.TabCompleter;
import me.knighthat.plugin.command.sub.*;
import me.knighthat.plugin.message.Messenger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CommandManager implements TabExecutor {

    public static final @NotNull List<SubCommand> SUB_COMMANDS = new LinkedList<>();

    static {
        SUB_COMMANDS.add(new ReloadCommand());
        SUB_COMMANDS.add(new ListCommand());
        SUB_COMMANDS.add(new PeakCommand());
        SUB_COMMANDS.add(new ResetCommand());
        SUB_COMMANDS.add(new DeleteCommand());
        SUB_COMMANDS.add(new TeleportCommand());
        SUB_COMMANDS.add(new HelpCommand());
        HelpCommand.reload();
    }

    @Override
    public boolean onCommand ( @NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args ) {
        if (args.length == 0) return true;

        SubCommand sub = get(args[0]);
        if (sub == null) return true;

        if (sub instanceof PlayerCommand && !( sender instanceof Player )) {
            Messenger.send(sender, "cmd_requires_player", null, null, null);
            return true;
        }
        if (sub instanceof Preconditions preconditions
                && !preconditions.prerequisite(sender, args)) return true;
        if (sub instanceof PermissionCommand instance
                && !instance.hasPermission(sender, args)) {
            Messenger.send(sender, "no_cmd_perm", null, null, null);
            return true;
        }

        sub.dispatch(sender, args);
        return true;
    }

    private @Nullable SubCommand get ( @NotNull String name ) {

        for (SubCommand sub : SUB_COMMANDS)
            if (sub.name().equalsIgnoreCase(name))
                return sub;

        return null;
    }

    @Override
    public @Nullable List<String> onTabComplete ( @NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args ) {
        List<String> results = new ArrayList<>();

        List<SubCommand> available = new ArrayList<>();
        if (args.length >= 1)
            for (SubCommand sub : SUB_COMMANDS) {
                if (sub instanceof PermissionCommand instance
                        && !instance.hasPermission(sender, args))
                    continue;

                available.add(sub);
            }

        if (args.length == 1) {

            available
                    .stream()
                    .map(SubCommand::name)
                    .forEach(results::add);
        } else
            for (SubCommand sub : available) {
                if (!sub.name().equalsIgnoreCase(args[0]) ||
                        ( sub instanceof PlayerCommand && !( sender instanceof Player ) ))
                    continue;

                if (sub instanceof TabCompleter completer)
                    results.addAll(completer.tabComplete(sender, label, args));
            }

        return results
                .stream()
                .filter(a -> a.startsWith(args[args.length - 1]))
                .toList();
    }
}
