package ClearCorrect.Scrabble;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class ScrabbleMatch
 */
@WebServlet("/Service")
public class Service extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private List<String> wordEntries;
	/**
     * Default constructor. 
     */
    public Service() {	
    	
    }

    /**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Game.readWordListOnce(getServletContext());
		
		int row = Integer.parseInt(request.getParameter("row")!=null?request.getParameter("row"):"0");
		int col = Integer.parseInt(request.getParameter("col")!=null?request.getParameter("col"):"0");
		
		String action = request.getParameter("action");
		String user = request.getParameter("user");
		String word = request.getParameter("word");
		String position = request.getParameter("position");
		
		HttpSession session = Game.createSession(request);
		
		String sessionId = session.getId();
					
		wordEntries = Game.getWordEntries(sessionId);
		
		renderGrid(sessionId);
		
		PrintWriter writer = response.getWriter();

		if(action!=null) {
			
			switch(action) {
				case "newMatch":
					newMatch(sessionId);
					break;
				case "updateGridSize":
					resizeGrid(sessionId, col, row);
					break;
				case "getGridSize":
					getGridSize(writer, sessionId);
					break;
				case "getGrid":
					getGrid(writer, sessionId);
					break;
				case "getUsers":
					getUsers(writer, sessionId);
					break;
				case "getPlayHistory":
					getPlayHistory(writer, sessionId);
					break;
				case "getAllSessions":
					getAllSessions(writer);
					break;
				case "addUser":
					addUser(writer, sessionId, user);
					break;
				case "addWord":
					addWord(writer, sessionId, user, word, col, row, position);
					break;
			}
		}
		else
		{
			writer.append("SCRABBLE MATCH - " + (new Date()).toString());
			renderGrid(sessionId);
			writer.append(Game.printSession(sessionId));
		}
	}
	
	public void getAllSessions(PrintWriter writer){
		writer.append(Game.printSessions());
	}
	
	public void getPlayHistory(PrintWriter writer, String sessionId){
		List<String> wordHistory = Game.getWordEntries(sessionId);
		writer.append(String.join("\n", wordHistory));
	}
	
	public void getGrid(PrintWriter writer, String sessionId){
		writer.append(renderGrid(sessionId));
	}
	
	public void getUsers(PrintWriter writer, String sessionId){
		List<String> users = Game.getUsers(sessionId);
		writer.append(String.join("\n", users));
	}
	
	public void newMatch(String sessionId){
		Game.initMatch(sessionId);
	}
	
	public void getGridSize(PrintWriter writer, String sessionId){
		int width = Game.getGridWidth(sessionId);
		int height = Game.getGridHeight(sessionId);
		writer.append(width+"\n"+height);
	}
	
	public void addUser(PrintWriter writer, String sessionId, String user){
		if(Game.addUniqueUser(sessionId, user)){
			writer.append("SessionID = "+ sessionId + " - " + user);
		}
	}
	
	public void addWord(PrintWriter writer, String sessionId, String user, String word, int col, int row, String position){
		if(Game.checkWord(word)){
			String wordEntry = user + "," + word + "@" + Grid.getColumnHeader(col) + Grid.PadNum(row, 2) + "," + position;
			if(Game.addWord(sessionId, wordEntry)){
				writer.append("SessionID = "+ sessionId + " - " + wordEntry);
			}
		}else {
			writer.append("word not found");
		}
	}
	
	public void resizeGrid(String sessionId, int width, int height) {
		Game.setGridWidth(sessionId, width);
		Game.setGridHeight(sessionId, height);
	}
	
	public String renderGrid(String sessionId) {
		
		int width = Game.getGridWidth(sessionId);
		int height = Game.getGridHeight(sessionId);
		
		Game.setGrid(sessionId, Grid.draw(width, height));
		
		for(String entry : wordEntries) {
			Game.drawWord(sessionId, entry);
		}
		
		Game.fillGridWithBlanks(sessionId);
		
		return Game.getGrid(sessionId);
	}
}
