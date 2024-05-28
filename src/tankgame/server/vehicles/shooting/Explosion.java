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
import blackhole.common.ObjectHandler;
import blackhole.server.physicsEngine.boundingAreas.Circle;
import blackhole.server.physicsEngine.boundingAreas.Polygon;
import blackhole.utils.Vector;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import tankgame.server.Damageable;
import tankgame.server.vehicles.HPModule;

/**
 *
 * @author fabian
 */
public class Explosion {

	private Vector position;
	private double height;
	private double radius;
	private double damageFactor;

	public Explosion(Vector pos, double h, double r, double dmgF) {
		position = pos;
		height = h;
		radius = r;
		damageFactor = dmgF;
	}

	public void boom(ObjectHandler handler) {
		ArrayList<HPModule> modules = new ArrayList<>();
		CopyOnWriteArrayList<GameObject> objs = handler.getObjects();
		for (int i = 0; i < objs.size(); i++) {
			if (objs.get(i) instanceof Damageable) {
				ArrayList<HPModule> mods = ((Damageable) objs.get(i)).getModules();
				if (mods != null) {
					modules.addAll(mods);
				}
			}
		}
		boom(modules);
	}

	public void boom(ArrayList<HPModule> modules) {
		/*BoundingArea[] bAs = new BoundingArea[modules.size() + 1];

		for (int i = 0; i < modules.size(); i++) {
			bAs[i] = modules.get(i).getHitbox();
		}
		bAs[bAs.length - 1] = boundingArea;

		Collision[] colls = colldetect.broadPhase(bAs);

		for (int i = 0; i < colls.length; i++) {
			Collision maybeColl = colls[i];
			HPModule mod = null;
			if (maybeColl.getImpacted() == boundingArea) {
				mod = (HPModule) maybeColl.getImpacting().getContainer();
			} else if (maybeColl.getImpacting() == boundingArea) {
				mod = (HPModule) maybeColl.getImpacted().getContainer();
			}
			if (mod != null) {
				if (mod.getLowEnd() < height + radius && mod.getHighEnd() > height - radius) {
					Collision coll = colldetect.collide(mod.getHitbox(), boundingArea);

					Debug.log(coll);
					if (coll != null && coll.getMTV() != null) {
						double minR = radius - coll.getMTV().magnitude();
						if (minR < radius * 1.5) {
							int dmg = (int) ((1 - minR / radius) * damageFactor);
							Debug.log(minR + ", " + radius + ", " + damageFactor);
							if (mod.getArmour() > 0.5) {
								dmg /= 2;
								dmg -= mod.getArmour();
							}
							dmg = Math.max(0, dmg);
							mod.setHitpoints(mod.getHitpoints() - dmg);
							mod.setFire(1.3);
						}
					}
				}
			}
		}*/

		for (int i = 0; i < modules.size(); i++) {
			HPModule mod = modules.get(i);
			if (mod.getLowEnd() < height + radius && mod.getHighEnd() > height - radius) {
				Polygon p = modules.get(i).getHitbox();
				double smallestRadius = -1;
				boolean inside = true;
				for (int j = 0; j < p.getEdges().length; j++) {
					Vector norm = p.getEdge(j).getNormal();
					Vector diff = Vector.subtract(p.getPoint(j), position);
					double dot = norm.dot(diff);
					if (dot < 0) {
						inside = false;
						Vector intersect = Vector.add(position, Vector.multiply(norm, dot));
						Vector diff2 = Vector.subtract(intersect, p.getPoint(j));
						double dot2 = diff2.dot(p.getEdge(j).normalized());
						if (dot2 > 0 && diff2.magnitude() < p.getEdge(j).magnitude()) {
							double rad = -dot;
							if (smallestRadius < 0 || smallestRadius > rad) {
								smallestRadius = rad;
							}
						}
					}
				}
				if (inside) {
					smallestRadius = 0;
				} else if (smallestRadius < 0) {
					for (int j = 0; j < p.getPoints().length; j++) {
						Vector diff = Vector.subtract(p.getPoint(j), position);
						if (diff.magnitude() < smallestRadius || smallestRadius < 0) {
							smallestRadius = diff.magnitude();
						}
					}
				}
				if (smallestRadius < radius) {

					int dmg = (int) ((1 - smallestRadius / radius) * damageFactor);
					if (mod.getArmour() > 0.5) {
						dmg /= 2;
						dmg -= mod.getArmour();
					}
					dmg = Math.max(0, dmg);
					mod.setHitpoints(mod.getHitpoints() - dmg);
					mod.setFire(1.3);
				}
			}
		}
	}
}
