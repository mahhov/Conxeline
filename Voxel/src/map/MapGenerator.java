package map;

import shapes.Cube;
import shapes.Pyramid;
import engine.Math3D;

public class MapGenerator {

	static final int EMPTY = 0, CUBE = 1, PYRAMID = 2;

	public static Cell[][][] castle() {
		int size = 100;
		int zheight = 15;
		int[][][] map = new int[size][size][zheight];

		int[][] heightMap = new int[size][size];
		// out grounds
		for (int x = 1; x < size - 1; x++)
			for (int y = 1; y < size - 1; y++)
				if (x > 55 || y > 55) {
					if (Math.random() <= .2) {
						for (int dx = x - 1; dx < x + 2; dx++)
							for (int dy = y - 1; dy < y + 2; dy++)
								heightMap[dx][dy] += 1;
						heightMap[x][y] += 1;
					}
				}

		// convert height map to 3d map
		for (int x = 0; x < size; x++)
			for (int y = 0; y < size; y++)
				if (x > 55 || y > 55) {
					int zTop = Math.min(heightMap[x][y] + 1, zheight - 1);
					int zBot = zTop;
					for (int xx = x - 1; xx <= x + 1; xx++)
						for (int yy = y - 1; yy <= y + 1; yy++)
							if (Math3D.inRange(xx, yy, size, size)
									&& heightMap[xx][yy] + 2 < zBot)
								zBot = heightMap[xx][yy] + 2;
					for (int z = 0; z < zheight; z++)
						if (z == zTop + 1 && z > 2) {
							map[x][y][z] = CUBE;// PYRAMID;
							// PYRAMID STILL USES THE SUBTRACTION DIFFUSE
							// rather than the multiplication diffuse
						} else if (z >= zBot && z <= zTop + 1) {
							map[x][y][z] = CUBE;
						}
				}

		// ground + walls+ half roof
		for (int x = 0; x < 50; x++)
			for (int y = 0; y < 50; y++) {
				int zMax;
				if ((x == 15 && y == 15) || (x == 25 && y == 25))
					zMax = 14;
				else if (((x == 10 || x == 39) && (y >= 10 && y <= 39))
						|| ((y == 10 || y == 39) && (x >= 10 && x <= 39)))
					zMax = 12;
				else
					zMax = 4;
				for (int z = 3; z < zMax; z++)
					map[x][y][z] = CUBE;
				if (x < 18 && x > 10 && (y >= 10 && y <= 39))
					map[x][y][11] = CUBE;
			}

		return protrude3DMap(size, zheight, map);
	}

	public static Cell[][][] blastHeightMapCellsTops(int size, int maxHeight,
			double prob) {
		int[][] height = new int[size][size];

		for (int x = 1; x < size - 1; x++)
			for (int y = 1; y < size - 1; y++)
				if (Math.random() <= prob) {
					for (int dx = x - 1; dx < x + 2; dx++)
						for (int dy = y - 1; dy < y + 2; dy++)
							height[dx][dy] += 1;
					height[x][y] += 1;
				}

		return protrudeHeightMapPyramids(size, maxHeight, height);
	}

	public static Cell[][][] wallsHeightMap(int size, int maxHeight) {
		int[][] height = new int[size][size];

		for (int x = 1; x < size - 1; x++)
			for (int y = 1; y < size - 1; y++)
				// (int) (Math.random() * maxHeight / 2)
				height[x][y] = x == 3 || x == 8 || x == 20 ? (int) (Math
						.random() * maxHeight / 2) : 0;

		return protrudeHeightMapPyramids(size, maxHeight, height);
	}

	public static Cell[][][] protrudeHeightMap(int size, int maxHeight,
			int[][] height) {
		Cell[][][] map = new Cell[size][size][maxHeight];
		int count = 0;

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
					if (z >= zBot && z <= zTop) {
						count++;
						map[x][y][z] = new Cell(new Cube(x, y, z, 1));
					} else
						map[x][y][z] = new Cell();
			}

		System.out.println("map content: " + count);
		return map;
	}

	public static Cell[][][] protrudeHeightMapPyramids(int size, int maxHeight,
			int[][] height) {
		Cell[][][] map = new Cell[size][size][maxHeight];
		int count = 0;

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
					if (z == zTop + 1) {
						count++;
						map[x][y][z] = new Cell(new Pyramid(x, y, z, 1));
					} else if (z >= zBot && z <= zTop) {
						count++;
						map[x][y][z] = new Cell(new Cube(x, y, z, 1));
					} else
						map[x][y][z] = new Cell();
			}

		System.out.println("map content: " + count);
		return map;
	}

	public static Cell[][][] protrude3DMap(int size, int maxHeight,
			int[][][] mapData) {
		Cell[][][] map = new Cell[size][size][maxHeight];
		int count = 0;

		for (int x = 0; x < size; x++)
			for (int y = 0; y < size; y++)
				for (int z = 0; z < maxHeight; z++) {
					count++;
					switch (mapData[x][y][z]) {
					case CUBE:
						map[x][y][z] = new Cell(new Cube(x, y, z, 1));
						break;
					case PYRAMID:
						map[x][y][z] = new Cell(new Pyramid(x, y, z, 1));
						break;
					case EMPTY:
						count--;
						map[x][y][z] = new Cell();
					}
				}

		System.out.println("map content: " + count);
		return map;
	}
}
