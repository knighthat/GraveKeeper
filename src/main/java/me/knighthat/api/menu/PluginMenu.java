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

package me.knighthat.api.menu;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.HashMap;
import java.util.Map;

public abstract class PluginMenu implements InventoryHolder, InteractableMenu {

    private final @NonNull Map<Integer, ItemStack> content = new HashMap<>(54);
    private final @NonNull String title;
    private final int size;

    protected PluginMenu(@NonNull String title, @Range(from = 1, to = 54) int size) {
        this.title = title;
        this.size = size;
    }

    /**
     * Use PluginMenu.build()
     *
     * @return Inventory
     */
    @Deprecated
    @Override
    public @NotNull Inventory getInventory() {
        return Bukkit.createInventory(this, 9, "404");
    }

    @Override
    public void onClick(@NonNull InventoryClickEvent event) {
        event.setCancelled(true);
    }

    public void setItem(@Range(from = 0, to = 53) int slot, @NonNull ItemStack item) {
        this.content.put(slot, item);
    }

    public void setContent(@NonNull Map<Integer, ItemStack> content) {
        this.content.putAll(content);
    }

    public @NonNull Inventory build() {
        Inventory inventory = Bukkit.createInventory(this, this.size, this.title);
        content.forEach((inventory::setItem));
        return inventory;
    }
}