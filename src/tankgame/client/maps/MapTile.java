/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tankgame.client.maps;

import blackhole.client.game.ClientManager;
import blackhole.client.game.ClientObject;
import blackhole.client.game.ClientObjectHandler;
import blackhole.client.game.TextureDrawStrategy;
import blackhole.client.graphicsEngine.Camera;
import blackhole.client.graphicsEngine.GameTexture;
import blackhole.client.graphicsEngine.GraphicsBackend;
import blackhole.utils.Vector;

/**
 *
 * @author fabian
 */
public class MapTile extends ClientObject {

	private String texturePath;

	private TextureDrawStrategy drawStrat;
	private int texSize;

	private double textureUnloadTimer;

	public MapTile(String tex, int expectedTexSize) {
		texturePath = tex;
		texSize = expectedTexSize;
		drawStrat = new TextureDrawStrategy();
		setDrawStrategy(drawStrat);
		setDrawPosition(-3);
	}

	@Override
	public void step(double dtime) {
		ClientObjectHandler chandler = (ClientObjectHandler) getHandler();
		Camera cam = chandler.getCamera();
		double camDist = Vector.subtract(cam.getPosition(), getPosition()).magnitude();
		Vector size = new Vector(texSize, texSize);
		double rad = size.magnitude() / chandler.getScale();
		double camRad = cam.getSize().magnitude() / chandler.getScale();

		if (camDist > rad + camRad * 4) {
			textureUnloadTimer += dtime;
			if (textureUnloadTimer > 20) {
				setVisible(false);
				if (drawStrat.getTexture() != null) {
					GraphicsBackend backend = ClientManager.getInstance().getGraphicsBackend();
					backend.unloadImage(drawStrat.getTexture().getName());
					drawStrat.setTexture(null);
				}
			}
		} else {
			textureUnloadTimer = 0;
			if (!isVisible() || drawStrat.getTexture() == null) {
				setVisible(true);
				GraphicsBackend backend = ClientManager.getInstance().getGraphicsBackend();
				GameTexture tex = backend.createGameTexture(texturePath);
				drawStrat.setTexture(tex);
				texSize = tex.getWidth();
			}
		}
	}

}
