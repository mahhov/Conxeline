package map;

import shapes.Shape;
import shapes.Surface;

public class Cell {
	Shape content;
	double light;
	double[] dirLight;

	Cell() {
		dirLight = new double[6];
	}

	Cell(Shape content) {
		this.content = content;
		dirLight = new double[6];
	}

	Surface[] paint() {
		if (content != null)
			return content.paint();
		return null;
	}

	void addContent(Shape shape) {
		content = shape;
		content.updateLight(dirLight);
	}

	void removeContent() {
		content = null;
	}

	void lightSource(double power) {
		boolean change = false;
		for (int i = 0; i < dirLight.length; i++)
			if (dirLight[i] < power) {
				dirLight[i] = power;
				change = true;
			}
		if (change) {
			if (power > light)
				light = power;
			if (content != null) {
				content.updateLight(dirLight);
			}
		}
	}

	double blacken() {
		double r = light;
		if (light != 0) {
			// for (int i = 0; i < dirLight.length; i++)
			// dirLight[i] = 0;
			dirLight = new double[6]; // speed imporvement?
			light = 0;
			if (content != null)
				content.updateLight(dirLight);
		}
		return r;
	}

	boolean brighten(double power, int direction) {
		if (power > dirLight[direction]) {
			dirLight[direction] = power;
			if (content != null)
				content.updateLight(dirLight);
			if (power > light) {
				light = power;
				return true;
			}
		}
		return false;
	}

	public boolean darken(double power, int direction) {
		// you should never have power>dirLight[direction] except
		// if dirLight was updated incorrectly
		// if (power > dirLight[direction] && power > 0.1)
		// System.out.println("ERROR IN LIGHTING");

		// if (power != dirLight[direction])
		// System.out.println(power + " = " + dirLight[direction] + ", "
		//	+ direction);

		if (power == dirLight[direction]) {
			dirLight[direction] = 0;
			if (content != null)
				content.updateLight(dirLight);
			if (power == light) {
				light = dirLight[0];
				for (int dir = 1; dir < 6; dir++) {
					if (dirLight[dir] > light)
						light = dirLight[dir];
				}
				return true;
			}
		}
		return false;
	}

	double getLight(int direction) {
		double r = light;
		if (content != null) {
			r *= content.diffuse[direction];
		} else
			r *= Map.DIFFUSE;
		if (light <= 0)
			return 0;
		return r;
	}

	double getLight(double light, int direction) {
		if (content != null) {
			light *= content.diffuse[direction];
		} else
			light *= Map.DIFFUSE;
		if (light <= 0)
			return 0;
		return light;
	}
}
