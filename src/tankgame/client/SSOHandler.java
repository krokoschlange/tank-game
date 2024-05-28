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
import blackhole.client.game.input.MouseControl;
import blackhole.client.graphicsEngine.HandlerPanel;
import blackhole.client.graphicsEngine.AbstractGameWindow;
import blackhole.networkData.ObjectSpawn;
import blackhole.utils.Debug;
import blackhole.utils.Vector;
import java.awt.Color;

/**
 *
 * @author fabian
 */
public class SSOHandler extends ClientObjectHandler {

	private ClientTeamManager teamManager;

	public SSOHandler() {
		HandlerPanel pan = new HandlerPanel();
		setPanel(pan);
		pan.background = new Color(128, 128, 128);
		pan.position = new Vector(0, 0);
		AbstractGameWindow win = ClientManager.getInstance().getGraphicsBackend().getWindow();
		pan.size = new Vector(win.getWidth(), win.getHeight());

		//MouseControl.getInstance().setCurserCaptured(true);
	}
	
	public ClientTeamManager getTeamManager() {
		return teamManager;
	}

	@Override
	public void step(double dtime) {
		/*for (int i = 0; i < getObjects().size(); i++) {
			Debug.log(getObjects().get(i));
		}
		Debug.log("CLIENT: " + getObjects().size() + "-----");*/
	}

	@Override
	public void init() {
		Fake3DHandler fh = Fake3DHandler.getInstance();
		fh.setHandler(this);
		fh.activate();
		
		teamManager = new ClientTeamManager();
		new ClientBaseManager();
	}

	@Override
	public void cleanUp() {
	}

	@Override
	public void windowResized(int newW, int newH) {
		getPanel().size.set(newW, newH);
		//double ratio = (1. * newH) / newW;
		//getCamera().setHeight((int) (getCamera().getWidth() * ratio));
	}
}
