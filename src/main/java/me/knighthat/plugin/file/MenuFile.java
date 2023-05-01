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

package me.knighthat.plugin.file;

import lombok.NonNull;
import me.knighthat.api.file.PluginFile;
import me.knighthat.debugger.Debugger;
import me.knighthat.plugin.GraveKeeper;
import org.bukkit.Material;
import org.jetbrains.annotations.Range;

import java.text.MessageFormat;

public class MenuFile extends PluginFile {


    public MenuFile(@NonNull GraveKeeper plugin) {
        super(plugin, "menu");
    }

    public @Range(from = 0, to = 53) int slot(@NonNull String path, int max) {
        int slot = super.integer(path);
        if (slot < 1 || slot > max) {
            Debugger.warn(
                    MessageFormat.format("{0} returns out of bound number (1 to {1})", path, max)
            );
            slot = 1;
        }
        return slot - 1;
    }

    public @NonNull Material material(@NonNull String path) {
        String materialString = super.string(path).toUpperCase();
        try {
            return Material.valueOf(materialString);
        } catch (IllegalArgumentException e) {
            Debugger.warn(
                    MessageFormat.format("{0} returns invalid material name", path)
            );
            return Material.AIR;
        }

    }
}
