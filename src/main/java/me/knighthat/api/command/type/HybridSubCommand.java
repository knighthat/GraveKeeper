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

package me.knighthat.api.command.type;

import lombok.NonNull;
import me.knighthat.api.command.SubCommand;
import me.knighthat.plugin.message.Messenger;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

import java.util.Map;

public abstract class HybridSubCommand extends SubCommand {

    @Override
    public boolean playerOnly() {
        return false;
    }

    @Deprecated
    @Override
    public @NonNull String permission() {
        return "";
    }

    @Override
    public boolean hasPermission(@NonNull Permissible permissible) {
        return true;
    }

    @Override
    public boolean prerequisite(@NonNull CommandSender sender, String @NonNull [] args) {
        Player target;

        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                Messenger.send(sender, "cmd_requires_player");
                return false;
            } else {
                target = player;
            }
        } else {
            target = Bukkit.getPlayer(args[0]);
            if (target == null || !target.isOnline()) {
                Messenger.send(sender, "player_not_found", Map.of("%player", args[0]));
                return false;
            }
        }

        if ((target == sender && !sender.hasPermission(this.selfPermission())) ||
                (target != sender && !sender.hasPermission(this.playerPermission()))) {
            Messenger.send(sender, "no_cmd_perm");
            return false;
        }

        return true;
    }

    public @NonNull String selfPermission() {
        return "grave.command." + getName() + ".self";
    }

    public @NonNull String playerPermission() {
        return "grave.command." + getName() + ".players";
    }

    @Override
    public void execute(@NonNull CommandSender sender, String @NonNull [] args) {
        Player target = args.length >= 1 ? Bukkit.getPlayer(args[0]) : (Player) sender;
        assert target != null;

        execute(sender, target, args);
    }

    public abstract void execute(@NonNull CommandSender sender, @NonNull Player target, String @NonNull [] args);
}
