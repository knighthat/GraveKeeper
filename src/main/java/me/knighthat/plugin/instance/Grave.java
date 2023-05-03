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

import lombok.Data;
import lombok.NonNull;
import me.knighthat.utils.Validation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

@Data
public final class Grave implements Serializable {

    @Serial
    private static final long serialVersionUID = 1485169032725408696L;

    private final @NonNull String id;
    private final @Nullable UUID owner;
    private final @NonNull Coordinates coordinates = new Coordinates();
    private final @NonNull Content content = new Content();
    private final @NonNull Date date = new Date();

    private @NonNull Material material = Material.CHEST;

    public Grave(@Nullable String id,
                 @Nullable UUID owner,
                 @Nullable Location location,
                 @Nullable Map<Integer, ItemStack> content,
                 @Range(from = 0, to = Integer.MAX_VALUE) int experience) {

        if (id == null) {
            String uuid = UUID.randomUUID().toString();
            uuid = uuid.substring(uuid.lastIndexOf("-") + 1);
            this.id = uuid;
        } else {
            this.id = id;
        }

        this.owner = owner == null ? UUID.randomUUID() : owner;

        if (location != null)
            this.coordinates.setLocation(location);

        if (content != null)
            this.content.setItems(content);

        this.content.setExperience(experience);
    }

    /**
     * Creates an invalid Grave instance
     */
    public Grave() {
        this(null, null, null, null, 0);
    }

    public Grave(@NonNull me.knighthat.plugin.grave.Grave oldGrave) {
        this(oldGrave.getId(),
                oldGrave.getOwner(),
                oldGrave.getCoordinates().get(),
                oldGrave.getContent().items(),
                oldGrave.getContent().experience());
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
        this(null, owner, location, content, experience);
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
     * Creates a pair for replacing placeholders.<br>
     * If "grave" is not valid, return empty map
     *
     * @return Pair of %place_holder:[replacement]
     */
    public @NonNull Map<String, String> replacements() {
        Map<String, String> placeholders = new HashMap<>(0);

        placeholders.put("%id", this.id);
        placeholders.put("%owner", this.owner.toString());

        Player player = Bukkit.getPlayer(owner);
        if (player != null && player.isOnline()) {
            placeholders.put("%player", player.getName());
            placeholders.put("%player_display", player.getDisplayName());
        }

        placeholders.put("%date", this.date.toString());
        placeholders.putAll(this.content.replacements());
        placeholders.putAll(this.coordinates.replacements());

        return placeholders;
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
     * @return Grave{id=%id,owner=%owner,coordinates=%coordinates,content=%content,date=%date}
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
