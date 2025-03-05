package fox.ryukkun_.advancementRanking.command;

import fox.ryukkun_.advancementRanking.AdvancementReader;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
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
        if (!(commandSender instanceof Player sender) || strings.length == 0) {
            return false;
        }

        Player target = Bukkit.getPlayerExact(strings[0]);
        if (target == null) {
            return false;
        }

        List<Boolean> doneList = AdvancementReader.getDoneList(target);
        List<Advancement> notDoneAdvancements = new ArrayList<>();
        int adSize = Ranking.availableAdvancement.size();
        int count = (int) doneList.stream()
                .filter(ad -> ad)
                .count();
        for (int i = 0; i < doneList.size(); i++) {
            if (!doneList.get(i)) {
                notDoneAdvancements.add(Ranking.availableAdvancement.get(i));
            }
        }

        double percent = (double) (count * 100) / adSize;
        int percentDec = (int) (percent * 100 % 100);
        String percentStr = (int) percent + "." + (percentDec <= 9 ? "0"+percentDec : percentDec) + "%";
        ComponentBuilder texts = new ComponentBuilder("")
                .append("--------------").strikethrough(true).color(ChatColor.DARK_AQUA)
                .append(" advancement ").strikethrough(false)
                .append("---------------\n").strikethrough(true)
                .append(target.getName() + " :  " + percentStr + " " + count + "/" + Ranking.availableAdvancement.size() + "\n").reset()
                .append("達成できていない進捗 : ");
        for(int i = 0; i < Math.min(10, notDoneAdvancements.size()); i++) {
            Advancement ad = notDoneAdvancements.get(i);
            texts.append(ad.getDisplay().getTitle()).reset();
            texts.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ad.getKey().getNamespace()+":"+ad.getKey().getKey())));
            switch (ad.getDisplay().getType()) {
                case TASK -> texts.color(ChatColor.GREEN);
                case GOAL -> texts.color(ChatColor.GOLD);
                case CHALLENGE -> texts.color(ChatColor.DARK_PURPLE);
                default -> texts.color(ChatColor.WHITE);
            }
            if (i == 9 && notDoneAdvancements.size() >= 11) {
                texts.append(".....").reset();
            } else {
                texts.append(", ").reset();
            }
        }
        texts.append("\n-----------------------------------------\n")
                .strikethrough(true)
                .color(ChatColor.DARK_AQUA)
                .build();

        sender.spigot().sendMessage(texts.create());

        return true;
    }
}
