/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tankgame.server;

import blackhole.common.GameObjectUpdateStrategy;
import blackhole.server.game.ServerObject;
import blackhole.utils.Debug;
import blackhole.utils.Vector;
import java.util.ArrayList;
import tankgame.client.ClientTeamBase;

/**
 *
 * @author fabian
 */
public class TeamBase extends ServerObject {

	private ServerTeam team;

	private double radius;

	private String name;

	private double captureProgress;

	private Vector spawnOffset;

	/**
	 * capture progress per second per person
	 */
	private double captureSpeed;

	public TeamBase(double rad, double speed, String n) {
		GameObjectUpdateStrategy ustrat = getDefaultUpdateStrategy();
		ustrat.addParameter("progress", (p) -> {
			captureProgress = (double) p;
		}, () -> {
			return captureProgress;
		});
		ustrat.addParameter("name", (p) -> {
			name = (String) p;
		}, () -> {
			return name;
		});
		ustrat.addParameter("team", (t) -> {
			ServerTeamManager stm = ServerTeamManager.getCurrentTeamManager();
			if (stm.getTeamByName((String) t) != null) {
				team = stm.getTeamByName((String) t);
			}
		}, () -> {
			if (team != null) {
				return team.getName();
			} else {
				return null;
			}
		});
		setClientObjectClass(ClientTeamBase.class);
		setAlwaysLoaded(true);
		radius = rad;
		captureSpeed = speed;
		name = n;
		spawnOffset = new Vector();
	}

	public void setCaptureProgress(double progress) {
		captureProgress = progress;
		if (team != null && captureProgress >= 1) {
			team.addSpawnPosition(Vector.add(getPosition(), spawnOffset));
		} else if (team != null && captureProgress < 1) {
			team.removeSpawnPosition(Vector.add(getPosition(), spawnOffset));
		}
		addToUpdate("progress");
	}

	public double getCaptureProgress() {
		return captureProgress;
	}

	public void setTeam(ServerTeam t) {
		if (team != null) {
			team.removeSpawnPosition(Vector.add(getPosition(), spawnOffset));
		}
		team = t;
		if (team != null && captureProgress >= 1) {
			team.addSpawnPosition(Vector.add(getPosition(), spawnOffset));
		}
		addToUpdate("team");
	}

	public ServerTeam getTeam() {
		return team;
	}

	public String getName() {
		return name;
	}

	public void setSpawnOffset(Vector off) {
		spawnOffset = off;
	}

	public Vector getSpawnOffset() {
		return spawnOffset;
	}

	@Override
	public void step(double dtime) {
		ArrayList<Player> players = ((GOHandler) getHandler()).getPlayers();

		ArrayList<Player> playersInZone = new ArrayList<>();

		for (int i = 0; i < players.size(); i++) {
			Player p = players.get(i);
			if (Vector.subtract(p.getRealPosition(), getRealPosition()).magnitude() < radius && p.isSpawned()) {
				playersInZone.add(p);
			}
		}

		ArrayList<ServerTeam> contestingTeams = new ArrayList<>();

		for (int i = 0; i < playersInZone.size(); i++) {
			Player p = playersInZone.get(i);
			if (!contestingTeams.contains(p.getTeam()) && p.getTeam() != null) {
				contestingTeams.add(p.getTeam());
			}
		}
		if (contestingTeams.size() > 1) {
			//nothing here (yet)(idk)
		} else if (contestingTeams.size() == 1) {
			if (getTeam() != contestingTeams.get(0)) {
				setCaptureProgress(0);
				setTeam(contestingTeams.get(0));
			} else if (getCaptureProgress() < 1) {
				setCaptureProgress(getCaptureProgress() + captureSpeed * dtime * playersInZone.size());
			}
		} else if (contestingTeams.isEmpty()) {
			if (getCaptureProgress() < 1 && getCaptureProgress() > 0) {
				setCaptureProgress(getCaptureProgress() - captureSpeed * dtime);
				if (getCaptureProgress() < 0) {
					setTeam(null);
				}
			}
		}
		if (getCaptureProgress() < 0) {
			setCaptureProgress(0);
		} else if (getCaptureProgress() > 1) {
			setCaptureProgress(1);
		}
	}

	@Override
	public void init() {
		ServerTeamManager.getCurrentTeamManager().addBase(this);
	}

	@Override
	public void remove() {
		ServerTeamManager.getCurrentTeamManager().removeBase(this);
		super.remove(); //To change body of generated methods, choose Tools | Templates.

	}

}
