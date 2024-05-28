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
import blackhole.client.game.ClientObject;
import blackhole.client.game.ClientObjectHandler;
import blackhole.client.graphicsEngine.AbstractDrawer;
import blackhole.client.graphicsEngine.AbstractFramebuffer;
import blackhole.client.graphicsEngine.Camera;
import blackhole.client.graphicsEngine.HandlerPanel;
import blackhole.server.physicsEngine.boundingAreas.Polygon;
import blackhole.utils.Settings;
import blackhole.utils.Vector;
import java.util.ArrayList;

/**
 *
 * @author fabian
 */
public class Fake3DHandler extends ClientObject {

	private static Fake3DHandler SINGLETON_OBJ;

	private PlayerClientObject playerObj;

	private ArrayList<Fake3DDrawStrategy> objects;

	private AbstractFramebuffer framebuffer;

	private int heightLayerFactor;
	private double scale3DFactor;
	//private int layerSkip;
	private double shift3DFactor;

	public static Fake3DHandler getInstance() {
		if (SINGLETON_OBJ == null) {
			SINGLETON_OBJ = new Fake3DHandler();
		}
		return SINGLETON_OBJ;
	}

	private Fake3DHandler() {
		objects = new ArrayList<>();
		heightLayerFactor = 10;
		//layerSkip = 5;
		scale3DFactor = 0.03;
		shift3DFactor = 0.02;
	}

	public void addObject(Fake3DDrawStrategy obj) {
		if (!objects.contains(obj)) {
			objects.add(obj);
		}
	}

	public void removeObject(Fake3DDrawStrategy obj) {
		if (objects.contains(obj)) {
			objects.remove(obj);
		}
	}

	public void updateBuffer() {
		ClientObjectHandler chandler = (ClientObjectHandler) getHandler();
		AbstractDrawer drawer = ClientManager.getInstance().getGraphicsBackend().getDrawer();
		if (framebuffer == null
				|| (framebuffer.getWidth() != (int) chandler.getPanel().size.x() / 1
				|| framebuffer.getHeight() != (int) chandler.getPanel().size.y() / 1)) {
			if (framebuffer != null) {
				drawer.destroyFramebuffer(framebuffer, true);
			}
			framebuffer = drawer.createFramebuffer((int) chandler.getPanel().size.x() / 1,
					(int) chandler.getPanel().size.y() / 1);
			if (!framebuffer.isReady()) {
				drawer.destroyFramebuffer(framebuffer);
				framebuffer = null;
			}
		}
		if (framebuffer != null) {
			drawer.setDrawBuffer(framebuffer);
			drawer.clear(0.3, 0.3, 0.3, 0.0);
			drawer.setDrawBuffer(null);
		}

	}

	public int getHeightLayerFactor() {
		return heightLayerFactor;
	}

	public double getShift3DFactor() {
		return shift3DFactor;
	}

	public double getScale3DFactor() {
		return scale3DFactor;
	}

	public void setPlayerObject(PlayerClientObject obj) {
		playerObj = obj;
	}

	public int getDrawPosFromHeight(double h) {
		return ((int) h) * heightLayerFactor;
	}

	@Override
	public void onDraw(double dtime) {
		updateBuffer();
	}

	@Override
	public void draw(int drawPos) {
		if (drawPos % heightLayerFactor == 0) {
			ClientObjectHandler chandler = (ClientObjectHandler) getHandler();

			setPosition(chandler.getCamera().getPosition());
			setRotation(chandler.getCamera().getRotation());
			setScale(Vector.divide(chandler.getCamera().getSize(), chandler.getPanel().size));
			if (framebuffer == null) {
				return;
			}

			double height = drawPos / heightLayerFactor;

			AbstractDrawer drawer = ClientManager.getInstance().getGraphicsBackend().getDrawer();

			drawer.setDrawBuffer(framebuffer);
			drawer.clear(0, 0, 0, 0);
			for (int i = 0; i < objects.size(); i++) {
				objects.get(i).draw3D(drawPos);
			}

			double alpha = 0.07;
			if (drawPos == 0) {
				alpha = 1;
			}

			double game_scale = Double.parseDouble(Settings.getProperty("game_scale"));

			HandlerPanel panel = chandler.getPanel();
			Camera cam = chandler.getCamera();

			for (int i = 0; i < objects.size(); i++) {
				Fake3DDrawStrategy obj = objects.get(i);
				if (obj.getObject().isVisible()) {
					ArrayList<Fake3DShadowData> shadows = obj.getShadowData(height);
					if (playerObj != null) {
						Vector camPos = cam.getPosition();

						for (int j = 0; j < shadows.size(); j++) {
							Vector playerPos = playerObj.getRealPosition();
							Polygon poly = shadows.get(j).area;

							for (int k = 0; k < poly.getEdges().length; k++) {
								Vector p1 = Vector.add(obj.getObject().getRealPosition(), poly.getPoint(k));
								Vector dir = Vector.subtract(p1, playerPos);
								if (dir.dot(poly.getEdge(k).getNormal()) > 0) {
									Vector p13D = obj.transformPoint(p1, height, cam);
									Vector p2 = Vector.add(obj.getObject().getRealPosition(), poly.nextPoint(k));
									Vector p23D = obj.transformPoint(p2, height, cam);

									Vector cameraScale = new Vector(panel.size.x() * 1.0 / cam.getWidth(),
											-(panel.size.y() * 1.0 / cam.getHeight())
									);

									Vector p1s = Vector.subtract(p13D, camPos).multiply(game_scale).multiply(cameraScale);
									Vector p2s = Vector.subtract(p23D, camPos).multiply(game_scale).multiply(cameraScale);

									p1s.rotate(-cam.getRotation());
									p2s.rotate(-cam.getRotation());

									Vector pPp1 = Vector.subtract(p1, playerPos).multiply(new Vector(1, -1));
									Vector pPp2 = Vector.subtract(p2, playerPos).multiply(new Vector(1, -1));

									double angle = Math.acos(pPp1.dot(pPp2) / (pPp1.magnitude() * pPp2.magnitude())) / 2;

									double bufferDiag = panel.size.magnitude();
									Vector middle = Vector.add(p13D, p23D).divide(2);
									Vector middle2 = Vector.add(p1, p2).divide(2);
									Vector camVector = Vector.subtract(camPos, middle).multiply(game_scale).multiply(cameraScale);
									Vector playerVector = Vector.subtract(middle2, playerPos).multiply(cameraScale).normalize();
									double length = Math.max(0, Vector.dot(camVector, playerVector)) + bufferDiag;

									pPp1.normalize().multiply(length / Math.cos(angle)).add(p1s);
									pPp2.normalize().multiply(length / Math.cos(angle)).add(p2s);

									drawer.drawPolygon(0.3, 0.3, 0.3, alpha, false, p1s, p2s, pPp2, pPp1);
								}
							}
						}
					}
				}
			}
			drawer.setDrawBuffer(null);

			Vector fbSize = drawer.getDrawbufferSize();
			Vector panSize = panel.size;
			Vector panPos = panel.position;
			drawer.drawTexture(framebuffer.toTexture(), 1,
					panPos.x() - fbSize.x() / 2, panPos.y() - fbSize.y() / 2,
					panPos.x() + panSize.x() - fbSize.x() / 2,
					panPos.y() + panSize.y() - fbSize.y() / 2);
		}
	}

	@Override
	public ArrayList<Integer> getDrawPositions() {
		ArrayList<Integer> dp = new ArrayList<>();
		for (int i = 0; i < objects.size(); i++) {
			dp.addAll(objects.get(i).getDrawPositions());
		}
		return dp;
	}

	@Override
	public void init() {
		updateBuffer();
	}
	
	public void clear() {
		objects.clear();
	}
}
