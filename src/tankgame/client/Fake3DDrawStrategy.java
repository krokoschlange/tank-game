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
import blackhole.client.game.DrawStrategy;
import blackhole.client.graphicsEngine.AbstractDrawer;
import blackhole.client.graphicsEngine.Camera;
import blackhole.client.graphicsEngine.GameDrawable;
import blackhole.common.GameObject;
import blackhole.utils.Vector;
import java.util.ArrayList;

/**
 *
 * @author fabian
 */
public class Fake3DDrawStrategy implements DrawStrategy {

	private GameObject object;

	private double baseHeight;
	private double step;
	private ArrayList<GameDrawable> textures;

	private ArrayList<Fake3DShadowData> shadows;

	protected Fake3DHandler handler3D;

	public Fake3DDrawStrategy() {
		handler3D = Fake3DHandler.getInstance();

		textures = new ArrayList<>();
		shadows = new ArrayList<>();
	}

	@Override
	public void activate(GameObject obj) {
		DrawStrategy.super.activate(obj);
		handler3D.addObject(this);
	}

	@Override
	public void remove() {
		DrawStrategy.super.remove();
		handler3D.removeObject(this);
	}

	@Override
	public void setObject(GameObject obj) {
		object = obj;
	}

	@Override
	public GameObject getObject() {
		return object;
	}

	public void setBaseHeight(double bH) {
		baseHeight = bH;
	}

	public double getBaseHeight() {
		return baseHeight;
	}

	public void setStep(double sH) {
		step = sH;
	}

	public double getStep() {
		return step;
	}

	public void addTexture(GameDrawable tex) {
		textures.add(tex);
	}

	public void setTexture(GameDrawable tex, int i) {
		if (i >= 0 && i < textures.size()) {
			textures.set(i, tex);
		} else {
			addTexture(tex);
		}
	}

	public void removeTexture(GameDrawable tex) {
		if (textures.contains(tex)) {
			textures.remove(tex);
		}
	}

	public void removeTexture(int tex) {
		if (tex >= 0 && tex < textures.size()) {
			textures.remove(tex);
		}
	}

	public ArrayList<Integer> getDrawPositions() {
		ArrayList<Integer> dp = new ArrayList<>();
		for (int i = 0; i < textures.size(); i++) {
			//dp.add(((int) (baseHeight + step * i)) * handler3D.getHeightLayerFactor());
			dp.add(handler3D.getDrawPosFromHeight(baseHeight + step * i));
		}
		return dp;
	}

	public void clearTextures() {
		textures.clear();
	}

	public GameDrawable getTexture(int i) {
		return textures.get(i);
	}

	public ArrayList<GameDrawable> getTextures() {
		return textures;
	}

	public void setShadows(ArrayList<Fake3DShadowData> shad) {
		shadows = shad;
	}

	public ArrayList<Fake3DShadowData> getShadows() {
		return shadows;
	}

	public ArrayList<Fake3DShadowData> getShadowData(double height) {
		ArrayList<Fake3DShadowData> ret = new ArrayList<>();
		for (int i = 0; i < shadows.size(); i++) {
			if (shadows.get(i).lowEnd <= height && height < shadows.get(i).highEnd) {
				ret.add(shadows.get(i));
			}
		}
		return ret;
	}
	
	public Vector transformPoint(Vector pos, double height, Camera cam) {
		return Vector.add(pos, Vector.subtract(pos, cam.getPosition()).multiply(height * handler3D.getShift3DFactor()));
	}

	public void draw3D(int drawPos) {
		/*int tex = -1;
		for (int i = 0; tex == -1 && i < textures.size(); i++) {
			int dp = ((int) (baseHeight + step * i)) * handler3D.getHeightLayerFactor();
			if (dp == drawPos) {
				tex = i;
			}
		}
		double height = baseHeight + tex * step;
		if (tex != -1) {
			try {
				if (getObject().isVisible()) {
					ClientObjectHandler chandler = (ClientObjectHandler) getObject().getHandler();
					AbstractDrawer drawer = ClientManager.getInstance().getGraphicsBackend().getDrawer();

					Vector pos = transformPoint(getObject().getRealPosition(), height, chandler.getCamera());

					drawer.drawTexture(textures.get(tex), 1, pos,
							getObject().getRealRotation(),
							Vector.multiply(getObject().getRealScale(), 1 + handler3D.getScale3DFactor() * height),
							chandler.getCamera(), chandler.getPanel(), chandler.getScale());

				}
			} catch (NullPointerException e) {
			}
		}*/
		
		for (int i = 0; i < textures.size(); i++) {
			double height = baseHeight + i * step;
			if (handler3D.getDrawPosFromHeight(height) == drawPos) {
				try {
				if (getObject().isVisible()) {
					ClientObjectHandler chandler = (ClientObjectHandler) getObject().getHandler();
					AbstractDrawer drawer = ClientManager.getInstance().getGraphicsBackend().getDrawer();

					Vector pos = transformPoint(getObject().getRealPosition(), height, chandler.getCamera());

					drawer.drawTexture(textures.get(i), 1, pos,
							getObject().getRealRotation(),
							Vector.multiply(getObject().getRealScale(), 1 + handler3D.getScale3DFactor() * height),
							chandler.getCamera(), chandler.getPanel(), chandler.getScale());

				}
			} catch (NullPointerException e) {
			}
			}
		}
	}

	@Override
	public void draw(int drawPos) {
		//draw3D(drawPos);
		// we don't draw here
	}

}
