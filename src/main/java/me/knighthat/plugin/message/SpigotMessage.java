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
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
final class SpigotMessage extends PluginMessage<BaseComponent> {

    public SpigotMessage(@NonNull String message, @Nullable Hover hover, @Nullable Click click) {
        super(message, hover, click);
    }

    public SpigotMessage(@NonNull String message) {
        super(message, null, null);
    }

    @Override
    public @NonNull BaseComponent build() {
        TextComponent message = (TextComponent) super.getMessage();

        if (super.getHover() != null)
            if (super.getHover().action().equals(Hover.Action.SHOW_TEXT)) {
                Object value = super.getHover().value();

                if (value instanceof String text) {
                    ComponentBuilder builder = new ComponentBuilder(text);

                    HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, builder.create());
                    message.setHoverEvent(hoverEvent);
                } else {
                    super.sendHoverError(value, String.class);
                }
            }

        if (super.getClick() != null)
            if (super.getClick().action().equals(Click.Action.OPEN_MENU)) {
                Object value = super.getClick().value();

                if (value instanceof Grave grave) {
                    String command = "/grave peak " + grave.getId();
                    ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, command);
                    message.setClickEvent(clickEvent);
                } else {
                    super.sendClickError(value, Grave.class);
                }
            }

        return message;
    }

    @Contract(
            value = "_,_ -> this",
            pure = true
    )
    @Override
    public @NonNull SpigotMessage replace(@NonNull String match, @NonNull BaseComponent replacement) {
        List<BaseComponent> extras = super.getMessage().getExtra();
        List<BaseComponent> results = new ArrayList<>();

        for (BaseComponent component : extras) {
            String plainText = component.toPlainText();
            if (!plainText.contains(match)) {
                results.add(component);
                continue;
            }

            String[] split = plainText.split(match, 2);

            TextComponent before = new TextComponent(split[0]);
            before.copyFormatting(component);
            results.add(before);

            if (!replacement.hasFormatting())
                replacement.copyFormatting(component);
            results.add(replacement);

            if (split.length > 1) {
                TextComponent after = new TextComponent(split[1]);
                after.copyFormatting(component);
                results.add(after);
            }
        }

        var finalResult = new TextComponent(results.toArray(BaseComponent[]::new));
        super.setMessage(finalResult);

        return this;
    }

    @Override
    public @NonNull BaseComponent mergeMessages(@NonNull List<BaseComponent> messages) {
        ComponentBuilder builder = new ComponentBuilder();
        messages.forEach(builder::append);

        return new TextComponent(builder.create());
    }

    @Override
    public @NonNull List<BaseComponent> applyColor(@NonNull Map<String, String> colorMessagePair) {
        List<BaseComponent> components = new ArrayList<>(colorMessagePair.size());

        colorMessagePair.forEach((k, v) -> {

            TextComponent message = new TextComponent(k);
            ChatColor color = ChatColor.of(v);
            message.setColor(color);

            components.add(message);
        });

        return components;
    }
}
