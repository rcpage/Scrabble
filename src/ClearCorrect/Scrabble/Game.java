package ClearCorrect.Scrabble;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class Game {
	
	static int defaultGridWidth = 15;
	static int defaultGridHeight = 15;
	
	static List<String> knownWords = new ArrayList<String>();
	
	static List<String> sessionIds = new ArrayList<String>();
	
	static Map<String, Integer> gridWidth = new HashMap<String, Integer>();
	static Map<String, Integer> gridHeight = new HashMap<String, Integer>();
	
	static Map<String, String> grid = new HashMap<String, String>();
	static Map<String, Map<String, String>> gridCells = new HashMap<String, Map<String, String>>();
	
	static Map<String, List<String>> words = new HashMap<String, List<String>>();
	static Map<String, List<String>> users = new HashMap<String, List<String>>();
	
	public static void readWordListOnce(ServletContext context){
		if(knownWords.size() > 0) return;
		Date start = new Date();
		try { 
			String path = context.getRealPath("/WEB-INF/lib/words.txt");
			System.out.println("word.txt = " + path);
			Path filepath = Paths.get(path);
			knownWords = Files.readAllLines(filepath,  StandardCharsets.UTF_8);
		}
		catch(Exception error){
			
		}
		Date end = new Date();
		long timespan = end.getTime() - start.getTime();
		System.out.println(knownWords.size() + " words loaded. Took " + timespan + "ms.");
	}
	
	public static HttpSession createSession(HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		String id = session.getId();
		
		//only init session once
		if(sessionIds.contains(id)) 
			return session;
		
		initMatch(id);
		
		return session;
	}
	
	public static boolean checkWord(String word){
		Date start = new Date();
		boolean isKnown = knownWords.contains(word);
		Date end = new Date();
		long timespan = end.getTime() - start.getTime();
		System.out.println("\"" + word + "\" isKnown=" + isKnown + ". Took " + timespan + "ms");
		return isKnown;
	}
	
	public static void initMatch(String id) {
		if(sessionIds.contains(id) == false) {
			sessionIds.add(id);
		}
		      grid.put(id, "");
		     words.put(id, new ArrayList<String>());
		     users.put(id, new ArrayList<String>());
		 gridWidth.put(id, defaultGridWidth);
		gridHeight.put(id, defaultGridHeight);
		 gridCells.put(id, new HashMap<String, String>());
	}
	
	public static boolean checkWordCollisions(String sessionId, String wordEntry){
		
		boolean validPlacement = false;
		
		Map<String, String> wordCellMap = getWordIndexMap(wordEntry);
		
		Map<String, String> gridCellMap = gridCells.get(sessionId);
		
		Set<Entry<String, String>> list = wordCellMap.entrySet();
		
		int cellCollisions = 0;
		for(Entry<String, String> entry : list){
			String cellIndex = entry.getKey();
			String value = entry.getValue().toUpperCase();
			if(gridCellMap.containsKey(cellIndex) && gridCellMap.get(cellIndex).equals(value)){
				cellCollisions++;
			}
		}

		if(cellCollisions > 0 || gridCellMap.size() == 0){
			validPlacement = true;
		}
		
		return validPlacement;
	}
	
	public static List<String> getWordEntries(String sessionId){
		return words.get(sessionId);
	}
	
	public static boolean addWord(String sessionId, String word){
		boolean added = false;
		List<String> list = words.get(sessionId);
		if(checkWordCollisions(sessionId, word)) {
			if(list.contains(word) == false){
				list.add(word);
				added = true;
			}
		}
		return added;
	}
	
	public static List<String> getUsers(String sessionId){
		return users.get(sessionId);
	}
	
	public static boolean addUniqueUser(String sessionId, String user){
		if(user == null)return false;
		List<String> list = users.get(sessionId);
		if(list.contains(user) == false) {
			list.add(user);
			return true;
		}
		return false;
	}
	
	public static String getGrid(String sessionId) {
		return grid.get(sessionId);
	}
	
	public static String setGrid(String sessionId, String value){
		return grid.put(sessionId, value);
	}
	
	public static int getGridWidth(String sessionId) {
		return gridWidth.get(sessionId);
	}
	
	public static int getGridHeight(String sessionId) {
		return gridHeight.get(sessionId);
	}
	
	public static void setGridWidth(String sessionId, int value) {
		gridWidth.put(sessionId, value);
	}
	
	public static void setGridHeight(String sessionId, int value) {
		gridHeight.put(sessionId, value);
	}
	
	public static String printSession(String sessionId) {
		
		String grid = Game.getGrid(sessionId);
		List<String> words = Game.getWordEntries(sessionId);
		List<String> users = Game.getUsers(sessionId);
		
		StringBuilder sessionInfo = new StringBuilder();
		
		sessionInfo.append(grid);
		
		sessionInfo.append("\n\nWord History:");
		
		for(String word : words) {
			sessionInfo.append("\n" + word);
		}
		
		sessionInfo.append("\n\nUsers:");
		
		for(String user : users) {
			sessionInfo.append("\n" + user);
		}
		
		return sessionInfo.toString();
	}
	
	public static List<String> getAllSessions() {
		return sessionIds;
	}
	
	public static String printSessions() {
		
		StringBuilder string = new StringBuilder();
		for(String sessionId : sessionIds) {
			string.append("\n\nSession:" + sessionId);
			string.append(printSession(sessionId));
		}
		
		return string.toString();
	}
	
	public static Map<String, String> getWordIndexMap(String wordEntry){
		Map<String, String> map = new HashMap<String, String>();

		String[] parts = wordEntry.split(",");
		
		if(parts.length != 3) return map;
		
		String user 	= parts[0];
		String entry 	= parts[1];
		String position = parts[2];
		
		String word = entry.substring(0, entry.indexOf("@"));
		String startPosition = entry.substring(entry.indexOf("@") + 1);
		char colChar = startPosition.charAt(0);
		int charColumnIndex = Grid.ColumnHeader.indexOf(colChar);
		int row = Integer.parseInt(startPosition.substring(1));
		WordPosition wordPosition = WordPosition.valueOf(position);
		
		if(wordPosition == WordPosition.Vertical) {
			for(int i = 0;i < word.length();i++) {
				String index = colChar + Grid.PadNum(row + i, 2);
				char c = word.charAt(i);
				System.out.println(index + " = " + c);
				map.put(index, " "+c+" ");
			}
		}
		else if(wordPosition == WordPosition.Horizontal) {
			for(int i = 0;i < word.length();i++) {
				String index = Grid.ColumnHeader.charAt(charColumnIndex + i) + Grid.PadNum(row, 2);
				char c = word.charAt(i);
				System.out.println(index + " = " + c);
				map.put(index, " "+c+" ");
			}
		}
		else {
			
		}
		
		System.out.println(word + " col=" + charColumnIndex + ", row=" + row + " WordPosition=" + wordPosition.toString());
		
		return map;
	}
	
	public static void drawWord(String sessionId, String wordEntry) {

		String grid = getGrid(sessionId);
		
		Map<String, String> wordCellMap = getWordIndexMap(wordEntry);
		
		Map<String, String> gridCellMap = gridCells.get(sessionId);
		Set<Entry<String, String>> list = wordCellMap.entrySet();
		
		for(Entry<String, String> entry : list){
			String cellIndex = entry.getKey();
			String value = entry.getValue().toUpperCase();
			grid = grid.replace(cellIndex, value);
			gridCellMap.put(cellIndex, value);
		}
		
		//update grid session
		setGrid(sessionId, grid);
	
	}
	
	public static void fillGridWithBlanks(String sessionId) {
		String grid = getGrid(sessionId);
		for(int row = 1;row<=getGridHeight(sessionId);row++) {
			for(int col=0;col<getGridWidth(sessionId);col++) {
				String replace = Grid.ColumnHeader.charAt(col) + Grid.PadNum(row, 2);
				String blank = "   ";
				grid = grid.replaceAll(replace, blank);
			}
		}
		//update grid session
		setGrid(sessionId, grid);
	}
	
}
