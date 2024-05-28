/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tankgame.client.maps;

import blackhole.client.game.ClientManager;
import blackhole.client.graphicsEngine.GameTexture;
import blackhole.client.graphicsEngine.GraphicsBackend;
import blackhole.server.physicsEngine.boundingAreas.Polygon;
import blackhole.utils.Vector;
import java.text.DecimalFormat;
import java.util.ArrayList;
import tankgame.client.Fake3DClientObject;
import tankgame.client.Fake3DDrawStrategy;
import tankgame.client.Fake3DDrawUpdateStrategy;
import tankgame.client.Fake3DShadowData;
import tankgame.client.TankGameClient;
import tankgame.server.Fake3DVisualStrategy;

/**
 *
 * @author fabian
 */
public class ClientBaseBuilding extends Fake3DClientObject {

	public ClientBaseBuilding() {
		Fake3DDrawUpdateStrategy f3dStrat = new Fake3DDrawUpdateStrategy();
		setDrawStrategy(f3dStrat);

		getDefaultUpdateStrategy().addParameter("scale", (val) -> {
		}, () -> {
			return null;
		});
	}

	@Override
	public void step(double dtime) {
	}

	@Override
	public void init() {
		Fake3DDrawStrategy f3dStrat = getDrawStrategy();

		DecimalFormat fm = new DecimalFormat("0000");

		int skip = 1;
		int res = 1024;
		if (TankGameClient.getInstance().getQuality() >= 1) {
			skip = 2;
		}
		if (TankGameClient.getInstance().getQuality() >= 2) {
			res = 512;
		}
		String resFolder = res == 1024 ? "base" : "base/base_low";
		f3dStrat.setStep(skip * 0.25);

		GraphicsBackend backend = ClientManager.getInstance().getGraphicsBackend();
		for (int i = 1; i < 12; i += skip) {
			f3dStrat.addTexture(backend.createGameTexture("/res/" + resFolder + "/" + fm.format(i) + ".png"));
		}
		f3dStrat.addTexture(backend.createGameTexture("/res/" + resFolder + "/" + fm.format(12) + ".png"));

		Vector[] p = {new Vector(-3.46, 4.57), new Vector(-3.46, -1.72),
				new Vector(1.35, -1.72), new Vector(1.35, 0.72),
				new Vector(4.17, 0.72), new Vector(4.17, 4.57)
			};

			ArrayList<Fake3DShadowData> shadows = f3dStrat.getShadows();
			shadows.add(new Fake3DShadowData(new Polygon(p, null), 0, 3));
			f3dStrat.setShadows(shadows);

		double fac = 1024d / res;
		setScale(0.09 * fac, 0.09 * fac);
	}
}
