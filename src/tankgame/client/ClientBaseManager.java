/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tankgame.client;

import blackhole.client.game.ClientManager;
import blackhole.client.graphicsEngine.AbstractDrawer;
import blackhole.utils.Vector;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import tankgame.client.ui.Group;
import tankgame.client.ui.Theme;
import tankgame.client.ui.UI;
import tankgame.client.ui.Widget;

/**
 *
 * @author fabian
 */
public class ClientBaseManager {

	private static ClientBaseManager currentBaseMgr;

	private ArrayList<ClientTeamBase> bases;
	private UI captureUI;

	private class BaseIndicator extends Widget {

		private ClientTeam playerTeam;

		public BaseIndicator(UI ui, ClientTeam team) {
			super(ui);
			playerTeam = team;

		}

		@Override
		public void draw() {
			double squareSize = getScreenSize().y() - 4;

			double width = squareSize * bases.size();

			AbstractDrawer drawer = ClientManager.getInstance().getGraphicsBackend().getDrawer();
			Vector fbSize = drawer.getDrawbufferSize();

			Vector midPoint = Vector.add(getScreenPos(),
					Vector.divide(getScreenSize(), 2)).subtract(Vector.divide(fbSize, 2));

			for (int i = 0; i < bases.size(); i++) {
				double x = midPoint.x() - width / 2 + (squareSize + 4) * i;
				double y = midPoint.y() - squareSize / 2 + 2;

				Vector p1 = new Vector(x, y);
				Vector p3 = new Vector(x + squareSize, y + squareSize);
				Vector p2 = new Vector(p1.x(), p3.y());
				Vector p4 = new Vector(p3.x(), p1.y());

				float[] color = getTheme().getBackground().getComponents(null);
				drawer.drawPolygon(color[0], color[1], color[2], color[3], true, p1, p2, p3, p4);

				double prog = bases.get(i).getCaptureProgress();
				float intensity = 1;
				if (prog < 1) {
					intensity = 0.7f;
				}

				Vector p1Filling = new Vector(p2.x(), p2.y() - squareSize * prog);
				Vector p4Filling = new Vector(p3.x(), p3.y() - squareSize * prog);

				float[] colorFilling;
				if (playerTeam == bases.get(i).getTeam()) {
					colorFilling = new float[]{0, 0, intensity, 1};
				} else {
					colorFilling = new float[]{intensity, 0, 0, 1};
				}
				drawer.drawPolygon(colorFilling[0], colorFilling[1],
						colorFilling[2], colorFilling[3], true, p1Filling, p2, p3,
						p4Filling);
				
				String name = bases.get(i).getName();
				if (name == null) {
					name = " ";
				}
				Font font = new Font("Helvetica", 0, (int) (squareSize * 0.75));
				Rectangle2D rect = drawer.getTextExtents(name, font);
				
				Vector textPos = new Vector(
						p1.x() + (squareSize - rect.getWidth()) / 2,
						p2.y() - (squareSize - drawer.getLineHeight(font)) / 2 - drawer.getDescent(font));
				
				drawer.drawString(name, textPos, font, 0, 0, 0, 1, true);
			}

			/*AbstractDrawer drawer = ClientManager.getInstance().getGraphicsBackend().getDrawer();
			Vector fbSize = drawer.getDrawbufferSize();

			Vector p1 = Vector.subtract(getScreenPos(), Vector.divide(fbSize, 2));
			Vector p3 = Vector.add(p1, getScreenSize());
			Vector p2 = new Vector(p1.x(), p3.y());
			Vector p4 = new Vector(p3.x(), p1.y());

			float[] color = getTheme().getBackground().getComponents(null);
			drawer.drawPolygon(color[0], color[1], color[2], color[3], true, p1, p2, p3, p4);
			
			double prog = base.getCaptureProgress();
			float intensity = 1;
			if (prog < 1) {
				intensity = 0.7f;
			}
			
			Vector p1Filling = new Vector(p2.x(), p2.y() - getScreenSize().y() * prog);
			Vector p4Filling = new Vector(p3.x(), p3.y() - getScreenSize().y() * prog);
			
			float[] colorFilling;
			if (playerTeam == base.getTeam()) {
				colorFilling = new float[]{0, 0, intensity, 1};
			} else {
				colorFilling = new float[]{intensity, 0, 0, 1};
			}
			drawer.drawPolygon(colorFilling[0], colorFilling[1],
					colorFilling[2], colorFilling[3], true, p1Filling, p2, p3,
					p4Filling);*/
		}
	}

	public ClientBaseManager() {
		currentBaseMgr = this;
		bases = new ArrayList<>();

		createUI();
	}

	public static ClientBaseManager getCurrentBaseMgr() {
		return currentBaseMgr;
	}

	public void addBase(ClientTeamBase base) {
		bases.add(base);
	}

	public void removeBase(ClientTeamBase base) {
		bases.remove(base);
	}

	public void createUI() {
		Theme transparent = new Theme(new Color(0, 0, 0, 0), Color.gray,
				Color.cyan, Color.black, Color.black);

		if (captureUI == null) {
			captureUI = new UI();
		} else {
			captureUI.clear();
		}

		captureUI.setDrawPosition(9);
		captureUI.setTheme(transparent);
	}

	public UI getCaptureUI() {
		return captureUI;
	}

	public void updateUI(ClientTeam team) {
		captureUI.clear();

		BaseIndicator ind = new BaseIndicator(captureUI, team);
		ind.setSize(new Vector(1, 0.1));
		captureUI.addChild(ind, 0, 0, 1, 1, Group.N | Group.W | Group.E);
	}
}
