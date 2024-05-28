/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tankgame.common;

import java.util.ArrayList;

/**
 *
 * @author fabian
 * @param <T>
 */
public abstract class TeamManager<T extends Team> {
	
	private ArrayList<T> teams;
		
	public TeamManager() {
		teams = new ArrayList<>();
	}
	
	public void addTeam(T team) {
		teams.add(team);
	}
	
	public void removeTeam(T team) {
		teams.remove(team);
	}
	
	public ArrayList<T> getAllTeams() {
		return teams;
	}
	public T getTeamByName(String name) {
		for (int i = 0; i < teams.size(); i++) {
			if (teams.get(i).getName().equals(name)) {
				return teams.get(i);
			}
		}
		return null;
	}
}
