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
package tankgame.server;

import blackhole.common.GameObject;
import blackhole.server.game.Client;
import blackhole.server.game.ServerObjectHandler;
import blackhole.server.physicsEngine.core.PhysicsHandler;
import blackhole.utils.Debug;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import tankgame.common.Team;
import tankgame.server.maps.Finland;
import tankgame.server.maps.GameMap;

/**
 *
 * @author fabian.baer2
 */
public class GOHandler extends ServerObjectHandler {

	private ArrayList<Player> players;

	private ArrayList<ServerTeam> teams;

	private int spawnDelay;
	private long lastBattleEnd;

	private double clearTimer;
	private double battleTimer;

	public GOHandler() {
		players = new ArrayList<>();
		teams = new ArrayList<>();

		spawnDelay = 10;
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	@Override
	public void clientConnect(Client c) {
		Player p = new Player(c);
		players.add(p);
		long deltaTime = System.nanoTime() - lastBattleEnd;
		double st = -deltaTime / 1e9 + spawnDelay;
		if (st < 0.1) {
			st = 0.1;
		}
		p.setSpawnTimer(st);

		p.setHandler(this);
		p.activate();
		p.addToUpdate("waitForSpawn");
	}

	@Override
	public void clientDisconnect(Client c) {
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).getClient() == c) {
				players.get(i).remove();
				players.remove(i);
			}
		}
	}

	@Override
	public void step(double dtime) {
		/*for (int i = 0; i < getObjects().size(); i++) {
			Debug.log(getObjects().get(i));
		}
		Debug.log("SERVER: " + getObjects().size() + "-----");*/
		ServerTeamManager stm = ServerTeamManager.getCurrentTeamManager();
		if (stm != null && battleTimer < 0) {
			ServerTeam winner = stm.getWinner();
			if (winner != null) {
				Debug.log("WINNER " + winner.getName() + ", " + winner.getID() + ", " + stm + ": " + stm.getAllTeams().size() + ", " + stm.getBases().size());
				ArrayList<ServerTeam> teams = stm.getAllTeams();
				for (int i = 0; i < teams.size(); i++) {
					if (teams.get(i) != winner) {
						teams.get(i).setBattleState(Team.BattleState.DEFEAT);
					} else {
						teams.get(i).setBattleState(Team.BattleState.VICTORY);
					}
				}

				lastBattleEnd = System.nanoTime();
				Debug.log("WHAT");

				for (int i = 0; i < players.size(); i++) {
					//players.get(i).die();
					//players.get(i).setSpawned(false);
					//addToUpdate("isSpawned");
					players.get(i).setSpawnTimer(spawnDelay);
					players.get(i).addToUpdate("waitForSpawn");
				}

				clearTimer = spawnDelay / 2;

				battleTimer = spawnDelay;
			}
		}
		if (clearTimer > 0) {
			clearTimer -= dtime;
			if (clearTimer <= 0) {
				CopyOnWriteArrayList<GameObject> objs = getObjects();
				Iterator<GameObject> it = objs.iterator();
				while (it.hasNext()) {
					GameObject obj = it.next();
					if (!(obj instanceof Player)) {
						obj.remove();
					} else {
						Player p = (Player) obj;
						p.setTeam(null);
						p.setSpawned(false);
						p.setVisible(false);
						p.setParent(null);
					}
				}
				loadMap();
			}
		}
		if (battleTimer >= 0) {
			battleTimer -= dtime;
		}
	}

	@Override
	public void init() {
		setPhysicsHandler(new PhysicsHandler());

		loadMap();

	}

	public void loadMap() {
		GameMap map = new Finland();
		map.load(this);
	}

	@Override
	public void cleanUp() {
	}
}
