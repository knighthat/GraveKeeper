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

package me.knighthat.plugin.command.sub;

import me.knighthat.api.command.conditions.Preconditions;
import me.knighthat.debugger.Debugger;
import me.knighthat.plugin.command.CommandManager;
import me.knighthat.plugin.command.SubCommand;
import me.knighthat.plugin.file.MessageFile;
import me.knighthat.plugin.message.Messenger;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class HelpCommand extends SubCommand implements Preconditions {

    private static final @NotNull List<HelpTemplate> COMMAND_TEMPLATES = new ArrayList<>();
    private static @NotNull String footer = "";
    private static @NotNull String header = "";

    public static void reload () {
        COMMAND_TEMPLATES.clear();

        MessageFile file = Messenger.FILE;
        String mainPath = "help_message";
        if (!file.contains(mainPath))
            return;

        if (!file.get().isConfigurationSection(mainPath)) {
            String err = "Error occurs while reading help in message.yml";
            String cause = "%s is not a valid %s in YAML format";
            cause = String.format(cause, mainPath, ConfigurationSection.class.getName());

            Debugger.err(err, cause);

            return;
        }

        header = file.message(mainPath.concat(".header"));
        footer = file.string(mainPath.concat(".footer"));

        for (SubCommand sub : CommandManager.SUB_COMMANDS) {
            String path = mainPath + "." + sub.name() + ".";

            HelpTemplate template =
                    new HelpTemplate(
                            file.string(path.concat("usage")),
                            file.list(path.concat("permissions")),
                            file.list(path.concat("description"))
                    );

            COMMAND_TEMPLATES.add(template);
        }
    }

    public static int total () {
        float divided = COMMAND_TEMPLATES.size() / 3f;
        return (int) Math.ceil(divided);
    }

    @Override
    public void dispatch ( @NotNull CommandSender sender, String @NotNull [] args ) {
        int page = args.length > 1 ? Integer.parseInt(args[1]) : 1;

        String replacedHeader = replacePage(header, page);
        String replacedFooter = replacePage(footer, page);
        Messenger.sendCommandHelp(sender, replacedHeader, replacedFooter, this.get(page));
    }

    @Contract ( "_,_ -> new" )
    private @NotNull String replacePage ( @NotNull String input, int page ) {
        return input
                .replace("%current_page", String.valueOf(page))
                .replace("%total_page", String.valueOf(total()));
    }

    private @NotNull List<HelpTemplate> get ( int page ) {
        int start = ( page - 1 ) * 3;
        int stop = start + 3;

        List<HelpTemplate> results = new ArrayList<>();

        for (int i = start ; i < stop ; i++) {
            if (i >= COMMAND_TEMPLATES.size()) break;

            results.add(COMMAND_TEMPLATES.get(i));
        }

        return results;
    }

    @Override
    public boolean prerequisite ( @NotNull CommandSender sender, String @NotNull [] args ) {
        String messagePath = "";
        if (args.length > 1) {
            try {
                int page = Integer.parseInt(args[1]);
                if (page < 0 || page > total())
                    messagePath = "invalid_page_number";
            } catch (NumberFormatException e) {
                messagePath = "not_a_number";
            }

            if (!messagePath.isEmpty())
                Messenger.send(sender, messagePath, null, null, Map.of("%input", args[1]));
        }
        return messagePath.isEmpty();
    }


    public record HelpTemplate(@NotNull String usage,
                               @NotNull List<String> permissions,
                               @NotNull List<String> description) {
        @Override
        public String toString () {
            StringJoiner joiner = new StringJoiner("\n");

            joiner.add(this.usage);
            permissions.forEach(joiner::add);
            description.forEach(joiner::add);

            return joiner.toString();
        }
    }
}
