package shapes;

import java.awt.Color;

import map.Map;

import engine.Math3D;

public class LightCube extends Cube {

	// oriented along axis
	public LightCube(double cx, double cy, double cz, double power) {
		super(cx, cy, cz, Math3D.maxMin(Math.sqrt(power) / 10, 1, .1));
		for (int i = 0; i < 6; i++)
			diffuse[i] = Map.DIFFUSE;
		monotoneColors(new Color((int) Math3D.maxMin(power * 3, 250, 30), 0, 0));
	}

}
