package shapes;

import java.awt.Color;

import engine.Math3D;

public class Cube extends Shape {

	Surface top, bottom, left, right, front, back;

	// oriented along axis
	public Cube(double cx, double cy, double cz, double size) {
		// diffuse[Math3D.LEFT] = 100;
		// diffuse[Math3D.RIGHT] = 100;
		// diffuse[Math3D.FRONT] = 100;
		// diffuse[Math3D.BACK] = 100;
		// diffuse[Math3D.TOP] = 100;
		// diffuse[Math3D.BOTTOM] = 100;

		size /= 2;
		double topZ = cz + size;
		double bottomZ = cz - size;
		double leftX = cx - size;
		double rightX = cx + size;
		double frontY = cy - size;
		double backY = cy + size;

		double[] x = new double[] { leftX, rightX, rightX, leftX };
		double[] y = new double[] { backY, backY, frontY, frontY };
		double[] z = new double[] { bottomZ, topZ, topZ, bottomZ };

		// from back/left -> back/right -> front/right -> front/left
		top = new Surface(x, y, topZ, true);
		bottom = new Surface(x, y, bottomZ, false);

		// from bottom/back -> top/back -> top/front -> bottom/front
		left = new Surface(leftX, y, z, true);
		right = new Surface(rightX, y, z, false);

		x = new double[] { leftX, leftX, rightX, rightX };
		// from left/bottom -> left/top -> right/top -> right/bottom
		front = new Surface(x, frontY, z, true);
		back = new Surface(x, backY, z, false);

		if (Math.random() < .9)
			monotoneColors(Math3D.CUBE_MONOTONE_COLOR);
		else
			randomColors();
	}

	void monotoneColors(Color c) {
		top.setColor(c);
		bottom.setColor(c);
		back.setColor(c);
		front.setColor(c);
		right.setColor(c);
		left.setColor(c);
	}

	void rainbowColors() {
		top.setColor(Color.RED);
		bottom.setColor(Color.CYAN);
		back.setColor(Color.MAGENTA);
		front.setColor(Color.WHITE);
		right.setColor(Color.BLACK);
		left.setColor(Color.GRAY);
	}

	void randomColors() {
		Color c = Math3D.randomColor();
		top.setColor(c);
		bottom.setColor(c);
		back.setColor(c);
		front.setColor(c);
		right.setColor(c);
		left.setColor(c);
	}

	public Surface[] paint() {
		return new Surface[] { top, bottom, left, right, front, back };
	}

	public void updateLight(double[] lights) {
		left.light = lights[Math3D.LEFT];
		right.light = lights[Math3D.RIGHT];
		top.light = lights[Math3D.TOP];
		bottom.light = lights[Math3D.BOTTOM];
		front.light = lights[Math3D.FRONT];
		back.light = lights[Math3D.BACK];
	}

}
