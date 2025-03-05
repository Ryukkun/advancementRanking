package fox.ryukkun_.advancementRanking;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import fox.ryukkun_.advancementRanking.command.Ranking;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public class AdvancementReader {
    private static final Path advancementPATH = Bukkit.getServer().getWorldContainer().toPath().resolve("world/advancements");
    private static final List<String> availableAdName = Ranking.availableAdvancement.stream()
            .map(ad -> ad.getKey().getNamespace() + ":" + ad.getKey().getKey())
            .toList();


    public static int getCount(OfflinePlayer player) {
        return (int) getDoneList(player).stream()
                .filter(done -> done)
                .count();
    }


    public static List<Boolean> getDoneList(OfflinePlayer player) {
        if (player.isOnline() && player.getPlayer() != null) {
            return Ranking.availableAdvancement.stream()
                    .map(advancement -> player.getPlayer().getAdvancementProgress(advancement).isDone())
                    .toList();
        }

        try (JsonReader reader = new JsonReader(new FileReader(advancementPATH.resolve(player.getUniqueId() +".json").toFile()))) {
            JsonObject json = new Gson().fromJson(reader, JsonObject.class);
            Set<String> keys = json.keySet();

            return availableAdName.stream()
                    .map(ad -> {
                        if (keys.contains(ad)) {
                            final JsonObject advancementObject = json.getAsJsonObject(ad);
                            if (advancementObject.has("done")) {
                                return (advancementObject.get("done").getAsBoolean());
                            }
                        }
                        return false;
                    })
                    .toList();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return List.of();
    }
}
