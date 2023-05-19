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

package me.knighthat.plugin.command.tabcomplete;

import lombok.NonNull;
import me.knighthat.api.command.tabcomplete.TabCompleter;
import me.knighthat.plugin.instance.Grave;
import me.knighthat.plugin.persistent.DataHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface HybridTabComplete extends TabCompleter {

    @Override
    @NonNull
    default List<String> tabComplete(@NonNull CommandSender sender, @NonNull String s, String @NonNull [] args) {
        List<String> results = new ArrayList<>();

        switch (args.length) {
            case 2 -> {
                if (!(sender instanceof Player player))
                    break;

                Grave[] graves = DataHandler.pull(player);
                results.addAll(Arrays.stream(graves).map(Grave::getId).toList());
            }
            case 3 -> {
                for (Player player : Bukkit.getOnlinePlayers())
                    results.add(player.getName());
            }
        }
        return results;
    }
}
