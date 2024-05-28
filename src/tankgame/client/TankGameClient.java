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
import blackhole.client.game.input.InputEvent;
import blackhole.client.graphicsEngine.AbstractGameWindow;
import blackhole.client.network.ClientEndpoint;
import blackhole.client.soundEngine.SoundCore;
import blackhole.utils.Debug;
import blackhole.utils.Settings;
import blackhole.utils.Vector;
import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import tankgame.client.ui.Button;
import tankgame.client.ui.Entry;
import tankgame.client.ui.Group;
import tankgame.client.ui.Label;
import tankgame.client.ui.Theme;
import tankgame.client.ui.UI;
import tankgame.client.ui.UIHandler;

/**
 *
 * @author fabian
 */
public class TankGameClient {
	
	private static TankGameClient singleton;
	
	private ClientManager manager;
	private ClientEndpoint network;
	private SoundCore sound;
	private SSOHandler ssoHandler;
	private UIHandler uiHandler;
	
	private HashMap<String, UI> uis;
	
	private int quality;
	
	private TankGameClient() {
		uis = new HashMap<>();
		network = ClientEndpoint.getInstance();
		manager = ClientManager.getInstance();
		
	}
	
	public static TankGameClient getInstance() {
		if (singleton == null) {
			singleton = new TankGameClient();
		}
		return singleton;
	}
	
	public void start() {
		String basePath = System.getProperty("user.dir");
		
		
		
		manager.loadGame("/res/config/game.conf", basePath);
		
		while (!manager.getGraphicsBackend().getWindow().isRunning()) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				
			}
		}
		
		quality = 0;
		if (Settings.getProperty("quality") != null) {
			try {
				quality = Integer.parseInt(Settings.getProperty("quality"));
			} catch (NumberFormatException e) {
				
			}
		}
		
		uiHandler = new UIHandler();
		manager.addObjectHandler(uiHandler);
		
		initUIs();
		activateUI("mainMenu");
		
		manager.clientLoop();
		ssoHandler = new SSOHandler();
	}
	
	public void connect(String addr, int port) {
		if (ssoHandler != null) {
			ssoHandler.clear();
		}
		ssoHandler = new SSOHandler();
		manager.setServerObjectHandler(ssoHandler);
		network.forceConnect(addr, port);
	}
	
	public void disconnect() {
		if (network.isConnected()) {
			network.disconnect();
			ssoHandler.clear();
			manager.setServerObjectHandler(null);
		}
	}
	
	public void kill() {
		manager.killClient();
	}
	
	public boolean isRunning() {
		return manager.isRunning();
	}
	
	public int getQuality() {
		return quality;
	}
	
	public UIHandler getUIHandler() {
		return uiHandler;
	}
	
	public void registerUI(String name, UI ui) {
		uis.put(name, ui);
	}
	
	public UI getUI(String name) {
		return uis.get(name);
	}
	
	public void removeUI(String name) {
		uis.remove(name);
	}
	
	public void activateUI(String name) {
		activateUI(getUI(name));
	}
	
	public void deactivateUI(String name) {
		deactivateUI(getUI(name));
	}
	
	public void activateUI(UI ui) {
		uiHandler.addUI(ui);
	}
	
	public void deactivateUI(UI ui) {
		uiHandler.removeUI(ui);
	}
	
	public void setUI(String name) {
		setUI(getUI(name));
	}
	
	public void setUI(UI ui) {
		uiHandler.clear();
		uiHandler.addUI(ui);
	}
	
	public void initUIs() {
		Theme transparent = new Theme(new Color(0, 0, 0, 0), Color.gray,
				Color.cyan, Color.black, Color.black);
		Theme darkBG = new Theme(new Color(0.2f, 0.2f, 0.2f, 1), Color.gray,
				Color.cyan, Color.black, Color.black);
		AbstractGameWindow win = ClientManager.getInstance().getGraphicsBackend().getWindow();
		
		UI mainMenu = new UI();
		
		Entry ipEntry = new Entry(mainMenu);
		ipEntry.setTheme(darkBG);
		ipEntry.setText("127.0.0.1");
		ipEntry.getTextElement().setCursorPos(9);
		ipEntry.getTextElement().setRelativeFontSize(0.8);
		ipEntry.setSize(new Vector(0.35, 0.05));
		ipEntry.setPosition(new Vector(0.325, 0.4));
		mainMenu.addChild(ipEntry, 0, 0, 1, 1, Group.IGNORE);
		
		Entry portEntry = new Entry(mainMenu);
		portEntry.setTheme(darkBG);
		portEntry.setText("9999");
		portEntry.getTextElement().setCursorPos(9);
		portEntry.getTextElement().setRelativeFontSize(0.8);
		portEntry.setSize(new Vector(0.35, 0.05));
		portEntry.setPosition(new Vector(0.325, 0.46));
		mainMenu.addChild(portEntry, 0, 0, 1, 1, Group.IGNORE);
		
		mainMenu.setSize(new Vector(win.getWidth(), win.getHeight()));
		Button playBtn = new Button(mainMenu);
		playBtn.setTheme(darkBG);
		playBtn.setSize(new Vector(0.2, 0.1));
		playBtn.setText("PLAY");
		playBtn.setReleaseEvent(() -> {
			try {
				TankGameClient.getInstance().connect(ipEntry.getText(), Integer.parseInt(portEntry.getText()));
			} catch (NumberFormatException e) {
				
			}
			deactivateUI("mainMenu");
			activateUI("hud");
		});
		playBtn.setPosition(new Vector(0.4, 0.7));
		mainMenu.addChild(playBtn, 0, 0, 1, 1, Group.IGNORE);
		
		registerUI("mainMenu", mainMenu);
		
		class HUDUI extends UI {
			
			private Label fpsLabel;
			
			@Override
			public boolean handle(InputEvent event) {
				boolean ret = super.handle(event);
				if (event.isType(InputEvent.KEY) && event.key == 27 && event.active) {
					activateUI("escMenu");
					ret = true;
				}
				return ret;
			}
			
			@Override
			public void step(double dtime) {
				AbstractGameWindow win = manager.getGraphicsBackend().getWindow();
				fpsLabel.setText("FPS:" + (int) win.getCurrentFPS());
				super.step(dtime); //To change body of generated methods, choose Tools | Templates.
			}
			
		}
		;
		HUDUI hud = new HUDUI();
		hud.setSize(new Vector(win.getWidth(), win.getHeight()));
		hud.addColumn(4, 0);
		hud.setTheme(transparent);
		Label label = new Label(hud);
		label.setFont(new Font("Noto Sans", 0, 10));
		//label.setTheme(transparent);
		label.setSize(new Vector(0.04, 0.02));
		hud.fpsLabel = label;
		hud.addChild(label, 0, 0, 1, 1, Group.N | Group.E);
		registerUI("hud", hud);
		
		UI escMenu = new UI() {
			@Override
			public boolean handle(InputEvent event) {
				boolean ret = super.handle(event);
				if (event.isType(InputEvent.KEY) && event.key == 27 && event.active) {
					deactivateUI("escMenu");
					ret = true;
				}
				return ret;
			}
			
		};
		//escMenu.setSize(new Vector(win.getWidth(), win.getHeight()));
		escMenu.setTheme(transparent);
		Group panel = new Group(escMenu);
		panel.setSize(new Vector(0.4, 0.4));
		panel.addRow(1, 0);
		escMenu.addChild(panel, 0, 0, 1, 1, Group.C);
		Button backBtn = new Button(escMenu);
		//backBtn.setFont(new Font("Helvetica", 0, 10));
		backBtn.setText("Back");
		backBtn.setSize(new Vector(0.1, 0.04));
		backBtn.setReleaseEvent(() -> {
			TankGameClient.getInstance().disconnect();
			setUI("mainMenu");
		});
		panel.addChild(backBtn, 1, 0, 1, 1, Group.C);
		escMenu.setDrawPosition(15);
		escMenu.setEatAllEvents(true);
		registerUI("escMenu", escMenu);
	}
}
