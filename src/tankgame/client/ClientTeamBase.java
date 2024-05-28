/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tankgame.client;

import blackhole.client.game.ClientObject;
import blackhole.common.GameObjectUpdateStrategy;

/**
 *
 * @author fabian
 */
public class ClientTeamBase extends ClientObject {
	private double captureProgress;
	
	private ClientTeam team;
	
	private String name;
	
	public ClientTeamBase() {
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
			ClientTeamManager stm = ClientTeamManager.getCurrentTeamManager();
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
	}
	
	public void setCaptureProgress(double progress) {
		captureProgress = progress;
	}

	public double getCaptureProgress() {
		return captureProgress;
	}

	public void setTeam(ClientTeam t) {
		team = t;
	}

	public ClientTeam getTeam() {
		return team;
	}

	public String getName() {
		return name;
	}

	@Override
	public void init() {
		super.init();
		ClientBaseManager.getCurrentBaseMgr().addBase(this);
	}
	
	@Override
	public void remove() {
		super.remove();
		ClientBaseManager.getCurrentBaseMgr().removeBase(this);
	}
}
