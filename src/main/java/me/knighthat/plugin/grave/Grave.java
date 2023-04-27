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
import me.knighthat.utils.Validation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;

@Data
public final class Grave implements Serializable {

    private final @NonNull String id = genId();
    private final @Nullable UUID owner;
    private final @NonNull Coordinates coordinates = new Coordinates();
    private final @NonNull Content content = new Content();
    private final @NonNull Date date = new Date();

    private @NonNull Material material = Material.CHEST;

    /**
     * Creates an invalid Grave instance
     */
    public Grave() {
        this.owner = UUID.randomUUID();
    }

    /**
     * Creates a valid Grave instance
     *
     * @param owner      Player's UUID
     * @param location   Grave's location
     * @param content    Player's inventory at death
     * @param experience Player's EXP at death
     */
    public Grave(@NonNull UUID owner,
                 @NonNull Location location,
                 @NonNull Map<Integer, ItemStack> content,
                 @Range(from = 0, to = Integer.MAX_VALUE) int experience) {
        this.owner = owner;
        this.coordinates.setLocation(location);
        this.content.setItems(content);
        this.content.setExperience(experience);
    }

    /**
     * Converts randomized java.util.UUID's last 12 characters
     * into String and use it as unique identity for grave
     *
     * @return 12 characters [a-fA-f0-9]
     */
    @NonNull String genId() {
        String uuid = UUID.randomUUID().toString();
        return uuid.substring(uuid.lastIndexOf("-") + 1);
    }

    /**
     * Check if "Grave" actually exists in world
     *
     * @return true if one is found, false otherwise
     */
    public boolean isValid() {
        Location loc = this.coordinates.get();
        return loc != null && Validation.isGrave(loc.getBlock());
    }

    /**
     * Places Block with provided Material
     *
     * @param material Represents the "Grave"
     */
    public void place(@NonNull Material material) {
        Location loc = this.coordinates.get();
        if (loc == null)
            return;
        this.material = material;
        loc.getBlock().setType(material);
    }

    /**
     * Thanos: *snap*
     * Grave: I don't feel so good
     */
    public void remove() {
        if (this.isValid())
            this.coordinates.get().getBlock().setType(Material.AIR);
    }

    /**
     * Better comparison algorithm
     *
     * @param obj reference object
     * @return true if values are similar, otherwise, false.
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof Grave grave &&
                grave.isValid() &&
                grave.getOwner() != null &&
                grave.getOwner().equals(this.owner) &&
                grave.getCoordinates().equals(this.coordinates) &&
                grave.getContent().equals(this.content);
    }

    /**
     * Custom toString() returns String follows format
     *
     * @return Grave{id=%id%,owner=%owner%,coordinates=%coordinates%,content=%content%,date=%date%}
     */
    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(",", "Grave{", "}");
        joiner.add("id=" + this.id);
        joiner.add("owner=" + (this.owner == null ? "null" : this.owner.toString()));
        joiner.add("coordinates=" + this.coordinates);
        joiner.add("content=" + this.content);
        joiner.add("date=" + date);

        return joiner.toString();
    }
}
