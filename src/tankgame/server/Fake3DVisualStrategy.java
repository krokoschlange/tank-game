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
package tankgame.server;

import blackhole.common.GameObject;
import blackhole.common.UpdateStrategy;
import blackhole.networkData.ObjectUpdate;
import blackhole.networkData.updateData.TextureUpdate;
import blackhole.server.data.TextureData;
import java.util.ArrayList;
import tankgame.client.Fake3DShadowData;

/**
 *
 * @author fabian
 */
public class Fake3DVisualStrategy implements UpdateStrategy {

	private GameObject object;

	private double baseHeight;
	private double step;
	private ArrayList<TextureData> textures;

	private ArrayList<Fake3DShadowData> shadows;

	public Fake3DVisualStrategy() {
		baseHeight = 0;
		step = 0.5;
	}

	@Override
	public void setObject(GameObject obj) {
		object = obj;
		textures = new ArrayList<>();
		shadows = new ArrayList<>();
	}

	@Override
	public GameObject getObject() {
		return object;
	}

	public void setBaseHeight(double bH) {
		baseHeight = bH;
		object.addToUpdate("base_height");
	}

	public double getBaseHeight() {
		return baseHeight;
	}

	public void setStep(double s) {
		step = s;
		object.addToUpdate("step");
	}

	public double getStep() {
		return step;
	}

	public void addTexture(TextureData tex) {
		textures.add(tex);
		object.addToUpdate("update_texture");
	}

	public void addTexture(String tex) {
		textures.add(new TextureData(tex));
		object.addToUpdate("update_texture");
	}

	public void setTexture(TextureData tex, int i) {
		if (i >= 0 && i < textures.size()) {
			textures.set(i, tex);
			object.addToUpdate("update_texture");
		} else {
			addTexture(tex);
		}
	}

	public void setTexture(String tex, int i) {
		if (i >= 0 && i < textures.size()) {
			textures.set(i, new TextureData(tex));
			object.addToUpdate("update_texture");
		} else {
			addTexture(tex);
		}
	}

	public void removeTexture(TextureData tex) {
		textures.remove(tex);
		object.addToUpdate("update_texture");
	}

	public void removeTexture(int tex) {
		textures.remove(tex);
		object.addToUpdate("update_texture");
	}

	public TextureData getTexture(int tex) {
		return textures.get(tex);
	}

	public void setShadows(ArrayList<Fake3DShadowData> shad) {
		shadows = shad;
		object.addToUpdate("shadows");
	}

	public ArrayList<Fake3DShadowData> getShadows() {
		return shadows;
	}

	public ArrayList<TextureData> getTextures() {
		return textures;
	}

	@Override
	public ObjectUpdate getUpdate(ObjectUpdate update) {
		ArrayList<String> updateData = object.getUpdateData();
		if (updateData.contains("update_texture")) {
			ArrayList<TextureUpdate> texData = new ArrayList<>();
			for (int i = 0; i < textures.size(); i++) {
				texData.add(textures.get(i).getUpdateData());
			}
			update.data.put("update_texture", texData);
		}
		if (updateData.contains("base_height")) {
			update.data.put("base_height", getBaseHeight());
		}
		if (updateData.contains("step")) {
			update.data.put("step", getStep());
		}
		if (updateData.contains("shadows")) {
			update.data.put("shadows", getShadows());
		}
		return update;
	}

	@Override
	public ObjectUpdate getUpdateAll(ObjectUpdate update) {
		ArrayList<TextureUpdate> texData = new ArrayList<>();
		for (int i = 0; i < textures.size(); i++) {
			texData.add(textures.get(i).getUpdateData());
		}
		update.data.put("update_texture", texData);
		update.data.put("base_height", getBaseHeight());
		update.data.put("step", getStep());
		update.data.put("shadows", getShadows());

		return update;
	}

	@Override
	public void update(ObjectUpdate update) {

	}
}
