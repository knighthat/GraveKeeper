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

import lombok.Data;
import lombok.NonNull;
import me.knighthat.utils.ExpCalc;
import me.knighthat.utils.Validation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Data
public final class Grave {

    private final @NonNull String id = genId();
    private final @Nullable UUID owner;
    private final @NonNull Coordinate coordinate = new Coordinate();
    private final @NonNull Content content = new Content();
    private final @NonNull Date date = new Date();

    private @NonNull Material material = Material.CHEST;

    public Grave() {
        this.owner = UUID.randomUUID();
    }

    public Grave(@NonNull Player owner, @NonNull Map<Integer, ItemStack> content) {
        this.owner = owner.getUniqueId();

        Location location = owner.getLocation();
        this.coordinate.setLocation(location);

        this.content.setContent(content);

        int experience = ExpCalc.total(owner);
        this.content.setExperience(experience);
    }

    @NonNull String genId() {
        String uuid = UUID.randomUUID().toString();
        return uuid.substring(uuid.lastIndexOf("-") + 1);
    }

    public boolean isValid() {
        Location loc = this.coordinate.get();
        return loc != null && Validation.isGrave(loc.getBlock());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof Grave grave &&
                grave.isValid() &&
                grave.getOwner() != null &&
                grave.getOwner().equals(this.owner) &&
                grave.getCoordinate().equals(this.coordinate) &&
                grave.getContent().equals(this.content);
    }

    public void place(@NonNull Material material) {
        Location loc = this.coordinate.get();
        if (loc == null)
            return;
        this.material = material;
        loc.getBlock().setType(material);
    }

    public void remove() {
        if (this.isValid())
            this.coordinate.get().getBlock().setType(Material.AIR);
    }
}
