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

import blackhole.server.game.ServerObject;
import blackhole.utils.Debug;
import blackhole.utils.Vector;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import tankgame.client.ClientGun;

/**
 *
 * @author fabian
 */
public class ServerGun extends ServerObject {
	private double reload;
	private double reloadTimer;
	private AmmoInfo[] ammo;
	private int loadedAmmo;
	
	private double elevation;
	private double height;
	
	private class AmmoInfo {
		public Class bulletClass;
		public Object[] constArgs;
		public double muzzleVel;
	}
	
	public ServerGun(Class[] ammoClasses, Object[][] constArgs, double[] muzzleVels, double rel) {
		ammo = new AmmoInfo[ammoClasses.length];
		for (int i = 0; i < ammoClasses.length; i++) {
			AmmoInfo ammoInfo = new AmmoInfo();
			ammoInfo.bulletClass = ammoClasses[i];
			ammoInfo.constArgs = constArgs[i];
			ammoInfo.muzzleVel = muzzleVels[i];
			ammo[i] = ammoInfo;
		}
		reload = rel;
		
		setClientObjectClass(ClientGun.class);
		
		getDefaultUpdateStrategy().addParameter("elevation",
				(val) -> {setElevation((double) val);},
				() -> {return getElevation();});
	}
	
	public void setReload(double r) {
		reload = r;
	}
	
	public double getReload() {
		return reload;
	}
	
	public void setLoadedAmmo(int ammo) {
		loadedAmmo = ammo;
	}
	
	public int getLoadedAmmo() {
		return loadedAmmo;
	}
	
	public void setElevation(double e) {
		elevation = e;
	}
	
	public double getElevation() {
		return elevation;
	}
	
	public void setHeight(double h) {
		height = h;
	}
	
	public double getHeight() {
		return height;
	}
	
	public void shoot() {
		if (reloadTimer == 0) {
			reloadTimer = reload;
			Class[] classes = new Class[ammo[loadedAmmo].constArgs.length];
			for (int i = 0; i < classes.length; i++) {
				classes[i] = ammo[loadedAmmo].constArgs[i].getClass();
				//Debug.log(classes[i]);
			}
			try {
				Constructor<?> con = ammo[loadedAmmo].bulletClass.getConstructor(classes);
				Bullet bullet = (Bullet) con.newInstance(ammo[loadedAmmo].constArgs);
				bullet.setHandler(getHandler());
				Vector bulletVel = new Vector(0, 1).rotate(getRealRotation()).multiply(Math.cos(elevation) * ammo[loadedAmmo].muzzleVel);
				//Debug.log("BULLETVEL: " + bulletVel);
				bullet.setPosition(getRealPosition());
				bullet.setVelocity(bulletVel);
				bullet.setVerticalVelocity(Math.sin(elevation) * ammo[loadedAmmo].muzzleVel);
				bullet.setHeight(height);
				bullet.setRotation(getRealRotation());
				bullet.setOldPosition(Vector.subtract(getRealPosition(), bulletVel.normalized().multiply(0.1)));
				bullet.setOldVertPos(height);
				bullet.activate();
				
				//Debug.log("bullet spawned");
			} catch (NoSuchMethodException | SecurityException e) {
				//Debug.logError(e);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
				//Debug.logError(ex);
			}
		}
	}
	
	@Override
	public void step(double dtime) {
		reloadTimer -= dtime;
		reloadTimer = Math.max(reloadTimer, 0);
	}

	@Override
	public void init() {
	}
	
}
