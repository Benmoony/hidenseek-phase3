package com.cascadia.hidenseek;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by deb on 11/30/16.
 * This class extends on Hashtable to allow accessing the players by an index as if this was an array.
 * When players are added to the Hashtable, they are given an index that increments starting at 0.
 */

public class PlayerList extends Hashtable<Integer, Player> {
    // When players are added to the hashtable, they are appended to the arraylist
    // also, which gives them a position
    ArrayList<Integer> playerIDs = new ArrayList<>();
    // Return the player at a position
    public Player get(int idx) {
        return super.get(playerIDs.get(idx));
    }
    // Make sure to clear out the position array when the hashtable is cleared
    @Override
    public void clear() {
        super.clear();
        playerIDs.clear();
    }
    /* When adding a Player to the Hashtable, put the playerID in the list so the player
     * can be found by index.
     */
    @Override
    public Player put(Integer id, Player player) {
        super.put(id, player);
        playerIDs.add(player.getId());
        return player;
    }
}
