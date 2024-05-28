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

import blackhole.server.physicsEngine.core.PhysicsStrategy;
import blackhole.utils.Vector;
import java.util.ArrayList;
import tankgame.server.Damageable;
import tankgame.server.Fake3DGameObject;
import tankgame.server.Player;

/**
 *
 * @author fabian
 */
public abstract class Vehicle extends Fake3DGameObject implements Damageable {

	protected Player[] seats;
	protected ArrayList<HPModule> modules;

	public Vehicle() {
		seats = new Player[1];
		seats[0] = null;
		modules = new ArrayList<>();

		setPhysicsStrategy(new PhysicsStrategy());
		
		getDefaultUpdateStrategy().addParameter("seats",
				(s) -> {
				},
				() -> {
					Integer[] ret = new Integer[seats.length];
					for (int i = 0; i < seats.length; i++) {
						ret[i] = seats[i] == null ? null : seats[i].getID();
					}
					return ret;
				});
	}

	public void setDriver(Player d) {
		seats[0] = d;
		addToUpdate("seats");
	}

	public Player getDriver() {
		return seats[0];
	}
	
	public void setPassenger(int pos, Player p) {
		seats[pos] = p;
		addToUpdate("seats");
	}
	
	public Player getPassenger(int pos) {
		return seats[pos];
	}
	
	public void removePlayerFromVehicle(Player p) {
		for (int i = 0; i < seats.length; i++) {
			if (seats[i] == p) {
				seats[i] = null;
			}
		}
		addToUpdate("seats");
	}

	public void addModule(HPModule module) {
		modules.add(module);
	}

	public void removeModule(HPModule module) {
		modules.remove(module);
	}

	@Override
	public Vector getRealPosition() {
		return super.getRealPosition();
	}

	@Override
	public double getRealRotation() {
		return super.getRealRotation();
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
	public void moduleHitpointCallback(HPModule module, int change) {

	}

	@Override
	public void step(double dtime) {
		for (int i = 0; i < modules.size(); i++) {
			modules.get(i).step(dtime);
		}
	}

	@Override
	public void init() {

	}
}
