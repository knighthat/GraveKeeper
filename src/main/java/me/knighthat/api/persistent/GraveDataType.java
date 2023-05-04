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

package me.knighthat.api.persistent;

import me.knighthat.debugger.Debugger;
import me.knighthat.plugin.instance.Grave;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;


/**
 * Allows server to write and read "Grave Object" as bytes from NBT
 */
public class GraveDataType implements PersistentDataType<byte[], Grave[]> {

    @Override
    public @NotNull Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public @NotNull Class<Grave[]> getComplexType() {
        return Grave[].class;
    }

    @Override
    public byte @NotNull [] toPrimitive(Grave @NotNull [] graves, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        return CompletableFuture.supplyAsync(() -> {
            try (
                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                    BukkitObjectOutputStream bOutStream = new BukkitObjectOutputStream(outStream)
            ) {
                bOutStream.writeObject(graves);
                bOutStream.flush();

                return outStream.toByteArray();
            } catch (IOException e) {
                Debugger.err("Couldn't convert Grave Object to Bytes", e.getLocalizedMessage());
                e.printStackTrace();

                return new byte[0];
            }
        }).join();
    }

    @Override
    public Grave @NotNull [] fromPrimitive(byte @NotNull [] bytes, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        return CompletableFuture.supplyAsync(() -> {
            try (
                    ByteArrayInputStream inStream = new ByteArrayInputStream(bytes);
                    BukkitObjectInputStream bInStream = new BukkitObjectInputStream(inStream)
            ) {
                return (Grave[]) bInStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                Debugger.err("Couldn't convert Bytes to Grave Object", e.getLocalizedMessage());
                e.printStackTrace();

                return new Grave[0];
            }
        }).join();
    }
}
