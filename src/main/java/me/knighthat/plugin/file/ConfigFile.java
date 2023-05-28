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
import me.knighthat.api.file.yaml.YamlFile;
import me.knighthat.plugin.GraveKeeper;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ConfigFile extends YamlFile {

    public ConfigFile ( @NonNull GraveKeeper plugin ) {
        super(plugin, "config");
    }

    public static @NotNull String replacePlaceholder ( @NotNull String original ) {
        return original.replace("%player_display", "%display");
    }

    public @NotNull Material material ( @NotNull String path ) {
        return this.material(path, Material.AIR);
    }

    public @NotNull Material material ( @NotNull String path, @NotNull Material def ) {
        Material material = def;
        String fromFile = super.string(path);
        for (Material m : Material.values())
            if (fromFile.equalsIgnoreCase(m.name()))
                material = m;
        return material;
    }

    public @NotNull Material wallSign () {
        Material fromFile = this.material("epitaph.material");
        return fromFile.name().endsWith("WALL_SIGN") ? fromFile : Material.OAK_WALL_SIGN;
    }

    public @NotNull List<String> epitaph () {
        return super.list("epitaph.texts");
    }

    public long coolDown () {
        //    Millis * Seconds
        return 1000L * super.Long("cool-down");
    }

    @Override
    public synchronized void reload () {
        super.reload();

        String version = super.getPlugin().getDescription().getVersion();

        if (!get().getString("version", "").equals(version))
            super.getPlugin().saveResource(super.getName(), true);

        write("version", version, false);

        for (String key : get().getKeys(true))
            if (get().isString(key)) {
                String value = super.string(key);
                super.write(key, replacePlaceholder(value), false);
            } else if (get().isList(key)) {
                List<String> newList = super.list(key).stream().map(ConfigFile::replacePlaceholder).toList();
                super.write(key, newList, false);
            }
        super.save();
    }
}
