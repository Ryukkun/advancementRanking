package fox.ryukkun_.advancementRanking.command;

import fox.ryukkun_.advancementRanking.AdvancementReader;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.advancement.Advancement;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.UUID;

public class AdvancementRankingCommand implements CommandExecutor {
    public static final HashSet<String> availableAdvancement = new HashSet<>();
    static {
        final Iterator<Advancement> advancements = Bukkit.advancementIterator();

            while (advancements.hasNext()) {
            Advancement advancement = advancements.next();
            if (advancement.getDisplay() == null) {
                continue;
            }

            availableAdvancement.add("minecraft:" + advancement.getKey().getKey());
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length <= 1) {
            return false;
        }

        int min, max;
        try {
            min = Integer.parseInt(args[0]);
            max = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            return false;
        }

        final ArrayList<@NonNull UUID> uuids = new ArrayList<>(
                Bukkit.getOnlinePlayers().stream()
                        .map(Entity::getUniqueId)
                        .toList());
        uuids.addAll(Arrays.stream(Bukkit.getOfflinePlayers())
                        .map(OfflinePlayer::getUniqueId)
                        .filter(uuid -> !uuids.contains(uuid))
                        .toList());

        if (uuids.size() <= min) {
            sender.sendMessage("表示できるプレイヤーがいません ( playerCount : " + uuids.size() + " )");
            return true;
        }
        final ArrayList<Pair> doneAdvCount = new ArrayList<>(uuids.stream()
                .map(uuid -> new Pair(uuid, AdvancementReader.getCount(uuid)))
                .toList());
        doneAdvCount.sort(Comparator.comparingInt(Pair::doneAdvCount).reversed());

        final int[] ranks = new int[doneAdvCount.size()];
        for (int i = 0, lastRank = -1, lastCount = -1; i < doneAdvCount.size(); i++) {
            final int count = doneAdvCount.get(i).doneAdvCount;
            if (lastCount == count) {
                ranks[i] = lastRank;
                continue;
            }
            ranks[i] = lastRank = i+1;
            lastCount = count;
        }

        ComponentBuilder texts = new ComponentBuilder("")
                .append("--------------").strikethrough(true).color(ChatColor.GOLD)
                .append(" advancement ").strikethrough(false)
                .append("---------------\n").strikethrough(true);
        for (int i = min; i < Math.min(max, uuids.size()); i++) {
            Pair pair = doneAdvCount.get(i);
            int rank = ranks[i];
            double percent = Math.floor(((double) (pair.doneAdvCount()*10000)) / availableAdvancement.size()) / 100;
            String air = String.join("", Collections.nCopies(5-(String.valueOf(percent).length()-1), "_"));

            texts.append("    " + ((rank <= 9) ? "0" : "") + rank + "位 :  ").reset();
            rankingColor(texts, rank);
            texts.append(air).color(ChatColor.DARK_GRAY);
            texts.append(percent + "% ");
            rankingColor(texts, rank);
            texts.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(pair.doneAdvCount() + " / " + availableAdvancement.size())));

            texts.append(Bukkit.getOfflinePlayer(pair.uuid()).getName() + "\n").reset();
        }
        texts.append("-----------------------------------------\n")
                .strikethrough(true)
                .color(ChatColor.GOLD)
                .build();

        if (args.length >= 3) {
            Player player = Bukkit.getPlayerExact(args[2]);
            if (player == null) {
                sender.sendMessage("指定したプレイヤーが見つかりません。");
                return true;
            }
            player.spigot().sendMessage(texts.create());
            return true;
        }
        Bukkit.getServer().spigot().broadcast(texts.create());
        return true;

    }

    private void rankingColor(ComponentBuilder texts, int rank) {
        switch (rank) {
            case 1:
                texts.color(ChatColor.YELLOW).bold(true);
                break;
            case 2:
                texts.color(ChatColor.AQUA).bold(true);
                break;
            case 3:
                texts.color(ChatColor.WHITE).bold(true);
                break;
            default:
                texts.color(ChatColor.GRAY).bold(true);
        }
    }

    public record Pair(UUID uuid, int doneAdvCount) {
    }
}
