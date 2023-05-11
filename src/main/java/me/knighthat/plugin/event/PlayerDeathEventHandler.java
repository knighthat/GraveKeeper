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

package me.knighthat.plugin.event;

import lombok.NonNull;
import me.knighthat.api.persistent.DataHandler;
import me.knighthat.plugin.instance.Grave;
import me.knighthat.plugin.message.Messenger;
import me.knighthat.utils.ExpCalc;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;

public class PlayerDeathEventHandler {

    private static final @NonNull Material MATERIAL = Material.CHEST;

    /**
     * Handles placing "Grave", adding NBT data to "Grave" and Player
     *
     * @param player Whose "Grave" belong to
     */
    public static void process(@NonNull Player player) {
        Grave grave = registerDeathChest(player);

        Messenger.sendDeathMessage(player, grave);
    }

    /**
     * Creates an instance of "Grave" that stores player's info, experience points
     * and other info, such as, location, id, owner's uuid, date
     *
     * @param who Whose "Grave" belong to
     * @return Instance represents "Grave"
     */
    private static @NonNull Grave registerDeathChest(@NonNull Player who) {
        Map<Integer, ItemStack> content = getPlayerInventory(who);
        Location location = who.getLocation();
        int exp = ExpCalc.total(who);

        Grave grave = new Grave(who.getUniqueId(), location, content, exp);
        grave.place(MATERIAL);

        DataHandler.push(who, grave);
        TileState state = (TileState) location.getBlock().getState();
        DataHandler.push(state, grave.getId());

        return grave;
    }

    /**
     * Converts player's inventory into a
     * Map of Slot:ItemStack
     *
     * @param player Whose inventory will be queried
     * @return Map of Slot:ItemStack
     */
    private static @NonNull Map<Integer, ItemStack> getPlayerInventory(@NonNull Player player) {
        PlayerInventory inventory = player.getInventory();
        Map<Integer, ItemStack> inventoryMap = new HashMap<>(inventory.getContents().length);

        for (int slot = 0; slot < inventory.getSize(); slot++) {
            ItemStack itemAtSlot = inventory.getItem(slot);

            if (itemAtSlot != null && !itemAtSlot.getType().equals(Material.AIR))
                inventoryMap.put(slot, itemAtSlot);
        }

        return inventoryMap;
    }
}
