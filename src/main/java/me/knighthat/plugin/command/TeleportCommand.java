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
import me.knighthat.api.command.conditions.PlayerCommand;
import me.knighthat.api.command.conditions.ReverseHybridTabComplete;
import me.knighthat.api.command.type.ReverseHybridSubCommand;
import me.knighthat.plugin.instance.Grave;
import me.knighthat.plugin.message.Messenger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportCommand extends ReverseHybridSubCommand implements PlayerCommand, ReverseHybridTabComplete {

    @Override
    public void execute(@NonNull CommandSender sender, @NonNull Player target, @NonNull Grave grave) {
        Player player = (Player) sender;

        Location destination = grave.getCoordinates().get().add(.5d, 1d, .5d);
        destination.setPitch(90f);

        boolean isSafe = true;
        for (int i = 1; i <= 2; i++) {
            Location clone = destination.clone();
            isSafe = clone.add(0d, i, 0d).getBlock().getType().equals(Material.AIR);
        }

        String path = "teleport_not_safe";

        if (isSafe) {
            player.teleport(destination);
            path = "teleport_message";
        }

        Messenger.send(sender, path, target, grave, null);
    }
}
