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

import me.knighthat.api.file.yaml.YamlFile;
import me.knighthat.plugin.GraveKeeper;
import org.jetbrains.annotations.NotNull;

public class MessageFile extends YamlFile {

    public MessageFile ( @NotNull GraveKeeper plugin ) {
        super(plugin, "messages");
    }

    public @NotNull String prefix () {
        return super.string("prefix");
    }

    public @NotNull String message ( @NotNull String path ) {
        return prefix().concat(super.string(path));
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
                super.write(key, ConfigFile.replacePlaceholder(value), false);
            }
        super.save();
    }
}
