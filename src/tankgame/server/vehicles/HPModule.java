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

import blackhole.common.GameObject;
import blackhole.server.physicsEngine.boundingAreas.BoundingAreaContainer;
import blackhole.server.physicsEngine.boundingAreas.Polygon;
import blackhole.utils.Debug;
import java.util.ArrayList;
import java.util.Random;
import tankgame.server.Damageable;

/**
 *
 * @author fabian.baer2
 */
public class HPModule implements BoundingAreaContainer {

    private Damageable object;
    private int hitpoints;
    private Polygon hitbox;
    private double armour;
    private boolean considerAngle;
    private double spallAmount;
    private double lowEnd;
    private double highEnd;
	
	private double flammability;
	private boolean onFire;

    public HPModule(Damageable obj, int hp, Polygon hb, double ar,
            boolean angle, double spall, double lE, double hE, double flammable) {
        object = obj;
        hitpoints = hp;
        setHitbox(hb);
        armour = ar;
        considerAngle = angle;
        spallAmount = spall;
        lowEnd = lE;
        highEnd = hE;
		flammability = flammable;
		onFire = false;
    }

    public void setObject(Damageable obj) {
        object = obj;
    }

    public Damageable getObject() {
        return object;
    }

    public void setHitpoints(int hp) {
        int delta = hp - hitpoints;
        hitpoints = hp;
        if (object != null) {
            object.moduleHitpointCallback(this, delta);
        }
    }

    public int getHitpoints() {
        return hitpoints;
    }

    public void setHitbox(Polygon hb) {
        hitbox = hb;
		hb.setContainer(this);
    }

    public Polygon getHitbox() {
        return hitbox;
    }

    public void setArmour(double ar) {
        armour = ar;
    }

    public double getArmour() {
        return armour;
    }

    public void setSpallAmount(double spall) {
        spallAmount = spall;
    }

    public double getSpallAmount() {
        return spallAmount;
    }

    public void doConsiderAngle(boolean state) {
        considerAngle = state;
    }

    public boolean isConsideringAngle() {
        return considerAngle;
    }

    public void setLowEnd(double lE) {
        lowEnd = lE;
    }

    public double getLowEnd() {
        return lowEnd;
    }

    public void setHighEnd(double hE) {
        highEnd = hE;
    }

    public double getHighEnd() {
        return highEnd;
    }
	
	public boolean isOnFire() {
		return onFire;
	}
	
	public void setFire(double heat) {
		Random rand = new Random();
		if (rand.nextDouble() < flammability * heat) {
			onFire = true;
		}
	}
	
	public void stopFire() {
		onFire = false;
	}

    public void step(double dtime) {
        if (getObject() != null && getHitbox() != null) {
            getHitbox().setPosition(getObject().getRealPosition());
            getHitbox().setRotation(getObject().getRealRotation());
		}
		if (onFire) {
			setHitpoints((int) (getHitpoints() - 75 * dtime));
			
			ArrayList<HPModule> mods = getObject().getModules();
			for (int i = 0; i < mods.size(); i++) {
				mods.get(i).setFire(1.5);
			}
		}
    }
}
