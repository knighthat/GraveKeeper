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

import lombok.NonNull;
import me.knighthat.debugger.Debugger;
import me.knighthat.plugin.GraveKeeper;
import me.knighthat.plugin.instance.Grave;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class PluginFile extends FileTemplate {

    protected PluginFile(@NonNull GraveKeeper plugin, @NonNull String fileName) {
        super(plugin, fileName);
    }

    /**
     * Applies placeholders with template %placeholder:[value]
     *
     * @param original     String to be replaced
     * @param replacements Pair of %placeholder:[value]
     * @return new string with %placeholder replaced by [value]
     */
    @NonNull String apply(@NonNull String original, @NonNull Map<String, String> replacements) {
        String message = original;
        for (Map.Entry<String, String> entry : replacements.entrySet())
            message = message.replace(entry.getKey(), entry.getValue());

        return message;
    }

    /**
     * Get string from path and return new string
     * replaced by replacements
     *
     * @param path         Where to get string
     * @param replacements Pair of %placeholder:[value]
     * @return new string with %placeholder replaced by [value]
     */
    public @NonNull String string(@NonNull String path, @NonNull Map<String, String> replacements) {
        String original = this.string(path);
        return this.apply(original, replacements);
    }

    /**
     * Get string from path and return new string
     * replaced by every pair of %placeholder:[value]
     * provided by Grave.
     *
     * @param path  Where to get string
     * @param grave Instance offers replacements
     * @return new string with %placeholder replaced by [value]
     */
    public @NonNull String string(@NonNull String path, @NonNull Grave grave) {
        return this.string(path, grave.replacements());
    }

    /**
     * Get string from path<br>
     * Returns "404" if path's not exist
     * or path doesn't contain string.
     * Plus, log a warning to console
     *
     * @param path Where to get string
     * @return A string from file
     */
    public @NonNull String string(@NonNull String path) {
        if (contains(path) && !get().isString(path)) {
            String message = "{0} is not a string!";
            Debugger.warn(MessageFormat.format(message, path));
        }
        return get().getString(path, "404");
    }

    /**
     * Get integer from path.<br>
     * Returns "0" if path's not exist
     * or path doesn't contain integer.
     * Plus, log a warning to console
     *
     * @param path Where to get int
     * @return A string from file
     */
    public int integer(@NonNull String path) {
        if (contains(path) && !get().isInt(path)) {
            String message = "{0} is not an integer!";
            Debugger.warn(MessageFormat.format(message, path));
        }
        return get().getInt(path, 0);
    }

    /**
     * Get a list of string from path.<br>
     * Returns an empty list if none found.
     *
     * @param path Where to get list
     * @return A list from file
     */
    public @NonNull List<String> list(@NonNull String path) {
        List<String> result = new ArrayList<>();

        if (contains(path))
            if (!get().isList(path)) {
                String message = "{0} is not a list!";
                Debugger.warn(MessageFormat.format(message, path));
            } else
                result = super.get().getStringList(path);

        return result;
    }

    /**
     * Checks if path exists in file.<br>
     * Log a warning to console
     * if path does not exist.
     *
     * @param path Where to get object
     * @return If path exists in file
     */
    public boolean contains(@NonNull String path) {
        if (!get().contains(path)) {
            String message = "{0} not found in {1}! (404)";
            Debugger.warn(MessageFormat.format(message, path, super.getFileName()));
        }
        return get().contains(path);
    }
}
