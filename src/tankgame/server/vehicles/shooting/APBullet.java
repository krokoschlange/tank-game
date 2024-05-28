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

import blackhole.common.GameObjectUpdateStrategy;
import blackhole.utils.Vector;
import java.util.Random;
import tankgame.server.Damageable;
import tankgame.server.vehicles.HPModule;

/**
 *
 * @author fabian
 */
public class APBullet extends Bullet {

	private double explosives;
	private double fuze;
	private double fuzeDelay;
	private double riccochetAngle;
	private double riccochetPenLoss;
	private double normalization;
	private double distanceLoss;

	private Vector explosionPos;
	private boolean canBeRemoved;

	public APBullet(double pen, int dmg, double expl, double fz, double fzDel,
			double ricc, double riccLoss, double norm, double distLoss) {
		super(pen, dmg);
		explosives = expl;
		fuze = fz;
		fuzeDelay = fzDel;
		riccochetAngle = ricc;
		riccochetPenLoss = riccLoss;
		normalization = norm;
		distanceLoss = distLoss;

		canBeRemoved = true;

		explosionPos = null;

		GameObjectUpdateStrategy ustrat = getDefaultUpdateStrategy();
		ustrat.addParameter("explosives", (val) -> {
		}, () -> {
			return explosives;
		});
		ustrat.addParameter("explosion", (val) -> {
		}, () -> {
			if (explosionPos != null) {
				canBeRemoved = true;
			}
			return explosionPos;
		});
	}

	public void setExplosives(double e) {
		explosives = e;
		addToUpdate("explosives");
	}

	public double getExplosives() {
		return explosives;
	}

	public void setFuze(double f) {
		fuze = f;
	}

	public double getFuze() {
		return fuze;
	}

	public void setFuzeDelay(double fd) {
		fuzeDelay = fd;
	}

	public double getFuzeDelay() {
		return fuzeDelay;
	}

	public void setRiccochetAngle(double ricc) {
		riccochetAngle = ricc;
	}

	public double getRiccochetAngle() {
		return riccochetAngle;
	}

	public void setRiccochetPenLoss(double riccLoss) {
		riccochetPenLoss = riccLoss;
	}

	public double getRiccochetPenLoss() {
		return riccochetPenLoss;
	}

	public void setNormalization(double n) {
		normalization = n;
	}

	public double getNormalization() {
		return normalization;
	}

	@Override
	public void step(double dtime) {
		if (explosionPos != null && canBeRemoved) {
			remove();
		} else if (canBeRemoved) {
			
			double dist = Vector.subtract(getPosition(), oldPosition).magnitude();
			penetration -= distanceLoss * dist;
			super.step(dtime);
			
			
		}

	}

	public ImpactResult explode(double armour, Vector impactPos, Vector diff, Damageable spallObj) {
		if (armour > fuze && explosives > 0) {
			Vector explPos = impactPos;
			if (penetration > 0) {
				explPos = Vector.add(impactPos, diff.normalized().multiply(fuzeDelay));
			}
			double rad = Math.sqrt(explosives);
			double dmg = explosives * 100;
			double height = getHeight();//(Fake3DVisualStrategy) getVisualStrategy()).getBaseHeight();
			Explosion explosion = new Explosion(explPos, height, rad, dmg);
			explosion.boom(getHandler());

			explosionPos = explPos;
			addToUpdate("explosion");
			canBeRemoved = false;
			createExplosionSpall(spallObj, explPos, height, getVelocity(), getVerticalVelocity());
			remove();
			return ImpactResult.end;
		}
		return ImpactResult.continueImpacting;
	}

	public void createPenetrationSpall(HPModule mod, Vector pos, double vpos, Vector vel, double vvel) {
		int spallAmount = (int) (mod.getSpallAmount() * 50);

		Random rand = new Random();

		for (int i = 0; i < spallAmount; i++) {
			APBullet spall = new APBullet(10, 15, 0, 0, 0, 45, 0.8, 0, 5);
			spall.setSpallObject(mod.getObject());
			spall.setHandler(getHandler());
			spall.setServerOnly(true);

			double velSpread = 0.35 * vel.magnitude();
			Vector spallVel = Vector.add(vel, getVelocity().getNormal().multiply(rand.nextDouble() * velSpread - velSpread / 2));
			double spallVVel = vvel + rand.nextDouble() * velSpread - velSpread / 2;
			spall.setPosition(new Vector(pos));
			spall.setVelocity(spallVel);
			spall.setVerticalVelocity(spallVVel);
			spall.setHeight(vpos);
			spall.setRotation(0);
			spall.setOldPosition(Vector.subtract(pos, spallVel.normalized().multiply(0.1)));
			spall.setOldVertPos(vpos);

			spall.activate();
		}
	}

	public void createSplashSpall(HPModule mod, Vector pos, double vpos, Vector vel,
			double vvel) {
		int spallAmount = (int) (mod.getSpallAmount() * (-Math.pow(2, 5.9 - explosives) + 50));
		Random rand = new Random();

		for (int i = 0; i < spallAmount; i++) {
			APBullet spall = new APBullet(10, 15, 0, 0, 0, 45, 0.8, 0, 5);
			spall.setSpallObject(mod.getObject());
			spall.setHandler(getHandler());
			spall.setServerOnly(true);

			double velSpread = 0.35 * vel.magnitude();
			Vector spallVel = Vector.add(vel, vel.getNormal().multiply(rand.nextDouble() * velSpread - velSpread / 2));
			double spallVVel = vvel + rand.nextDouble() * velSpread - velSpread / 2;
			spall.setPosition(new Vector(pos));
			spall.setVelocity(spallVel);
			spall.setVerticalVelocity(spallVVel);
			spall.setHeight(vpos);
			spall.setRotation(0);
			spall.setOldPosition(Vector.subtract(pos, spallVel.normalized().multiply(0.001)));
			spall.setOldVertPos(vpos);
			spall.activate();
		}
	}

	public void createExplosionSpall(Damageable spallObj, Vector pos, double vpos, Vector vel, double vvel) {
		int spallAmount = (int) (-Math.pow(2, 4.32 - explosives) + 50);

		Random rand = new Random();

		for (int i = 0; i < spallAmount; i++) {
			APBullet spall = new APBullet(10, 15, 0, 0, 0, 45, 0.8, 0, 5);
			spall.setSpallObject(spallObj);
			spall.setHandler(getHandler());
			spall.setServerOnly(true);

			Vector spallVel = new Vector(0, 400).rotate(rand.nextDouble() * 2 * Math.PI).add(vel);
			double spallVVel = vvel + rand.nextDouble() * 800 - 400;
			spall.setPosition(new Vector(pos));
			spall.setVelocity(spallVel);
			spall.setVerticalVelocity(spallVVel);
			spall.setHeight(vpos);
			spall.setRotation(0);
			spall.setOldPosition(Vector.subtract(pos, spallVel.normalized().multiply(0.1)));
			spall.setOldVertPos(vpos);
			spall.activate();
		}
	}

	@Override
	public ImpactResult handleImpact(ImpactInfo imp) {
		Vector diff = Vector.subtract(getPosition(), oldPosition);

		Vector impactPos = Vector.add(oldPosition, Vector.multiply(diff, imp.dist));

		if (imp.mod == null) {
			penetration = -100;
			explode(fuze + 100000, impactPos, diff, null);
			remove();
			return ImpactResult.end;
		}

		double armour = imp.mod.getArmour();
		if (imp.mod.isConsideringAngle()) {
			Vector edge = imp.mod.getHitbox().getEdge(imp.edge);

			double angle = Math.acos(Math.abs(getVelocity().dot(edge)) / (getVelocity().magnitude() * edge.magnitude()));
			if (Math.abs(angle) < Math.toRadians(riccochetAngle)) {
				//riccochet
				Vector norm = edge.getNormal();
				double shift = norm.dot(getVelocity());
				setVelocity(getVelocity().add(norm.multiply(-2 * shift)));

				double rot = Math.atan2(-getVelocity().x(), getVelocity().y());
				setRotation(rot);
				penetration *= riccochetPenLoss;
				setPosition(Vector.add(impactPos, getVelocity().normalized().multiply(0.05)));
				return ImpactResult.recalculateImpacts;
			}
			angle = angle - Math.signum(angle) * Math.toRadians(normalization);
			armour = imp.mod.getArmour() / Math.sin(angle);
		}

		penetration -= armour;
		Vector insideSpallPos = Vector.add(impactPos, diff.normalized().multiply(0.15));

		if (penetration < 0) {
			createSplashSpall(imp.mod,
					insideSpallPos,
					getHeight(), getVelocity(), getVerticalVelocity());
			explode(armour, impactPos, diff, imp.mod.getObject());
			remove();
			//Debug.log("Bullet removed 2");
			return ImpactResult.end;
		}

		//penetration
		createPenetrationSpall(imp.mod,
				insideSpallPos,
				getHeight(), getVelocity(), getVerticalVelocity());

		imp.mod.setHitpoints(imp.mod.getHitpoints() - damage);
		imp.mod.setFire(1.1);

		explode(armour, impactPos, diff, imp.mod.getObject());
		return ImpactResult.continueImpacting;
	}

	@Override
	public void remove() {
		setVelocity(new Vector());
		if (canBeRemoved) {
			super.remove();
		}
	}
}
