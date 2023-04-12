package me.pinont.songkran.events;

import me.pinont.songkran.Songkran;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import org.bukkit.event.block.Action;
import org.bukkit.scheduler.BukkitTask;

public class WaterGun implements Listener {

    public WaterGun() {
        Songkran.plugin.getLogger().info("WaterGun is loaded!");
    }

    private BukkitTask task = null;

    @EventHandler
    public void opBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        if (!itemInMainHand.getType().isAir() && itemInMainHand.getItemMeta().getDisplayName().contains("Water Gun")) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You can't break block with water gun!");
        }
    }

    @EventHandler
    public void onWaterGun(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();

        Action click = event.getAction();
        if (!itemInMainHand.getType().isAir() && itemInMainHand.getItemMeta().getDisplayName().contains("Water Gun") && player.getCooldown(itemInMainHand.getType()) == 0) {
            if (click == Action.LEFT_CLICK_AIR || click == Action.LEFT_CLICK_BLOCK || click == Action.RIGHT_CLICK_AIR) {
                shootWater(player, itemInMainHand);
                if (task != null) {
                    task.cancel();
                }
            } else if (click == Action.RIGHT_CLICK_BLOCK) {
                if (event.getClickedBlock().getType().equals(Material.WATER) || event.getClickedBlock().getType().equals(Material.WATER_CAULDRON) || task == null) {
                    refileWater(player, itemInMainHand);
                    player.setCooldown(itemInMainHand.getType(), (itemInMainHand.getType().getMaxDurability() - itemInMainHand.getDurability()) / 2);
                    player.setCooldown(itemInMainHand.getType(), 20);
                } else if (task != null) {
                    player.sendMessage(ChatColor.RED + "You can only refill water gun once!");
                    player.playSound(player.getLocation(), "minecraft:block.anvil.use", 1, 1);
                } else {
                    player.sendMessage(ChatColor.RED + "You can't refill water gun here!");
                    player.playSound(player.getLocation(), "minecraft:entity.ender_pearl.throw", 1, 1);
                    player.setCooldown(itemInMainHand.getType(), 7);
                }
            }
        }
    }

    @EventHandler
    public void snowBallHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Snowball) {
            if (event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();
                player.damage(4);
            }
        } else {
            Player player = (Player) event.getDamager();
            player.sendMessage(ChatColor.RED + "Don't hit other players with water gun!");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void playerWetTodie(PlayerDeathEvent event) {
        Player player = event.getEntity();
        event.setDeathMessage(ChatColor.AQUA + player.getName() + " is wet to die!");
    }

    public void refileWater(Player player,ItemStack item) {
        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (item.getDurability() > 0) {
                    item.setDurability((short) (item.getDurability() - 20));
                    player.playSound(player.getLocation(), "minecraft:item.bucket.fill", 1, 1);
                } else {
                    cancel();
                }
            }
        }.runTaskTimer(Songkran.plugin, 10, item.getDurability()/25);
    }

    public void shootWater(Player player, ItemStack item) {
        if (item.getDurability() < item.getType().getMaxDurability() - 26) {
            // shoot water
            Snowball snowball = player.launchProjectile(Snowball.class);
            snowball.setVelocity(player.getLocation().getDirection().multiply(2));

            // set water drip to snowball location
            Location location = snowball.getLocation();
            location.getWorld().spawnParticle(Particle.WATER_DROP, location, 1);
            player.playSound(player.getLocation(), "minecraft:block.water.ambient", 1, 1);


            // set water gun durablity down
            item.setDurability((short) (item.getDurability() + 25));

            // set item cooldown
            player.setCooldown(item.getType(), 2);
        } else if (item.getItemMeta().getDisplayName().contains("Water Gun") && item.getDurability() >= item.getType().getMaxDurability() - 26) {
            player.sendMessage(ChatColor.RED + "Water gun is out of water!");
        }
    }
}
