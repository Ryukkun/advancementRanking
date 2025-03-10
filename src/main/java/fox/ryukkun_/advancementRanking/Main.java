package fox.ryukkun_.advancementRanking;

import fox.ryukkun_.advancementRanking.command.Ranking;
import fox.ryukkun_.advancementRanking.command.ProgressDetail;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getCommand("advancement-ranking").setExecutor(new Ranking());
        this.getCommand("advancement-progress").setExecutor(new ProgressDetail());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
