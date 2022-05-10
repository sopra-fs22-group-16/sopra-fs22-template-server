package ch.uzh.ifi.hase.soprafs22.game.logger;

import ch.uzh.ifi.hase.soprafs22.game.logger.interfaces.IGameLogger;
import ch.uzh.ifi.hase.soprafs22.game.logger.interfaces.IGameStatistics;

import java.util.*;

/**
 * Implementation of GameLogger to provide GameStatistics.
 */
public final class GameLogger implements IGameStatistics, IGameLogger {
    private final Map<Long, Integer> unitsPerPlayer;

    private final List<Map<Long, Integer>> turnSnapshots = new ArrayList<>();
    private int totalMoves = 0;
    private int turn = 0;

    public GameLogger(Map<Long, Integer> unitsPerPlayer) {
        this.unitsPerPlayer = new HashMap<>();
        this.unitsPerPlayer.putAll(unitsPerPlayer);
        // Add a ghost entry with the initial state, so we can compute kills in first turn.
        turnSnapshots.add(Map.copyOf(unitsPerPlayer));
    }

    @Override
    public Map<Long, List<Integer>> unitsPerPlayer() {
        Map<Long, List<Integer>> result = new HashMap<>();
        for(long player : unitsPerPlayer.keySet())
            result.put(player, new ArrayList<>());
        for (Map<Long, Integer> turnSnapshot : turnSnapshots)
            for (long player : turnSnapshot.keySet())
                result.get(player).add(turnSnapshot.get(player));
        return result;
    }

    @Override
    public float averageUnitsPerTurn() {
        int allUnitsInAllTurns = 0;
        boolean first = true;
        for(Map<Long, Integer> snapshot :  turnSnapshots) {
            // Ignore the first (ghost entry).
            if (first) {
                first = false;
                continue;
            }
            allUnitsInAllTurns += snapshot.values().stream().mapToInt(Integer::intValue).sum();
        }
        return (float) allUnitsInAllTurns / (float) (turnSnapshots.size() - 1); // -1 for the ghost entry.
    }

    private List<Integer> killsPerTurn() {
        List<Integer> result = new ArrayList<>();
        for (int i = 1; i < turnSnapshots.size(); i++) {
            // All the units that were alive in previous turn
            int prev = aliveUnitsAtTurn(i - 1);
            // All the units that are alive now
            int curr = aliveUnitsAtTurn(i);
            result.add(prev-curr);
        }
        return result;
    }

    private int aliveUnitsAtTurn(int turn) {
        return aliveUnits(turnSnapshots.get(turn));
    }

    private int aliveUnits(Map<Long, Integer> snapshot) {
        int sum = 0;
        for(int units : snapshot.values())
            sum += units;
        return sum;
    }

    @Override
    public float averageKillsPerTurn() {
        return killsPerTurn().stream().mapToInt(Integer::intValue).sum() / (float)(turnSnapshots.size()-1);
    }

    @Override
    public int totalMoves() {
        return totalMoves;
    }

    @Override
    public void unitKilledAtTurn(int turn, long playerId) {
        if (turn != this.turn)
            throw new IllegalStateException("Wrong turn!");
        unitsPerPlayer.replace(playerId, unitsPerPlayer.get(playerId) -1);
    }

    @Override
    public void move(int turn) {
        if (turn != this.turn)
            throw new IllegalStateException("Wrong turn!");
        totalMoves++;
    }

    @Override
    public void nextTurn() {
        // Take a snapshot with the units per player!
        turnSnapshots.add(Map.copyOf(unitsPerPlayer));
        turn++;
    }
}