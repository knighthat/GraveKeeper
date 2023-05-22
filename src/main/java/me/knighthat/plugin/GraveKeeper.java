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

import me.knighthat.KnightHatAPI;
import me.knighthat.plugin.command.CommandManager;
import me.knighthat.plugin.event.EventController;
import me.knighthat.plugin.file.ConfigFile;
import me.knighthat.plugin.file.MenuFile;
import me.knighthat.plugin.file.MessageFile;
import me.knighthat.plugin.message.Messenger;
import me.knighthat.plugin.persistent.DataHandler;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.NamespacedKey;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class GraveKeeper extends JavaPlugin {

    public static GraveKeeper INSTANCE;

    public static MenuFile MENU;
    public static ConfigFile CONFIG;

    @Override
    public void onEnable() {
        INSTANCE = this;

        KnightHatAPI.init(this);

        DataHandler.KEY = new NamespacedKey(this, getDescription().getName());

        CONFIG = new ConfigFile(this);
        Messenger.FILE = new MessageFile(this);
        Messenger.AUDIENCES = BukkitAudiences.create(this);
        MENU = new MenuFile(this);

        // Register event handler to Server
        getServer().getPluginManager().registerEvents(new EventController(), this);

        // Register commands and tab completer
        PluginCommand command = getCommand("gravekeeper");
        CommandManager commandManager = new CommandManager();

        command.setExecutor(commandManager);
        command.setTabCompleter(commandManager);
    }
}
