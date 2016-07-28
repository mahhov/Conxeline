package engine;

import java.awt.Color;

public class Math3D {

	public static final Color GREEN_GRAY = new Color(40, 50, 40);
	public static final Color BLUE_GRAY = new Color(100, 100, 125);
	public static final Color RED_GRAY = new Color(120, 60, 60);
	public static final Color CUBE_MONOTONE_COLOR = RED_GRAY;
	public static final Color PYRAMID_MONOTONE_COLOR = BLUE_GRAY;

	public static final double sqrt2 = Math.sqrt(2);

	public static final int LEFT = 0, RIGHT = 1, TOP = 2, BOTTOM = 3,
			FRONT = 4, BACK = 5;
	public static final int[][] DIRECTION = new int[6][4];

	static void load() {
		loadTrig(100);
		loadDirection();

		// testing accuracy of 3 versions of pow
		// speed was equivalent (i didn't test it too much)
		// pow was most accurate, pow2 and pow3 gave same answers
		// but pow can add flickering when moving due to (int)
		// for (int i = 0; i < 10; i++) {
		// double a = .95;
		// double b = Math.random() * 10;
		// // System.out.println(b);
		// double r = (Math.pow(a, b));
		// System.out.println(Math3D.pow(a, (int) b) - r);
		// System.out.println(Math3D.pow2(a, b) - r);
		// System.out.println(Math3D.pow3(a, b) - r);
		// System.out.println("");
		// }

	}

	static void loadDirection() {
		DIRECTION[LEFT] = new int[] { 1, 0, 0, RIGHT };
		DIRECTION[RIGHT] = new int[] { -1, 0, 0, LEFT };
		DIRECTION[TOP] = new int[] { 0, 0, -1, BOTTOM };
		DIRECTION[BOTTOM] = new int[] { 0, 0, 1, TOP };
		DIRECTION[FRONT] = new int[] { 0, 1, 0, BACK };
		DIRECTION[BACK] = new int[] { 0, -1, 0, FRONT };
	}

	static double[] point(double x, double y, double z) {
		return new double[] { x, y, z };
	}

	static double[] origin(double[] point, Camera camera, double[] sun) {
		double ox = point[0] - camera.x;
		double oy = point[1] - camera.y;
		double oz = point[2] - camera.z;

		double light = dotProduct(ox, oy, oz, point[0] - sun[0], point[1]
				- sun[1], point[2] - sun[2]) / 2 + 0.5;

		double oa = Math.atan2(oy, ox) - camera.angle;
		double d = Math.sqrt(ox * ox + oy * oy);
		oy = d * Math.sin(oa);
		ox = d * Math.cos(oa);

		oa = Math.atan2(oz, ox) + camera.angleZ;
		d = Math.sqrt(ox * ox + oz * oz);
		oz = d * Math.sin(oa);
		ox = d * Math.cos(oa);

		return new double[] { ox, oy, oz, light };
	}

	static double[] origin(double[] point, Camera camera, double light) {
		double ox = point[0] - camera.x;
		double oy = point[1] - camera.y;
		double oz = point[2] - camera.z;

		// double light = dotProduct(ox, oy, oz, point[0] - sun[0], point[1]
		// - sun[1], point[2] - sun[2]) / 2 + 0.5;

		double oa = Math.atan2(oy, ox) - camera.angle;
		double d = Math.sqrt(ox * ox + oy * oy);
		oy = d * Math.sin(oa);
		ox = d * Math.cos(oa);

		oa = Math.atan2(oz, ox) + camera.angleZ;
		d = Math.sqrt(ox * ox + oz * oz);
		oz = d * Math.sin(oa);
		ox = d * Math.cos(oa);

		return new double[] { ox, oy, oz, light };
	}

	static double[] toScreen(double[] point) {
		if (point[0] <= 0)
			return null;
		return new double[] { (point[1] / point[0]), -(point[2] / point[0]),
				(1 / point[0]), point[3] };
	}

	static double dotProduct(double x, double y, double z, double x2,
			double y2, double z2) {
		double mag1 = Math.sqrt(x * x + y * y + z * z);
		double mag2 = Math.sqrt(x2 * x2 + y2 * y2 + z2 * z2);
		return (x * x2 + y * y2 + z * z2) / mag1 / mag2;
	}

	static int[][] transform(double[][] a, int scale, int shift) {
		int[][] aa = new int[a.length][a[0].length];
		for (int i = 0; i < a.length; i++)
			for (int j = 0; j < a[i].length; j++)
				aa[i][j] = (int) (a[i][j] * scale + shift);
		return aa;
		// int[][] aa = new int[a.length][];
		// for (int i = 0; i < a.length; i++)
		// aa[i] = transform(a[i], scale, shift);
		// return aa;
	}

	public static int[][] transform(double[][] a) {
		return transform(a, 800, 400);
	}

	public static double[] transform(double[] a, int scale, int shift) {
		double[] aa = new double[a.length];
		for (int i = 0; i < a.length; i++)
			aa[i] = a[i] * scale + shift;
		return aa;
	}

	static float sinTable[];
	static int trigAccuracy;

	public static void loadTrig(int accuracy) {
		trigAccuracy = accuracy;
		sinTable = new float[accuracy];
		for (int i = 0; i < accuracy; i++)
			sinTable[i] = (float) Math.sin(Math.PI / 2 * i / accuracy);
	}

	public static float xsin(double xd) {
		// return (float) Math.sin(xd);

		float x = (float) (xd / Math.PI * 2);

		int sign;

		if (x < 0) {
			sign = -1;
			x = -x;
		} else
			sign = 1;

		x = x - ((int) (x / 4)) * 4;
		if (x >= 2) {
			sign *= -1;
			x -= 2;
		}

		if (x > 1)
			x = 2 - x;

		if (x == 1)
			return 1;

		return sign * sinTable[(int) (x * trigAccuracy)];

	}

	public static float xcos(double x) {
		return xsin(x + Math.PI / 2);
	}

	public static double maxMin(double z, double max, double min) {
		return Math.min(Math.max(z, min), max);
	}

	public static int maxMin(int z, int max, int min) {
		return Math.min(Math.max(z, min), max);
	}

	public static double sin2(double sin1, double cos1, double sin2, double cos2) {
		return sin1 * cos2 + cos1 * sin2;
	}

	public static double cos2(double sin1, double cos1, double sin2, double cos2) {
		return cos1 * cos2 - sin1 * sin2;
	}

	public static double tan2(double sin1, double cos1, double sin2, double cos2) {
		double tan1 = sin1 / cos1;
		double tan2 = sin2 / cos2;
		return (tan1 + tan2) / (1 - tan1 * tan2);
		// return sin2(sin1, cos1, sin2, cos2) / cos2(sin1, cos1, sin2, cos2);
	}

	public static boolean inRange(int x, int y, int width, int height) {
		return x >= 0 && y >= 0 && x < width && y < height;
	}

	public static boolean inRange(int x, int y, int z, int width, int height,
			int zheight) {
		return x >= 0 && y >= 0 && z >= 0 && x < width && y < height
				&& z < zheight;
	}

	public static double pow(double base, int p) {
		double result = 1;
		while (p > 0) {
			if ((p & 1) != 0)
				result *= base;
			base *= base;
			p = p >>> 1;
		}
		return result;
	}

	public static double pow2(double base, double p) {
		int x = (int) (Double.doubleToLongBits(base) >> 32);
		int y = (int) (p * (x - 1072632447) + 1072632447);
		return Double.longBitsToDouble(((long) y) << 32);
	}

	public static double pow3(double base, double p) {
		long tmp = Double.doubleToLongBits(base);
		long tmp2 = (long) (p * (tmp - 4606921280493453312L)) + 4606921280493453312L;
		return Double.longBitsToDouble(tmp2);
	}

	public static Color randomColor() {
		return new Color((float) Math.random(), (float) Math.random(),
				(float) Math.random());
	}

}
