package net.blf02.dungeondash.commands;

import net.blf02.dungeondash.game.CreateState;
import net.blf02.dungeondash.game.DDMap;
import net.blf02.dungeondash.inventory.MapCreationGUI;
import net.blf02.dungeondash.utils.Tracker;
import net.blf02.dungeondash.utils.Util;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateHandler {

    public static final String[] helpMsg = new String[]{
            "=DungeonDash Creation Commands=",
            "`/ddash create help` - Displays this help message",
            "`/ddash create [Map Name]` - Begins creating a map with the name 'Map Name'",
            "`/ddash create spawn` (=Lime Dye=) - Run after creating a map to set the starting point for players.",
            "`/ddash create end1` (=Red Dye=) - Run after the above command to set the first corner of the ending zone where you're standing.",
            "`/ddash create end2` (=Red Dye=) - Run after the above command to set the second corner for the ending zone where you're standing.",
            "`/ddash create settings` (=Command Block=) - Opens the settings for the map to adjust options, save, and/or cancel creation.",
            /* No longer shown due to /ddash create settings
            "`/ddash create void_respawn` (=Bedrock=) - Enables/disables the void respawning players. Defaults to false.",
            "`/ddash create water_respawn` (=Water Bucket=) - Enables/disables water respawning players. Defaults to false.",
            "`/ddash create respawns_kill` (=Totem of Undying=) - Enables/disables respawns killing the player instead. Defaults to false.",
            "`/ddash create save` (=Command Block=) - Saves the map to disk, and makes it available to players to play.",
            "`/ddash create cancel` (=Barrier in Rightmost Hotbar Slot=) - Run during any point of the creation process to cancel creation.",
            "`/ddash create change_speed` - Changes the speed of the chaser."
            */
    };

    public static void handleCreateCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            Util.sendMessage(sender, "Only execute create commands as a player!");
            return;
        }
        Player player = (Player) sender;
        if (args[1].equals("help")) {
            Util.sendMessage(sender, helpMsg);
        } else if (Tracker.playStatus.get(player.getDisplayName()) != null) {
            Util.sendMessage(player, "You're currently playing a map! Please use `/ddash leave` to leave, first!");
        } else if (Tracker.creationStatus.get(player.getDisplayName()) == null) {
            if (Tracker.getMap(args[1]) != null) {
                Util.sendMessage(sender, "That map already exists!");
            } else {
                Tracker.creationStatus.put(player.getDisplayName(),
                        new CreateState(player, new DDMap(args[1], null, null, null), null));
                Util.sendMessage(sender, "Created map with name '" + args[1] + "'! Type `/ddash create spawn` or use the =Lime Dye= to set the spawnpoint for this map, or `/ddash create cancel` to cancel creation!");
            }
        } else if (args[1].equals("spawn")) {
            CreateState old = Tracker.creationStatus.get(player.getDisplayName());
            old.map.start = player.getLocation();
            Util.sendMessage(sender, "Set start position of map! Type `/ddash create end1` or use the =Red Dye= to set the first corner of the ending zone!");
        } else if (args[1].equals("end1")) {
            CreateState old = Tracker.creationStatus.get(player.getDisplayName());
            old.map.endCorner1 = player.getLocation();
            old.currentCornerItem = 2;
            Util.sendMessage(sender, "Set first corner of end! Type `/ddash create end2` or use the =Red Dye= to set the other corner of the ending zone!");
        } else if (args[1].equals("end2")) {
            CreateState old = Tracker.creationStatus.get(player.getDisplayName());
            old.map.endCorner2 = player.getLocation();
            old.currentCornerItem = 1;
            Util.sendMessage(sender, "Set second corner of the map successfully! Use `/ddash create help` to see what other commands and items you can run, or scroll around your hotbar!");
        } else if (args[1].equals("save")) {
            CreateState state = Tracker.creationStatus.get(player.getDisplayName());
            DDMap map = state.map;
            if (!map.isFullMap()) {
                Util.sendMessage(sender, "Error: Map is missing needed properties! Make sure you've set the `spawn` and both `end corners`!");
                return;
            }
            boolean res;
            if (state.mapAtInitialRun != null) {
                res = map.saveMap(sender, state.mapAtInitialRun);
            } else {
                res = map.saveMap(sender);
            }
            if (res) {
                Util.sendMessage(sender, "Map saved successfully!");
                Tracker.maps.add(map);
                Tracker.beforeGameStates.add(state.beforeGameState);
                Tracker.creationStatus.remove(player.getDisplayName());
            }
        } else if (args[1].equals("void_respawns") || args[1].equals("void_respawn")) {
            CreateState state = Tracker.creationStatus.get(player.getDisplayName());
            DDMap map = state.map;
            map.voidRespawns = !map.voidRespawns;
            if (map.voidRespawns) {
                Util.sendMessage(sender, "Players entering the void now respawn!");
            } else {
                Util.sendMessage(sender, "Players entering the void no longer respawn!");
            }
        } else if (args[1].equals("water_respawns") || args[1].equals("water_respawn")) {
            CreateState state = Tracker.creationStatus.get(player.getDisplayName());
            DDMap map = state.map;
            map.waterRespawns = !map.waterRespawns;
            if (map.waterRespawns) {
                Util.sendMessage(sender, "Players entering water now respawn!");
            } else {
                Util.sendMessage(sender, "Players entering water no longer respawn!");
            }
        } else if (args[1].equals("respawns_kill") || args[1].equals("respawn_kill") || args[1].equals("respawn_kills")) {
            CreateState state = Tracker.creationStatus.get(player.getDisplayName());
            DDMap map = state.map;
            map.respawnsKill = !map.respawnsKill;
            if (map.respawnsKill) {
                Util.sendMessage(sender, "Players are now killed instead of respawning!");
            } else {
                Util.sendMessage(sender, "Players now respawn normally!");
            }
        } else if (args[1].equals("change_chaser")) {
            CreateState state = Tracker.creationStatus.get(player.getDisplayName());
            DDMap map = state.map;
            if (map.chaserMode == DDMap.ChaserMode.CHASE_LAST) {
                map.chaserMode = DDMap.ChaserMode.SHADOW;
                Util.sendMessage(sender, "All players now have an individual shadow that copies their moves!");
            } else if (map.chaserMode == DDMap.ChaserMode.SHADOW) {
                map.chaserMode = DDMap.ChaserMode.NO_CHASER;
                Util.sendMessage(sender, "No chaser! First to the finish, wins!");
            } else if (map.chaserMode == DDMap.ChaserMode.NO_CHASER) {
                map.chaserMode = DDMap.ChaserMode.CHASE_LAST;
                Util.sendMessage(sender, "Chaser has been enabled, and will chase whoever is in last place!");
            }
        } else if (args[1].equals("change_speed")) {
            CreateState state = Tracker.creationStatus.get(player.getDisplayName());
            DDMap map = state.map;
            map.chaserSpeedEnum = map.chaserSpeedEnum.getNext();
            map.updateChaserSpeed();
            Util.sendMessage(sender, "Updated chaser speed to " + map.chaserSpeedEnum);
        } else if (args[1].equals("cancel")) {
            CreateState state = Tracker.creationStatus.get(player.getDisplayName());
            if (Tracker.creationStatus.remove(player.getDisplayName()) != null) {
                Tracker.beforeGameStates.add(state.beforeGameState);
                Util.sendMessage(sender, "Cancelled creation!");
            } else {
                Util.sendMessage(sender, "Could not cancel creation process as one was not started!");
            }
        } else if (args[1].equals("settings")) {
            CreateState state = Tracker.creationStatus.get(player.getDisplayName());
            MapCreationGUI gui = new MapCreationGUI(state);
            gui.openOnPlayer(player);
        }
        else {
            Util.sendMessage(sender, "Invalid subcommand! Type `/ddash create help` for help!");
        }
    }
}
