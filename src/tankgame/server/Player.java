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

import blackhole.client.game.input.InputEvent;
import blackhole.client.game.input.InputEventListener;
import blackhole.common.GameObject;
import blackhole.common.GameObjectUpdateStrategy;
import tankgame.server.vehicles.Vehicle;

import blackhole.server.game.Client;
import blackhole.server.game.ServerObject;
import blackhole.server.game.TextureVisualStrategy;
import blackhole.server.physicsEngine.boundingAreas.Circle;
import blackhole.server.physicsEngine.boundingAreas.Polygon;
import blackhole.server.physicsEngine.core.PhysicsStrategy;
import blackhole.utils.Debug;
import blackhole.utils.Vector;
import java.util.ArrayList;
import tankgame.client.ClientPlayer;
import tankgame.client.PlayerClientObject;
import tankgame.server.vehicles.HPModule;

/**
 *
 * @author fabian
 */
public class Player extends Fake3DGameObject implements Damageable {

	private final Client client;

	private ArrayList<HPModule> modules;
	private HPModule module;

	private InputEventListener inputListener;
	private boolean enterEvent;

	private ServerTeam team;

	private boolean isSpawned;
	private int selectedSpawnPos;

	private double spawnTimer;

	public Player(Client c) {
		client = c;

		modules = new ArrayList<>();

		Vector[] p = new Vector[]{
			new Vector(0, .18),
			new Vector(-.13, .13),
			new Vector(-.18, 0),
			new Vector(-.13, -.13),
			new Vector(0, -.18),
			new Vector(.13, -.13),
			new Vector(.18, 0),
			new Vector(.13, .13),};
		module = new HPModule(this, 100, new Polygon(p, null), 0, false, 0, 0, 1.8, 0);
		modules.add(module);
		setInterpolate(true);

		inputListener = (event) -> {
			if (event.isType(InputEvent.CONTROL) && event.controls[0].equals("enter") && event.active) {
				enterEvent = true;
			}
		};
		client.addEventListener(inputListener);

		GameObjectUpdateStrategy ustrat = getDefaultUpdateStrategy();
		ustrat.addParameter("team", (t) -> {
			ServerTeamManager manager = ServerTeamManager.getCurrentTeamManager();
			Debug.log("srvr" + (String) t);
			if (manager != null) {
				team = manager.getTeamByName((String) t);
				addToUpdate("selectSpawn");
			}
		}, () -> {
			if (team != null) {
				return team.getName();
			}
			return null;
		});
		ustrat.addParameter("isSpawned", (s) -> {
			boolean spawned = (boolean) s;
			if (spawned && !isSpawned) {
				spawn();
			} else {
				isSpawned = false;
			}
		}, () -> {
			return isSpawned();
		});
		ustrat.addParameter("spawnPos", (s) -> {
			Debug.log("srvr" + (int) s);
			selectedSpawnPos = (int) s;
		}, () -> {
			return null;
		});

		ustrat.addParameter("spawnTimer", (s) -> {
		}, () -> {
			return spawnTimer;
		});
		ustrat.addParameter("waitForSpawn", (s) -> {
		}, () -> {
			return null;
		}, false);
		ustrat.addParameter("selectTeam", (s) -> {
		}, () -> {
			return null;
		}, false);
		ustrat.addParameter("selectSpawn", (s) -> {
		}, () -> {
			return null;
		}, false);
		ustrat.addParameter("hp", (s) -> {
			module.setHitpoints((int) (100 * (double) s));
		}, () -> {
			return module.getHitpoints() / 100d;
		});

		isSpawned = false;
	}

	public Client getClient() {
		return client;
	}

	@Override
	public Class<?> getClientObjectClass(Client c) {
		if (c == client) {
			return PlayerClientObject.class;
		}
		return ClientPlayer.class;
	}

	public void spawn() {
		setPosition(new Vector(team.getSpawnPos(selectedSpawnPos)));
		modules.get(0).setHitpoints(100);
		modules.get(0).stopFire();
		isSpawned = true;
		setVisible(true);
		Debug.log("Spawned");
		getPhysicsStrategy().setPhysicsHandler(getHandler().getPhysicsHandler());
		getPhysicsStrategy().activate(this);
	}

	public void setSpawned(boolean state) {
		isSpawned = state;
		addToUpdate("isSpawned");
	}

	public boolean isSpawned() {
		return isSpawned;
	}

	public void setTeam(ServerTeam t) {
		team = t;
	}

	public ServerTeam getTeam() {
		return team;
	}

	public void setSpawnTimer(double t) {
		this.spawnTimer = t;
		addToUpdate("spawnTimer");
	}

	public double getSpawnTimer() {
		return spawnTimer;
	}

	@Override
	public void step(double dtime) {
		if (getSpawnTimer() > 0) {
			setSpawnTimer(getSpawnTimer() - dtime);
			if (getSpawnTimer() <= 0) {
				addToUpdate("selectTeam");
			}
		}

		for (int i = 0; i < modules.size(); i++) {
			modules.get(i).step(dtime);
		}

		if (getParent() == null) {
			double dx = client.getCameraPosition().x() - getPosition().x();
			double dy = client.getCameraPosition().y() - getPosition().y();

			setRotation(Math.atan2(-dx, dy));
			setAngularVelocity(0);
			if (client.isControlActive("fwd")) {
				setVelocity(new Vector(0, 1.6).rotate(getRotation()));
			} else {
				setVelocity(new Vector(0, 0));
			}
		}
		if (enterEvent) {
			if (getParent() == null) {
				GameObject[] objs = getHandler().getObjectsInRadius(getPosition(), 2);
				for (int i = 0; i < objs.length; i++) {
					if (objs[i] instanceof Vehicle) {
						Vehicle vehicle = (Vehicle) objs[i];
						if (vehicle.getDriver() == null) {
							setVelocity(new Vector(0, 0));
							setPosition(0, 0);
							vehicle.setDriver(this);
							setParent(vehicle);
							setVisible(false);
							getPhysicsStrategy().remove();
							break;
						}
					}
				}
			} else {
				Vehicle vehicle = (Vehicle) getParent();
				vehicle.setDriver(null);
				setParent(null);
				setPosition(new Vector(vehicle.getPosition()));
				setVisible(true);
				getPhysicsStrategy().setPhysicsHandler(getHandler().getPhysicsHandler());
				getPhysicsStrategy().activate(this);
			}
			enterEvent = false;
		}

		/*    if (client.isControlActive("enter")) {
                GameObject[] objs = getHandler().getObjectsInRadius(getPosition(), 2);
                for (int i = 0; i < objs.length && !entering; i++) {
                    if (objs[i] instanceof Vehicle) {
                        Vehicle vehicle = (Vehicle) objs[i];
                        if (vehicle.getDriver() == null) {
                            setVelocity(new Vector(0, 0));
                            vehicle.setDriver(this);
                            setParent(vehicle.getID());
                            setPosition(0, 0);
                            setVisible(false);
                            getPhysicsStrategy().remove();
                            entering = true;
                        }
                    }
                }
            } else {
                entering = false;
            }
        } else {

            if (client.isControlActive("enter")) {
                if (!entering) {
                    Vehicle vehicle = (Vehicle) getHandler().getObjectByID(getParent());
                    vehicle.setDriver(null);
                    setParent(null);
                    setPosition(new Vector(vehicle.getPosition()));
                    setVisible(true);
                    getPhysicsStrategy().setPhysicsHandler(getHandler().getPhysicsHandler());
                    getPhysicsStrategy().activate(this);
                    Debug.log(getPhysicsStrategy().getCollisionboxes().get(0).getPosition());
                    entering = true;
                }

            } else {
                entering = false;
            }
        }*/
	}

	@Override
	public void init() {
		PhysicsStrategy pStrat = new PhysicsStrategy();

		setPhysicsStrategy(pStrat);
		pStrat.setMass(80);
		pStrat.addCollisionbox(new Circle(.18, pStrat));

		//setVisible(true);
		setScale(0.05, 0.05);
	}

	@Override
	public void remove() {
		if (getParent() != null) {
			GameObject obj = getParent();
			if (obj instanceof Vehicle) {
				((Vehicle) obj).removePlayerFromVehicle(this);
			}
		}
		super.remove();
	}

	@Override
	public ArrayList<HPModule> getModules() {
		return modules;
	}

	@Override
	public ArrayList<HPModule> getInternalModules() {
		return getModules();
	}

	@Override
	public void moduleHitpointCallback(HPModule module, int delta) {
		addToUpdate("hp");
		if (module.getHitpoints() < 0 && isSpawned()) {
			die();
		}
	}

	public void die() {
		setVisible(false);
		if (getParent() != null && getParent() instanceof Vehicle) {
			((Vehicle) getParent()).removePlayerFromVehicle(this);
		}
		setParent(null);
		isSpawned = false;
		addToUpdate("isSpawned");
		addToUpdate("selectSpawn");
	}
}
