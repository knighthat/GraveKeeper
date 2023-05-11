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

package me.knighthat.plugin.command;

import lombok.NonNull;
import me.knighthat.api.command.CommandManager;
import me.knighthat.api.command.SubCommand;
import me.knighthat.debugger.Debugger;
import me.knighthat.plugin.file.MessageFile;
import me.knighthat.plugin.message.Messenger;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class HelpCommand extends SubCommand {

    private static final @NonNull List<HelpTemplate> COMMAND_TEMPLATES = new ArrayList<>();
    private static @NonNull String footer = "";
    private static @NonNull String header = "";

    static {
        reload();
    }

    public static void reload() {
        COMMAND_TEMPLATES.clear();

        MessageFile file = Messenger.FILE;
        String mainPath = "help_message";
        if (!file.contains(mainPath))
            return;

        if (!file.get().isConfigurationSection(mainPath)) {
            String currentClass = file.get().get(mainPath).getClass().getName();
            Debugger.err("Wrong YAML layout!"
                    , currentClass + " is not org.bukkit.configurationConfigurationSection");
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

    public static int total() {
        float divided = COMMAND_TEMPLATES.size() / 3f;
        return (int) Math.ceil(divided);
    }

    @Override
    public void execute(@NonNull CommandSender sender, String @NonNull [] args) {
        int page = 1;

        try {
            page = Integer.parseInt(args[0]);

            if (page < 1 || page > total()) {
                Messenger.send(sender
                        , "invalid_page_number"
                        , null
                        , null
                        , Map.of("%input", args[0])
                );
                return;
            }

        } catch (NumberFormatException e) {
            Messenger.send(sender
                    , "not_a_number"
                    , null
                    , null
                    , Map.of("%input", args[0])
            );
        } catch (IndexOutOfBoundsException ignored) {
        }

        String replacedHeader = replacePage(header, page);
        String replacedFooter = replacePage(footer, page);
        Messenger.sendCommandHelp(sender, replacedHeader, replacedFooter, this.get(page));
    }

    @Contract("_,_ -> new")
    private @NonNull String replacePage(@NonNull String input, int page) {
        return input
                .replace("%current_page", String.valueOf(page))
                .replace("%total_page", String.valueOf(total()));
    }

    private @NonNull List<HelpTemplate> get(int page) {
        int start = (page - 1) * 3;
        int stop = start + 3;

        List<HelpTemplate> results = new ArrayList<>();

        for (int i = start; i < stop; i++) {
            if (i >= COMMAND_TEMPLATES.size()) break;

            results.add(COMMAND_TEMPLATES.get(i));
        }

        return results;
    }


    public record HelpTemplate(@NonNull String usage,
                               @NonNull List<String> permissions,
                               @NonNull List<String> description) {

        @Override
        public String toString() {
            StringJoiner joiner = new StringJoiner("\n");

            joiner.add(this.usage);
            permissions.forEach(joiner::add);
            description.forEach(joiner::add);

            return joiner.toString();
        }
    }
}
