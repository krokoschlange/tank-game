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
package tankgame.client;

import tankgame.client.sights.VisionDevice;
import blackhole.client.game.ClientObject;
import blackhole.networkData.ObjectUpdate;
import blackhole.utils.Debug;
import tankgame.client.sights.AimVisionDevice;

/**
 *
 * @author fabian
 */
public class ClientGun extends ClientObject {

	private VisionDevice gunsight;
	
	private double elevation;
	
	private double minElevation;
	
	private double maxElevation;

	public ClientGun() {
		getDefaultUpdateStrategy().addParameter("elevation",
				(val) -> {setElevation((double) val, false);},
				() -> {return getElevation();});
	}
	
	public void setGunSight(VisionDevice vd) {
		gunsight = vd;
	}

	public VisionDevice getGunSight() {
		return gunsight;
	}

	public void setMinElevation(double elev) {
		minElevation = elev;
	}

	public double getMinElevation() {
		return minElevation;
	}

	public void setMaxElevation(double elev) {
		maxElevation = elev;
	}

	public double getMaxElevation() {
		return maxElevation;
	}
	
	public void setElevation(double elev, boolean addToUpdate) {
		elev = Math.max(elev, minElevation);
		elev = Math.min(elev, maxElevation);
		elevation = elev;
		if (gunsight instanceof AimVisionDevice) {
			((AimVisionDevice) gunsight).setElevation(elev);
		}
		if (addToUpdate) {
			addToUpdate("elevation");
		}
	}
	
	public void setElevation(double elev) {
		setElevation(elev, true);
	}
	
	public double getElevation() {
		return elevation;
	}
}
