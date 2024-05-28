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
import blackhole.common.GameObjectUpdateStrategy;
import blackhole.utils.Debug;
import blackhole.utils.Vector;
import tankgame.client.visuals.ExplosionEffect;

/**
 *
 * @author fabian
 */
public class ClientBullet extends Fake3DClientObject {

	private BulletTracer tracer;

	private double explosives;

	public ClientBullet() {
		
		Fake3DDrawUpdateStrategy f3duStrat = new Fake3DDrawUpdateStrategy();
		setDrawStrategy(f3duStrat);
		addUpdateStrategy(f3duStrat);

		GameObjectUpdateStrategy ustrat = getDefaultUpdateStrategy();
		ustrat.addParameter("explosives", (val) -> {
			explosives = (double) val;
		}, () -> {
			return null;
		});
		ustrat.addParameter("explosion", (val) -> {
			Vector explPos = (Vector) val;
			if (explPos != null && explosives > 0) {
				double explSize = 15 - Math.pow(2, 3.907 - explosives);
				ExplosionEffect boom = new ExplosionEffect(explSize);
				boom.setHandler(getHandler());
				boom.setPosition(explPos);
				setVelocity(new Vector());
				boom.activate();
				boom.start();
			}
		}, () -> {
			return null;
		});
		ustrat.addParameter("trcrC", (val) -> {
			tracer.setColor((float[]) val);
		}, () -> {
			return null;
		});
		ustrat.addParameter("trcrD", (val) -> {
			tracer.setTracerTime((double) val);
		}, () -> {
			return null;
		});
		
	}

	@Override
	public void step(double dtime) {
		super.step(dtime);
		double h = getHeight();
		if (!getPosition().equals(new Vector())) {
			tracer.addPosition(getRealPosition(), h);
		}
	}

	@Override
	public void init() {
		tracer = new BulletTracer(new float[]{1, 0, 0, 0.5f}, 2);
		tracer.setParent(this);
		tracer.setVisible(true);
		tracer.setHandler(getHandler());
		tracer.activate();
		//tracer.
	}
}
