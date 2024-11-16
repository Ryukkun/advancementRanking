package fox.ryukkun_.advancementRanking;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import fox.ryukkun_.advancementRanking.command.AdvancementRankingCommand;
import org.bukkit.Bukkit;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;

public class AdvancementReader {
    private static final Path advancementPATH = Bukkit.getServer().getWorldContainer().toPath().resolve("world/advancements");

    public static int getCount(UUID uuid) {
        int ret = 0;
        try (JsonReader reader = new JsonReader(new FileReader(advancementPATH.resolve(uuid.toString()+".json").toFile()))) {
            JsonObject json = new Gson().fromJson(reader, JsonObject.class);
            Set<String> keys = json.keySet();
            keys.retainAll(AdvancementRankingCommand.availableAdvancement);

            for (String key : keys) {
                final JsonObject advancementObject = json.getAsJsonObject(key);
                if (!advancementObject.has("done")) {
                    continue;
                }
                if (advancementObject.get("done").getAsBoolean()) {
                    ret++;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }
}
