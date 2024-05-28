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
package tankgame.client.ui;

import blackhole.client.game.input.InputEvent;
import blackhole.utils.Vector;
import java.util.ArrayList;

/**
 *
 * @author fabian
 */
public class Group extends Widget {

	private ArrayList<Child> children;

	private ArrayList<Row> rows;
	private ArrayList<Row> columns;

	protected static class Row {

		double weight;
		int padding;
	}

	protected static class Child {

		Widget widget;
		int row;
		int column;
		int rowSpan;
		int columnSpan;

		int sides;
	}

	public static final int N = 1;
	public static final int W = 2;
	public static final int S = 4;
	public static final int E = 8;
	public static final int C = 16;
	public static final int IGNORE = 32;

	public Group(UI ui) {
		super(ui);
		children = new ArrayList<>();
		rows = new ArrayList<>();
		Row row = new Row();
		row.padding = 0;
		row.weight = 1;
		rows.add(row);

		columns = new ArrayList<>();
		Row column = new Row();
		column.padding = 0;
		column.weight = 1;
		columns.add(column);
	}

	public void addChild(Widget w) {
		addChild(w, 0, 0, 1, 1, 0b1111);
	}

	public void addChild(Widget w, int r, int c, int rs, int cs, int side) {
		if (w.getParent() != null) {
			w.getParent().removeChild(w);
		}
		w.setParent(this);
		Child child = new Child();
		child.widget = w;
		child.column = c;
		child.row = r;
		child.columnSpan = cs;
		child.rowSpan = rs;
		child.sides = side;

		rearrangeChild(child);

		children.add(child);
	}

	public void removeChild(Widget w) {
		for (int i = 0; i < children.size(); i++) {
			if (children.get(i).widget == w) {
				children.get(i).widget.setParent(null);
				children.remove(i);
			}
		}
	}

	@Override
	public void setPosition(Vector pos) {
		super.setPosition(pos);
		for (int i = 0; i < children.size(); i++) {
			rearrangeChild(children.get(i));
		}
	}

	@Override
	public void setSize(Vector s) {
		super.setSize(s);
		for (int i = 0; i < children.size(); i++) {
			rearrangeChild(children.get(i));
		}
	}

	public void addRow(double weight, int padding) {
		Row row = new Row();
		row.weight = weight;
		row.padding = padding;

		rows.add(row);

		for (int i = 0; i < children.size(); i++) {
			rearrangeChild(children.get(i));
		}
	}

	public void addColumn(double weight, int padding) {
		Row column = new Row();
		column.weight = weight;
		column.padding = padding;

		columns.add(column);

		for (int i = 0; i < children.size(); i++) {
			rearrangeChild(children.get(i));
		}
	}

	public void removeRow(int row) {
		rows.remove(row);
		for (int i = 0; i < children.size(); i++) {
			rearrangeChild(children.get(i));
		}
	}

	public void removeColumn(int column) {
		columns.remove(column);
		for (int i = 0; i < children.size(); i++) {
			rearrangeChild(children.get(i));
		}
	}

	public void configureRow(int r, double weight, int padding) {
		Row row = rows.get(r);
		row.weight = weight;
		row.padding = padding;

		for (int i = 0; i < children.size(); i++) {
			rearrangeChild(children.get(i));
		}
	}

	public void configureColumn(int c, double weight, int padding) {
		Row column = columns.get(c);
		column.weight = weight;
		column.padding = padding;

		for (int i = 0; i < children.size(); i++) {
			rearrangeChild(children.get(i));
		}
	}

	protected void rearrangeChild(Child child) {
		if (child.sides != IGNORE) {
			double rowWeight = rows.get(child.row).weight;
			for (int i = 1; i < child.rowSpan; i++) {
				rowWeight += rows.get(child.row + i).weight;
			}
			double totalRowWeight = 0;
			for (int i = 0; i < rows.size(); i++) {
				totalRowWeight += rows.get(i).weight;
			}
			double preRowWeight = 0;
			for (int i = 0; i < child.row; i++) {
				preRowWeight += rows.get(i).weight;
			}

			double rowHeight = getSize().y() / totalRowWeight * rowWeight;
			double rowPos = getSize().y() / totalRowWeight * preRowWeight;// + getPosition().y();
			double topPadding = rows.get(child.row).padding / getRoot().getScreenSize().y();
			double bottomPadding = rows.get(child.row + child.rowSpan - 1).padding / getRoot().getScreenSize().y();

			double childMaxHeight = Math.max(rowHeight - topPadding - bottomPadding, 0);
			double childMinYPos = rowPos + topPadding;

			double columnWeight = columns.get(child.column).weight;
			for (int i = 1; i < child.columnSpan; i++) {
				columnWeight += columns.get(child.column + i).weight;
			}
			double totalColumnWeight = 0;
			for (int i = 0; i < columns.size(); i++) {
				totalColumnWeight += columns.get(i).weight;
			}
			double preColumnWeight = 0;
			for (int i = 0; i < child.column; i++) {
				preColumnWeight += columns.get(i).weight;
			}

			double columnWidth = getSize().x() / totalColumnWeight * columnWeight;
			double columnPos = getSize().x() / totalColumnWeight * preColumnWeight;// + getPosition().x();
			double leftPadding = columns.get(child.column).padding / getRoot().getScreenSize().x();
			double rightPadding = columns.get(child.column + child.columnSpan - 1).padding / getRoot().getScreenSize().x();

			double childMaxWidth = Math.max(columnWidth - leftPadding - rightPadding, 0);
			double childMinXPos = columnPos + leftPadding;

			double childYPos = childMinYPos;
			double childHeight = childMaxHeight;
			double childXPos = childMinXPos;
			double childWidth = childMaxWidth;

			if (child.sides != C) {
				if ((child.sides | (N | S)) != child.sides) {
					childHeight = Math.min(child.widget.getSize().y(), childMaxHeight);
					if ((child.sides | N) != child.sides) {
						childYPos = childMinYPos + childMaxHeight - childHeight;
					}
				}

				if ((child.sides | (E | W)) != child.sides) {
					childWidth = Math.min(child.widget.getSize().x(), childMaxWidth);
					if ((child.sides | E) != child.sides) {
						childXPos = childMinXPos + childMaxWidth - childWidth;
					}
				}
			} else {
				childWidth = Math.min(child.widget.getSize().x(), childMaxWidth);
				childHeight = Math.min(child.widget.getSize().y(), childMaxHeight);

				childXPos = childMinXPos + (childMaxWidth - childWidth) / 2;
				childYPos = childMinYPos + (childMaxHeight - childHeight) / 2;
			}

			child.widget.setPosition(new Vector(childXPos, childYPos));
			child.widget.setSize(new Vector(childWidth, childHeight));
		}
	}
	
	public void clear() {
		children.clear();
		rows.clear();
		Row row = new Row();
		row.padding = 0;
		row.weight = 1;
		rows.add(row);

		columns.clear();
		Row column = new Row();
		column.padding = 0;
		column.weight = 1;
		columns.add(column);
	}

	@Override
	public boolean handle(InputEvent event) {
		super.handle(event);
		for (int i = children.size() - 1; i >= 0; i--) {
			if (children.get(i).widget.handle(event)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void draw() {
		super.draw();
		for (int i = 0; i < children.size(); i++) {
			children.get(i).widget.draw();
		}
	}
}
