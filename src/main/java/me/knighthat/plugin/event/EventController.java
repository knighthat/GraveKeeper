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

import me.knighthat.utils.Validation;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class EventController implements Listener {

    @EventHandler ( priority = EventPriority.LOWEST )
    public void onPlayerDeath ( PlayerDeathEvent event ) {
        Player player = event.getEntity();

        if (!PlayerDeathEventHandler.process(player)) return;

        List<ItemStack> drops = event.getDrops();
        if (drops.isEmpty() && event.getDroppedExp() == 0)
            return;

        event.setDroppedExp(0);
        drops.removeIf(drop -> !drop.getType().equals(Material.AIR));
    }

    @EventHandler
    public void playerInteractWithGrave ( PlayerInteractEvent event ) {
        Block block = event.getClickedBlock();

        switch (event.getAction()) {
            case RIGHT_CLICK_BLOCK, LEFT_CLICK_BLOCK -> {

                if (!Validation.isGrave(block))
                    return;

                event.setCancelled(true);
                GraveRetrievalEventHandler.process(event.getPlayer(), block);
            }
        }
    }
}