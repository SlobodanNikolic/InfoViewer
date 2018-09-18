package infoViewer.actions;

import javax.swing.AbstractAction;

public class ActionManager {

	private static ActionManager instance;
	
	private NewFile newFile;
	private Save save;
	private Close close;
	private CloseAll closeAll;
	private Delete delete;
	private About about;
	private Connect connect;
	private Disconnect disconnect;
	private ReadReport readReport;

	private ActionManager() {

		newFile = new NewFile();
		save = new Save();
		close = new Close();
		closeAll = new CloseAll();
		delete = new Delete();
		about = new About();
		connect = new Connect();
		readReport = new ReadReport();
		disconnect = new Disconnect();
	}
	
	public static ActionManager getInstance() {

		if (instance == null)
			instance = new ActionManager();
		return instance;
	}
	
	public AbstractAction getAction(String name) {
		
		switch(name) {
		
		case "newFile": return newFile;
		case "save": return save;
		case "close": return close;
		case "closeAll": return closeAll;
		case "delete": return delete;
		case "about": return about;
		case "connect": return connect;
		case "disconnect": return disconnect;
		case "readReport": return readReport;
		
		}
		return null;
	}
}

