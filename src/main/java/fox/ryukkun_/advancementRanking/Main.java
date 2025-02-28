package fox.ryukkun_.advancementRanking;

import fox.ryukkun_.advancementRanking.command.AdvancementRanking;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getCommand("advancement-ranking").setExecutor(new AdvancementRanking());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
