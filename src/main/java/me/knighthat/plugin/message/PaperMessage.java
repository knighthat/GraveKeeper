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
import me.knighthat.plugin.instance.Grave;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

final class PaperMessage extends PluginMessage<Component> {

    public PaperMessage(@NonNull String message, @Nullable Hover hover, @Nullable Click click) {
        super(message, hover, click);
    }

    public PaperMessage(@NonNull String message) {
        super(message, null, null);
    }

    @Override
    public @NonNull Component build() {
        Component message = super.getMessage();

        HoverEvent<?> hoverEvent = null;
        ClickEvent clickEvent = null;

        if (super.getHover() != null)
            if (super.getHover().action().equals(Hover.Action.SHOW_TEXT)) {
                Object value = super.getHover().value();

                if (value instanceof String text) {
                    Component builder = Component.text(text);

                    hoverEvent = HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, builder);
                } else {
                    super.sendHoverError(value, String.class);
                }
            }

        if (super.getClick() != null)
            if (super.getClick().action().equals(Click.Action.OPEN_MENU)) {
                Object value = super.getClick().value();

                if (value instanceof Grave grave) {
                    String command = "/grave peak " + grave.getId();
                    clickEvent = ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, command);
                } else {
                    super.sendClickError(value, Grave.class);
                }
            }

        return message.hoverEvent(hoverEvent).clickEvent(clickEvent);
    }

    @Contract(
            value = "_,_ -> this",
            pure = true
    )
    @Override
    public @NonNull PaperMessage replace(@NonNull String match, @NonNull Component replacement) {
        TextReplacementConfig.Builder config = TextReplacementConfig.builder();
        config.matchLiteral(match).replacement(replacement);

        Component message = super.getMessage();
        setMessage(message.replaceText(config.build()));
        
        return this;
    }

    @Override
    public @NonNull Component mergeMessages(@NonNull List<Component> messages) {
        TextComponent.Builder builder = Component.text();
        messages.forEach(builder::append);

        return builder.build();
    }

    @Override
    public @NonNull List<Component> applyColor(@NonNull Map<String, String> colorMessagePair) {
        List<Component> components = new ArrayList<>(colorMessagePair.size());

        colorMessagePair.forEach((k, v) -> {
            Component message = Component.text(k);
            TextColor color = TextColor.fromHexString(v);

            components.add(message.color(color));
        });

        return components;
    }
}
