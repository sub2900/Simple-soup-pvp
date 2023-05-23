import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class SoupPvPMiniGame extends JavaPlugin implements Listener {

    private Map<Player, Integer> playerSoupCount;

    @Override
    public void onEnable() {
        playerSoupCount = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack currentItem = player.getItemInHand();

        boolean isRightClick = event.getAction().toString().startsWith("RIGHT");
        boolean isHoldingSoup = currentItem.getType() == Material.MUSHROOM_SOUP;

        if (isRightClick && isHoldingSoup) {
            player.setHealth(Math.min(player.getHealth() + 7, player.getMaxHealth()));
            int currentCount = playerSoupCount.getOrDefault(player, 0);

            if (currentCount > 1) {
                playerSoupCount.put(player, currentCount - 1);
            } else {
                player.getInventory().remove(currentItem);
                playerSoupCount.remove(player);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();

        if (killer instanceof Player) {
            int currentCount = playerSoupCount.getOrDefault(killer, 0);
            playerSoupCount.put(killer, currentCount + 1);
        }

        Bukkit.getScheduler().runTaskLater(this, () -> {
            player.spigot().respawn();
            player.setHealth(player.getMaxHealth());
        }, 1);
    }
}
