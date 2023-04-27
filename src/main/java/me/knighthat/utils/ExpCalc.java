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

package me.knighthat.utils;

import lombok.NonNull;
import org.bukkit.entity.Player;

public class ExpCalc {

    public static int nextReq(int level) {
        int diff1 = 2, diff2 = 7;
        if (level > 30) {
            diff1 = 9;
            diff2 = -149;
        } else if (level > 15) {
            diff1 = 5;
            diff2 = -38;
        }
        return (diff1 * level) + diff2;
    }

    public static float at(int level) {
        float levelSqr = (float) Math.pow(level, 2), diff1 = 1f, diff2 = 6f;
        int diff3 = 0;

        if (level > 31) {
            diff1 = 4.5f;
            diff2 = -162.5f;
            diff3 = 2220;
        } else if (level > 16) {
            diff1 = 2.5f;
            diff2 = -40.5f;
            diff3 = 360;
        }

        return (diff1 * levelSqr) + (diff2 * level) + diff3;
    }

    public static int total(@NonNull Player player) {
        int level = player.getLevel();
        return (int) (at(level) + (nextReq(level) * player.getExp()));
    }
}
