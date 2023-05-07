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

package me.knighthat.plugin.handler;

import lombok.NonNull;
import me.knighthat.api.command.CommandManager;
import me.knighthat.api.command.SubCommand;
import me.knighthat.api.style.Colorization;
import me.knighthat.plugin.file.MessageFile;

import java.util.ArrayList;
import java.util.List;

public class Helper {

    private static final @NonNull List<CommandHelpTemplate> COMMAND_TEMPLATES = new ArrayList<>();
    private static @NonNull String header = "";
    private static @NonNull String footer = "";

    static {
        reload();
    }

    public static void reload() {
        COMMAND_TEMPLATES.clear();

        String mainPath = "help_message";

        MessageFile file = Messenger.FILE;
        if (!file.contains(mainPath))
            return;

        String headerPath = mainPath.concat(".header");
        if (file.contains(headerPath)) {
            String headerMessage = file.message(headerPath);
            header = Colorization.color(headerMessage);
        }

        String footerPath = mainPath.concat(".footer");
        if (file.contains(footerPath)) {
            String footerMessage = file.string(footerPath);
            footer = Colorization.color(footerMessage);
        }

        for (SubCommand sub : CommandManager.SUB_COMMANDS) {
            String path = mainPath + "." + sub.getName() + ".";

            String usage = file.string(path.concat("usage"));
            usage = Colorization.color(usage);

            List<String> permissions = file.list(path.concat("permissions"));
            permissions = permissions.stream().map(Colorization::color).toList();

            List<String> description = file.list(path.concat("description"));
            description = description.stream().map(Colorization::color).toList();

            CommandHelpTemplate template = new CommandHelpTemplate(sub.getName(), usage, permissions, description);
            COMMAND_TEMPLATES.add(template);
        }
    }

    public static @NonNull List<String> get(int page) {
        int perPage = 3;
        int start = (page - 1) * perPage;
        int stop = start + perPage;

        List<String> result = new ArrayList<>();

        if (page < 1 || start >= COMMAND_TEMPLATES.size())
            return result;

        if (!header.isEmpty())
            result.add(replacements(header, page));

        for (int i = start; i < stop; i++) {
            if (i >= COMMAND_TEMPLATES.size())
                break;

            if (i != start)
                result.add("");

            CommandHelpTemplate template = COMMAND_TEMPLATES.get(i);
            result.add(template.usage());
            result.addAll(template.permissions());
            result.addAll(template.description());
        }

        if (!footer.isEmpty())
            result.add(replacements(footer, page));

        return result;
    }

    private static @NonNull String replacements(@NonNull String message, int page) {
        return message
                .replace("%current_page", String.valueOf(page))
                .replace("%total_page", String.valueOf(totalPages()));
    }

    public static int totalPages() {
        float divided = COMMAND_TEMPLATES.size() / 3f;
        return (int) Math.ceil(divided);
    }

    record CommandHelpTemplate(@NonNull String name,
                               @NonNull String usage,
                               @NonNull List<String> permissions,
                               @NonNull List<String> description) {
    }
}
