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

package me.knighthat.utils;

import lombok.NonNull;
import me.knighthat.plugin.grave.Grave;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class GraveData {

    public static @NonNull Map<String, String> replacements(@NonNull Grave grave, @NonNull DataType type) {
        Map<String, String> replacements = new HashMap<>();
        if (!grave.isValid())
            return replacements;

        if (type.equals(DataType.CONTENT) || type.equals(DataType.ALL))
            replacements.putAll(contentReplacements(grave));

        if (type.equals(DataType.COORDINATES) || type.equals(DataType.ALL))
            replacements.putAll(coordReplacements(grave));

        if (type.equals(DataType.ALL)) {
            replacements.put("%id", grave.getId());
            replacements.put("%owner", grave.getOwner().toString());
            replacements.put("%date", grave.getDate().toString());
        }

        return replacements;
    }

    private static @NonNull Map<String, String> coordReplacements(@NonNull Grave grave) {
        return grave.getCoordinates().replacements();
    }

    private static @NonNull Map<String, String> contentReplacements(@NonNull Grave grave) {
        StringJoiner joiner = new StringJoiner(",", "[", "]");
        grave.getContent().items().forEach((s, i) -> {
            int amount = i.getAmount();
            String name = i.getType().name().toLowerCase();

            if (i.getItemMeta().hasDisplayName())
                name = i.getItemMeta().getDisplayName();

            joiner.add(MessageFormat.format("{0}x{1}", amount, name));
        });

        return Map.of(
                "%material", joiner.toString(),
                "%exp", String.valueOf(grave.getContent().experience())
        );
    }


    public enum DataType {
        ALL, COORDINATES, CONTENT
    }
}
