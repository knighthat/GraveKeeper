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

package me.knighthat.plugin.message;

import lombok.Getter;
import lombok.NonNull;
import me.knighthat.api.style.Colorization;
import me.knighthat.plugin.file.MessageFile;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class Messenger {

    public final static @NonNull Colorization STYLE = text -> ChatColor.translateAlternateColorCodes('&', text);
    public static @NonNull MessageFile FILE;

    /**
     * Temporary way to send message to player
     *
     * @param to      Recipient of the message
     * @param message Class contains actual message
     */
    public static void send(@NonNull CommandSender to, @NonNull Message message) {
        String styled = STYLE.color(message.text);
        to.sendMessage(styled);
    }

    /**
     * Queries message from MessageFile then
     * creates and a Message instance to send to player
     *
     * @param to   Recipient of the message
     * @param path Reference path from "message.yml"
     */
    public static void send(@NonNull CommandSender to, @NonNull String path) {
        Message message = new Message(FILE.message(path));
        send(to, message);
    }

    /**
     * Queries message from MessageFile then
     * replaces all the placeholders provided
     * inside Map. Finally, creates a Message instance
     * to send to player
     *
     * @param to           Recipient of the message
     * @param path         Reference path from "message.yml"
     * @param replacements Pair of %place_holder%:[replacement]
     */
    public static void send(@NonNull CommandSender to, @NonNull String path, @NonNull Map<String, String> replacements) {
        String message = FILE.message(path);
        for (Map.Entry<String, String> entry : replacements.entrySet())
            message = message.replace(entry.getKey(), entry.getValue());

        send(to, new Message(message));
    }

    /**
     * Represent String message
     *
     * @param text Actual message
     */
    public record Message(@Getter @NonNull String text) {
    }
}