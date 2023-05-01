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

package me.knighthat.plugin.deprecated.grave;

import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

@Setter
public final class Content implements Serializable {

    private @NonNull Map<Integer, ItemStack> items = new HashMap<>(0);
    private @Range(from = 0, to = Integer.MAX_VALUE) int experience = 0;

    /**
     * @return Item and where it goes in Inventory
     */
    public @NonNull Map<Integer, ItemStack> items() {
        return this.items;
    }

    /**
     * @return Experience points
     */
    public @Range(from = 0, to = Integer.MAX_VALUE) int experience() {
        return this.experience;
    }

    /**
     * Give items and experience points to provided player.
     * If player's inventory is full, items will be dropped to the ground.
     * If slot is occupied, item will be placed in the nearest empty slot.
     *
     * @param recipient The items and experience are given to
     */
    public void giveTo(@NonNull Player recipient) {
        PlayerInventory inventory = recipient.getInventory();

        items.forEach((s, i) -> {
            if (inventory.firstEmpty() > -1) {
                int slot = inventory.getItem(s) != null ? inventory.firstEmpty() : s;
                inventory.setItem(slot, i);

            } else
                recipient.getWorld().dropItem(recipient.getLocation(), i);
        });
        recipient.giveExp(this.experience);
    }

    /**
     * Better comparison algorithm
     *
     * @param obj reference object
     * @return true if values are similar, otherwise, false.
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof Content storage
                && storage.experience() == this.experience
                && storage.items() == this.items;
    }

    /**
     * Custom toString() returns String follows format
     *
     * @return [item=[%MATERIAL],exp=%exp]
     */
    @Override
    public String toString() {
        StringJoiner items = new StringJoiner(",", "items=[", "]");
        this.items.values().stream()
                .map(ItemStack::getType)
                .map(Material::name)
                .forEach(items::add);

        StringJoiner joiner = new StringJoiner(",", "[", "]");
        joiner.add(items.toString());
        joiner.add("exp=" + this.experience);

        return joiner.toString();
    }
}
