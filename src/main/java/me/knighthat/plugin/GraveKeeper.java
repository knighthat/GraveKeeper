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

package me.knighthat.plugin;

import me.knighthat.api.command.CommandManager;
import me.knighthat.debugger.Debugger;
import me.knighthat.plugin.event.EventController;
import me.knighthat.plugin.file.MenuFile;
import me.knighthat.plugin.file.MessageFile;
import me.knighthat.plugin.handler.Messenger;
import me.knighthat.plugin.menu.MenuManager;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class GraveKeeper extends JavaPlugin {

    public static boolean isPaper = Bukkit.getServer().getName().equals("Paper");

    {
        Debugger.FALLBACK = this.getLogger();
        if (getServer().getName().equals("Paper"))
            Debugger.LOGGER = this.getSLF4JLogger();
        Messenger.FILE = new MessageFile(this);
        MenuManager.FILE = new MenuFile(this);
    }

    @Override
    public void onEnable() {

        // Register event handler to Server
        getServer().getPluginManager().registerEvents(new EventController(), this);

        // Register commands and tab completer
        registerCommands();
    }


    private void registerCommands() {
        PluginCommand command = getCommand("gravekeeper");
        CommandManager commandManager = new CommandManager();

        command.setExecutor(commandManager);
        command.setTabCompleter(commandManager);
    }
}
