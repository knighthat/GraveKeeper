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

package me.knighthat.api.command.type;

import lombok.NonNull;
import me.knighthat.api.command.PermissionStatus;
import me.knighthat.api.command.SubCommand;
import me.knighthat.api.command.conditions.MultiplePermissions;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class HybridSubCommand extends SubCommand implements MultiplePermissions {

    protected @NonNull String selfPermission() {
        return this.permission("self");
    }

    protected @NonNull String playerPermission() {
        return this.permission("players");
    }

    private @NonNull String permission(@NonNull String type) {
        return "grave.command." + super.name() + "." + type;
    }

    @Override
    public @NonNull PermissionStatus hasPermission(@NonNull CommandSender sender, String @NonNull [] args) {
        Player target;

        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                return PermissionStatus.NOT_PLAYER;
            } else {
                target = player;
            }
        } else {
            target = Bukkit.getPlayer(args[0]);
            if (target == null || !target.isOnline())
                return PermissionStatus.PLAYER_NOT_FOUND;
        }

        return this.hasPermission(sender, target);
    }

    protected @NonNull PermissionStatus hasPermission(@NonNull CommandSender sender, @NonNull Player target) {
        boolean failedSelf = target == sender && !sender.hasPermission(this.selfPermission());
        boolean failedAll = target != sender && !sender.hasPermission(this.playerPermission());

        return failedSelf || failedAll ? PermissionStatus.NO_PERMISSION : PermissionStatus.PASSED;
    }


    @Override
    public void execute(@NonNull CommandSender sender, String @NonNull [] args) {
        Player target = args.length >= 1 ? Bukkit.getPlayer(args[0]) : (Player) sender;
        assert target != null;

        execute(sender, target, args);
    }

    public abstract void execute(@NonNull CommandSender sender, @NonNull Player target, String @NonNull [] args);
}
