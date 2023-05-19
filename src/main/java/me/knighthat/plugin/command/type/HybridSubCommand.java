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

package me.knighthat.plugin.command.type;

import me.knighthat.api.command.conditions.Preconditions;
import me.knighthat.plugin.command.sub.condition.MultiplePermissionsCommand;
import me.knighthat.plugin.instance.Grave;
import me.knighthat.plugin.message.Messenger;
import me.knighthat.plugin.persistent.DataHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public abstract class HybridSubCommand extends MultiplePermissionsCommand implements Preconditions {

    // Template: /grave <SubCommand> <id> [player]

    @Override
    public boolean prerequisite(@NotNull CommandSender sender, String @NotNull [] args) {
        switch (args.length) {
            case 2:
                if (sender instanceof Player) break;
                Messenger.send(sender, "cmd_requires_player", null, null, null);
            case 1:
                Messenger.send(sender, "missing_id", null, null, null);
            case 0:
                return false;
            default:
                Player target = Bukkit.getPlayer(args[2]);
                if (target == null || !target.isOnline()) {
                    Messenger.send(sender,
                            "player_not_found",
                            null, null,
                            Map.of("%player", args[2])
                    );
                    return false;
                }
        }
        return true;
    }

    @Override
    public boolean hasPermission(@NotNull CommandSender sender, String @NotNull [] args) {
        Player target = args.length >= 3 ? Bukkit.getPlayer(args[2]) : (Player) sender;
        return super.hasPermission(sender, target);
    }

    @Override
    public void dispatch(@NotNull CommandSender sender, String @NotNull [] args) {
        Player target = args.length >= 3 ? Bukkit.getPlayer(args[2]) : (Player) sender;
        assert target != null && target.isOnline();

        Grave grave = DataHandler.get(target, args[1]);
        if (!grave.isValid()) {
            Messenger.send(sender,
                    "grave_not_found",
                    target,
                    grave,
                    Map.of("%input", args[1])
            );
            return;
        }
        this.dispatch(sender, target, grave);
    }

    public abstract void dispatch(@NotNull CommandSender sender, @NotNull Player target, @NotNull Grave grave);
}
