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

package me.knighthat.plugin.instance;

import lombok.NonNull;
import me.knighthat.utils.placeholder.OfferPlaceholders;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import javax.annotation.Nullable;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;

public final class Coordinates implements Serializable, OfferPlaceholders {

    @Serial
    private static final long serialVersionUID = 1485169032725408696L;

    private @Nullable UUID world = null;
    private int x = 0;
    private int y = 0;
    private int z = 0;

    /**
     * Turns org.bukkit.Location to:
     * - World's UUID
     * - X, Y, Z coordinates
     *
     * @param location Location of the block
     */
    public void setLocation ( @NonNull Location location ) {
        this.world = location.getWorld().getUID();
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
    }

    /**
     * Turns raw values back to
     * org.bukkit.Location
     *
     * @return In-game location
     */
    public @Nullable Location get () {
        return world == null ? null :
                new Location(Bukkit.getWorld(this.world), this.x, this.y, this.z);
    }

    /**
     * Creates a pair for replacing placeholders
     *
     * @return Pair of %place_holder:[replacement]
     */
    @Override
    public @NonNull Map<String, String> replacements () {
        Map<String, String> placeholders = new HashMap<>(3);
        if (world != null)
            placeholders.put("%world", world.toString());
        placeholders.put("%x", String.valueOf(this.x));
        placeholders.put("%y", String.valueOf(this.y));
        placeholders.put("%z", String.valueOf(this.z));

        return placeholders;
    }

    /**
     * Better comparison algorithm
     *
     * @param obj reference object
     *
     * @return true if values are similar, otherwise, false.
     */
    @Override
    public boolean equals ( @Nullable Object obj ) {
        return obj instanceof Coordinates coordinate && coordinate.get() == this.get();
    }

    /**
     * Custom toString() returns String follows format
     *
     * @return [world=%world,x=%x,y=%y,z=%z]
     */
    @Override
    public String toString () {
        StringJoiner joiner = new StringJoiner(",", "[", "]");
        joiner.add("world=" + ( world == null ? "null" : world.toString() ));
        joiner.add("x=" + this.x);
        joiner.add("y=" + this.y);
        joiner.add("z=" + this.z);

        return joiner.toString();
    }
}
