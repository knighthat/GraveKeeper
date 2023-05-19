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
import me.knighthat.api.message.InteractiveMessage;
import me.knighthat.api.message.PlainTextMessage;
import me.knighthat.api.message.PluginMessage;
import me.knighthat.api.style.hex.AdventureHex;
import me.knighthat.debugger.Debugger;
import me.knighthat.plugin.command.sub.HelpCommand;
import me.knighthat.plugin.file.MessageFile;
import me.knighthat.plugin.instance.Grave;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class Messenger {

    public static final boolean isPaper = Bukkit.getServer().getName().equals("Paper");

    public static MessageFile FILE;
    public static BukkitAudiences AUDIENCES;

    public static <T extends PluginMessage> void send(@NotNull CommandSender to, @NotNull T messageInstance) {
        Component message = messageInstance.build();

        if (AUDIENCES == null) {
            String err = "Cannot send message %s", cause = "%s has not been initialized in %s";
            String wanted = BukkitAudiences.class.getName(), thisClass = Messenger.class.getName();

            err = String.format(err, AdventureHex.text(message));
            cause = String.format(cause, wanted, thisClass);

            Debugger.err(err, cause);
            return;
        }

        Audience audience = to instanceof Player player ? AUDIENCES.player(player) : AUDIENCES.console();
        messageInstance.send(audience);
    }

    public static <T extends PluginMessage> void send(
            @NotNull CommandSender to,
            @NotNull T messageInstance,
            @Nullable Player player,
            @Nullable Grave grave,
            @Nullable Map<String, String> additions
    ) {
        if (player != null) {
            if (KnightHatAPI.IS_PAPER)
                messageInstance.postBuildReplace("%player_display", player.displayName());
            else
                messageInstance.replace("%player_display", player.getDisplayName());
            messageInstance.replace("%player", player.getName());
        }
        if (grave != null) messageInstance.replace(grave.replacements());
        if (additions != null) messageInstance.replace(additions);

        send(to, messageInstance);
    }

    /**
     * Grabs message from MessageFile then
     * replaces all replacements from Grave,
     * plus, player's name and display name.
     * Finally, creates a Message instance
     * to send to player.
     *
     * @param to          Recipient of the message
     * @param messagePath Reference path from "message.yml"
     * @param player      Whose name will be replaced
     * @param grave       Grave instance
     * @param additions   Additional replacements
     */
    public static void send(
            @NotNull CommandSender to,
            @NotNull String messagePath,
            @Nullable Player player,
            @Nullable Grave grave,
            @Nullable Map<String, String> additions
    ) {
        PlainTextMessage message = new PlainTextMessage(FILE.message(messagePath));
        send(to, message, player, grave, additions);
    }

    public static void send(@NotNull CommandSender to, @NotNull String messagePath) {
        PlainTextMessage message = new PlainTextMessage(FILE.message(messagePath));
        send(to, message);
    }

    public static void sendIdList(@NonNull CommandSender to, @NonNull Player target, @NonNull Grave... graves) {
        List<PluginMessage> messages = new ArrayList<>(graves.length);

        for (Grave grave : graves) {
            InteractiveMessage message = new InteractiveMessage(" - " + grave.getId());

            Component hoverMessage = AdventureHex.parse("Left-click to open grave");
            message.setHoverEvent(HoverEvent.showText(hoverMessage));
            String command = "/grave peak " + grave.getId();
            message.setClickEvent(ClickEvent.runCommand(command));

            messages.add(message);
        }
        String path = to == target ? "self_graves" : "player_graves";
        send(to, path, target, null, null);

        messages.forEach(msg -> send(to, msg));
    }

    public static void sendCommandHelp(@NonNull CommandSender to, @NonNull String header, @NonNull String footer, @NonNull List<HelpCommand.HelpTemplate> templates) {
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
