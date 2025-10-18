package dev.eministar.echolog.logging;

import dev.eministar.echolog.EchoLogPlugin;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;

public class DiscordWebhookSender {

    private final EchoLogPlugin plugin;

    public DiscordWebhookSender(EchoLogPlugin plugin) { this.plugin = plugin; }

    public void sendAsync(String webhook, String line, boolean simplified, String basePath, java.util.Map<String,String> ctx) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try { send(webhook, line, simplified, basePath); }
            catch (Exception e) {
                plugin.getLogger().warning("[EchoLog] Discord send failed: " + e.getMessage());
            }
        });
    }

    private void send(String webhook, String line, boolean simplified, String basePath) throws Exception {
        if (webhook == null || webhook.isBlank()) return;

        String json;
        if (simplified) {
            // plain text
            String escaped = escape(line);
            json = "{\"content\":\"" + escaped + "\"}";
        } else {
            // simple embed
            String title = "EchoLog • " + basePath.replace("logs.", "");
            String now = OffsetDateTime.now().toString();
            String escaped = escape(line);
            json = "{"
                    + "\"embeds\":[{"
                    + "\"title\":\"" + escape(title) + "\","
                    + "\"description\":\"" + escaped + "\","
                    + "\"timestamp\":\"" + now + "\""
                    + "}]}";
        }

        URL url = new URL(webhook);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setConnectTimeout(8000);
        con.setReadTimeout(12000);
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/json");

        try (OutputStream os = con.getOutputStream()) {
            os.write(json.getBytes(StandardCharsets.UTF_8));
        }

        int code = con.getResponseCode();
        if (code < 200 || code >= 300) {
            throw new IllegalStateException("HTTP " + code + " from Discord");
        }
        con.disconnect();
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
    }
}
