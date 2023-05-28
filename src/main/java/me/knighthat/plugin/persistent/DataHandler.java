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

package me.knighthat.plugin.persistent;

import lombok.NonNull;
import me.knighthat.plugin.instance.Grave;
import org.bukkit.NamespacedKey;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class DataHandler {

    private static final @NonNull Map<UUID, Grave[]> GRAVE_LIST = new HashMap<>();

    private static final @NonNull PersistentDataType<byte[], Grave[]> CHEST = new GraveArrayDataType();
    private static final @NonNull PersistentDataType<String, String> ID = PersistentDataType.STRING;
    public static NamespacedKey KEY;

    /*

        GRAVE

    */
    public static @NonNull String pull ( @NonNull TileState state ) {
        PersistentDataContainer container = state.getPersistentDataContainer();
        return container.has(KEY, ID) ? container.get(KEY, ID) : "";
    }

    public static void push ( @NonNull TileState state, @NonNull String id ) {
        state.getPersistentDataContainer().set(KEY, ID, id);
        state.update();
    }


    /*

        PLAYER

    */
    public static Grave @NonNull [] pull ( @NonNull Player player ) {
        UUID playerUID = player.getUniqueId();

        if (!GRAVE_LIST.containsKey(playerUID)) {
            PersistentDataContainer container = player.getPersistentDataContainer();
            Grave[] graves = container.has(KEY, CHEST) ? container.get(KEY, CHEST) : new Grave[0];
            GRAVE_LIST.put(playerUID, graves);
        }

        return GRAVE_LIST.get(playerUID);
    }


    public static void push ( @NonNull Player player, @NonNull Grave grave ) {
        Grave[] graves = pull(player);
        Grave[] newArr = Arrays.copyOf(graves, graves.length + 1);
        newArr[graves.length] = grave;

        set(player, newArr);
    }

    public static @NonNull Grave get ( @NonNull Player player, @NonNull String id ) {
        return Arrays.stream(pull(player))
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(new Grave());
    }

    public static void set ( @NonNull Player player, @NonNull Grave[] graves ) {
        GRAVE_LIST.put(player.getUniqueId(), graves);
        player.getPersistentDataContainer().set(KEY, CHEST, graves);
    }

    public static void remove ( @NonNull Player player, @NonNull String id ) {
        Set<Grave> graves = new HashSet<>(List.of(pull(player)));
        graves.removeIf(c -> c.getId().equals(id));

        set(player, graves.toArray(Grave[]::new));
    }

    public static void reset ( @NonNull Player player ) {
        for (Grave grave : pull(player))
            grave.remove();

        GRAVE_LIST.remove(player.getUniqueId());
        player.getPersistentDataContainer().remove(KEY);
    }

}
