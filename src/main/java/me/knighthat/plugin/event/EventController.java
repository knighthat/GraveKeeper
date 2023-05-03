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
import me.knighthat.api.menu.PluginMenu;
import me.knighthat.api.persistent.DataHandler;
import me.knighthat.plugin.instance.Grave;
import me.knighthat.plugin.message.Messenger;
import me.knighthat.utils.Validation;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

public class EventController implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        List<ItemStack> drops = event.getDrops();

        if (drops.isEmpty() && event.getDroppedExp() == 0)
            return;

        PlayerDeathEventHandler.process(player);

        event.setShouldDropExperience(false);
        drops.removeIf(drop -> !drop.getType().equals(Material.AIR));
    }

    @EventHandler
    public void playerInteractWithGrave(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();

        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && Validation.isGrave(block)) {
            event.setCancelled(true);
            GraveRetrievalEventHandler.process(event.getPlayer(), block);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void playerBreaksGrave(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (!Validation.isGrave(block))
            return;

        event.setCancelled(true);

        String id = DataHandler.pull((TileState) block.getState());
        Grave[] graveArr = DataHandler.pull(player);

        for (Grave grave : graveArr)
            if (grave.getId().equals(id) && grave.isValid()) {
                GraveRetrievalEventHandler.process(event.getPlayer(), block);
                return;
            }

        Messenger.send(player, "not_owner");
    }

    @EventHandler
    public void playerClicksInventory(InventoryClickEvent event) {
        Inventory inventory = event.getView().getTopInventory();
        if (inventory.getHolder() instanceof PluginMenu menu)
            menu.onClick(event);
    }

    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "0.7")
    @EventHandler(priority = EventPriority.HIGH)
    public void playerJoinEvent(@NonNull PlayerJoinEvent event) {
        try {
            Player player = event.getPlayer();
            me.knighthat.plugin.grave.Grave[] oldArr = me.knighthat.api.deprecated.persistent.DataHandler.pull(player);
            List<Grave> graves = new ArrayList<>(oldArr.length);

            for (me.knighthat.plugin.grave.Grave grave : oldArr)
                if (grave.isValid())
                    graves.add(new Grave(grave));

            DataHandler.set(player, graves.toArray(Grave[]::new));
        } catch (ClassCastException ignored) {
        }
    }
}