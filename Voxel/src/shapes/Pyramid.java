package shapes;

import java.awt.Color;

import map.Map;
import engine.Math3D;

// square base

public class Pyramid extends Shape {
	Surface bottom, left, right, front, back;

	// oriented along axis
	public Pyramid(double cx, double cy, double cz, double side) {
		// double sideDiffuse = Map.DIFFUSE * 4 / 5;
		// diffuse[Math3D.LEFT] = sideDiffuse;
		// diffuse[Math3D.RIGHT] = sideDiffuse;
		// diffuse[Math3D.FRONT] = sideDiffuse;
		// diffuse[Math3D.BACK] = sideDiffuse;
		// diffuse[Math3D.TOP] = Map.DIFFUSE;
		diffuse[Math3D.LEFT] = 5;
		diffuse[Math3D.RIGHT] = 5;
		diffuse[Math3D.FRONT] = 5;
		diffuse[Math3D.BACK] = 5;
		diffuse[Math3D.TOP] = 1;
		diffuse[Math3D.BOTTOM] = 100;

		side /= 2;
		double topZ = cz + side;
		double bottomZ = cz - side;
		double leftX = cx - side;
		double rightX = cx + side;
		double frontY = cy - side;
		double backY = cy + side;

		// from back/left -> back/right -> front/right -> front/left
		double[] x = new double[] { leftX, rightX, rightX, leftX };
		double[] y = new double[] { backY, backY, frontY, frontY };
		double[] z = new double[] { bottomZ, topZ, topZ, bottomZ };
		bottom = new Surface(x, y, bottomZ, false);

		// from back/bottom -> front/bottom -> center/top
		z = new double[] { bottomZ, bottomZ, topZ };
		y = new double[] { backY, frontY, cy };
		x = new double[] { leftX, leftX, cx };
		left = new Surface(x, y, z, false);
		x = new double[] { rightX, rightX, cx };
		right = new Surface(x, y, z, true);

		// from left/bottom -> right/bottom -> center/top
		x[0] = leftX;
		y[0] = frontY;
		front = new Surface(x, y, z, false);
		y = new double[] { backY, backY, cy };
		back = new Surface(x, y, z, true);

		monotoneColors(Math3D.PYRAMID_MONOTONE_COLOR);
	}

	void monotoneColors(Color c) {
		bottom.setColor(c);
		back.setColor(c);
		front.setColor(c);
		right.setColor(c);
		left.setColor(c);
	}

	void rainbowColors() {
		bottom.setColor(Color.GREEN);
		back.setColor(Color.PINK);
		front.setColor(Color.BLUE);
		right.setColor(Color.YELLOW);
		left.setColor(Color.ORANGE);
	}

	public Surface[] paint() {
		return new Surface[] { bottom, left, right, front, back };
	}

	public void updateLight(double[] lights) {
		bottom.light = lights[Math3D.BOTTOM];

		double sideLightFromTop = 0.5;
		double sideLightFromSide = 1 - sideLightFromTop;

		left.light = lights[Math3D.LEFT] * sideLightFromSide
				+ lights[Math3D.TOP] * sideLightFromTop;
		right.light = lights[Math3D.RIGHT] * sideLightFromSide
				+ lights[Math3D.TOP] * sideLightFromTop;
		front.light = lights[Math3D.FRONT] * sideLightFromSide
				+ lights[Math3D.TOP] * sideLightFromTop;
		back.light = lights[Math3D.BACK] * sideLightFromSide
				+ lights[Math3D.TOP] * sideLightFromTop;
	}
}
