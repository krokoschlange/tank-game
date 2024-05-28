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
public class ClientTree extends Fake3DClientObject {

	public ClientTree() {
		Fake3DDrawUpdateStrategy f3dStrat = new Fake3DDrawUpdateStrategy();
		setDrawStrategy(f3dStrat);

		getDefaultUpdateStrategy().addParameter("scale", (val) -> {
		}, () -> {
			return null;
		});
		getDefaultUpdateStrategy().addParameter("base_height",
				(val) -> {setHeight((double) val);}
				, () -> {return null;});
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
	public void step(double dtime) {
	}

	@Override
	public void setHeight(double height) {
		super.setHeight(height);
		Fake3DDrawStrategy f3dStrat = getDrawStrategy();
		ArrayList<Fake3DShadowData> shadows = f3dStrat.getShadows();
		shadows.clear();
		shadows.add(new Fake3DShadowData(createRegularPolygon(8, 0.2), 0 + getHeight(), 5 + getHeight()));
		shadows.add(new Fake3DShadowData(createRegularPolygon(8, 4), 5 + getHeight(), 7 + getHeight()));
		shadows.add(new Fake3DShadowData(createRegularPolygon(8, 5.6), 7 + getHeight(), 10 + getHeight()));
		shadows.add(new Fake3DShadowData(createRegularPolygon(8, 4.8), 10 + getHeight(), 12 + getHeight()));
		shadows.add(new Fake3DShadowData(createRegularPolygon(8, 0.8), 12 + getHeight(), 15 + getHeight()));
		f3dStrat.setShadows(shadows);
	}
	
	

	@Override
	public void init() {
		Fake3DDrawStrategy f3dStrat = getDrawStrategy();

		DecimalFormat fm = new DecimalFormat("0000");

		int skip = 1;
		int res = 512;
		if (TankGameClient.getInstance().getQuality() >= 1) {
			skip = 2;
		}
		if (TankGameClient.getInstance().getQuality() >= 2) {
			res = 256;
		}
		String resFolder = res == 512 ? "tree2" : "tree2/tree2_low";
		f3dStrat.setStep(skip * 0.5);

		GraphicsBackend backend = ClientManager.getInstance().getGraphicsBackend();
		for (int i = 1; i < 28; i += skip) {
			f3dStrat.addTexture(backend.createGameTexture("/res/" + resFolder + "/" + fm.format(i) + ".png"));
		}

		ArrayList<Fake3DShadowData> shadows = f3dStrat.getShadows();
		shadows.add(new Fake3DShadowData(createRegularPolygon(8, 0.2), 0 + getHeight(), 5 + getHeight()));
		shadows.add(new Fake3DShadowData(createRegularPolygon(8, 4), 5 + getHeight(), 7 + getHeight()));
		shadows.add(new Fake3DShadowData(createRegularPolygon(8, 5.6), 7 + getHeight(), 10 + getHeight()));
		shadows.add(new Fake3DShadowData(createRegularPolygon(8, 4.8), 10 + getHeight(), 12 + getHeight()));
		shadows.add(new Fake3DShadowData(createRegularPolygon(8, 0.8), 12 + getHeight(), 15 + getHeight()));
		f3dStrat.setShadows(shadows);

		double fac = 512d / res;
		setScale(0.2 * fac, 0.2 * fac);
	}
}
