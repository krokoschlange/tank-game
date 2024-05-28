/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tankgame.client.ui;

import blackhole.client.game.ClientManager;
import blackhole.client.game.input.InputEvent;
import blackhole.client.graphicsEngine.AbstractDrawer;
import blackhole.client.graphicsEngine.GraphicsBackend;
import blackhole.utils.Vector;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 *
 * @author fabian
 */
public class DropDown extends Widget {

	private ArrayList<String> options;

	private int currentOption;

	private boolean extended;
	private Group extension;
	private double heightPerOption;

	private UIEvent selectEvent;

	/*private Font font;
	private Font useFont;
	private String useText;*/
	
	private TextElement text;
	
	private GraphicsBackend backend;

	public DropDown(UI ui) {
		super(ui);
		backend = ClientManager.getInstance().getGraphicsBackend();
		currentOption = -1;
		extension = new Group(ui);
		/*font = new Font("Helvetica", 0, 20);
		useFont = font;
		useText = " ";*/
		text = new TextElement(this);
		text.setRelativeFontSize(0.8);

	}

	public void setOptions(ArrayList<String> opt) {
		options = opt;
		createExtension();
	}

	public ArrayList<String> getOptions() {
		return options;
	}

	public void setCurrentOption(int opt) {
		currentOption = opt;
		text.setText(options.get(currentOption));
		if (selectEvent != null) {
			selectEvent.process();
		}
	}

	public int getCurrentOption() {
		return currentOption;
	}

	public void setSelectEvent(UIEvent event) {
		selectEvent = event;
	}

	public void setHeightPerOption(double height) {
		heightPerOption = height;
		createExtension();
	}

	public double getHeightPerOption() {
		return heightPerOption;
	}

	public void setFont(Font f) {
		/*font = f;
		useFont = font;
		if (currentOption >= 0 && currentOption < options.size()) {
			useText = options.get(currentOption);
		} else {
			useText = " ";
		}*/
		text.setFont(f);
	}

	public Font getFont() {
		//return font;
		return text.getFont();
	}

	public void createExtension() {
		extension.clear();

		extension.setSize(new Vector(getSize().x(), heightPerOption * options.size()));
		extension.removeRow(0);
		for (int i = 0; i < options.size(); i++) {
			extension.addRow(1, 0);
			
			Button btn = new Button(getRoot());
			btn.setText(options.get(i));
			int opt = i;
			btn.setReleaseEvent(() -> {
				getParent().removeChild(extension);
				extended = false;
				setCurrentOption(opt);
			});

			extension.addChild(btn, i, 0, 1, 1, 0b1111);
		}
	}

	@Override
	public boolean handle(InputEvent event) {
		boolean eaten = super.handle(event);
		if (eaten) {
			return true;
		}

		if (event.isType(InputEvent.BUTTON) && event.button == 1) {
			if (isTouching(event.mousePos) && !extended) {
				extended = true;
				getParent().addChild(extension, 0, 0, 1, 1, Group.IGNORE);
				extension.setPosition(new Vector(getPosition().x(), getPosition().y() + getSize().y()));
				return true;
			}
		}
		return false;
	}

	@Override
	public void draw() {
		AbstractDrawer drawer = backend.getDrawer();
		Vector fbSize = drawer.getDrawbufferSize();

		Vector p1 = Vector.subtract(getScreenPos(), Vector.divide(fbSize, 2));
		Vector p3 = Vector.add(p1, getScreenSize());
		Vector p2 = new Vector(p1.x(), p3.y());
		Vector p4 = new Vector(p3.x(), p1.y());

		float[] bg = getTheme().getBackground().getComponents(null);

		drawer.drawPolygon(bg[0], bg[1], bg[2], bg[3], true, p1, p2, p3, p4);

		if (currentOption >= 0 && currentOption < options.size()) {
			text.draw();
			/*float[] txt = getTheme().getContrast().getComponents(null);

			Rectangle2D layout = drawer.getTextExtents(useText, useFont);

			int fontSize = useFont.getSize();
			while (getScreenSize().y() < layout.getHeight() + layout.getY() && fontSize >= 0) {
				fontSize--;
				useFont = new Font(font.getName(), font.getStyle(), fontSize);
				layout = drawer.getTextExtents(options.get(currentOption), useFont);
			}

			if (fontSize > 0) {
				while (getScreenSize().x() < layout.getWidth() && useText.length() >= 3) {
					useText = useText.substring(0, useText.length() - 3) + "..";
					layout = drawer.getTextExtents(useText, useFont);
				}
				if (getScreenSize().x() > layout.getWidth()) {

					Vector textSize = new Vector(layout.getWidth(), layout.getHeight());

					Vector offset = Vector.subtract(getScreenSize(),
							textSize).divide(2);
					offset.set(offset.x(), -offset.y());

					Vector textPos = Vector.add(p2, offset);

					textPos.set(textPos.x(), textPos.y() - layout.getY());
					drawer.drawString(useText, textPos, useFont, txt[0], txt[1], txt[2], txt[3], true);
				}
			}*/
		}
		if (extended) {
			extension.draw();
		}
	}
}
