package list;

public class LinkedList {

	public LinkedListNode head;
	boolean[][][] alreadyAdded;

	public LinkedList(int width, int height, int zheight) {
		alreadyAdded = new boolean[width][height][zheight];
	}

	// adds in front
	public void add(int x, int y, int z) {
		if (alreadyAdded[x][y][z] == false) {
			alreadyAdded[x][y][z] = true;
			head = new LinkedListNode(x, y, z, head);
		}
	}
	
	public class LinkedListNode {
		public int x, y, z;
		public LinkedListNode next;

		LinkedListNode(int x, int y, int z, LinkedListNode next) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.next = next;
		}

	}
}
