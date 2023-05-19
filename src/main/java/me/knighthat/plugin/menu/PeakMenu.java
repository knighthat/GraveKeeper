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

package me.knighthat.plugin.menu;

import me.knighthat.api.menu.DisplayOnly;
import me.knighthat.api.menu.PluginMenu;
import me.knighthat.api.menu.button.CancelEventButton;
import me.knighthat.api.menu.button.MenuItem;
import me.knighthat.api.style.hex.AdventureHex;
import me.knighthat.api.style.hex.SpigotHex;
import me.knighthat.plugin.GraveKeeper;
import me.knighthat.plugin.instance.Grave;
import me.knighthat.plugin.message.Messenger;
import me.knighthat.utils.PlaceHolder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

public final class PeakMenu extends PluginMenu implements DisplayOnly {

    private static final @NotNull String PATH = "peak_menu";

    private PeakMenu(@NotNull String title, @Range(from = 1, to = 54) int size) {
        super(null);
        if (Messenger.isPaper) {
            Component titleComponent = AdventureHex.parse(title);
            super.inventory = Bukkit.createInventory(this, size, titleComponent);
        } else {
            String titleString = SpigotHex.parse(title);
            super.inventory = Bukkit.createInventory(this, size, titleString);
        }
    }

    public static @Nullable PeakMenu create(@NotNull Grave grave) {
        ConfigurationSection section = GraveKeeper.MENU.section(PATH);
        if (section == null) return null;

        String title = section.getString("title", "404");
        title = PlaceHolder.replace(title, grave);

        PeakMenu menu = new PeakMenu(title, 54);
        grave.getContent().items().forEach((s, i) -> {
            MenuItem item = new MenuItem(i, new CancelEventButton());
            menu.setItem(s, item);
        });

        Material material = GraveKeeper.MENU.material(PATH.concat(".exp.material"));

        String name = GraveKeeper.MENU.string(PATH.concat(".exp.name"));
        String expPoints = String.valueOf(grave.getContent().experience());
        name = PlaceHolder.replace(name, "%exp", expPoints);

        MenuItem bottle = new MenuItem(material, 1, AdventureHex.parse(name), new CancelEventButton());
        int slot = GraveKeeper.MENU.slot(PATH.concat(".exp.slot"), 54);
        menu.setItem(slot, bottle);

        return menu;
    }
}
