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
package tankgame.client.sights;

import blackhole.client.game.ClientManager;
import blackhole.client.graphicsEngine.AbstractGameWindow;
import blackhole.utils.Debug;
import blackhole.utils.Vector;
import java.awt.Color;
import tankgame.client.TankGameClient;
import tankgame.client.ui.Group;
import tankgame.client.ui.ImageLabel;
import tankgame.client.ui.Theme;
import tankgame.client.ui.UI;
import tankgame.client.ui.UIHandler;

/**
 *
 * @author fabian
 */
public class AimVisionDevice extends VisionDevice {

	private ImageLabel crosshair;
	private ImageLabel distanceLines;
	private UI ui;

	public AimVisionDevice() {
		Theme transparent = new Theme(new Color(0, 0, 0, 0), Color.gray,
				Color.cyan, Color.black, Color.black);
		AbstractGameWindow win = ClientManager.getInstance().getGraphicsBackend().getWindow();

		ui = new UI();
		ui.setTheme(transparent);
		ui.setSize(new Vector(win.getWidth(), win.getHeight()));
		crosshair = new ImageLabel(ui);
		crosshair.setDrawMode(ImageLabel.DrawMode.RESIZE_MAX);
		crosshair.setSize(new Vector(1, 1));
		ui.addChild(crosshair, 0, 0, 1, 1, Group.IGNORE);
		distanceLines = new ImageLabel(ui);
		distanceLines.setDrawMode(ImageLabel.DrawMode.RESIZE_MAX);
		distanceLines.setSize(new Vector(1, 1));
		ui.addChild(distanceLines, 0, 0, 1, 1, Group.IGNORE);
		ui.setDrawPosition(8);
	}
	
	public void setCrosshair(String path) {
		crosshair.setDrawable(ClientManager.getInstance().getGraphicsBackend().createGameTexture(path));
	}
	
	public void setDistanceLines(String path) {
		distanceLines.setDrawable(ClientManager.getInstance().getGraphicsBackend().createGameTexture(path));
	}
	
	public void setElevation(double angle) {
		double posy = angle / getFOV();
		distanceLines.getPosition().set(distanceLines.getPosition().x(), -posy);
		distanceLines.setPosition(distanceLines.getPosition());
	}

	@Override
	public void activate() {
		TankGameClient.getInstance().activateUI(ui);
	}

	@Override
	public void deactivate() {
		TankGameClient.getInstance().deactivateUI(ui);
	}
}
