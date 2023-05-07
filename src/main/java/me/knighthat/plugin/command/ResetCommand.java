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
import me.knighthat.api.command.conditions.OfferTabComplete;
import me.knighthat.api.command.type.HybridSubCommand;
import me.knighthat.api.persistent.DataHandler;
import me.knighthat.plugin.handler.Messenger;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ResetCommand extends HybridSubCommand implements OfferTabComplete {

    @Override
    public void execute(@NonNull CommandSender sender, @NonNull Player target, String @NonNull [] args) {
        DataHandler.reset(target);

        String path = target == sender ? "self_reset" : "player_reset";
        Messenger.send(sender, path, target);
    }

    @Override
    public @NonNull List<String> onTabComplete(@NonNull CommandSender sender, String @NonNull [] args) {
        List<String> results = new ArrayList<>();

        if (args.length == 2 && sender.hasPermission(super.playerPermission()))
            for (Player player : Bukkit.getOnlinePlayers())
                results.add(player.getName());

        return results;
    }
}
