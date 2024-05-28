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
import blackhole.client.graphicsEngine.GameDrawable;
import blackhole.client.graphicsEngine.GraphicsBackend;
import blackhole.common.GameObject;
import blackhole.common.UpdateStrategy;
import blackhole.networkData.ObjectUpdate;
import blackhole.networkData.updateData.AnimationUpdate;
import blackhole.networkData.updateData.DrawableUpdate;
import blackhole.networkData.updateData.TextureUpdate;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author fabian
 */
public class Fake3DDrawUpdateStrategy extends Fake3DDrawStrategy implements UpdateStrategy {

	@Override
	public ObjectUpdate getUpdate(ObjectUpdate update) {
		GameObject obj = getObject();
		if (obj == null) {
			return update;
		}
		ArrayList<String> updateData = getObject().getUpdateData();
		if (updateData.contains("update_texture")) {
			ArrayList<TextureUpdate> texData = new ArrayList<>();
			for (int i = 0; i < getTextures().size(); i++) {
				GameDrawable tex = getTexture(i);
				TextureUpdate tupdt = new TextureUpdate();
				tupdt.name = tex.getName();
				tupdt.offset = tex.getOffset();
				tupdt.rotationOffset = tex.getRotOffset();
				//tupdt.filters = null; //TODO (maybe)
				texData.add(tupdt);
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
		for (int i = 0; i < getTextures().size(); i++) {
			GameDrawable tex = getTexture(i);
			TextureUpdate tupdt = new TextureUpdate();
			tupdt.name = tex.getName();
			tupdt.offset = tex.getOffset();
			tupdt.rotationOffset = tex.getRotOffset();
			texData.add(tupdt);
		}
		update.data.put("update_texture", texData);
		update.data.put("base_height", getBaseHeight());
		update.data.put("step", getStep());
		update.data.put("shadows", getShadows());

		return update;
	}

	@Override
	public void update(ObjectUpdate update) {
		HashMap<String, Object> data = update.data;
		if (data == null) {
			return;
		}
		if (data.containsKey("update_texture")) {
			getTextures().clear();
			ArrayList<DrawableUpdate> texData = (ArrayList<DrawableUpdate>) data.get("update_texture");
			for (int i = 0; i < texData.size(); i++) {

				GraphicsBackend backend = ClientManager.getInstance().getGraphicsBackend();
				if (texData.get(i) instanceof AnimationUpdate) {
					//addTexture(new AnimatedTexture((AnimationUpdate) texData.get(i)));
					addTexture(backend.createGameAnimation((AnimationUpdate) texData.get(i)));
				} else {
					//addTexture(new Texture(texData.get(i)));
					addTexture(backend.createGameTexture((TextureUpdate) texData.get(i)));
				}
				/*Texture tex = ResourceLoader.getTexture(texData.get(i).name);
				tex.setOffset(texData.get(i).offset);
				tex.setRotationOffset(texData.get(i).rotationOffset);
				for (TextureFilterData filter : texData.get(i).filters) {
					RGBImageFilter fil = FilterHandler.getInstance().getFilter(filter.name, filter.args);
					tex.applyFilter(fil);
				}
				tex.useBuffer(texData.get(i).useBuffer);
				addTexture(tex);*/
			}
		}
		if (data.containsKey("base_height")) {
			setBaseHeight((double) data.get("base_height"));
		}
		if (data.containsKey("step")) {
			setStep((double) data.get("step"));
		}
		if (data.containsKey("shadows")) {
			setShadows((ArrayList<Fake3DShadowData>) data.get("shadows"));
		}
	}
}
