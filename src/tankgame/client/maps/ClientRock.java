/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tankgame.client.maps;

import blackhole.client.game.ClientManager;
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

/**
 *
 * @author fabian
 */
public class ClientRock extends Fake3DClientObject {

	public ClientRock() {
		Fake3DDrawUpdateStrategy f3dStrat = new Fake3DDrawUpdateStrategy();
		setDrawStrategy(f3dStrat);

		getDefaultUpdateStrategy().addParameter("scale", (val) -> {
		}, () -> {
			return null;
		});
		getDefaultUpdateStrategy().addParameter("base_height",
				(val) -> {
					setHeight((double) val);
				},
				 () -> {
					return null;
				});
	}

	private Polygon createRegularPolygon(int p, double r) {
		Vector v = new Vector(0, r);
		Vector[] pol = new Vector[p];
		for (int i = 0; i < p; i++) {
			pol[i] = new Vector(v.rotate(2 * Math.PI / p));
		}
		return new Polygon(pol, null);
	}

	@Override
	public void setHeight(double height) {
		super.setHeight(height);
		Fake3DDrawStrategy f3dStrat = getDrawStrategy();
		ArrayList<Fake3DShadowData> shadows = f3dStrat.getShadows();
		shadows.clear();
		shadows.add(new Fake3DShadowData(createRegularPolygon(8, 39), getHeight(), getHeight() + 4));
		f3dStrat.setShadows(shadows);
	}

	@Override
	public void step(double dtime) {
	}

	@Override
	public void init() {
		Fake3DDrawStrategy f3dStrat = getDrawStrategy();

		DecimalFormat fm = new DecimalFormat("0000");

		f3dStrat.setStep(1);

		GraphicsBackend backend = ClientManager.getInstance().getGraphicsBackend();
		for (int i = 9; i < 12; i += 1) {
			f3dStrat.addTexture(backend.createGameTexture("/res/rock/" + fm.format(i) + ".png"));
		}
		f3dStrat.addTexture(backend.createGameTexture("/res/rock/" + fm.format(12) + ".png"));

		ArrayList<Fake3DShadowData> shadows = f3dStrat.getShadows();
		shadows.clear();
		shadows.add(new Fake3DShadowData(createRegularPolygon(8, 39), getHeight(), getHeight() + 4));
		f3dStrat.setShadows(shadows);

		setScale(3, 3);
	}
}
