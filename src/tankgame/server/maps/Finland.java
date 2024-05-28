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
package tankgame.server.maps;

import blackhole.common.GameObject;
import blackhole.common.ObjectHandler;
import blackhole.server.game.ServerObject;
import blackhole.server.physicsEngine.boundingAreas.Circle;
import blackhole.server.physicsEngine.boundingAreas.Polygon;
import blackhole.server.physicsEngine.collision.Contact;
import blackhole.server.physicsEngine.core.Force;
import blackhole.server.physicsEngine.core.PhysicsStrategy;
import blackhole.utils.Debug;
import blackhole.utils.Vector;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import tankgame.client.Fake3DShadowData;
import tankgame.client.maps.ClientBaseBuilding;
import tankgame.client.maps.ClientRock;
import tankgame.client.maps.ClientTree;
import tankgame.client.maps.FinlandGround;
import tankgame.server.Damageable;
import tankgame.server.Fake3DGameObject;
import tankgame.server.Fake3DVisualStrategy;
import tankgame.server.ServerTeam;
import tankgame.server.ServerTeamManager;
import tankgame.server.TeamBase;
import tankgame.server.VehicleSpawner;
import tankgame.server.vehicles.HPModule;
import tankgame.server.vehicles.Tank_BT42;
import tankgame.server.vehicles.Tank_BT7;

/**
 *
 * @author fabian.baer2
 */
public class Finland extends GameMap {
	
	private Polygon createRegularPolygon(int p, double r) {
		Vector v = new Vector(0, r);
		Vector[] pol = new Vector[p];
		for (int i = 0; i < p; i++) {
			pol[i] = new Vector(v.rotate(2 * Math.PI / p));
		}
		return new Polygon(pol, null);
	}

	private void addTree(ObjectHandler handler, double x, double y, double r, double h) {
		Tree tree = new Tree();
		tree.setPosition(x, y);
		tree.setRotation(r);
		tree.setHeight(h);
		tree.setHandler(handler);
		tree.activate();
	}
	
	/*private void addRock(ObjectHandler handler, double x, double y, double r, double h) {
		Rock rock = new Rock();
		rock.setPosition(x, y);
		rock.setRotation(r);
		rock.setHeight(h);
		rock.setHandler(handler);
		rock.activate();
	}*/

	private class Tree extends Fake3DGameObject {
		
		public Tree() {
			setClientObjectClass(ClientTree.class);
		}

		@Override
		public void step(double dtime) {
			//setRotation(getRotation() + 0.1 * dtime);
			if (getVelocity().magnitude() > 0.01) {
				PhysicsStrategy pS = getPhysicsStrategy();
				pS.applyForce(new Force(Vector.multiply(Vector.divide(getVelocity(), -dtime), pS.getMass()),
						new Vector()));
			}
		}

		@Override
		public void init() {

			setDrawPosition(0);
			setVisible(true);
			setInterpolate(true);
			

			PhysicsStrategy pS = new PhysicsStrategy();
			setPhysicsStrategy(pS);
			pS.addCollisionbox(new Circle(0.5, pS));
			pS.setMass(1e20);
			pS.setMomentOfInertia(1e20);
			pS.setFriction(1);
		}
	}

	private class BaseBuilding extends Fake3DGameObject implements Damageable {

		private ArrayList<HPModule> modules;

		public BaseBuilding() {
			setClientObjectClass(ClientBaseBuilding.class);
		}
		
		@Override
		public void step(double dtime) {
			if (getVelocity().magnitude() > 0.01) {
				PhysicsStrategy pS = getPhysicsStrategy();
				pS.applyForce(new Force(Vector.multiply(Vector.divide(getVelocity(), -dtime), pS.getMass()),
						new Vector()));
			}
			for (int i = 0; i < modules.size(); i++) {
				modules.get(i).step(dtime);
			}
		}

		@Override
		public void init() {
			setDrawPosition(0);
			setVisible(true);
			setInterpolate(true);
			

			PhysicsStrategy pS = new PhysicsStrategy();
			setPhysicsStrategy(pS);

			Vector[] p1 = {new Vector(-3.46, 4.57), new Vector(-3.46, -1.72),
				new Vector(1.35, -1.72), new Vector(1.35, 4.57)};
			Vector[] p2 = {new Vector(1.35, 4.57), new Vector(1.35, 0.72),
				new Vector(4.17, 0.72), new Vector(4.17, 4.57)};

			pS.addCollisionbox(new Polygon(p1, pS));
			pS.addCollisionbox(new Polygon(p2, pS));
			pS.setMass(1e20);
			pS.setMomentOfInertia(1e20);
			pS.setFriction(1);

			modules = new ArrayList<>();
			modules.add(new HPModule(this, 0, new Polygon(p1, null), 1000, true, 0, 0, 3, 0));
			modules.add(new HPModule(this, 0, new Polygon(p2, null), 1000, true, 0, 0, 3, 0));
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
		}

	}
	
	/*private class Rock extends Fake3DGameObject implements Damageable {

		private ArrayList<HPModule> modules;

		public Rock() {
			setClientObjectClass(ClientRock.class);
		}
		
		@Override
		public void step(double dtime) {
			if (getVelocity().magnitude() > 0.01) {
				PhysicsStrategy pS = getPhysicsStrategy();
				pS.applyForce(new Force(Vector.multiply(Vector.divide(getVelocity(), -dtime), pS.getMass()),
						new Vector()));
			}
			for (int i = 0; i < modules.size(); i++) {
				modules.get(i).step(dtime);
			}
		}

		@Override
		public void init() {
			setDrawPosition(0);
			setVisible(true);
			setInterpolate(true);
			

			PhysicsStrategy pS = new PhysicsStrategy();
			pS.setStatic(true);
			setPhysicsStrategy(pS);

			Polygon p = createRegularPolygon(8, 34.5);
			p.setContainer(pS);

			pS.addCollisionbox(p);
			pS.setMass(1e20);
			pS.setMomentOfInertia(1e20);
			pS.setFriction(1);

			modules = new ArrayList<>();
			modules.add(new HPModule(this, 0, createRegularPolygon(8, 34.5), 1000, true, 0, 0, 4, 0));
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
		}
	}*/

	@Override
	public void load(ObjectHandler handler) {

		ServerTeamManager stm = new ServerTeamManager();

		ServerTeam finland = new ServerTeam("Finland", 0);

		//ArrayList<Vector> fSpawns = new ArrayList<>();
		//fSpawns.add(new Vector(10, 10));
		//fSpawns.add(new Vector(20, 10));
		//finland.setSpawnPositions(fSpawns);
		finland.setHandler(handler);
		finland.activate();

		ServerTeam theGloriousMotherland = new ServerTeam("USSR", 0);

		//ArrayList<Vector> mSpawns = new ArrayList<>();
		//mSpawns.add(new Vector(-5, 0));
		//theGloriousMotherland.setSpawnPositions(mSpawns);
		theGloriousMotherland.setHandler(handler);
		theGloriousMotherland.activate();

		ServerObject map = new ServerObject() {
			@Override
			public void step(double dtime) {
				//setRotation(getRotation() + 0.1 * dtime);
			}

			@Override
			public void init() {
				/*TextureVisualStrategy tStrat = new TextureVisualStrategy();
                setVisualStrategy(tStrat);
                addUpdateStrategy(tStrat);

                tStrat.setTexture("/res/landscape1_1024.png");
                setDrawPosition(-3);
                setVisible(true);
                setInterpolate(true);
                setScale(0.8, 0.8);*/
			}
		};
		//map.setPosition(20, -10);
		//map.setRotation(3);
		map.setClientObjectClass(FinlandGround.class);
		map.setAlwaysLoaded(true);
		map.setHandler(handler);
		map.activate();

		HashMap<String, Class<? extends GameObject>> spawnerMap = new HashMap<>();
		spawnerMap.put(finland.getName(), Tank_BT42.class);
		spawnerMap.put(theGloriousMotherland.getName(), Tank_BT7.class);

		BaseBuilding base;
		TeamBase b;
		VehicleSpawner spawner;

		base = new BaseBuilding();
		base.setPosition(195, 15);
		base.setHandler(handler);
		base.activate();

		b = new TeamBase(10, 0.033333, "A");
		b.setPosition(195, 15);
		b.setHandler(handler);
		b.setSpawnOffset(new Vector(3, -3));
		b.setCaptureProgress(1);
		b.setTeam(finland);
		b.activate();

		spawner = new VehicleSpawner();
		spawner.setPosition(201, 15);
		spawner.setTeamVehicleMap(spawnerMap);
		spawner.setSpawnTime(30);
		spawner.setBase(b);
		spawner.setHandler(handler);
		spawner.activate();

		base = new BaseBuilding();
		base.setPosition(75, -80);
		base.setHandler(handler);
		base.activate();

		b = new TeamBase(10, 0.033333, "B");
		b.setPosition(75, -80);
		b.setHandler(handler);
		b.activate();

		spawner = new VehicleSpawner();
		spawner.setPosition(81, -82);
		spawner.setTeamVehicleMap(spawnerMap);
		spawner.setSpawnTime(30);
		spawner.setBase(b);
		spawner.setHandler(handler);
		spawner.activate();

		base = new BaseBuilding();
		base.setPosition(168, -175);
		base.setHandler(handler);
		base.activate();

		b = new TeamBase(10, 0.033333, "C");
		b.setPosition(168, -175);
		b.setHandler(handler);
		b.setSpawnOffset(new Vector(3, -3));
		b.setCaptureProgress(1);
		b.setTeam(theGloriousMotherland);
		b.activate();

		spawner = new VehicleSpawner();
		spawner.setPosition(175, -177);
		spawner.setTeamVehicleMap(spawnerMap);
		spawner.setSpawnTime(30);
		spawner.setBase(b);
		spawner.setHandler(handler);
		spawner.activate();

		addTree(handler, -30, 7, 0, 0);
		addTree(handler, 20, -10, 3, 0);
		addTree(handler, 5, 7, 3, 0);
		addTree(handler, -30, -25, 3, 0);
		addTree(handler, -30, -40, 2, 0);
		addTree(handler, 25, -40, 1, 0);
		addTree(handler, -20, -25, 0, 0);
		addTree(handler, 0, -4, 3, 0);
		addTree(handler, 200, 33, 3, 0);
		addTree(handler, 211, 35, 3, 0);
		addTree(handler, 190, 40, 1, 0);
		addTree(handler, 208.2, 23.38, 4.15, 0);
		addTree(handler, 221.43, 32.22, 1.74, 0);
		addTree(handler, 235.88, 31.93, 1, 0);
		addTree(handler, 245.97, 20.2, 1.7, 0);
		addTree(handler, 248.38, 5.66, 5.55, 0);
		addTree(handler, 259.43, -14.08, 2.03, 0);
		addTree(handler, 268.68, -4.7, 5.44, 0);
		addTree(handler, 239.55, 17.14, 4.45, 0);
		addTree(handler, 217.2, 15.58, 0.35, 0);
		addTree(handler, 218.16, -0.11, 3.91, 0);
		addTree(handler, 196.21, -31.06, 2.16, 0);
		addTree(handler, 168.88, -49.32, 0.17, 0);
		addTree(handler, 145.72, -61.45, 1.39, 0);
		addTree(handler, 89.92, -71.14, 3.74, 0);
		addTree(handler, 52.61, -59.16, 2.79, 0);
		addTree(handler, 32.23, -52.39, 4.8, 0);
		addTree(handler, 14.29, -46.48, 6.16, 0);
		addTree(handler, -5.7, -34.77, 5.47, 0);
		addTree(handler, 1.75, -68.15, 5.9, 0);
		addTree(handler, 200.99, 4.66, 2.1, 0);
		addTree(handler, 190.05, 1.9, 2.65, 0);
		addTree(handler, 155.23, -12.71, 2.56, 0);
		addTree(handler, 131.11, -14.33, 2.53, 0);
		addTree(handler, 125.97, 27.53, 1.17, 0);
		addTree(handler, 111.54, 25.75, 0.22, 0);
		addTree(handler, 143.21, 22.8, 2.06, 0);
		addTree(handler, 169.45, 31.97, 6.07, 0);
		addTree(handler, 175.02, 21.28, 1.72, 0);
		addTree(handler, 88.08, 23.92, 6.2, 0);
		addTree(handler, 58.27, 34.12, 1.44, 0);
		addTree(handler, 44.22, 14.07, 1.85, 0);
		addTree(handler, 54.75, -2.25, 1.4, 0);
		addTree(handler, 27.35, 7.54, 0.21, 0);
		addTree(handler, -17.55, 25.2, 2.3, 0);
		addTree(handler, -9.15, -12.04, 2.78, 0);
		addTree(handler, -8.58, -46.12, 5, 0);
		addTree(handler, -4.8, -79.1, 5.91, 0);
		addTree(handler, -2.06, -90.51, 2.93, 0);
		addTree(handler, 21.42, -92.27, 0.31, 0);
		addTree(handler, 34.02, -83.4, 3.81, 0);
		addTree(handler, 63.51, -90.17, 6.02, 0);
		addTree(handler, 82.89, -93.51, 4.39, 0);
		addTree(handler, 109.05, -94.05, 6.23, 0);
		addTree(handler, 136.67, -91.27, 3.07, 0);
		addTree(handler, 115.8, -125.69, 6.28, 0);
		addTree(handler, 45.58, -75.68, 5.82, 0);
		addTree(handler, 22.47, -74.2, 1.83, 0);
		addTree(handler, -14.84, -100.92, 0.7, 0);
		addTree(handler, 1.42, -164.81, 0.08, 0);
		addTree(handler, 361.77, -162.02, 2.2, 0);
		addTree(handler, 425.18, -168.59, 3.81, 0);
		addTree(handler, 445.28, -221.08, 3.14, 0);
		addTree(handler, 400.64, -244.67, 4.72, 0);
		addTree(handler, 382.46, -230.93, 1.29, 0);
		addTree(handler, 313.52, -198.84, 0.63, 0);
		addTree(handler, 277.64, -212.95, 2.54, 0);
		addTree(handler, 243.31, -235.39, 5.09, 0);
		addTree(handler, 234.65, -265.56, 5.96, 0);
		addTree(handler, 185.45, -268.48, 0.87, 0);
		addTree(handler, 412.72, -97.88, 4.62, 0);
		addTree(handler, 446.49, -39.49, 3.06, 0);
		addTree(handler, 371.54, -44.25, 6.02, 0);
		addTree(handler, 323.44, -53.88, 3.83, 0);
		addTree(handler, 297.41, -66.44, 4.28, 0);
		
		//addRock(handler, 200, -120, 0, 0);
		//addRock(handler, 220, -125, 0, 0);
		//addRock(handler, 190, -135, 0, 0);
		//addRock(handler, 170, -140, 0, 0);

		/*Tank t = new Tank_BT42();
		t.setHandler(handler);
		t.setPosition(-3, 4);
		t.setRotation(Math.PI);
		t.activate();

		t = new Tank_BT42();
		t.setHandler(handler);
		t.setPosition(-4, -3);
		t.setRotation(1.6);
		t.activate();*/
	}

	@Override
	public void destroy() {
	}

}
