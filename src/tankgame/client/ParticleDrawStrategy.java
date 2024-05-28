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
import blackhole.utils.Debug;
import blackhole.utils.Vector;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import tankgame.common.Particle;

/**
 *
 * @author fabian
 */
public class ParticleDrawStrategy extends Fake3DDrawUpdateStrategy {

	/*public int getLayerOfParticle(Particle p) {
		return (int) ((int) (p.getPosition().vpos / getStep()) * getStep() * handler3D.getHeightLayerFactor());
	}*/

	@Override
	public ArrayList<Integer> getDrawPositions() {

		ArrayList<Integer> dp = new ArrayList<>();

		ClientParticleSpawner spawner = (ClientParticleSpawner) getObject();
		CopyOnWriteArrayList<Particle> particles = spawner.getParticles();
		Iterator<Particle> it = particles.iterator();
		while (it.hasNext()) {
			//dp.add(getLayerOfParticle(it.next()));
			dp.add(handler3D.getDrawPosFromHeight(it.next().getPosition().vpos));
		}
		return dp;
	}

	@Override
	public void draw3D(int drawPos) {
		ClientParticleSpawner spawner = (ClientParticleSpawner) getObject();

		try {
			if (getObject().isVisible()) {
				CopyOnWriteArrayList<Particle> particles = spawner.getParticles();

				Iterator<Particle> it = particles.iterator();

				while (it.hasNext()) {
					Particle particle = it.next();
					if (handler3D.getDrawPosFromHeight(particle.getPosition().vpos) == drawPos) {
						ClientObjectHandler chandler = (ClientObjectHandler) getObject().getHandler();
						AbstractDrawer drawer = ClientManager.getInstance().getGraphicsBackend().getDrawer();

						Vector pos = transformPoint(particle.getPosition().pos, particle.getPosition().vpos, chandler.getCamera());

						drawer.drawTexture(getTexture(0),
								spawner.getParticleAlphaFunction().apply(particle.getRemainingLifetime()),
								pos,
								particle.getRotation(),
								Vector.multiply(particle.getSize(), 1 + handler3D.getScale3DFactor() * particle.getPosition().vpos),
								chandler.getCamera(),
								chandler.getPanel(),
								chandler.getScale());
					}
				}

			}
		} catch (NullPointerException e) {
		}
	}
}
