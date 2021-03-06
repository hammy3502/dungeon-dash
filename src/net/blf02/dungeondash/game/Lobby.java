package net.blf02.dungeondash.game;

import net.blf02.dungeondash.config.Config;
import net.blf02.dungeondash.utils.Position;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class Lobby {

    public boolean gameStarted = false;
    public List<PlayerState> playerStates = new ArrayList<>();
    public int ticksUntilStart = Config.ticksInLobby;
    public List<ArmorStand> chasers = new LinkedList<>();
    public Queue<PlayerState> positionsLastToFirst = new PriorityQueue<>();
    public DDMap map;
    // Linked lists use FIFO
    public Map<PlayerState, LinkedList<Position>> playerToPositions = new HashMap<>();

    public Lobby(DDMap map) {
        this.map = map;
    }

    public void addPositionForPlayer(PlayerState playerState, Position position) {
        LinkedList<Position> positions = playerToPositions.get(playerState);
        if (positions == null) {
            positions = new LinkedList<>();
            playerToPositions.put(playerState, positions);
        }
        positions.offer(position);
    }

    public Location getNextPositionForChaser(PlayerState playerState, Location currentPosition) {
        Position toReturn = playerToPositions.get(playerState).remove();
        // Constantly loop forward until the player isn't standing still anymore.
        // This function can eventually be expanded to cover more edge cases that the shadow should skip.
        while (toReturn.getX() == currentPosition.getX() && toReturn.getY() == currentPosition.getY()
        && toReturn.getZ() == currentPosition.getZ()) {
            toReturn = playerToPositions.get(playerState).remove();
        }
        return toReturn.asLocation(playerState.player.getWorld());
    }

    public void cleanLobby() {
        // Remove chaser if it exists
        for (ArmorStand chaser : chasers) {
            chaser.remove();
        }
        // Lose all players
        for (PlayerState p : playerStates) {
            p.triggerLoss();
        }
    }

    public void resetRankings() {
        positionsLastToFirst.clear();
        positionsLastToFirst.addAll(this.playerStates);
    }
}
