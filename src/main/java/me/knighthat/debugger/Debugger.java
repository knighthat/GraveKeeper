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

package me.knighthat.debugger;

import lombok.NonNull;
import org.jetbrains.annotations.Nullable;


public class Debugger {

    public static java.util.logging.Logger FALLBACK;
    public static @Nullable org.slf4j.Logger LOGGER = null;

    public static void err(@NonNull String error, @NonNull String cause) {
        try {
            log(LOGGER, DebugLevel.ERR, error);
            log(LOGGER, DebugLevel.ERR, "Caused by: " + cause);
        } catch (NullPointerException e) {
            log(FALLBACK, DebugLevel.ERR, error);
            log(FALLBACK, DebugLevel.ERR, "Caused by: " + cause);
        }
    }

    public static void log(@NonNull String message) {
        try {
            log(LOGGER, DebugLevel.INFO, message);
        } catch (NullPointerException e) {
            log(FALLBACK, DebugLevel.INFO, message);
        }
    }

    public static void warn(@NonNull String warning) {
        try {
            log(LOGGER, DebugLevel.WARN, warning);
        } catch (NullPointerException e) {
            log(FALLBACK, DebugLevel.WARN, warning);
        }
    }


    public static void log(@NonNull org.slf4j.Logger logger, @NonNull DebugLevel level, @NonNull String message) {
        switch (level) {
            case INFO -> logger.info(message);
            case WARN -> logger.warn(message);
            case ERR -> logger.error(message);
        }
    }

    public static void log(@NonNull java.util.logging.Logger logger, @NonNull DebugLevel level, @NonNull String message) {
        switch (level) {
            case INFO -> logger.info(message);
            case WARN -> logger.warning(message);
            case ERR -> logger.severe(message);
        }
    }

    enum DebugLevel {
        INFO, WARN, ERR
    }
}
