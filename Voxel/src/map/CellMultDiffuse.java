package map;

import shapes.Shape;
import shapes.Surface;

// before changing lighting to use subtraction rather than multiplication

public class CellMultDiffuse {
	Shape content;
	double light;
	double[] dirLight;

	CellMultDiffuse() {
		dirLight = new double[6];
	}

	CellMultDiffuse(Shape content) {
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
			for (int i = 0; i < dirLight.length; i++)
				dirLight[i] = 0;
			// dirLight=new double[6]; speed imporvement?
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
		//
		// System.out.println(power + " = " + dirLight[direction]);

		if (Math.abs(power - dirLight[direction]) < 0.01) {
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
		return r;
	}

	double getLight(double light, int direction) {
		if (content != null) {
			light *= content.diffuse[direction];
		} else
			light *= Map.DIFFUSE;
		return light;
	}
}
