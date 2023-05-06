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
import me.knighthat.api.command.conditions.PlayerCommand;
import me.knighthat.plugin.menu.MenuManager;
import me.knighthat.plugin.message.Messenger;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends SubCommand implements PlayerCommand {

    @Override
    public @NonNull String permission() {
        return "reload";
    }

    @Override
    public boolean prerequisite(@NonNull CommandSender sender, String @NonNull [] args) {
        return true;
    }

    @Override
    public void execute(@NonNull CommandSender sender, String @NonNull [] args) {
        Messenger.FILE.reload();
        MenuManager.FILE.reload();
        Messenger.send(sender, "reload");
    }
}
