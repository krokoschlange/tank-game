/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tankgame.server;

import blackhole.common.GameObject;
import blackhole.server.game.ServerObject;
import blackhole.utils.Debug;
import blackhole.utils.Vector;
import tankgame.server.vehicles.Vehicle;
import java.util.HashMap;

/**
 *
 * @author fabian
 */
public class VehicleSpawner extends ServerObject {

	private HashMap<String, Class<? extends GameObject>> teamVehicleMap;

	private TeamBase base;

	private double spawnTime;
	private double timer;

	public VehicleSpawner() {
		teamVehicleMap = new HashMap<>();
	}

	public void setBase(TeamBase b) {
		base = b;
	}

	public TeamBase getBase() {
		return base;
	}

	public void setSpawnTime(double time) {
		spawnTime = time;
	}

	public double getSpawnTime() {
		return spawnTime;
	}

	public void setTeamVehicleMap(HashMap<String, Class<? extends GameObject>> map) {
		teamVehicleMap = map;
	}

	public HashMap<String, Class<? extends GameObject>> getTeamVehicleMap() {
		return teamVehicleMap;
	}

	@Override
	public void step(double dtime) {
		timer -= dtime;

		if (timer <= 0 && base != null && base.getTeam() != null
				&& base.getCaptureProgress() >= 1) {
			boolean canSpawn = true;
			GameObject[] objs = getHandler().getObjectsInRadius(getPosition(), 5);
			for (int i = 0; i < objs.length && canSpawn; i++) {
				if (objs[i] instanceof Vehicle) {
					canSpawn = false;
				}
			}
			if (canSpawn) {
				try {
					
					GameObject obj = teamVehicleMap.get(base.getTeam().getName()).newInstance();
					obj.setHandler(getHandler());
					
					obj.activate();
					obj.setPosition(new Vector(getPosition()));
					obj.setRotation(getRotation());
					timer = spawnTime;
				} catch (IllegalAccessException | InstantiationException e) {
					
				}
			}
		}
	}

	@Override
	public void init() {
		setServerOnly(true);
	}
}
