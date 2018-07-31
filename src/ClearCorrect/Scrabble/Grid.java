package ClearCorrect.Scrabble;

public class Grid {
	
	public static String ColumnHeader = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	public static String draw(int width, int height) {
		StringBuilder grid = new StringBuilder();
		grid.append(gridHeader(width));
		for(int i = 1;i<=height;i++) {
			grid.append(gridLine(width));
			grid.append(gridRow(i, width));
		}
		return grid.toString();
	}
	
	//return "\n00 | A | B | C | D | E | F | G | H | I | J | K | L | M | N | O |";
	static String gridHeader(int width) {
		String str="\n   |";
		for(int i = 0;i<width;i++) {
			str += " " + getColumnHeader(i) + " |";
		}
		return str;
	}
	
	//return "\n---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---|";
	static String gridLine(int width) {
		String str="\n---+";
		for(int i = 0;i<width;i++) {
			str += "---+";
		}
		return str;
	}
	
	//"\n01 |A01|B01|C01|D01|E01|F01|G01|H01|I01|J01|K01|L01|M01|N01|O01|";
	static String gridRow(int row, int width) {
		String padRow = PadNum(row, 2);
		String str = "\n" + padRow + " |";
		for(int i = 0;i<width;i++) {
			str += getColumnHeader(i) + padRow + "|";
		}
		return str;
	}
	
	static String getColumnHeader(int col) {
		return ColumnHeader.charAt(col % ColumnHeader.length()) + "";
	}
	
	static String PadNum(int num, int size) {
		String numString = num + "";
		while(numString.length() < size) {
			numString  = "0" + numString;
		}
		return numString;
	}
	
}
