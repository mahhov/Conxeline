package map;

import shapes.Cube;
import shapes.FastCube;
import engine.Math3D;

public class MapGeneratorOld {

	static boolean[][][] randomMap(int size, double prob) {
		boolean[][][] map = new boolean[size][size][size];
		for (int x = 0; x < size; x++)
			for (int y = 0; y < size; y++)
				for (int z = 0; z < size; z++)
					if (Math.random() <= prob)
						map[x][y][z] = true;
		return map;
	}

	static boolean[][][] heightMap(int size, int maxHeight) {
		boolean[][][] map = new boolean[size][size][maxHeight];
		for (int x = 0; x < size; x++)
			for (int y = 0; y < size; y++) {
				int h = (int) (maxHeight * Math.random());
				for (int z = 0; z < h; z++)
					map[x][y][z] = true;
			}
		return map;
	}

	public static boolean[][][] blastHeightMap(int size, int maxHeight,
			double prob) {
		boolean[][][] map = new boolean[size][size][maxHeight];
		int[][] height = new int[size][size];

		for (int x = 1; x < size - 1; x++)
			for (int y = 1; y < size - 1; y++)
				if (Math.random() <= prob) {
					for (int dx = x - 1; dx < x + 2; dx++)
						for (int dy = y - 1; dy < y + 2; dy++)
							height[dx][dy] += 1;
					height[x][y] += 1;
				}

		for (int x = 1; x < size - 1; x++)
			for (int y = 1; y < size - 1; y++)
				for (int z = 0; z <= Math.min(height[x][y], maxHeight); z++)
					map[x][y][z] = true;

		return map;
	}

	public static double[][][] blastHeightMapLighted(int size, int maxHeight,
			double prob, double[] sun) {
		double[][][] map = new double[size][size][maxHeight];
		int[][] height = new int[size][size];

		for (int x = 1; x < size - 1; x++)
			for (int y = 1; y < size - 1; y++)
				if (Math.random() <= prob) {
					for (int dx = x - 1; dx < x + 2; dx++)
						for (int dy = y - 1; dy < y + 2; dy++)
							height[dx][dy] += 1;
					height[x][y] += 1;
				}

		for (int x = 1; x < size - 1; x++)
			for (int y = 1; y < size - 1; y++)
				for (int z = 0; z <= Math.min(height[x][y], maxHeight - 1); z++) {
					double dx = sun[0] - x;
					double dy = sun[1] - y;
					double dz = sun[2] - z;
					double light = Math.min(1,
							10 / Math.sqrt(dx * dx + dy * dy + dz * dz));
					map[x][y][z] = light;
				}

		return map;
	}

	public static Cell[][][] blastHeightMapCells(int size, int maxHeight,
			double prob) {
		Cell[][][] map = new Cell[size][size][maxHeight];
		int[][] height = new int[size][size];

		for (int x = 1; x < size - 1; x++)
			for (int y = 1; y < size - 1; y++)
				if (Math.random() <= prob) {
					for (int dx = x - 1; dx < x + 2; dx++)
						for (int dy = y - 1; dy < y + 2; dy++)
							height[dx][dy] += 1;
					height[x][y] += 1;
				}

		for (int x = 0; x < size; x++)
			for (int y = 0; y < size; y++) {
				int zTop = Math.min(height[x][y] + 1, maxHeight);
				for (int z = 0; z < zTop; z++)
					map[x][y][z] = new Cell(new Cube(x, y, z, 1));
				for (int z = zTop; z < maxHeight; z++)
					map[x][y][z] = new Cell();
			}

		return map;
	}

	public static Cell[][][] blastHeightMapCellsTops(int size, int maxHeight,
			double prob) {
		Cell[][][] map = new Cell[size][size][maxHeight];
		int[][] height = new int[size][size];

		for (int x = 1; x < size - 1; x++)
			for (int y = 1; y < size - 1; y++)
				if (Math.random() <= prob) {
					for (int dx = x - 1; dx < x + 2; dx++)
						for (int dy = y - 1; dy < y + 2; dy++)
							height[dx][dy] += 1;
					height[x][y] += 1;
				}

		for (int x = 0; x < size; x++)
			for (int y = 0; y < size; y++) {
				int zTop = Math.min(height[x][y] + 1, maxHeight - 1);
				int zBot = zTop;
				for (int xx = x - 1; xx <= x + 1; xx++)
					for (int yy = y - 1; yy <= y + 1; yy++)
						if (Math3D.inRange(xx, yy, size, size)
								&& height[xx][yy] + 2 < zBot)
							zBot = height[xx][yy] + 2;
				for (int z = 0; z < maxHeight; z++)
					if (z >= zBot && z <= zTop)
						map[x][y][z] = new Cell(new Cube(x, y, z, 1));
					else
						map[x][y][z] = new Cell();
			}

		return map;
	}

	public static Cell[][][] walledMap(int size, int maxHeight) {
		Cell[][][] map = new Cell[size][size][maxHeight];
		int[][] height = new int[size][size];

		for (int x = 1; x < size - 1; x++)
			for (int y = 1; y < size - 1; y++)
				height[x][y] = heightWall(x, y);

		for (int x = 0; x < size; x++)
			for (int y = 0; y < size; y++) {
				int zTop = Math.min(height[x][y] + 1, maxHeight - 1);
				int zBot = zTop;
				for (int xx = x - 1; xx <= x + 1; xx++)
					for (int yy = y - 1; yy <= y + 1; yy++)
						if (Math3D.inRange(xx, yy, size, size)
								&& height[xx][yy] + 2 < zBot)
							zBot = height[xx][yy] + 2;
				for (int z = 0; z < maxHeight; z++)
					if (z >= zBot && z <= zTop)
						map[x][y][z] = new Cell(new Cube(x, y, z, 1));
					else
						map[x][y][z] = new Cell();
			}

		return map;
	}

	static int heightWall(int x, int y) {
		if (x == 3)
			return 2;
		return 0;
	}

	static FastCube[][][] blastHeightMapConcavesFast(int size, int maxHeight,
			double prob) {
		FastCube[][][] map = new FastCube[size][size][maxHeight];
		int[][] height = new int[size][size];

		for (int x = 1; x < size - 1; x++)
			for (int y = 1; y < size - 1; y++)
				if (Math.random() <= prob) {
					for (int dx = x - 1; dx < x + 2; dx++)
						for (int dy = y - 1; dy < y + 2; dy++)
							height[dx][dy] += 1;
					height[x][y] += 1;
				}

		for (int x = 0; x < size; x++)
			for (int y = 0; y < size; y++)
				for (int z = 0; z <= Math.min(height[x][y], maxHeight - 1); z++)
					map[x][y][z] = new FastCube(x, y, z, 0.5);

		return map;
	}
}
