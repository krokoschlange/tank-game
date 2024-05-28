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
package tankgame.server.vehicles;

import blackhole.common.GameObjectUpdateStrategy;
import blackhole.server.physicsEngine.boundingAreas.Polygon;
import blackhole.server.physicsEngine.core.PhysicsStrategy;
import blackhole.utils.Debug;
import blackhole.utils.Vector;
import java.util.ArrayList;
import tankgame.client.vehicles.Tank_BT42_Client;
import tankgame.client.vehicles.Turret_BT42_Client;
import tankgame.server.Damageable;
import tankgame.server.Player;
import tankgame.server.vehicles.shooting.APBullet;
import tankgame.server.vehicles.shooting.ServerGun;

/**
 *
 * @author fabian
 */
public class Tank_BT42 extends Tank {

	private BT42_Turret turret;
	private ServerGun gun;

	private double leftSteeringLever;
	private double rightSteeringLever;

	private boolean shifting;

	private boolean tankOnFire;

	private HPModule engineModule;
	
	private ArrayList<Damageable> bulletFlyThrough;

	public Tank_BT42() {
		sidewaysTrackKineticFriction = 0.15;

		rollingFriction = 0.02;

		leftForcePoint = new Vector(-1.1, 0);
		rightForcePoint = new Vector(1.1, 0);

		frictionPoints = new ArrayList<>();
		oldFrictionPoints = new ArrayList<>();

		frictionPoints.add(new Vector(-1.1, 1));
		frictionPoints.add(new Vector(1.1, 1));
		frictionPoints.add(new Vector(-1.1, -1));
		frictionPoints.add(new Vector(1.1, -1));
		oldFrictionPoints.add(new Vector(-1.1, 1));
		oldFrictionPoints.add(new Vector(1.1, 1));
		oldFrictionPoints.add(new Vector(-1.1, -1));
		oldFrictionPoints.add(new Vector(1.1, -1));

		PhysicsStrategy pStrat = getPhysicsStrategy();

		pStrat.setMass(15000);
		pStrat.setMomentOfInertia((1. / 12) * pStrat.getMass() * (5.66 * 5.66 + 2.29 * 2.29));

		engine = new Engine(330000, 200, 50, 400);
		engine.start();

		transmission = new ClutchTurningTransmission(engine, 1800, new double[]{-17, 10, 6, 4});

		brakeForce = 70000;
		brakeRadius = 0.15;
		brakeFriction = 0.4;

		wheelRadius = 0.2;

		Vector[] p = new Vector[]{
			new Vector(1.1, 3.1),
			new Vector(1, 3.1),
			new Vector(1, -2.3),
			new Vector(1.1, -2.3)
		};
		addModule(new HPModule(this, 700, new Polygon(p, null), 10, true, 1, 0.31, 1.6, 0));
		p = new Vector[]{
			new Vector(-1, 3.1),
			new Vector(-1.1, 3.1),
			new Vector(-1.1, -2.3),
			new Vector(-1, -2.3)
		};
		addModule(new HPModule(this, 700, new Polygon(p, null), 10, true, 1, 0.31, 1.6, 0));
		p = new Vector[]{
			new Vector(-1.1, 3.1),
			new Vector(-1.1, 3),
			new Vector(1.1, 3),
			new Vector(1.1, 3.1)
		};
		addModule(new HPModule(this, 700, new Polygon(p, null), 10, true, 1, 0.31, 1.6, 0));
		p = new Vector[]{
			new Vector(-1.1, -2.2),
			new Vector(-1.1, -2.3),
			new Vector(1.1, -2.3),
			new Vector(1.1, -2.2)
		};
		addModule(new HPModule(this, 700, new Polygon(p, null), 10, true, 1, 0.31, 1.6, 0));

		p = new Vector[]{
			new Vector(-0.5, -1.8),
			new Vector(0.5, -1.8),
			new Vector(0.5, -0.5),
			new Vector(-0.5, -0.5)
		};
		addModule(engineModule = new HPModule(this, 1500, new Polygon(p, null), 0.5, false, 0, 0.35, 1.5, 0.15));
	}

	public void setTankOnFire(boolean state) {
		if (state != tankOnFire) {
			tankOnFire = state;
			addToUpdate("fire");
		}
	}

	@Override
	public void step(double dtime) {
		super.step(dtime);
		if (getDriver() != null) {
			double dx = getDriver().getClient().getCameraPosition().x() - turret.getRealPosition().x();
			double dy = getDriver().getClient().getCameraPosition().y() - turret.getRealPosition().y();

			turret.setTargetRot(Math.atan2(-dx, dy) - getRotation());

			if (getDriver().getClient().getActiveControls().contains("fire")) {
				gun.shoot();
			}
		}
		addToUpdate("engineRPM");
	}

	@Override
	public void steeringInput(double dtime) {

		ClutchTurningTransmission trans = (ClutchTurningTransmission) getTransmission();
		if (getDriver() != null) {
			if (getDriver().getClient().isControlActive("fwd")) {
				double throttle = engine.getThrottle();
				throttle += 3 * dtime;
				throttle = Math.min(throttle, 1);
				engine.setThrottle(throttle);
			} else {
				double throttle = engine.getThrottle();
				throttle -= 2 * dtime;
				throttle = Math.max(throttle, 0);
				engine.setThrottle(throttle);
			}
			if (getDriver().getClient().isControlActive("bwd")) {
				double throttle = engine.getThrottle();
				throttle -= 3 * dtime;
				throttle = Math.max(throttle, 0);
				engine.setThrottle(throttle);
			}
			if (getDriver().getClient().isControlActive("lft")) {
				leftSteeringLever += dtime;
				leftSteeringLever = Math.min(leftSteeringLever, 1);
			} else {
				leftSteeringLever -= dtime;
				leftSteeringLever = Math.max(leftSteeringLever, 0);
			}
			if (leftSteeringLever > 0) {
				trans.setLeftClutch(1 - Math.min(leftSteeringLever / 0.3, 1));
				if (leftSteeringLever > 0.3) {
					leftBrake = (leftSteeringLever - 0.3) / 0.7;
				}
			} else {
				trans.setLeftClutch(1);
			}
			if (getDriver().getClient().isControlActive("rht")) {
				rightSteeringLever += dtime;
				rightSteeringLever = Math.min(rightSteeringLever, 1);
			} else {
				rightSteeringLever -= dtime;
				rightSteeringLever = Math.max(rightSteeringLever, 0);
			}
			if (rightSteeringLever > 0) {
				trans.setRightClutch(1 - Math.min(rightSteeringLever / 0.3, 1));
				if (rightSteeringLever > 0.3) {
					rightBrake = (rightSteeringLever - 0.3) / 0.7;
				}
			} else {
				trans.setRightClutch(1);
			}
			if (getDriver().getClient().isControlActive("shift_up") && !shifting) {
				shifting = true;
				trans.setGear(trans.getGear() + 1);
				addToUpdate("gear");
			} else if (getDriver().getClient().isControlActive("shift_down") && !shifting) {
				shifting = true;
				trans.setGear(trans.getGear() - 1);
				addToUpdate("gear");
			} else {
				shifting = false;
			}
			if (getDriver().getClient().isControlActive("start")) {
				engine.start();
			}
		} else {
			double throttle = engine.getThrottle();
			throttle -= 0.5;
			throttle = Math.max(throttle, 0);
			engine.setThrottle(throttle);

			leftSteeringLever -= dtime;
			leftSteeringLever = Math.max(leftSteeringLever, 0);

			rightSteeringLever -= dtime;
			rightSteeringLever = Math.max(rightSteeringLever, 0);
		}
	}

	@Override
	public ArrayList<HPModule> getInternalModules() {
		ArrayList<HPModule> mods = new ArrayList<>();
		mods.addAll(getModules());
		if (getDriver() != null) {
			mods.addAll(getDriver().getModules());
		}
		mods.addAll(turret.getModules());
		return mods;
	}

	@Override
	public void setDriver(Player d) {
		bulletFlyThrough.remove(getDriver());
		super.setDriver(d);
		if (d != null) {
			d.setPosition(0, 0.7);
			bulletFlyThrough.add(d);
		}
	}

	class BT42_Turret extends Turret implements Damageable {

		private ArrayList<HPModule> modules;

		private HPModule turretRing;
		private HPModule gunModule;

		@Override
		public void init() {
			setClientObjectClass(Turret_BT42_Client.class);
			setVisible(true);

			setPosition(0, 1.3);

			modules = new ArrayList<>();

			Vector[] p = new Vector[]{
				new Vector(0.7, 1),
				new Vector(0.6, 1),
				new Vector(0.6, -1),
				new Vector(0.7, -1)
			};
			modules.add(new HPModule(this, 700, new Polygon(p, null), 10, true, 1, 1.6, 2.7, 0));
			p = new Vector[]{
				new Vector(-0.6, 1),
				new Vector(-0.7, 1),
				new Vector(-0.7, -1),
				new Vector(-0.6, -1)
			};
			modules.add(new HPModule(this, 700, new Polygon(p, null), 10, true, 1, 1.6, 2.7, 0));
			p = new Vector[]{
				new Vector(-0.7, 1),
				new Vector(-0.7, 0.9),
				new Vector(0.7, 0.9),
				new Vector(0.7, 1)
			};
			modules.add(new HPModule(this, 700, new Polygon(p, null), 10, true, 1, 1.6, 2.7, 0));
			p = new Vector[]{
				new Vector(-0.7, -0.9),
				new Vector(-0.7, -1),
				new Vector(0.7, -1),
				new Vector(0.7, -0.9)
			};
			modules.add(new HPModule(this, 700, new Polygon(p, null), 10, true, 1, 1.6, 2.7, 0));

			p = new Vector[]{
				new Vector(-0.7, 0.7),
				new Vector(-0.7, -0.7),
				new Vector(0.7, -0.7),
				new Vector(0.7, 0.7)
			};
			turretRing = new HPModule(this, 500, new Polygon(p, null), 10, true, 1, 1.6, 1.65, 0);
			modules.add(turretRing);
		}

		@Override
		public void step(double dtime) {
			super.step(dtime);
			for (int i = 0; i < modules.size(); i++) {
				modules.get(i).step(dtime);
			}
		}

		@Override
		public ArrayList<HPModule> getModules() {
			return modules;
		}

		@Override
		public ArrayList<HPModule> getInternalModules() {
			ArrayList<HPModule> mods = new ArrayList<>();
			mods.addAll(Tank_BT42.this.modules);
			if (getDriver() != null) {
				mods.addAll(getDriver().getModules());
			}
			mods.addAll(turret.getModules());
			return mods;
		}

		@Override
		public void moduleHitpointCallback(HPModule module, int delta) {
			Debug.log("TURRET HP CHANGE: " + delta + ": " + module + " (" + module.getHitpoints() + " HP)");
			if (module == turretRing) {
				setRotationSpeed(0.3 * (turretRing.getHitpoints() / 500));
			}
			if (module == gunModule) {
				//Tank_BT42.this.gun.setReload(15 + 500);
			}
		}
	}

	@Override
	public void init() {
		setScale(0.077, 0.077);
		setVisible(true);

		setInterpolate(true);

		setClientObjectClass(Tank_BT42_Client.class);
		;
		turret = new BT42_Turret();
		turret.setParent(this);
		turret.setHandler(getHandler());
		turret.activate();

		getPhysicsStrategy().setPhysicsHandler(getHandler().getPhysicsHandler());
		Vector[] p = new Vector[]{
			new Vector(1.1, 3.1),
			new Vector(-1.1, 3.1),
			new Vector(-1.1, -2.3),
			new Vector(1.1, -2.3)
		};
		getPhysicsStrategy().addCollisionbox(new Polygon(p, getPhysicsStrategy()));
		getPhysicsStrategy().setFriction(1);

		bulletFlyThrough = new ArrayList<>();
		bulletFlyThrough.add(this);
		bulletFlyThrough.add(turret);
		gun = new ServerGun(new Class[]{HE_4_5_Bullet.class}, new Object[][]{{bulletFlyThrough}}, new double[]{310}, 1);
		gun.setParent(turret);
		gun.setHeight(2);
		gun.setHandler(getHandler());
		gun.activate();

		GameObjectUpdateStrategy strat = getDefaultUpdateStrategy();
		strat.addParameter("gunID", (val) -> {
		}, () -> {
			return gun.getID();
		});

		strat.addParameter("engineRPM", (val) -> {
		}, () -> {
			return engine.isRunning() ? engine.getSpeed() : 0;
		});
		strat.addParameter("gear", (val) -> {
		}, () -> {
			ClutchTurningTransmission trans = (ClutchTurningTransmission) getTransmission();
			return trans.getGear();
		});
		strat.addParameter("fire", (val) -> {
		}, () -> {
			return tankOnFire;
		});

		addToUpdate("gunID");
	}

	@Override
	public void moduleHitpointCallback(HPModule module, int change) {
		Debug.log("HP CHANGE: " + change + ": " + module + " (" + module.getHitpoints() + " HP)");
		if (module == engineModule) {
			Debug.log("Engine hit");
			engine.setMaxPower(1. * engineModule.getHitpoints() / 1200 * 330000);
			setTankOnFire(engineModule.isOnFire());
		}
	}

	public static class HE_4_5_Bullet extends APBullet {

		public HE_4_5_Bullet(ArrayList<Damageable> ft) {
			super(10, 400, 2, 3, 0, 10, 0.5, 5, 0.004);
			flyThrough.addAll(ft);
		}

		@Override
		public void step(double dtime) {
			super.step(dtime);

		}

		@Override
		public void init() {
			getVisualStrategy().addTexture("/res/QF45H_HE.png");
			setVisible(true);
			setTracerColor(1, 1, 1, 0.3f);
			setTracerDuration(1);

			super.init();
			setScale(0.057, 0.057);
		}
	}

	@Override
	public void remove() {
		turret.remove();
		gun.remove();
		super.remove(); //To change body of generated methods, choose Tools | Templates.
	}

}
