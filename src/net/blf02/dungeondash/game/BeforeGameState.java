package net.blf02.dungeondash.game;

import net.blf02.dungeondash.utils.PlayerStorage;
import net.blf02.dungeondash.utils.Tracker;
import net.blf02.dungeondash.utils.Util;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.io.Serializable;
import java.util.Collection;

public class BeforeGameState implements Serializable {

    public transient Player player;

    public Location location;
    public double health;
    public int food;
    public float saturation;
    public ItemStack[] inventory;
    public GameMode gamemode;
    public Collection<PotionEffect> potionEffects;

    public BeforeGameState(Player player) {
        this.player = player;

        this.location = player.getLocation();
        this.health = player.getHealth();
        this.food = player.getFoodLevel();
        this.saturation = player.getSaturation();
        this.inventory = player.getInventory().getContents();
        this.gamemode = player.getGameMode();
        this.potionEffects = player.getActivePotionEffects();

        Util.runWithPlayerStorage(this.player, () -> this.saveBeforeGameState(false));
    }

    public void saveBeforeGameState(boolean makeNull) {
        PlayerStorage storage = Tracker.playerStorage.get(this.player.getUniqueId());
        if (storage == null) return;
        if (makeNull) {
            storage.setBeforeGameState(null);
        } else {
            storage.setBeforeGameState(this);
        }
    }

    public void restoreState(boolean useAsync) {
        player.setFallDistance(0);
        player.teleport(location);
        player.setFallDistance(0);
        player.setHealth(health);
        player.setFoodLevel(food);
        player.setSaturation(saturation);
        player.getInventory().setContents(inventory);
        player.setGameMode(gamemode);

        for (PotionEffect p : player.getActivePotionEffects()) {
            player.removePotionEffect(p.getType());
        }
        player.addPotionEffects(this.potionEffects);

        if (useAsync) {
            Util.runWithPlayerStorage(this.player, () -> this.saveBeforeGameState(true));
        } else {
            PlayerStorage storage = Tracker.playerStorage.get(this.player.getUniqueId());
            if (storage != null) {
                storage.setBeforeGameState(null);
            }
        }

    }

    @Override
    public String toString() {
        return "Gamemode " + gamemode.toString() + " with " + health + "HP and " + this.food + "food.";
    }
}
