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

package me.knighthat.api.file;

import lombok.Getter;
import lombok.NonNull;
import me.knighthat.debugger.Debugger;
import me.knighthat.plugin.GraveKeeper;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

public abstract class FileTemplate {

    @Getter
    private final @NonNull GraveKeeper plugin;
    @Getter
    private final @NonNull String fileName;
    private @Nullable File file;
    private @Nullable YamlConfiguration yaml;

    protected FileTemplate(@NonNull GraveKeeper plugin, @NonNull String fileName) {
        this.plugin = plugin;
        this.fileName = fileName.concat(".yml");
        init();
    }

    private void init() {
        createIfNotExist();
        reload();
    }

    public void reload() {
        createIfNotExist();

        assert this.file != null;
        yaml = YamlConfiguration.loadConfiguration(this.file);
    }

    public @NonNull YamlConfiguration get() {
        if (this.yaml == null)
            reload();

        return this.yaml;
    }

    public void save() {
        try {
            if (this.file == null || this.yaml == null)
                reload();
            this.yaml.save(this.file);
        } catch (IOException e) {
            Debugger.err("Error while saving " + this.fileName, e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    private void createIfNotExist() {
        if (this.file == null)
            file = new File(this.plugin.getDataFolder(), this.fileName);

        if (!this.file.exists())
            this.plugin.saveResource(this.fileName, false);
    }
}
