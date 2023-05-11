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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.knighthat.debugger.Debugger;
import org.jetbrains.annotations.Nullable;

@Getter(AccessLevel.PROTECTED)
public abstract class PluginMessage<T> implements Color<T> {

    private final @Nullable Hover hover;
    private final @Nullable Click click;
    @Setter
    private @NonNull T message;

    protected PluginMessage(@NonNull String message, @Nullable Hover hover, @Nullable Click click) {
        this.message = color(message);
        this.hover = hover;
        this.click = click;
    }

    public abstract @NonNull T build();

    public abstract PluginMessage<T> replace(@NonNull String match, @NonNull T replacement);

    protected void sendHoverError(@NonNull Object value, @NonNull Object required) {
        this.sendError("HoverEvent", value, required);
    }

    protected void sendClickError(@NonNull Object value, @NonNull Object required) {
        this.sendError("ClickEvent", value, required);
    }

    protected void sendError(@NonNull String event, @NonNull Object value, @NonNull Object required) {
        String errMessage = "Error occurs while applying " + event + " to " + this.message;
        String cause = value.getClass() + " is not " + required.getClass();

        Debugger.err(errMessage, cause);
    }

}
