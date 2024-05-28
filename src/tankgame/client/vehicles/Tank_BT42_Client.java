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
package tankgame.client.vehicles;

import blackhole.client.game.ClientManager;
import blackhole.client.graphicsEngine.GameAnimation;
import blackhole.client.graphicsEngine.GraphicsBackend;
import blackhole.common.GameObjectUpdateStrategy;
import blackhole.utils.Debug;
import blackhole.utils.Vector;
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import tankgame.client.ClientGun;
import tankgame.client.ClientParticleSpawner;
import tankgame.client.Fake3DDrawStrategy;
import tankgame.client.sights.AimVisionDevice;
import tankgame.client.ui.Group;
import tankgame.client.ui.Label;
import tankgame.client.ui.Theme;
import tankgame.client.ui.UI;
import tankgame.client.visuals.FireEffect;
import tankgame.common.Position3D;

/**
 *
 * @author fabian
 */
public class Tank_BT42_Client extends ClientVehicle {

	private ArrayList<GameAnimation> textures;

	private double driven;

	private int gunID;

	private ClientParticleSpawner exhaust1;
	private ClientParticleSpawner exhaust2;

	private double engineRPM;
	private int gear;
	private FireEffect fire;
	private boolean dead;

	private UI ui;
	private Label spdLabel;
	private Label rpmLabel;
	private Label gearLabel;

	public Tank_BT42_Client() {
		Fake3DDrawStrategy f3dStrat = new Fake3DDrawStrategy();
		setDrawStrategy(f3dStrat);
		//addUpdateStrategy(f3dStrat);
		textures = new ArrayList<>();
	}

	@Override
	public ClientGun getGun(int num) {
		ClientGun gun = (ClientGun) getHandler().getObjectByID(gunID);
		if (gun != null && gun.getGunSight() == null) {
			AimVisionDevice vd = new AimVisionDevice();
			vd.setCrosshair("/res/simple_sight.png");
			vd.setDistanceLines("/res/simple_sight_horiz.png");
			gun.setGunSight(vd);
			
			gun.setMinElevation(-0.0872665);
			gun.setMaxElevation(0.610865);
		}
		return gun;
	}

	@Override
	public UI getUI() {
		if (ui == null) {
			Theme transparent = new Theme(new Color(0, 0, 0, 0), Color.gray,
					Color.cyan, Color.black, Color.black);
			ui = new UI() {
				@Override
				public void step(double dtime) {
					super.step(dtime);
					int vel = (int) (Math.signum(new Vector(0, 1).rotate(getRotation()).dot(getVelocity())) * getVelocity().magnitude() * 3.6);
					spdLabel.setText(Integer.toString(vel) + " km/h");
					
					String gearText = Integer.toString(gear);
					if (gear < 1) {
						gearText = "R" + (-gear + 1);
					}
					gearLabel.setText("GEAR " + gearText);
					
					rpmLabel.setText("RPM " + Integer.toString((int) (engineRPM * 9.5493)));
				}
			};
			ui.setTheme(transparent);
			
			spdLabel = new Label(ui);
			spdLabel.setSize(new Vector(0.15, 0.03));
			spdLabel.setTheme(transparent);
			spdLabel.setPosition(new Vector(0.01, 0.88));
			ui.addChild(spdLabel, 0, 0, 1, 1, Group.IGNORE);
			
			rpmLabel = new Label(ui);
			rpmLabel.setSize(new Vector(0.15, 0.03));
			rpmLabel.setTheme(transparent);
			rpmLabel.setPosition(new Vector(0.01, 0.92));
			ui.addChild(rpmLabel, 0, 0, 1, 1, Group.IGNORE);
			
			gearLabel = new Label(ui);
			gearLabel.setSize(new Vector(0.15, 0.03));
			gearLabel.setTheme(transparent);
			gearLabel.setPosition(new Vector(0.01, 0.96));
			ui.addChild(gearLabel, 0, 0, 1, 1, Group.IGNORE);
			
			
		}
		return ui;
	}

	@Override
	public void step(double dtime) {
		double driveSpeed = new Vector(0, 1).rotate(getRotation()).dot(getVelocity());
		double frameLength = 0.1; // how far the tank has to move in between two animation frames

		driven += driveSpeed * dtime;

		int frame = (int) (driven / frameLength);

		frame = ((frame % 3) + 3) % 3;

		for (int i = 0; i < textures.size(); i++) {
			textures.get(i).setFrameNumber(frame);
		}

		exhaust1.setSpawnRate((1 - Math.pow(2, -0.015 * engineRPM)) * 20);
		exhaust2.setSpawnRate((1 - Math.pow(2, -0.015 * engineRPM)) * 20);

		double forwardVel = getVelocity().dot(new Vector(0, 1).rotate(getRealRotation()));
		exhaust1.setInitialVelocity(new Position3D(new Vector(0, -2 + forwardVel * 0.5), 0.1));
		exhaust2.setInitialVelocity(new Position3D(new Vector(0, -2 + forwardVel * 0.5), 0.1));
		exhaust1.setAcceleration(new Position3D(new Vector(0, 0.02 + 0.05 * forwardVel), 0.1));
		exhaust2.setAcceleration(new Position3D(new Vector(0, 0.02 + 0.05 * forwardVel), 0.1));
	}

	@Override
	public void init() {
		GameObjectUpdateStrategy ustrat = getDefaultUpdateStrategy();

		fire = new FireEffect(3);
		fire.setParent(this);
		fire.setPosition(0, -1);
		fire.setHeight(1.7);
		fire.setHandler(getHandler());
		fire.activate();

		ustrat.addParameter("gunID",
				(val) -> {
					gunID = (Integer) val;
				},
				() -> {
					return -1;
				});
		ustrat.addParameter("engineRPM",
				(val) -> {
					engineRPM = (double) val;
				},
				() -> {
					return null;
				}
		);
		ustrat.addParameter("gear",
				(val) -> {
					gear = (int) val;
				},
				() -> {
					return null;
				}
		);
		ustrat.addParameter("fire",
				(val) -> {
					boolean onFire = (boolean) val;
					Debug.log("FIRE" + onFire);
					if (onFire) {
						fire.start();
					} else {
						fire.end();
					}
				},
				() -> {
					return null;
				}
		);

		Fake3DDrawStrategy f3dStrat = getDrawStrategy();
		f3dStrat.setBaseHeight(0);
		f3dStrat.setStep(0.25);

		GraphicsBackend backend = ClientManager.getInstance().getGraphicsBackend();

		for (int i = 0; i < 8; i++) {
			int imgnum = i * 4 + 1;

			ArrayList<String> anim = new ArrayList<>();
			DecimalFormat df = new DecimalFormat("0000");
			for (int j = 0; j < 3; j++) {
				String path = "/res/bt42_3D/" + df.format(imgnum + j) + ".png";
				backend.loadImage(path);
				anim.add(path);
			}

			//AnimatedTexture tex = new AnimatedTexture(anim, "/res/bt42_3D/0001.png");
			GameAnimation tex = backend.createGameAnimation(anim);
			tex.setRotOffset(Math.PI / 2);
			tex.setOffset(new Vector(0, 0.5));

			f3dStrat.addTexture(tex);
			textures.add(tex);
		}

		setScale(0.077, 0.077);
		setVisible(true);

		exhaust1 = new ClientParticleSpawner();
		initExhaust(exhaust1, new Vector(0.45, -2.3));

		exhaust2 = new ClientParticleSpawner();
		initExhaust(exhaust2, new Vector(-0.45, -2.3));
	}

	private void initExhaust(ClientParticleSpawner exhaust, Vector pos) {
		GraphicsBackend backend = ClientManager.getInstance().getGraphicsBackend();

		exhaust.getDrawStrategy().addTexture(backend.createGameTexture("/res/smoke.png"));
		exhaust.getDrawStrategy().setStep(0.5);
		exhaust.setParent(this);
		exhaust.setHeight(1.7);
		exhaust.setPosition(pos);

		exhaust.setLifetime(0.7);
		exhaust.setSpawnRate(50);

		exhaust.setPositionSpread(new Position3D(new Vector(0.05, 0.05), 0.05));
		exhaust.setInitialVelocity(new Position3D(new Vector(0, -0.5), 0.1));
		exhaust.setVelocitySpread(new Position3D(new Vector(0.02, 0.02), 0.02));
		exhaust.setAcceleration(new Position3D(new Vector(0, 0.01), 0.01));
		exhaust.setAccelerationSpread(new Position3D(new Vector(0.005, 0.005), 0.005));

		exhaust.setParticleSize(new Vector(0.08, 0.08));
		exhaust.setSizeSpread(new Vector(0.01, 0.01));
		exhaust.setSizeVelocity(new Vector(0.01, 0.01));
		exhaust.setSizeVelocitySpread(new Vector(0.001, 0.001));
		exhaust.setSizeAcceleration(new Vector(0.015, 0.015));
		exhaust.setSizeAccelerationSpread(new Vector(0.005, 0.005));

		exhaust.setVisible(true);
		exhaust.setHandler(getHandler());
		exhaust.activate();
	}

	@Override
	public void remove() {
		exhaust1.remove();
		exhaust2.remove();
		fire.remove();
		super.remove();
	}
}
