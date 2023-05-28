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
import me.knighthat.plugin.GraveKeeper;
import me.knighthat.plugin.instance.Grave;
import me.knighthat.plugin.message.Messenger;
import me.knighthat.plugin.persistent.DataHandler;
import me.knighthat.utils.ExpCalc;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDeathEventHandler {

    public static final @NotNull Map<UUID, Long> COOL_DOWN = new HashMap<>();

    /**
     * Handles placing "Grave", adding NBT data to "Grave" and Player
     *
     * @param player Whose "Grave" belong to
     */
    public static boolean process ( @NonNull Player player ) {
        if (!( hasPermission(player) && hasSpareGrave(player) && expCrossThreshold(player) && !isInCoolDown(player) ))
            return false;

        Grave grave = registerDeathChest(player);

        if (!player.hasPermission("grave.cooldown.bypass"))
            setTimer(player.getUniqueId());

        Messenger.send(player, "death_message", player, grave, null);

        return true;
    }

    private static boolean hasPermission ( @NotNull Player player ) {
        return !GraveKeeper.CONFIG.Boolean("require-permission") || player.hasPermission("grave.create");
    }

    private static boolean hasSpareGrave ( @NotNull Player player ) {
        int limit = GraveKeeper.CONFIG.Integer("maximum-graves");
        if (limit == -1 || player.hasPermission("grave.limit.bypass")) return true;

        boolean hasSpareGrave = DataHandler.pull(player).length < limit;

        var totalGraves = Map.of("%total_graves", String.valueOf(limit));
        if (!hasSpareGrave)
            Messenger.send(player, "maximum_graves_met", player, null, totalGraves);

        return hasSpareGrave;
    }

    private static boolean expCrossThreshold ( @NotNull Player player ) {
        return !player.getInventory().isEmpty() || ExpCalc.total(player) > GraveKeeper.CONFIG.Integer("minimum-exp");
    }

    public static boolean isInCoolDown ( @NotNull Player player ) {
        UUID uuid = player.getUniqueId();

        if (COOL_DOWN.containsKey(uuid)) {
            long remainMillis = COOL_DOWN.get(uuid) - System.currentTimeMillis();
            if (remainMillis > 0) {
                var replacement = Map.of("%second", String.valueOf((int) ( remainMillis / 1000 )));
                Messenger.send(player, "cool_down_message", player, null, replacement);
                return true;
            }
        }
        return false;
    }

    private static void setTimer ( UUID uuid ) {
        long cd = System.currentTimeMillis() + GraveKeeper.CONFIG.coolDown();
        COOL_DOWN.put(uuid, cd);
    }

    /**
     * Creates an instance of "Grave" that stores player's info, experience points
     * and other info, such as, location, id, owner's uuid, date
     *
     * @param who Whose "Grave" belong to
     *
     * @return Instance represents "Grave"
     */
    private static @NonNull Grave registerDeathChest ( @NonNull Player who ) {
        Map<Integer, ItemStack> content = getPlayerInventory(who);
        Location location = findHighestY(who.getLocation());

        Grave grave = new Grave(who.getUniqueId(), location, content, ExpCalc.total(who));
        grave.place(Material.CHEST);

        DataHandler.push(who, grave);
        TileState state = (TileState) grave.getCoordinates().get().getBlock().getState();
        DataHandler.push(state, grave.getId());

        return grave;
    }

    private static @NotNull Location findHighestY ( @NotNull Location loc ) {
        if (GraveKeeper.CONFIG.Boolean("grave-always-on-ground")) {
            int highest = loc.getWorld().getHighestBlockYAt(loc);
            if (loc.getBlockY() <= highest) {

                while (true) {
                    Material below = loc.getBlock().getRelative(BlockFace.DOWN).getType();
                    if (below.isAir())
                        loc.setY(loc.getBlockY() - 1);
                    else break;
                }

            } else
                loc.setY(highest + 1);
        }
        return loc;
    }

    /**
     * Converts player's inventory into a
     * Map of Slot:ItemStack
     *
     * @param player Whose inventory will be queried
     *
     * @return Map of Slot:ItemStack
     */
    private static @NonNull Map<Integer, ItemStack> getPlayerInventory ( @NonNull Player player ) {
        PlayerInventory inventory = player.getInventory();
        Map<Integer, ItemStack> inventoryMap = new HashMap<>(inventory.getContents().length);

        for (int slot = 0 ; slot < inventory.getSize() ; slot++) {
            ItemStack itemAtSlot = inventory.getItem(slot);

            if (itemAtSlot != null && !itemAtSlot.getType().equals(Material.AIR))
                inventoryMap.put(slot, itemAtSlot);
        }

        return inventoryMap;
    }
}
