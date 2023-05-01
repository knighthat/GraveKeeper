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

import lombok.NonNull;
import me.knighthat.api.menu.PluginMenu;
import me.knighthat.api.style.Converter;
import me.knighthat.plugin.file.MenuFile;
import me.knighthat.plugin.grave.Grave;
import me.knighthat.plugin.message.Messenger;
import me.knighthat.utils.GraveData;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class MenuManager {

    private static final @NonNull String PEAK = "peak_menu.";
    public static @NonNull MenuFile FILE;


    public static @NonNull Inventory peak(@NonNull Grave grave) {

        String title = FILE.string(PEAK.concat("title"));
        title = Converter.apply(title, GraveData.replacements(grave, GraveData.DataType.ALL));

        PluginMenu menu = new PluginMenu(Messenger.STYLE.color(title), 54) {
        };
        menu.setContent(grave.getContent().items());

        int exp_slot = FILE.slot(PEAK.concat("exp.slot"), 54);
        menu.setItem(exp_slot, expBottle(grave));

        return menu.build();
    }

    private static @NonNull ItemStack expBottle(@NonNull Grave grave) {
        String exp_name = FILE.string(PEAK.concat("exp.name"));
        Map<String, String> replacements = GraveData.replacements(grave, GraveData.DataType.CONTENT);
        String name = Converter.apply(exp_name, replacements);

        ItemStack exp_bottle = new ItemStack(FILE.material(PEAK.concat("exp.material")));
        exp_bottle.editMeta(meta -> meta.setDisplayName(
                Converter.apply(Messenger.STYLE.color(name), grave.getCoordinates().replacements())
        ));

        return exp_bottle;
    }
}
