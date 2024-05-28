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
package tankgame.client.vehicles;

import blackhole.client.game.ClientManager;
import blackhole.client.game.ClientObject;
import blackhole.client.graphicsEngine.GameTexture;
import blackhole.client.graphicsEngine.GraphicsBackend;
import blackhole.utils.Debug;
import blackhole.utils.Vector;
import java.text.DecimalFormat;
import tankgame.client.ClientParticleSpawner;
import tankgame.client.Fake3DClientObject;
import tankgame.client.Fake3DDrawStrategy;

/**
 *
 * @author fabian.baer2
 */
public class Turret_BT7_Client extends Fake3DClientObject {
	
	public Turret_BT7_Client() {
		Fake3DDrawStrategy f3dStrat = new Fake3DDrawStrategy();
		setDrawStrategy(f3dStrat);
	}

	@Override
	public void step(double dtime) {
		//Debug.log(getID() + "; " + getParent());
	}

	@Override
	public void init() {
		Fake3DDrawStrategy f3dStrat = getDrawStrategy();
		f3dStrat.setBaseHeight(2);
		f3dStrat.setStep(0.25);
		
		DecimalFormat df = new DecimalFormat("0000");
		
		GraphicsBackend backend = ClientManager.getInstance().getGraphicsBackend();

		for (int i = 33; i < 37; i++) {
			String path = "/res/bt7_3D/" + df.format(i) + ".png";
			GameTexture tex = backend.createGameTexture(path);
			tex.setRotOffset(Math.PI / 2);
			tex.setOffset(new Vector(0, -0.45));

			f3dStrat.addTexture(tex);
		}
		
		/*
        tex = ResourceLoader.getTexture("/res/bt42_3D/0018.png");
        tex.useBuffer(false);
        tex.setRotationOffset(Math.PI / 2);
        tex.setOffset(new Vector(0, -0.45));
        
        f3dStrat.addTexture(tex);*/
	}
}
