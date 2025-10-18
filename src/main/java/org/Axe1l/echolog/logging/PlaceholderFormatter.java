package org.Axe1l.echolog.logging;

import org.Axe1l.echolog.EchoLogPlugin;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class PlaceholderFormatter {
    private final EchoLogPlugin plugin;
    private final DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public PlaceholderFormatter(EchoLogPlugin plugin) { this.plugin = plugin; }

    public String render(String pattern, Map<String,String> ctx) {
        String out = pattern;
        out = out.replace("%time%", LocalDateTime.now().format(timeFmt));
        out = replace(out, ctx, "player","uuid","message","world","x","y","z","reason","command","from","to","action","block");
        return out;
    }

    private String replace(String s, Map<String,String> m, String... keys) {
        for (String k : keys) {
            String v = m.getOrDefault(k, "");
            s = s.replace("%"+k+"%", v == null ? "" : v);
        }
        return s;
    }

    public String defaultFormatFor(String base) {
        return switch (base) {
            case "logs.chat"       -> "[%time%] %player%: %message%";
            case "logs.join_quit"  -> "[%time%] + %player% joined (%uuid%)";
            case "logs.death"      -> "[%time%] ☠ %player% died: %reason% at %world% (%x%/%y%/%z%)";
            case "logs.commands"   -> "[%time%] /%command% by %player%";
            case "logs.teleport"   -> "[%time%] %player% teleported %from% -> %to%";
            case "logs.block"      -> "[%time%] %player% %action% %block% at %world% (%x%/%y%/%z%)";
            default -> "[%time%] %message%";
        };
    }
}
