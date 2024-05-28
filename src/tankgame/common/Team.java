/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tankgame.common;

import blackhole.utils.Vector;
import java.util.ArrayList;

/**
 *
 * @author fabian
 */
public interface Team {
	enum BattleState {
		VICTORY,
		DEFEAT,
		ONGOING,
		DRAW
	}
	
	public String getName();

	public void setSpawnPositions(ArrayList<Vector> sP);

	public ArrayList<Vector> getSpawnPositions();

	public Vector getSpawnPos(int spawn);

	public void setPoints(int p);

	public int getPoints();
}
