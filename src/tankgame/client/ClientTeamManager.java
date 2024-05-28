/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tankgame.client;

import blackhole.utils.Vector;
import java.awt.Color;
import java.util.ArrayList;
import tankgame.client.ui.Button;
import tankgame.client.ui.Group;
import tankgame.client.ui.Theme;
import tankgame.client.ui.UI;
import tankgame.common.TeamManager;

/**
 *
 * @author fabian
 */
public class ClientTeamManager extends TeamManager<ClientTeam> {

	private static ClientTeamManager currentTeamManager = null;

	private UI selectionUI;
	private PlayerClientObject pco;

	public ClientTeamManager() {
		currentTeamManager = this;
		selectionUI = null;
		pco = null;
	}

	public static ClientTeamManager getCurrentTeamManager() {
		return currentTeamManager;
	}
	
	public PlayerClientObject getPlayerClientObject() {
		return pco;
	}

	public UI createTeamSelectionUI(PlayerClientObject player) {
		if (player != null) {
			pco = player;
		}
		
		ArrayList<ClientTeam> teams = getAllTeams();

		Theme transparent = new Theme(new Color(0, 0, 0, 0), Color.gray,
				Color.cyan, Color.black, Color.black);

		if (selectionUI == null) {
			selectionUI = new UI();
		} else {
			selectionUI.clear();
		}

		selectionUI.setEatAllEvents(true);
		selectionUI.setDrawPosition(10);
		selectionUI.setTheme(transparent);

		selectionUI.configureColumn(0, 1, 4);
		
		selectionUI.configureRow(0, 0.45, 0);
		
		selectionUI.addRow(0.1, 0);
		selectionUI.addRow(0.45, 0);

		int teamAmount = teams.size();

		for (int i = 1; i < teamAmount; i++) {
			selectionUI.addColumn(1, 4);
		}

		for (int i = 0; i < teamAmount; i++) {
			ClientTeam team = teams.get(i);

			Button btn = new Button(selectionUI);
			btn.setText(team.getName());
			btn.setSize(new Vector(1, 0.1));

			btn.setReleaseEvent(() -> {
				pco.setTeam(team);
				pco.addToUpdate("team");
				TankGameClient.getInstance().deactivateUI(selectionUI);
			});
			btn.setEnterEvent(() -> {
				if (team.getSpawnPos(0) != null) {
					pco.setPosition(team.getSpawnPos(0));
				}
			});

			selectionUI.addChild(btn, 1, i, 1, 1, Group.E | Group.W | Group.N);
			
		}

		return selectionUI;
	}
}
