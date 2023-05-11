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

package me.knighthat.api.message;

import lombok.NonNull;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface Color<T> {

    default @NonNull Pattern hexPattern() {
        return Pattern.compile("#[a-fA-F0-9]{6}");
    }

    default String @NonNull [] splitMessage(@NonNull String message) {
        Matcher matcher = hexPattern().matcher(message);
        List<String> results = new ArrayList<>();

        int lastStart = 0;
        while (matcher.find()) {
            int start = matcher.start(), end = matcher.end();

            if (end == lastStart)
                continue;

            String sub = message.substring(lastStart, start);
            results.add(sub);

            lastStart = start;
        }
        String sub = message.substring(lastStart);
        results.add(sub);

        return results.toArray(String[]::new);
    }

    default @NonNull Map<String, String> splitMessageToPair(String... messages) {
        Map<String, String> results = new LinkedHashMap<>(messages.length);

        for (String message : messages) {
            Matcher matcher = hexPattern().matcher(message);

            String code = "#FFFFFF";
            if (matcher.find()) {
                int start = matcher.start(), end = matcher.end();
                code = message.substring(start, end);
                
                message = message.replace(code, "");
            }

            results.put(message, code);
        }

        return results;
    }

    default @NonNull T color(@NonNull String message) {
        message = ampersand(message);

        String[] split = splitMessage(message);

        Map<String, String> messageColorPair = splitMessageToPair(split);
        List<T> applyColor = applyColor(messageColorPair);

        return mergeMessages(applyColor);
    }

    default @NonNull String ampersand(@NonNull String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    @NonNull T mergeMessages(@NonNull List<T> messages);

    @NonNull List<T> applyColor(@NonNull Map<String, String> colorMessagePair);
}
