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
import me.knighthat.api.persistent.DataHandler;
import me.knighthat.plugin.instance.Grave;
import me.knighthat.plugin.message.Messenger;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public abstract class ReverseHybridSubCommand extends HybridSubCommand {

    @Override
    public boolean prerequisite(@NonNull CommandSender sender, String @NonNull [] args) {
        Player target;

        switch (args.length) {
            case 0 -> {
                Messenger.send(sender, "missing_id");
                return false;
            }
            case 1 -> {
                if (!(sender instanceof Player player)) {
                    Messenger.send(sender, "cmd_requires_player");
                    return false;
                }
                target = player;
            }
            default -> {
                target = Bukkit.getPlayer(args[1]);
                if (target == null || !target.isOnline()) {
                    Messenger.send(sender, "player_not_found", Map.of("%player", args[1]));
                    return false;
                }
            }
        }

        if ((target == sender && !sender.hasPermission(super.selfPermission())) ||
                (target != sender && !sender.hasPermission(super.playerPermission()))) {
            Messenger.send(sender, "no_cmd_perm");
            return false;
        }

        return true;
    }

    @Override
    public void execute(@NonNull CommandSender sender, String @NonNull [] args) {
        Player target = args.length >= 2 ? Bukkit.getPlayer(args[1]) : (Player) sender;
        assert target != null;

        Grave grave = DataHandler.get(target, args[0]);
        if (!grave.isValid()) {
            Messenger.send(sender, "grave_not_found", Map.of("%id", args[0]));
            return;
        }

        execute(sender, target, grave);
    }

    @Deprecated
    @Override
    public void execute(@NonNull CommandSender sender, @NonNull Player target, String @NonNull [] args) {
    }

    public abstract void execute(@NonNull CommandSender sender, @NonNull Player target, @NonNull Grave grave);
}
