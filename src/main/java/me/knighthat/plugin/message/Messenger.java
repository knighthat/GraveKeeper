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

import lombok.NonNull;
import me.knighthat.KnightHatAPI;
import me.knighthat.api.message.PlainTextMessage;
import me.knighthat.api.message.PluginMessage;
import me.knighthat.api.style.Color;
import me.knighthat.debugger.Debugger;
import me.knighthat.plugin.command.sub.HelpCommand;
import me.knighthat.plugin.file.MessageFile;
import me.knighthat.plugin.instance.Grave;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings ( "deprecation" )
public class Messenger {

    public static final boolean isPaper = Bukkit.getServer().getName().equals("Paper");
    public static final @Nullable BukkitAudiences AUDIENCES = KnightHatAPI.AUDIENCES;
    public static MessageFile FILE;

    /**
     * Send message to player using subclass of {@link PluginMessage}
     *
     * @param to              message receiver
     * @param messageInstance message to be sent
     * @param <T>             subclass of {@link PluginMessage}
     */
    public static <T extends PluginMessage> void send ( @NotNull CommandSender to, @NotNull T messageInstance ) {
        Component message = messageInstance.build();

        if (AUDIENCES == null) {
            String err = "Cannot send message %s", cause = "%s has not been initialized in %s";
            String wanted = BukkitAudiences.class.getName(), thisClass = Messenger.class.getName();

            err = String.format(err, Color.serialize(message));
            cause = String.format(cause, wanted, thisClass);

            Debugger.err(err, cause);
            return;
        }

        Audience audience = to instanceof Player player ? AUDIENCES.player(player) : AUDIENCES.console();
        messageInstance.send(audience);
    }

    /**
     * Grabs message from MessageFile then substitute
     * all the provided placeholders with the condition
     * that they are {@link NotNull}
     *
     * @param to          Recipient of the message
     * @param messagePath Reference path from "message.yml"
     * @param player      whose name will replace player placeholders
     * @param grave       its values will replace all placeholders
     * @param additions   Additional replacements
     */
    public static void send (
            @NotNull CommandSender to,
            @NotNull String messagePath,
            @Nullable Player player,
            @Nullable Grave grave,
            @Nullable Map<String, String> additions
    ) {
        String fromFile = FILE.message(messagePath);
        PlainTextMessage message = new PlainTextMessage(fromFile);

        if (player != null) {
            if (KnightHatAPI.IS_PAPER)
                message.postBuildReplace("%display", player.displayName());
            else
                message.replace("%display", player.getDisplayName());
            message.replace("%player", player.getName());
        }
        if (grave != null) message.replace(grave.replacements());
        if (additions != null) message.replace(additions);

        send(to, message);
    }

    public static void sendCommandHelp ( @NonNull CommandSender to, @NonNull String header, @NonNull String footer, @NonNull List<HelpCommand.HelpTemplate> templates ) {
        if (templates.size() == 0) return;

        List<PluginMessage> messages = new ArrayList<>(templates.size() + 2);
        messages.add(new PlainTextMessage(header));
        messages.add(new PlainTextMessage(footer));
        templates.forEach(msg -> {
            int beforeLast = messages.size() - 1;
            messages.add(beforeLast, new PlainTextMessage(msg.toString()));
        });

        messages.forEach(msg -> send(to, msg));
    }
}
