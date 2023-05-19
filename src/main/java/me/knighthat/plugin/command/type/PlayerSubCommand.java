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
import me.knighthat.plugin.message.Messenger;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public abstract class PlayerSubCommand extends MultiplePermissionsCommand implements Preconditions {

    // Template: /grave <SubCommand> [player]

    @Override
    public boolean prerequisite(@NotNull CommandSender sender, String @NotNull [] args) {
        switch (args.length) {
            case 1:
                if (!(sender instanceof Player))
                    Messenger.send(sender, "cmd_requires_player", null, null, null);
                else break;
            case 0:
                return false;
            default:
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null || !target.isOnline()) {
                    Messenger.send(sender,
                            "player_not_found",
                            null, null,
                            Map.of("%player", args[1])
                    );
                    return false;
                }
        }
        return true;
    }

    @Override
    public boolean hasPermission(@NotNull CommandSender sender, String @NotNull [] args) {
        Player target = args.length >= 2 ? Bukkit.getPlayer(args[1]) : (Player) sender;
        return super.hasPermission(sender, target);
    }

    @Override
    public void dispatch(@NotNull CommandSender sender, String @NotNull [] args) {
        Player target = args.length >= 2 ? Bukkit.getPlayer(args[1]) : (Player) sender;
        assert target != null && target.isOnline();

        this.dispatch(sender, target, args);
    }

    public abstract void dispatch(@NotNull CommandSender sender, @NotNull Player player, String @NotNull [] args);
}
