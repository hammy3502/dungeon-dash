package net.blf02.dungeondash.game;

import net.blf02.dungeondash.DungeonDash;
import net.blf02.dungeondash.config.Config;
import net.blf02.dungeondash.config.Constants;
import net.blf02.dungeondash.utils.Tracker;
import net.blf02.dungeondash.utils.Util;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.org.eclipse.sisu.Nullable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class DDMap implements Serializable {

    private static final transient long serialVersionUID = 1791518827905011055L;

    public final String mapDisplayName;
    public Location start;
    public Location endCorner1;
    public Location endCorner2;

    public boolean respawnsKill = false;
    public boolean voidRespawns = false;
    public boolean waterRespawns = false;
    public ChaserMode chaserMode = ChaserMode.CHASE_LAST;
    public ChaserSpeed chaserSpeedEnum = ChaserSpeed.FAST;
    public String iconId = "COMPASS";
    public int mapVersion = 3;

    public transient double chaserSpeed = Config.distanceToMoveFast;

    public DDMap(String mapDisplayName, Location start, Location end1, Location end2) {
        this.mapDisplayName = mapDisplayName;
        this.start = start;
        this.endCorner1 = end1;
        this.endCorner2 = end2;
    }

    public void updateChaserSpeed() {
        this.chaserSpeed = this.chaserSpeedEnum.getSpeed(this.chaserMode);
    }

    public Location getCenterOfEnd() {
        return new Location(this.endCorner1.getWorld(),
                (Math.abs(this.endCorner1.getX()) + Math.abs(this.endCorner2.getX())) / 2.0,
                (Math.abs(this.endCorner1.getY()) + Math.abs(this.endCorner2.getY())) / 2.0,
                (Math.abs(this.endCorner1.getZ()) + Math.abs(this.endCorner2.getZ())) / 2.0,
                0, 0);
    }

    public boolean isFullMap() {
        return mapDisplayName != null && start != null &&
                endCorner1 != null && endCorner2 != null;
    }

    public boolean hasChaser() {
        return this.chaserMode != ChaserMode.NO_CHASER;
    }

    public boolean saveMap(@Nullable CommandSender sender) {
        String filePath = DungeonDash.mapsDir + this.mapDisplayName + ".data";
        try {
            BukkitObjectOutputStream out = new BukkitObjectOutputStream(
                    new GZIPOutputStream(new FileOutputStream(filePath)));
            out.writeObject(this);
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            Util.sendMessage(sender, "Failed to save map data! Maybe try setting the second corner again?");
            return false;
        }
    }

    public boolean saveMap(@Nullable CommandSender sender, DDMap oldMap) {
        boolean res = saveMap(sender);
        Tracker.maps.remove(oldMap);
        return res;
    }

    public ItemStack getGUIIcon() {
        Material itemMat = Material.getMaterial(iconId);
        if (itemMat == null) itemMat = Material.COMPASS;
        ItemStack toRet = new ItemStack(itemMat);
        ItemMeta meta = toRet.getItemMeta();
        ChatColor color = this.hasChaser() ? ChatColor.RED : ChatColor.BLUE;
        meta.setDisplayName(ChatColor.RESET + color.toString() + this.mapDisplayName);
        toRet.setItemMeta(meta);
        return toRet;
    }

    public static DDMap loadMap(String path) {
        try {
            BukkitObjectInputStream in = new BukkitObjectInputStream(
                    new GZIPInputStream(new FileInputStream(path)));
            DDMap map = (DDMap) in.readObject();
            in.close();
            if (map.chaserMode == null) {
                map.chaserMode = ChaserMode.CHASE_LAST;
            }
            if (map.chaserSpeedEnum == null) {
                map.chaserSpeedEnum = ChaserSpeed.FAST;
            }
            if (map.iconId == null) {
                map.iconId = "COMPASS";
            }
            map.updateChaserSpeed();
            return map;
        } catch (ClassNotFoundException | IOException e) {
            return null;
        }
    }

    @Override
    public int hashCode() {
        // No two maps share the same name, so we're safe to use the hash from the map name.
        return this.mapDisplayName.hashCode();
    }

    public enum ChaserMode implements Serializable {
        CHASE_LAST, SHADOW, NO_CHASER
    }

    public enum ChaserSpeed implements Serializable {
        SLOW(0),
        MEDIUM(1),
        FAST(2);

        int index;

        ChaserSpeed(int index) {
            this.index = index;
        }

        double getSpeed(ChaserMode mode) {
            if (mode == ChaserMode.CHASE_LAST) {
                return Constants.chaserSpeeds[index];
            } else if (mode == ChaserMode.SHADOW) {
                return Constants.shadowSpeeds[index];
            }
            return 0;
        }

        public ChaserSpeed getNext() {
            if (this == ChaserSpeed.SLOW) {
                return ChaserSpeed.MEDIUM;
            } else if (this == ChaserSpeed.MEDIUM) {
                return ChaserSpeed.FAST;
            } else {
                return ChaserSpeed.SLOW;
            }
        }
    }
}
