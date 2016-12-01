package com.cascadia.hidenseek;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by deb on 11/30/16.
 * This class extends on Hashtable to allow accessing the players by an index as if this was an array.
 * When players are added to the Hashtable, they are given an index that increments starting at 0.
 */

public class PlayerList extends Hashtable<Integer, Player> {
    ArrayList<Integer> playerIDs = new ArrayList<>();
    public Player get(int idx) {
        return super.get(playerIDs.get(idx));
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
