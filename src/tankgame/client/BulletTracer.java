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

import blackhole.client.game.ClientManager;
import blackhole.client.game.ClientObjectHandler;
import blackhole.client.graphicsEngine.AbstractDrawer;
import blackhole.client.graphicsEngine.Camera;
import blackhole.utils.Vector;
import java.util.ArrayList;
import tankgame.common.SpaceTimePosition3D;

/**
 *
 * @author fabian
 */
public class BulletTracer extends Fake3DClientObject {

	private double time;
	private double tracerTime;
	private ArrayList<SpaceTimePosition3D> oldPositions;

	private float[] color;
	
	private boolean removeable;

	public BulletTracer(float[] c, double t) {
		removeable = false;
		
		time = 0;
		tracerTime = t;
		color = c;
		oldPositions = new ArrayList<>();

		Fake3DDrawUpdateStrategy f3duStrat = new Fake3DDrawUpdateStrategy() {
			@Override
			public void draw3D(int drawPos) {
				if (getObject().isVisible()) {
					try {
						ClientObjectHandler chandler = (ClientObjectHandler) getObject().getHandler();
						AbstractDrawer drawer = ClientManager.getInstance().getGraphicsBackend().getDrawer();
						Camera cam = chandler.getCamera();
						float[] color = getColor();

						for (int i = 1; i < getPositions().size(); i++) {

							Vector tpos1 = getPositions().get(i).pos;
							double tvpos1 = getPositions().get(i).vpos;
							int dp = handler3D.getDrawPosFromHeight(tvpos1);
							if (dp == drawPos) {

								Vector pos3D1 = transformPoint(tpos1, tvpos1, cam);

								Vector tpos2 = getPositions().get(i - 1).pos;
								double tvpos2 = getPositions().get(i - 1).vpos;
								Vector pos3D2 = transformPoint(tpos2, tvpos2, cam);

								Vector horiz = Vector.subtract(pos3D1, pos3D2).getNormal();

								pos3D1.subtract(Vector.multiply(horiz, 0.025));
								pos3D2.subtract(Vector.multiply(horiz, 0.025));

								Vector pos3D3 = Vector.add(pos3D2, Vector.multiply(horiz, 0.05));
								Vector pos3D4 = Vector.add(pos3D1, Vector.multiply(horiz, 0.05));

								drawer.drawPolygon(new Vector(), 0, color[0], color[1], color[2], color[3],
										chandler.getCamera(), chandler.getPanel(),
										chandler.getScale(), true,
										pos3D1, pos3D4, pos3D3, pos3D2);
							}
						}
					} catch (NullPointerException e) {

					}
				}
			}

		};
		setDrawStrategy(f3duStrat);
		addUpdateStrategy(f3duStrat);
	}

	public void setColor(float[] color) {
		this.color = color;
	}
	
	public float[] getColor() {
		return color;
	}

	public void setTracerTime(double time) {
		tracerTime = time;
	}

	public double getTracerTime() {
		return tracerTime;
	}

	public void addPosition(Vector pos, double vpos) {
		oldPositions.add(new SpaceTimePosition3D(pos, vpos, time));
		removeable = true;
	}

	public ArrayList<SpaceTimePosition3D> getPositions() {
		return oldPositions;
	}

	public void update(double dtime) {
		time += dtime;
		for (int i = 0; i < oldPositions.size(); i++) {
			if (oldPositions.get(i).time + tracerTime < time) {
				oldPositions.remove(i);
				i--;
			}
		}
	}

	@Override
	public void step(double dtime) {
		if (getPositions().isEmpty() && removeable) {
			remove();
		}
		update(dtime);
	}
	
	
}
