package org.Axe1l.echolog.util;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public final class SoundFX {
    private SoundFX() {}

    public static void open(Player p)   { play(p, Sound.UI_TOAST_IN, 0.7f, 1.0f); }
    public static void click(Player p)  { play(p, Sound.UI_BUTTON_CLICK, 0.6f, 1.05f); }

    public static void success(Player p){ play(p, Sound.ENTITY_PLAYER_LEVELUP, 0.7f, 1.2f); }
    public static void ok(Player p)     { success(p); }

    public static void error(Player p)  { play(p, Sound.ENTITY_VILLAGER_NO, 0.8f, 1.0f); }
    public static void err(Player p)    { error(p); }

    public static void toggleOn(Player p){ play(p, Sound.BLOCK_NOTE_BLOCK_PLING, 0.8f, 1.35f); }
    public static void on(Player p)      { toggleOn(p); }

    public static void toggleOff(Player p){ play(p, Sound.BLOCK_NOTE_BLOCK_BASS, 0.8f, 0.85f); }
    public static void off(Player p)       { toggleOff(p); }

    private static void play(Player p, Sound s, float vol, float pitch) {
        try { p.playSound(p.getLocation(), s, vol, pitch); }
        catch (Throwable ignored) {}
    }
}
