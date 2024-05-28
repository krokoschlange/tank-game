/* 
 * The MIT License
 *
 * Copyright 2020 fabian.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package tankgame.client;

import blackhole.client.game.ClientObject;
import blackhole.common.GameObjectUpdateStrategy;
import blackhole.server.game.ServerObject;
import blackhole.utils.Debug;
import blackhole.utils.Vector;
import java.util.ArrayList;
import tankgame.common.Team;

/**
 *
 * @author fabian.baer2
 */
public class ClientTeam extends ClientObject implements Team {

	private String name;

	private ArrayList<Vector> spawns;

	private int points;
	
	private BattleState battleState;

	public ClientTeam() {
		points = 0;
		name = " ";
		spawns = new ArrayList<>();

		GameObjectUpdateStrategy strat = getDefaultUpdateStrategy();
		strat.addParameter("name", (v) -> {
			name = (String) v;
			ClientTeamManager.getCurrentTeamManager().createTeamSelectionUI(null);
		}, () -> {
			return null;
		});
		strat.addParameter("spawns", (v) -> {
			setSpawnPositions((ArrayList<Vector>) v);
		}, () -> {
			return null;
		});
		strat.addParameter("points", (v) -> {
			points = (int) v;
		}, () -> {
			return null;
		});
		strat.addParameter("battleState", (v) -> {
			battleState = (BattleState) v;
		}, () -> {
			return null;
		});
	}

	@Override
	public String getName() {
		return name;
	}
	
	public BattleState getBattleState() {
		return battleState;
	}

	@Override
	public void setSpawnPositions(ArrayList<Vector> sP) {
		spawns = sP;
		if (ClientTeamManager.getCurrentTeamManager().getPlayerClientObject() != null) {
			ClientTeamManager.getCurrentTeamManager().getPlayerClientObject().updateSpawnUISelection();
		}
		
	}

	@Override
	public ArrayList<Vector> getSpawnPositions() {
		return spawns;
	}

	@Override
	public Vector getSpawnPos(int spawn) {
		if (spawn >= 0 && spawn < spawns.size()) {
			return spawns.get(spawn);
		}
		return null;
	}

	@Override
	public void setPoints(int p) {
		points = p;
	}

	@Override
	public int getPoints() {
		return points;
	}

	@Override
	public void step(double dtime) {
	}

	@Override
	public void init() {
		if (ClientTeamManager.getCurrentTeamManager() != null) {
			ClientTeamManager.getCurrentTeamManager().addTeam(this);
		}
	}

	@Override
	public void remove() {
		if (ClientTeamManager.getCurrentTeamManager() != null) {
			ClientTeamManager.getCurrentTeamManager().removeTeam(this);
		}

		super.remove();
	}
}
