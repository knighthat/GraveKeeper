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

package me.knighthat.api.command;

import lombok.NonNull;
import me.knighthat.plugin.command.ReloadCommand;
import me.knighthat.plugin.message.Messenger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class CommandManager implements TabExecutor {

    private static final @NonNull List<SubCommand> SUB_COMMANDS = new LinkedList<>();

    static {
        SUB_COMMANDS.add(new ReloadCommand());
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) return true;

        SubCommand sub = get(args[0]);
        if (sub == null) return true;

        if (sub.playerOnly() && !(sender instanceof Player))
            return true;

        if (!sub.hasPermission(sender)) {
            Messenger.send(sender, "no_cmd_perm");
            return true;
        }

        if (!sub.prerequisite(sender, args))
            return true;

        sub.execute(sender, removeFirst(args));

        return true;
    }

    private @Nullable SubCommand get(@NonNull String name) {

        for (SubCommand sub : SUB_COMMANDS)
            if (sub.getName().equalsIgnoreCase(name))
                return sub;

        return null;
    }

    private String @NonNull [] removeFirst(@NonNull String[] args) {
        return args.length <= 1 ? new String[0] : Arrays.copyOfRange(args, 1, args.length);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String start, @NotNull String[] args) {
        List<String> results = new ArrayList<>();

        if (args.length == 1)
            for (SubCommand sub : SUB_COMMANDS)
                if (sub.getName().startsWith(args[0]))
                    results.add(sub.getName());

        return results;
    }
}