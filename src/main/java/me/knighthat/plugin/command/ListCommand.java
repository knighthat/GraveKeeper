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

import lombok.NonNull;
import me.knighthat.api.command.SubCommand;
import me.knighthat.api.persistent.DataHandler;
import me.knighthat.plugin.grave.Grave;
import me.knighthat.plugin.message.Messenger;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

import java.util.Map;
import java.util.StringJoiner;

public class ListCommand extends SubCommand {


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
        String selfPerm = PERMISSION.concat("list.self");
        String allPerm = PERMISSION.concat("list.player");

        if (args.length == 0 || args[0].equals(sender.getName())) {
            if (!(sender instanceof Player)) {
                Messenger.send(sender, "cmd_requires_player");
                return false;
            } else if (!sender.hasPermission(selfPerm)) {
                Messenger.send(sender, "no_cmd_perm");
                return false;
            }
            return true;
        }

        Player player = Bukkit.getPlayer(args[0]);
        if (player == null || !player.isOnline()) {
            Messenger.send(sender, "player_not_found", Map.of("%player", args[0]));
            return false;
        }

        if (!sender.hasPermission(allPerm)) {
            Messenger.send(sender, "no_cmd_perm");
            return false;
        }

        return true;
    }

    @Override
    public void execute(@NonNull CommandSender sender, String @NonNull [] args) {
        boolean isSelf = args.length == 0;
        Player target = isSelf ? (Player) sender : Bukkit.getPlayer(args[0]);
        assert target != null;

        String idList = graveArrayToString(DataHandler.pull(target));
        if (idList.isEmpty()) {
            Messenger.send(sender, isSelf ? "self_graves_empty" : "player_graves_empty");
            return;
        }

        Map<String, String> replacements = Map.of("%player", target.getName());
        Messenger.send(sender, isSelf ? "self_graves" : "player_graves", replacements);
        Messenger.send(sender, new Messenger.Message(idList));
    }

    private @NonNull String graveArrayToString(@NonNull Grave... graves) {
        if (graves.length == 0)
            return "";

        StringJoiner joiner = new StringJoiner("\n");
        for (Grave grave : graves)
            if (grave.isValid())
                joiner.add(" - " + grave.getId());

        return joiner.toString();
    }
}
