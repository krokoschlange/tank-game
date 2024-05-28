/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tankgame.server;

import blackhole.utils.Debug;
import tankgame.client.*;
import java.util.ArrayList;
import tankgame.client.ui.UI;
import tankgame.client.ui.UIHandler;
import tankgame.common.TeamManager;

/**
 *
 * @author fabian
 */
public class ServerTeamManager extends TeamManager<ServerTeam> {

	private static ServerTeamManager currentTeamManager = null;

	private ArrayList<TeamBase> bases;

	public ServerTeamManager() {
		currentTeamManager = this;
		bases = new ArrayList<>();
	}

	public static ServerTeamManager getCurrentTeamManager() {
		return currentTeamManager;
	}

	public void addBase(TeamBase base) {
		bases.add(base);
	}

	public void removeBase(TeamBase base) {
		bases.remove(base);
	}

	public ArrayList<TeamBase> getBases() {
		return bases;
	}

	public ServerTeam getWinner() {
		ServerTeam winner = null;
		for (int i = 0; i < bases.size(); i++) {
			ServerTeam team = bases.get(i).getTeam();
			if (team == null) {
				return null;
			}
			if (bases.get(i).getCaptureProgress() < 1) {
				return null;
			}
			if (winner == null) {
				winner = team;
			}
			if (winner != team) {
				return null;
			}
		}
		return winner;
	}
}
