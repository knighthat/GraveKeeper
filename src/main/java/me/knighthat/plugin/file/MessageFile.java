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
import me.knighthat.plugin.GraveKeeper;

import java.util.HashMap;
import java.util.Map;

public class MessageFile extends PluginFile {

    public MessageFile(@NonNull GraveKeeper plugin) {
        super(plugin, "messages");
    }

    public @NonNull String prefix() {
        return super.string("prefix");
    }

    public @NonNull String message(@NonNull String path, @NonNull Map<String, String> replacements) {
        String message = super.string(path, replacements);
        return prefix().concat(message);
    }

    public @NonNull String message(@NonNull String path) {
        return this.message(path, new HashMap<>(0));
    }
}
