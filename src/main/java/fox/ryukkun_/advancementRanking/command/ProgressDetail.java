package fox.ryukkun_.advancementRanking.command;

import fox.ryukkun_.advancementRanking.AdvancementReader;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ProgressDetail implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player) || strings.length == 0) {
            return false;
        }

        Player sender = (Player) commandSender;
        Player target = Bukkit.getPlayerExact(strings[0]);
        if (target == null) {
            return false;
        }

        List<Boolean> doneList = AdvancementReader.getDoneList(target);
        List<Advancement> doneAdvancements = new ArrayList<>();
        int count = 0;
        for (int i = 0; i < doneList.size(); i++) {
            if (doneList.get(i)) {
                doneAdvancements.add(AdvancementRanking.availableAdvancement.get(i));
                count++;
            }
        }

        double percent = (double) (count * 100 / AdvancementRanking.availableAdvancement.size());
        int percentDec = (int) (percent * 100 % 100);
        String percentStr = (int) percent + "." + (percentDec <= 9 ? "0"+percentDec : percentDec) + "%";
        ComponentBuilder texts = new ComponentBuilder("")
                .append("--------------").strikethrough(true).color(ChatColor.DARK_AQUA)
                .append(" advancement ").strikethrough(false)
                .append("---------------\n").strikethrough(true)
                .append(target.getName() + " :  " + percentStr + " " + count + "/" + AdvancementRanking.availableAdvancement.size()).reset()



        doneAdvancements.get(1).getDisplay().getTitle()
    }
}
