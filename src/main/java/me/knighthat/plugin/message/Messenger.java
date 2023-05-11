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
import me.knighthat.api.message.Click;
import me.knighthat.api.message.Hover;
import me.knighthat.api.message.PluginMessage;
import me.knighthat.plugin.command.HelpCommand;
import me.knighthat.plugin.file.MessageFile;
import me.knighthat.plugin.instance.Grave;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class Messenger {

    public static final boolean isPaper = Bukkit.getServer().getName().equals("Paper");

    public static MessageFile FILE;

    public static <T extends PluginMessage<?>> void send(@NonNull CommandSender to, @NonNull T message) {
        if (message instanceof PaperMessage pMessage) {
            to.sendMessage(pMessage.build());
        } else if (message instanceof SpigotMessage sMessage) {
            to.spigot().sendMessage(sMessage.build());
        }
    }

    public static @NonNull String message(@NonNull String path, @Nullable Map<String, String> replacements) {
        String fromFile = FILE.message(path);

        if (replacements != null)
            for (Map.Entry<String, String> entry : replacements.entrySet())
                fromFile = fromFile.replace(entry.getKey(), entry.getValue());

        return fromFile;
    }

    /**
     * Grabs message from MessageFile then
     * replaces all replacements from Grave,
     * plus, player's name and display name.
     * Finally, creates a Message instance
     * to send to player.
     *
     * @param to              Recipient of the message
     * @param messagePath     Reference path from "message.yml"
     * @param player          Whose name will be replaced
     * @param grave           Grave instance
     * @param addReplacements Additional replacements
     */
    public static void send(@NonNull CommandSender to, @NonNull String messagePath, @Nullable Player player, @Nullable Grave grave, @Nullable Map<String, String> addReplacements) {
        Map<String, String> replacements = new HashMap<>();
        if (addReplacements != null)
            replacements.putAll(addReplacements);
        if (grave != null)
            replacements.putAll(grave.replacements());

        String messageStr = message(messagePath, replacements);

        if (isPaper) {
            PaperMessage message = new PaperMessage(messageStr);

            if (player != null) {
                message.replace("%player_display", player.displayName());
                message.replace("%player", player.name());
            }

            send(to, message);
        } else {
            SpigotMessage message = new SpigotMessage(messageStr);

            if (player != null) {
                message.replace("%player_display", new TextComponent(player.getDisplayName()));
                message.replace("%player", new TextComponent(player.getName()));
            }

            send(to, message);
        }
    }

    public static void sendDeathMessage(@NonNull Player to, @NonNull Grave grave) {
        String messageStr = message("death_message", grave.replacements());
        PluginMessage<?> message =
                isPaper ? new PaperMessage(messageStr) : new SpigotMessage(messageStr);

        send(to, message);
    }

    public static void sendIdList(@NonNull CommandSender to, @NonNull Player target, @NonNull Grave... graves) {
        List<PluginMessage<?>> messages = new ArrayList<>(graves.length);

        for (Grave grave : graves) {
            String messageStr = " - " + grave.getId();
            Hover hover = new Hover(Hover.Action.SHOW_TEXT, "Left-click to open grave");
            Click click = new Click(Click.Action.OPEN_MENU, grave);

            messages.add(isPaper ?
                    new PaperMessage(messageStr, hover, click) :
                    new SpigotMessage(messageStr, hover, click)
            );
        }
        String path = to == target ? "self_graves" : "player_graves";
        send(to, path, target, null, null);

        messages.forEach(msg -> send(to, msg));
    }

    public static void sendCommandHelp(@NonNull CommandSender to, @NonNull String header, @NonNull String footer, @NonNull List<HelpCommand.HelpTemplate> templates) {
        if (templates.size() == 0) return;

        List<PluginMessage<?>> messages = new ArrayList<>(templates.size() + 2);
        // Add header to the front
        messages.add(isPaper ? new PaperMessage(header) : new SpigotMessage(header));
        // Add command usage, permission(s), description
        templates.stream().map(HelpCommand.HelpTemplate::toString).forEach(t ->
                messages.add(isPaper ? new PaperMessage(t) : new SpigotMessage(t))
        );
        // Add footer to the end
        messages.add(isPaper ? new PaperMessage(header) : new SpigotMessage(footer));

        messages.forEach(msg -> send(to, msg));
    }
}
