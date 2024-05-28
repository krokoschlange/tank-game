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
import blackhole.client.graphicsEngine.GraphicsBackend;
import blackhole.common.GameObjectUpdateStrategy;
import blackhole.utils.Debug;
import tankgame.client.vehicles.ClientVehicle;

/**
 *
 * @author fabian
 */
public class ClientPlayer extends Fake3DClientObject {

	private ClientVehicle currentVehicle;

	private ClientTeam currentTeam;

	private boolean isSpawned;
	private double hpRatio;

	private boolean regenTex;

	public ClientPlayer() {
		Fake3DDrawStrategy f3dStrat = new Fake3DDrawStrategy();
		setDrawStrategy(f3dStrat);

		GameObjectUpdateStrategy ustrat = getDefaultUpdateStrategy();
		ustrat.addParameter("team", (t)
				-> {
			ClientTeamManager manager = ClientTeamManager.getCurrentTeamManager();
			setTeam(manager.getTeamByName((String) t));
		}, () -> {
			Debug.log(currentTeam.getName());
			return currentTeam.getName();
		});

		ustrat.addParameter("isSpawned", (s) -> {
			boolean spawned = (boolean) s;
			if (spawned) {
				spawn();
			} else {
				despawn();
			}
		}, () -> {
			return isSpawned();
		});
		ustrat.addParameter("hp", (s) -> {
			hpRatio = (double) s;
		}, () -> {
			return hpRatio;
		});

		isSpawned = false;
	}

	public void spawn() {
		isSpawned = true;
		setTextures();
	}

	public void despawn() {
		isSpawned = false;
	}

	public boolean isSpawned() {
		return isSpawned;
	}

	public void setTeam(ClientTeam t) {
		if (t != currentTeam) {
			currentTeam = t;
			//setTextures();
		}
	}

	public ClientTeam getCurrentTeam() {
		return currentTeam;
	}

	public void setVehicle(ClientVehicle v) {
		currentVehicle = v;
	}

	public ClientVehicle getVehicle() {
		return currentVehicle;
	}

	public double getHPRatio() {
		return hpRatio;
	}

	@Override
	public void step(double dtime) {
		super.step(dtime); //To change body of generated methods, choose Tools | Templates.
		if (regenTex) {
			regenTex = false;
			String teamName = "";
			if (getCurrentTeam() != null) {
				teamName = getCurrentTeam().getName();
			}
			switch (teamName) {
				case "Finland":
					break;
				case "USSR":
					break;
				default:
					teamName = "USSR";
					break;
			}
			//teamName = "USSR";
			Fake3DDrawStrategy f3dStrat = getDrawStrategy();
			f3dStrat.setBaseHeight(0);
			f3dStrat.setStep(0.25);

			GraphicsBackend backend = ClientManager.getInstance().getGraphicsBackend();
			f3dStrat.clearTextures();
			f3dStrat.addTexture(backend.createGameTexture("/res/soldier/" + teamName + "/soldier5.png"));
			f3dStrat.addTexture(backend.createGameTexture("/res/soldier/" + teamName + "/soldier4.png"));
			f3dStrat.addTexture(backend.createGameTexture("/res/soldier/" + teamName + "/soldier4.png"));
			f3dStrat.addTexture(backend.createGameTexture("/res/soldier/" + teamName + "/soldier4.png"));
			f3dStrat.addTexture(backend.createGameTexture("/res/soldier/" + teamName + "/soldier3.png"));
			f3dStrat.addTexture(backend.createGameTexture("/res/soldier/" + teamName + "/soldier2.png"));
			f3dStrat.addTexture(backend.createGameTexture("/res/soldier/" + teamName + "/soldier2.png"));
			f3dStrat.addTexture(backend.createGameTexture("/res/soldier/" + teamName + "/soldier1.png"));
		}
	}

	public void setTextures() {
		regenTex = true;
	}

	@Override
	public void init() {
		super.init();
	}
}
