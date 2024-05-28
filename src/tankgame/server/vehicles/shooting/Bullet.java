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
package tankgame.server.vehicles.shooting;

import blackhole.common.GameObject;
import blackhole.common.GameObjectUpdateStrategy;
import blackhole.server.physicsEngine.boundingAreas.Polygon;
import blackhole.server.physicsEngine.core.Force;
import blackhole.server.physicsEngine.core.PhysicsStrategy;
import blackhole.utils.Vector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;
import tankgame.client.ClientBullet;
import tankgame.server.Damageable;
import tankgame.server.Fake3DGameObject;
import tankgame.server.Fake3DVisualStrategy;
import tankgame.server.vehicles.HPModule;

/**
 *
 * @author fabian.baer2
 */
public abstract class Bullet extends Fake3DGameObject {

	protected double penetration;
	protected int damage;
	protected Vector oldPosition;
	protected Damageable spallObject;
	protected double drag;
	protected double verticalVel;

	protected float[] tracerColor;
	protected double tracerDuration;

	protected ArrayList<Damageable> flyThrough;

	protected double oldVertPos;

	protected class ImpactInfo {

		public HPModule mod;
		public double dist;
		public int edge;

		public ImpactInfo(HPModule m, double d, int e) {
			mod = m;
			dist = d;
			edge = e;
		}
	}

	protected enum ImpactResult {
		continueImpacting,
		recalculateImpacts,
		end
	}

	public Bullet(double pen, int dmg) {
		penetration = pen;
		damage = dmg;
		spallObject = null;
		verticalVel = 0;

		oldVertPos = 1;
		oldPosition = new Vector();

		flyThrough = new ArrayList<>();

		tracerColor = new float[]{0, 0, 0, 0};
		tracerDuration = 0;

		setPhysicsStrategy(new PhysicsStrategy());
		setInterpolate(true);
		setClientObjectClass(ClientBullet.class);
		setAlwaysLoaded(true);

		GameObjectUpdateStrategy strat = getDefaultUpdateStrategy();
		strat.addParameter("trcrC", (val) -> {
		}, () -> {
			return tracerColor;
		});
		strat.addParameter("trcrD", (val) -> {
		}, () -> {
			return tracerDuration;
		});
	}

	public void setPenetration(double pen) {
		penetration = pen;
	}

	public double getPenetration() {
		return penetration;
	}

	public void setDamage(int dmg) {
		damage = dmg;
	}

	public int getDamage() {
		return damage;
	}

	public void setDrag(double d) {
		drag = d;
	}

	public double getDrag() {
		return drag;
	}

	public void setSpallObject(Damageable obj) {
		spallObject = obj;
	}

	public void setVerticalVelocity(double vvel) {
		verticalVel = vvel;
	}

	public double getVerticalVelocity() {
		return verticalVel;
	}

	public void setOldPosition(Vector pos) {
		oldPosition = pos;
	}

	public void setOldVertPos(double pos) {
		oldVertPos = pos;
	}

	public ArrayList<Damageable> getFlyThroughObjects() {
		return flyThrough;
	}

	public void setTracerColor(float r, float g, float b, float a) {
		tracerColor = new float[]{r, g, b, a};
	}

	public float[] getTracerColor() {
		return tracerColor;
	}

	public void setTracerDuration(double tracerDuration) {
		this.tracerDuration = tracerDuration;
	}

	public double getTracerDuration() {
		return tracerDuration;
	}

	@Override
	public void step(double dtime) {
		
		verticalVel -= 9.81 * dtime;
		setHeight(getHeight() + verticalVel * dtime);

		getPhysicsStrategy().applyForce(new Force((new Vector(getVelocity())).multiply(Vector.multiply(getVelocity(), drag)), new Vector()));

		if (getHeight() < 0) {
			double dist = oldVertPos / (oldVertPos - getHeight());

			ImpactInfo i = new ImpactInfo(null, dist, 0);
			handleImpact(i);
			remove();
		} else {
			ImpactInfo[] impacts;

			ImpactResult result = ImpactResult.continueImpacting;

			int recalcs = 0;
			do {
				//Debug.log("recalc " + dtime + ", " + getPosition() + ", " + penetration);
				impacts = getImpacts();
				for (int i = 0; i < impacts.length && result == ImpactResult.continueImpacting; i++) {
					result = handleImpact(impacts[i]);
				}
				recalcs++;
			} while (result == ImpactResult.recalculateImpacts && impacts.length > 0 && recalcs < 5);
			if (recalcs >= 5) {
				remove();
			}
		}

		oldPosition.set(getPosition().x(), getPosition().y());
		oldVertPos = getHeight();

		if (penetration <= 0) {
			remove();
		}
	}

	public ImpactInfo[] getImpacts() {
		ArrayList<HPModule> modules = new ArrayList<>();
		if (spallObject == null) {
			CopyOnWriteArrayList<GameObject> gobjs = getHandler().getObjects();
			for (int i = 0; i < gobjs.size(); i++) {
				if (gobjs.get(i) instanceof Damageable && !flyThrough.contains(gobjs.get(i))) {
					ArrayList<HPModule> mods = ((Damageable) gobjs.get(i)).getModules();
					if (mods != null) {
						modules.addAll(mods);
					}
				}
			}
		} else {
			modules = spallObject.getInternalModules();
		}

		Vector diff = Vector.subtract(getPosition(), oldPosition);
		Vector normal = diff.getNormal();

		ArrayList<ImpactInfo> colls = new ArrayList<>();

		for (int j = 0; j < modules.size(); j++) {
			HPModule mod = modules.get(j);

			Polygon p = mod.getHitbox();

			int maxN = p.getFurthestPointIDInDirection(normal);
			int minN = p.getFurthestPointIDInDirection(Vector.multiply(normal, -1));

			Vector maxNV = p.getPoint(maxN);
			Vector minNV = p.getPoint(minN);

			double dotMaxN = Vector.subtract(maxNV, getPosition()).dot(normal);
			double dotMinN = Vector.subtract(minNV, getPosition()).dot(normal);

			if (dotMaxN * dotMinN < 0) { // different signs

				int edge = 0;
				for (edge = 0; edge < p.getEdges().length; edge++) {
					Vector v1 = Vector.subtract(p.getPoint(edge), oldPosition);
					Vector v2 = Vector.subtract(p.nextPoint(edge), oldPosition);

					if (Vector.dot(v1, normal) * Vector.dot(v2, normal) < 0
							&& Vector.dot(p.getEdge(edge).getNormal(), diff) < 0) {
						break;
					}
				}

				if (edge < p.getEdges().length) {

					Vector v = p.getPoint(edge);
					Vector e = p.getEdge(edge);

					double s = ((oldPosition.y() - v.y()) * e.x()) / (e.y() * diff.x() - diff.y() * e.x()) - (oldPosition.x() - v.x()) / (diff.x() - diff.y() / e.y() * e.x());
					//should be the same (not tested):
					//s = ((oldPosition.y() - v.y()) * e.x() - (oldPosition.x() - v.x()) * e.y()) / (e.y() * diff.x() - diff.y() * e.x());
					if (s < 1 && s > 0) {
						double height = oldVertPos + s * (getHeight() - oldVertPos);
						if (mod.getLowEnd() < height && height < mod.getHighEnd()) {
							colls.add(new ImpactInfo(mod, s, edge));
						}
					}
				}
			}
		}

		ImpactInfo[] entries = new ImpactInfo[colls.size()];
		entries = colls.toArray(entries);
		Arrays.sort(entries, new Comparator<ImpactInfo>() {
			@Override
			public int compare(ImpactInfo t, ImpactInfo t1) {
				double diff = t.dist - t1.dist;
				if (diff > 0) {
					return 1;
				}
				return -1;
			}
		});
		return entries;
	}

	@Override
	public void init() {
		super.init(); //To change body of generated methods, choose Tools | Templates.
		getPhysicsStrategy().setPhysicsHandler(getHandler().getPhysicsHandler());
	}

	public abstract ImpactResult handleImpact(ImpactInfo imp);
}
