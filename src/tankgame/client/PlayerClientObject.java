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
import tankgame.client.sights.OverlayVisionDevice;
import tankgame.client.sights.VisionDevice;
import blackhole.client.game.ClientObjectHandler;
import blackhole.client.game.input.InputEvent;
import blackhole.client.game.input.InputEventListener;
import blackhole.client.game.input.InputHandler;
import blackhole.client.game.input.MouseControl;
import blackhole.client.graphicsEngine.AbstractDrawer;
import blackhole.client.graphicsEngine.Camera;
import blackhole.client.graphicsEngine.HandlerPanel;
import blackhole.common.GameObjectUpdateStrategy;
import blackhole.utils.Debug;
import blackhole.utils.Vector;
import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import tankgame.client.ui.Button;
import tankgame.client.ui.DropDown;
import tankgame.client.ui.Group;
import tankgame.client.ui.Label;
import tankgame.client.ui.Theme;
import tankgame.client.ui.UI;
import tankgame.client.ui.Widget;
import tankgame.client.vehicles.ClientVehicle;
import tankgame.common.Team;

/**
 *
 * @author fabian.baer2
 */
public class PlayerClientObject extends ClientPlayer {

	private VisionDevice activeVisionDevice;

	private VisionDevice defaultVisionDevice;
	private OverlayVisionDevice binoculars;
	private int standardCamWidth;

	private ClientGun activeGun;
	private int vehicleGun;

	private InputEventListener inputListener;

	private boolean inMenu;

	private MouseControl mouseControl;

	private int selectedSpawnPos;

	private DropDown spawnUIDD;

	private double spawnTimer;

	private UI spawnSelUI;
	private UI teamSelUI;
	private UI playerUI;

	public PlayerClientObject() {
		defaultVisionDevice = new VisionDevice();
		binoculars = new OverlayVisionDevice();
		binoculars.setOverlay("/res/binos_blur.png");
		binoculars.setFOV(Math.toRadians(10));
		binoculars.setCamScale(new Vector(0.05, 0.05));

		activeVisionDevice = defaultVisionDevice;
		standardCamWidth = 1000;
		Fake3DHandler.getInstance().setPlayerObject(this);

		inMenu = true;
		/*ServerSentStrategy ssoStrat = new ServerSentStrategy();
		setUnloadStrategy(ssoStrat);
		addUpdateStrategy(ssoStrat);*/

		mouseControl = InputHandler.getInstance().getMouseControl();

		inputListener = this::handleInput;
		InputHandler.getInstance().addListener(inputListener);

		/*if (getParent() != null) {
			ClientObject parent = (ClientObject) getHandler().getObjectByID(getParent());
		}*/
		GameObjectUpdateStrategy ustrat = getDefaultUpdateStrategy();
		ustrat.addParameter("position", (p) -> {
			if (isSpawned()) {
				setPosition((Vector) p);
			}
		}, () -> {
			return getPosition();
		});
		ustrat.addParameter("velocity", (p) -> {
			if (isSpawned()) {
				setVelocity((Vector) p);
			}
		}, () -> {
			return getVelocity();
		});
		ustrat.addParameter("spawnPos", (s) -> {
			if (s != null) {
				selectedSpawnPos = (int) s;
			}
		}, () -> {
			return selectedSpawnPos;
		});
		ustrat.addParameter("spawnTimer", (s) -> {
			spawnTimer = (double) s;
		}, () -> {
			return null;
		});
		ustrat.addParameter("waitForSpawn", (s) -> {
			UI ui = createSpawnWaitUI();
			TankGameClient.getInstance().deactivateUI(spawnSelUI);
			TankGameClient.getInstance().deactivateUI(teamSelUI);
			TankGameClient.getInstance().activateUI(ui);
		}, () -> {
			return null;
		}, false);
		ustrat.addParameter("selectTeam", (s) -> {
			teamSelection();
		}, () -> {
			return null;
		}, false);
		ustrat.addParameter("selectSpawn", (s) -> {
			UI ui = createSpawnSelectUI();
			TankGameClient.getInstance().activateUI(ui);
		}, () -> {
			return null;
		}, false);

	}

	public void setActiveVisionDevice(VisionDevice vd) {
		activeVisionDevice.deactivate();
		if (vd != null) {
			activeVisionDevice = vd;
		} else {
			activeVisionDevice = defaultVisionDevice;
		}
		activeVisionDevice.activate();
	}

	public VisionDevice getActiveVisionDevice() {
		return activeVisionDevice;
	}

	public void setDefaultVisionDevice(VisionDevice vd) {
		if (vd != null) {
			if (activeVisionDevice == defaultVisionDevice) {
				activeVisionDevice = vd;
			}
			defaultVisionDevice = vd;
		}
	}

	public VisionDevice getDefaultVisionDevice() {
		return defaultVisionDevice;
	}

	public void setActiveGun(ClientGun gun) {
		activeGun = gun;
	}

	public ClientGun getActiveGun() {
		return activeGun;
	}

	@Override
	public void setVehicle(ClientVehicle v) {
		if (v != null && getVehicle() != v) {
			super.setVehicle(v);
			vehicleGun = 0;
			if (!v.canUseHandgun()) {
				setActiveGun(v.getGun(vehicleGun));
			}
			UI vehicleUI = v.getUI();
			if (vehicleUI != null) {
				TankGameClient.getInstance().activateUI(vehicleUI);
			}
		} else if (v == null) {
			if (getActiveGun() == getVehicle().getGun(vehicleGun)) {
				setActiveGun(null);
			}
			if (getVehicle().getUI() != null) {
				TankGameClient.getInstance().deactivateUI(getVehicle().getUI());
			}
			super.setVehicle(null);
		}
	}

	@Override
	public void spawn() {
		super.spawn();
		ClientBaseManager.getCurrentBaseMgr().updateUI(getCurrentTeam());
		UI ui = ClientBaseManager.getCurrentBaseMgr().getCaptureUI();
		TankGameClient.getInstance().activateUI(ui);
		TankGameClient.getInstance().activateUI(playerUI);
	}

	@Override
	public void despawn() {
		super.despawn();
		UI ui = ClientBaseManager.getCurrentBaseMgr().getCaptureUI();
		TankGameClient.getInstance().deactivateUI(ui);
		TankGameClient.getInstance().deactivateUI(playerUI);
	}

	public void handleInput(InputEvent event) {
		/*if (event.isType(InputEvent.KEY) && event.key == 27 && event.active) {
			if (!inMenu) {
				inMenu = true;
				TankGameClient.getInstance().activateUI("escMenu");
				//mouseControl.setCurserVisible(true);
				mouseControl.setCurserCaptured(false);
			} else {
				inMenu = false;
				TankGameClient.getInstance().deactivateUI("escMenu");
				//mouseControl.setCurserVisible(false);
				mouseControl.setCurserCaptured(true);
			}
		} else */
		if (!inMenu) {
			if (event.isType(InputEvent.CONTROL) && event.controls[0].equals("binos") && event.active) {
				if (getActiveVisionDevice() == binoculars) {
					setActiveVisionDevice(null);
				} else {
					setActiveVisionDevice(binoculars);
				}
			} else if (event.isType(InputEvent.CONTROL) && event.controls[0].equals("aim_sight")
					&& event.active && activeGun != null) {
				if (getActiveVisionDevice() == activeGun.getGunSight()) {
					setActiveVisionDevice(null);
				} else {
					setActiveVisionDevice(activeGun.getGunSight());
				}
			} else if (event.isType(InputEvent.WHEEL)) {
				if (getActiveGun() != null) {
					getActiveGun().setElevation(getActiveGun().getElevation() + event.mouseDeltaRot * 0.02);
				}
			} else if (event.isType(InputEvent.KEY) && event.key == 84 && event.active) {
				Random rand = new Random();
				DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
				otherSymbols.setDecimalSeparator('.');
				otherSymbols.setGroupingSeparator(',');
				DecimalFormat fm = new DecimalFormat("####.##", otherSymbols);
				Debug.log("addTree(handler, " + fm.format(getRealPosition().x())
						+ ", " + fm.format(getRealPosition().y()) + ", "
						+ fm.format(rand.nextDouble() * 2 * Math.PI) + ", 0);");

			}
		}
	}

	@Override
	public void step(double dtime) {
		super.step(dtime);
		ArrayList<UI> activeUIs = TankGameClient.getInstance().getUIHandler().getUIs();
		boolean isInMenu = false;
		for (int i = 0; i < activeUIs.size(); i++) {
			UI ui = activeUIs.get(i);
			if (ui.isEatingAllEvents()) {
				isInMenu = true;
			}
		}
		if (isInMenu != inMenu) {
			inMenu = isInMenu;
			if (inMenu) {
				mouseControl.setCurserVisible(true);
				mouseControl.setCurserCaptured(false);
				InputHandler.getInstance().setSendNoEvents(true);
			} else {
				mouseControl.setCurserVisible(false);
				mouseControl.setCurserCaptured(true);
				InputHandler.getInstance().setSendNoEvents(false);
			}
		}
	}

	@Override
	public void onDraw(double dtime) {
		if (isSpawned()) {
			ClientObjectHandler chandler = (ClientObjectHandler) getHandler();
			Camera cam = chandler.getCamera();
			HandlerPanel panel = chandler.getPanel();

			//double cS = Math.max(0.05, Math.min(0.5 + MouseInput.getInstance().getWheelRotation() / 20, 0.7));
			//camScale.set(cS, cS);
			double ratio = (1. * panel.size.y()) / panel.size.x();

			double camHeight = standardCamWidth * ratio * activeVisionDevice.getCamScale().y();
			double camWidth = standardCamWidth * activeVisionDevice.getCamScale().x();
			double radius = camHeight;
			if (camWidth < camHeight) {
				radius = camWidth;
			}

			double fovDist = (radius / 2) / Math.tan(activeVisionDevice.getFOV() / 2);
			double camDist = Vector.subtract(cam.getPosition(), getRealPosition()).magnitude() * chandler.getScale();

			double newRadius = 2 * Math.tan(activeVisionDevice.getFOV() / 2) * (camDist + fovDist);
			double factor = newRadius / radius;
			camWidth *= factor;
			camHeight *= factor;

			cam.setWidth((int) camWidth);
			cam.setHeight((int) camHeight);
			Vector mousePos = new Vector(mouseControl.getCapturedMousePosition());
			mousePos = Vector.subtract(mousePos, Vector.divide(panel.size, 2));
			//mousePos.multiply(mousePos.magnitude);
			mousePos.divide(new Vector(50, -50));

			//Debug.log(getRealPosition());
			cam.setPosition(Vector.add(getRealPosition(), mousePos));
			/*if (!inMenu) {

				if (InputHandler.getInstance().isControlPressed("show_mouse")) {
					//mouseControl.setCurserVisible(true);
					mouseControl.setCurserCaptured(false);
				} else {
					//mouseControl.setCurserVisible(false);
					mouseControl.setCurserCaptured(true);
				}
			} else {
			}*/
		} else {
			ClientObjectHandler chandler = (ClientObjectHandler) getHandler();
			Camera cam = chandler.getCamera();
			HandlerPanel panel = chandler.getPanel();

			double ratio = (1. * panel.size.y()) / panel.size.x();

			double camHeight = standardCamWidth * ratio * activeVisionDevice.getCamScale().y();
			double camWidth = standardCamWidth * activeVisionDevice.getCamScale().x();

			cam.setWidth((int) camWidth);
			cam.setHeight((int) camHeight);

			cam.setPosition(getRealPosition());
		}

	}

	public void updateSpawnUISelection() {
		ArrayList<String> opts = new ArrayList<>();
		for (int i = 0; i < getCurrentTeam().getSpawnPositions().size(); i++) {
			opts.add("Spawn #" + i);
		}
		if (opts.isEmpty()) {
			opts.add("No Spawns available");
		}

		spawnUIDD.setOptions(opts);
	}

	/*@Override
	public void setTeam(ClientTeam t) {
		if (t != null) {
			super.setTeam(t);
			addToUpdate("team");

			UI ui;

			if (spawnTimer <= 0) {
				ui = createSpawnSelectUI();
			} else {
				ui = createSpawnWaitUI();
			}

			TankGameClient.getInstance().activateUI(ui);
		} else {
			UI ui = ClientTeamManager.getCurrentTeamManager().createTeamSelectionUI(this);
			TankGameClient.getInstance().activateUI(ui);

			ClientObjectHandler chandler = (ClientObjectHandler) getHandler();
			HandlerPanel panel = chandler.getPanel();
			mouseControl.setCapturedMousePosition(Vector.divide(panel.size, 2));
		}
	}*/
	private void teamSelection() {
		teamSelUI = ClientTeamManager.getCurrentTeamManager().createTeamSelectionUI(this);
		TankGameClient.getInstance().activateUI(teamSelUI);

		ClientObjectHandler chandler = (ClientObjectHandler) getHandler();
		HandlerPanel panel = chandler.getPanel();
		mouseControl.setCapturedMousePosition(Vector.divide(panel.size, 2));
	}

	private UI createSpawnSelectUI() {
		Theme transparent = new Theme(new Color(0, 0, 0, 0), Color.gray,
				Color.cyan, Color.black, Color.black);
		spawnSelUI = new UI();

		spawnSelUI.setEatAllEvents(true);
		spawnSelUI.setTheme(transparent);
		spawnSelUI.addRow(1, 0);

		spawnUIDD = new DropDown(spawnSelUI);

		updateSpawnUISelection();

		spawnUIDD.setSize(new Vector(0.35, 0.05));
		spawnUIDD.setHeightPerOption(0.05);

		spawnUIDD.setSelectEvent(() -> {
			int sp = spawnUIDD.getCurrentOption();
			if (sp >= 0 && sp < getCurrentTeam().getSpawnPositions().size()) {
				setPosition(getCurrentTeam().getSpawnPos(sp));
				selectedSpawnPos = sp;
				addToUpdate("spawnPos");
			}
		});
		spawnUIDD.setCurrentOption(0);

		spawnSelUI.addChild(spawnUIDD, 0, 0, 1, 1, Group.C);

		Button okBtn = new Button(spawnSelUI);
		okBtn.setSize(new Vector(0.1, 0.1));
		okBtn.setText("OK");

		okBtn.setReleaseEvent(() -> {
			int sp = spawnUIDD.getCurrentOption();
			if (sp >= 0 && sp < getCurrentTeam().getSpawnPositions().size()) {

			} else {
				return;
			}
			addToUpdate("spawnPos");
			spawn();
			addToUpdate("isSpawned");
			TankGameClient.getInstance().deactivateUI(spawnSelUI);
			//mouseControl.setCurserVisible(false);
			mouseControl.setCurserCaptured(true);
			ClientObjectHandler chandler = (ClientObjectHandler) getHandler();
			HandlerPanel panel = chandler.getPanel();
			mouseControl.setCapturedMousePosition(Vector.divide(panel.size, 2));
		});

		spawnSelUI.addChild(okBtn, 1, 0, 1, 1, Group.C);

		return spawnSelUI;
	}

	private UI createSpawnWaitUI() {
		Debug.log("WAIT");
		class WaitUI extends UI {

			Label spawnTimerLabel;
			Label resultLabel;

			@Override
			public void step(double dtime) {
				if (spawnTimer > 0) {
					spawnTimer -= dtime;
					spawnTimerLabel.setText(Integer.toString((int) Math.ceil(spawnTimer)));
					if (getCurrentTeam() != null && resultLabel.getText().equals(" ")) {
						String lbltext;
						Team.BattleState bs = getCurrentTeam().getBattleState();
						switch (bs) {
							case VICTORY:
								lbltext = "VICTORY";
								break;
							case ONGOING:
								lbltext = " ";
								break;
							case DEFEAT:
								lbltext = "DEFEAT";
								break;
							case DRAW:
								lbltext = "DRAW";
								break;
							default:
								lbltext = " ";
								break;
						}
						resultLabel.setText(lbltext);
					}
				} else if (spawnTimer < 0) {
					TankGameClient.getInstance().deactivateUI(this);
				}
			}
		}
		WaitUI ui = new WaitUI();

		Theme transparent = new Theme(new Color(0, 0, 0, 0), Color.gray,
				Color.cyan, Color.black, Color.black);

		ui.setEatAllEvents(
				true);
		ui.setTheme(transparent);

		ui.addRow(
				1, 0);

		ui.resultLabel = new Label(ui);

		ui.resultLabel.setTheme(transparent);

		ui.resultLabel.setSize(
				new Vector(1, 1));
		//lbl.setText("VICTORY");
		ui.resultLabel.setFont(
				new Font("Helvetica", 0, 50));
		ui.resultLabel.getTextElement().setRelativeFontSize(-1);
		ui.resultLabel.getTextElement().setCenterText(true);
		ui.addChild(ui.resultLabel,
				0, 0, 1, 1, 0b1111);

		ui.spawnTimerLabel = new Label(ui);

		ui.spawnTimerLabel.setTheme(transparent);

		ui.spawnTimerLabel.setSize(
				new Vector(0.35, 0.2));
		//spawnTimerLabel.setText(Integer.toString((int) Math.ceil(spawnTimer)));
		ui.spawnTimerLabel.setFont(
				new Font("Helvetica", 0, 50));
		ui.spawnTimerLabel.getTextElement().setRelativeFontSize(-1);
		ui.spawnTimerLabel.getTextElement().setCenterText(true);
		ui.addChild(ui.spawnTimerLabel,
				1, 0, 1, 1, 0b1111);

		return ui;
	}

	public void createPlayerUI() {
		Theme transparent = new Theme(new Color(0, 0, 0, 0), Color.gray,
				Color.cyan, Color.black, Color.black);
		playerUI = new UI();
		playerUI.setTheme(transparent);

		Widget hpBar = new Widget(playerUI) {
			@Override
			public void draw() {
				AbstractDrawer drawer = ClientManager.getInstance().getGraphicsBackend().getDrawer();
				Vector fbSize = drawer.getDrawbufferSize();

				Vector p1 = Vector.subtract(getScreenPos(), Vector.divide(fbSize, 2));
				Vector p3 = Vector.add(p1, getScreenSize());
				Vector p2 = new Vector(p1.x(), p3.y());
				Vector p4 = new Vector(p3.x(), p1.y());

				float[] color = getTheme().getBackground().getComponents(null);
				drawer.drawPolygon(color[0], color[1], color[2], color[3], true, p1, p2, p3, p4);

				int padding = 3;

				double hp = getHPRatio();

				p2.set(p2.x() + padding, p2.y() - padding);
				p3.set(p3.x() - padding, p2.y());
				p1.set(p2.x(), p2.y() - (getScreenSize().y() - 2 * padding) * hp);
				p4.set(p3.x(), p1.y());

				color = new float[]{(float) (1 - hp), (float) hp, 0, 1};
				drawer.drawPolygon(color[0], color[1], color[2], color[3], true, p1, p2, p3, p4);
			}
		};
		hpBar.setSize(new Vector(0.05, 0.2));
		hpBar.setPosition(new Vector(0.95, 0.8));
		playerUI.addChild(hpBar, 0, 0, 1, 1, Group.IGNORE);
	}

	@Override
	public void init() {
		super.init();
		createPlayerUI();
	}

	@Override
	public void remove() {
		super.remove(); //To change body of generated methods, choose Tools | Templates.
		InputHandler.getInstance().removeListener(inputListener);
		getActiveVisionDevice().deactivate();
	}
}
