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
import me.knighthat.api.command.conditions.MultiplePermissions;
import me.knighthat.api.command.conditions.OfferTabComplete;
import me.knighthat.api.command.conditions.PlayerCommand;
import me.knighthat.api.command.conditions.SinglePermission;
import me.knighthat.api.command.type.ReverseHybridSubCommand;
import me.knighthat.plugin.command.*;
import me.knighthat.plugin.message.Messenger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CommandManager implements TabExecutor {

    public static final @NonNull List<SubCommand> SUB_COMMANDS = new LinkedList<>();

    static {
        SUB_COMMANDS.add(new ReloadCommand());
        SUB_COMMANDS.add(new ListCommand());
        SUB_COMMANDS.add(new PeakCommand());
        SUB_COMMANDS.add(new ResetCommand());
        SUB_COMMANDS.add(new DeleteCommand());
        SUB_COMMANDS.add(new TeleportCommand());
        SUB_COMMANDS.add(new HelpCommand());
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) return true;

        SubCommand sub = get(args[0]);
        if (sub == null) return true;

        String[] newArgs = removeFirst(args);
        PermissionStatus permissionStatus = PermissionStatus.PASSED;

        if (!(sub instanceof PlayerCommand && !(sender instanceof Player))) {
            if (sub instanceof SinglePermission permission) {
                permissionStatus = permission.hasPermission(sender);
            } else if (sub instanceof MultiplePermissions permissions) {
                permissionStatus = permissions.hasPermission(sender, newArgs);
            }
        } else permissionStatus = PermissionStatus.NOT_PLAYER;

        Map<String, String> replacement = new HashMap<>();
        String path = switch (permissionStatus) {
            case MISSING_ID -> "missing_id";
            case NO_PERMISSION -> "no_cmd_perm";
            case NOT_PLAYER -> "cmd_requires_player";
            case PLAYER_NOT_FOUND -> {
                int index = sub instanceof ReverseHybridSubCommand ? 1 : 0;
                replacement.put("%player", newArgs[index]);
                yield "player_not_found";
            }
            case PASSED -> {
                sub.execute(sender, newArgs);
                yield "";
            }
        };

        if (!path.isEmpty())
            Messenger.send(sender, path, null, null, replacement);

        return true;
    }

    private @Nullable SubCommand get(@NonNull String name) {

        for (SubCommand sub : SUB_COMMANDS)
            if (sub.name().equalsIgnoreCase(name))
                return sub;

        return null;
    }

    private String @NonNull [] removeFirst(@NonNull String[] args) {
        return args.length <= 1 ? new String[0] : Arrays.copyOfRange(args, 1, args.length);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String start, @NotNull String[] args) {
        List<String> results = new ArrayList<>();

        if (args.length == 1) {

            for (SubCommand sub : SUB_COMMANDS)
                results.add(sub.name());

        } else {

            SubCommand sub = get(args[0]);
            if (sub instanceof PlayerCommand && !(sender instanceof Player))
                return results;

            if (sub instanceof OfferTabComplete instance)
                results.addAll(instance.onTabComplete(sender, args));

        }

        return results
                .stream()
                .filter(a -> a.startsWith(args[args.length - 1]))
                .sorted()
                .toList();
    }
}
