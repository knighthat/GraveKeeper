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

package me.knighthat.plugin.grave;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.UUID;

final class Coordinate {

    private @Nullable UUID world = null;
    private int x = 0;
    private int y = 0;
    private int z = 0;

    public void setLocation(@NonNull Location location) {
        this.world = location.getWorld().getUID();
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
    }

    public @Nullable Location get() {
        return world == null ? null :
                new Location(Bukkit.getWorld(this.world), this.x, this.y, this.z);
    }


}
