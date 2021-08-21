package net.blf02.dungeondash.inventory;

import net.blf02.dungeondash.game.CreateState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MapCreationGUI extends BaseGUI {

    private final CreateState state;

    public MapCreationGUI(CreateState state) {
        super(1, state.map.mapDisplayName + " Settings");
        this.state = state;
        updateItems();
    }

    public void updateItems() {
        ItemMeta meta;

        ItemStack voidSpawns;
        if (state.map.voidRespawns) {
            voidSpawns = new ItemStack(Material.BEDROCK);
            meta = voidSpawns.getItemMeta();
            meta.setDisplayName(ChatColor.GRAY + "Void Respawns - " + ChatColor.GREEN + "Enabled");
        } else {
            voidSpawns = new ItemStack(Material.BARRIER);
            meta = voidSpawns.getItemMeta();
            meta.setDisplayName(ChatColor.GRAY + "Void Respawns - " + ChatColor.RED + "Disabled");
        }
        voidSpawns.setItemMeta(meta);
        this.replaceItem(0, voidSpawns);

        ItemStack waterRespawns;
        if (state.map.waterRespawns) {
            waterRespawns = new ItemStack(Material.WATER_BUCKET);
            meta = waterRespawns.getItemMeta();
            meta.setDisplayName(ChatColor.BLUE + "Water Respawns - " + ChatColor.GREEN + "Enabled");
        } else {
            waterRespawns = new ItemStack(Material.BARRIER);
            meta = waterRespawns.getItemMeta();
            meta.setDisplayName(ChatColor.BLUE + "Water Respawns - " + ChatColor.RED + "Disabled");
        }
        waterRespawns.setItemMeta(meta);
        this.replaceItem(1, waterRespawns);

        ItemStack respawnsKill;
        if (state.map.respawnsKill) {
            respawnsKill = new ItemStack(Material.TOTEM_OF_UNDYING);
            meta = respawnsKill.getItemMeta();
            meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Respawns Kill - " + ChatColor.GREEN + "Enabled");
        } else {
            respawnsKill = new ItemStack(Material.BARRIER);
            meta = respawnsKill.getItemMeta();
            meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Respawns Kill - " + ChatColor.RED + "Disabled");
        }
        respawnsKill.setItemMeta(meta);
        this.replaceItem(2, respawnsKill);

        ItemStack hasChaser;
        if (state.map.hasChaser) {
            hasChaser = new ItemStack(Material.ARMOR_STAND);
            meta = hasChaser.getItemMeta();
            meta.setDisplayName(ChatColor.GRAY + "Use a Chaser - " + ChatColor.GREEN + "Enabled");
        } else {
            hasChaser = new ItemStack(Material.BARRIER);
            meta = hasChaser.getItemMeta();
            meta.setDisplayName(ChatColor.GRAY + "Use a Chaser - " + ChatColor.RED + "Disabled");
        }
        hasChaser.setItemMeta(meta);
        this.replaceItem(3, hasChaser);

        ItemStack saveItem = new ItemStack(Material.COMMAND_BLOCK);
        meta = saveItem.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_PURPLE + "Save Map");
        saveItem.setItemMeta(meta);
        this.replaceItem(7, saveItem);

        ItemStack cancelItem = new ItemStack(Material.BARRIER);
        meta = cancelItem.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_RED + "Cancel Creation/Undo Edits");
        cancelItem.setItemMeta(meta);
        this.replaceItem(8, cancelItem);
    }

    @Override
    public void onItemClick(ItemStack stack, int slot, HumanEntity player) {
        String command;
        boolean doExit = false;
        switch (slot) {
            case 0:
                command = "ddash create void_respawn";
                break;
            case 1:
                command = "ddash create water_respawn";
                break;
            case 2:
                command = "ddash create respawns_kill";
                break;
            case 3:
                command = "ddash create use_chaser";
                break;
            case 7:
                command = "ddash create save";
                doExit = true;
                break;
            case 8:
                command = "ddash create cancel";
                doExit = true;
                break;
            default:
                System.out.println("Bad option " + slot + "! Please report this to the dev!");
                return;
        }
        Bukkit.getServer().dispatchCommand(state.player, command);
        if (doExit) {
            state.player.closeInventory();
        } else {
            this.updateItems();
        }
    }
}
