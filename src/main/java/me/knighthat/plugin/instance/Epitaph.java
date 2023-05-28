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

import me.knighthat.KnightHatAPI;
import me.knighthat.api.message.PlainTextMessage;
import me.knighthat.api.style.hex.SpigotHex;
import me.knighthat.plugin.GraveKeeper;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;

public class Epitaph {

    @NotNull List<String> lines = new ArrayList<>(4);

    public Epitaph () {
        List<String> lines = GraveKeeper.CONFIG.epitaph();
        for (int i = 0 ; i < 4 && i < lines.size() ; i++)
            this.lines.add(lines.get(i));
    }

    public @Unmodifiable @NotNull List<String> lines () {
        return List.copyOf(this.lines);
    }

    public void place ( @NotNull Grave grave ) {
        Block chest = grave.getCoordinates().get().getBlock();
        Sign epitaph = this.convertFacingToSign(chest);

        Player owner = grave.owner();
        for (int i = 0 ; i < 4 ; i++) {
            String line = this.lines().get(i);
            PlainTextMessage plainText = new PlainTextMessage(line);
            plainText.replace(grave);

            if (owner != null) {
                if (KnightHatAPI.IS_PAPER)
                    plainText.postBuildReplace("%display", owner.displayName());
                else
                    plainText.replace("%display", owner.getDisplayName());
                plainText.replace("%player", owner.getName());
            }

            if (KnightHatAPI.IS_PAPER)
                epitaph.line(i, plainText.build());
            else
                epitaph.setLine(i, SpigotHex.parse(plainText.getMessage()));
        }
        epitaph.update();
    }

    private @NotNull Sign convertFacingToSign ( @NotNull Block chest ) {
        Directional facing = ( (Directional) chest.getBlockData() );
        Block faceWith = chest.getRelative(facing.getFacing());
        faceWith.setType(GraveKeeper.CONFIG.wallSign());
        return (Sign) faceWith.getState();
    }
}
