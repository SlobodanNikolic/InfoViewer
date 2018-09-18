package infoViewer.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import infoViewer.actions.ActionManager;
import infoViewer.listeners.DBTreeMouseListener;
import infoViewer.listeners.FileTreeMouseListener;
import infoViewer.model.trees.dbTree.DBTree;
import infoViewer.model.trees.dbTree.ReportTree;
import infoViewer.model.trees.fileTree.FileTree;
import infoViewer.view.database.DBFileView;
import infoViewer.view.database.dialogs.DBLoginDialog;
import infoViewer.view.files.FileView;

public class AppGUI extends JFrame {

	private static AppGUI instance;
	private FileView currentFileView;

	public static final int WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width * 2 / 3;
	public static final int HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height * 3 / 4;

	private JMenuBar menuBar;
	private JMenuItem newFile, close, closeAll, save, about, delete, connect, disconnect, readReport;
	private JToolBar toolBar;

	private JTabbedPane tabbedPane;

	private JTextField pathField;
	private JTextArea date, size;

	private FileTree fileTree;
	private DBTree dbTree;
	private ReportTree reportTree;

	private ArrayList<FileView> fileViews;
	private ArrayList<DBFileView> dbFileViews;

	private Connection connection;
	private DBLoginDialog login;

	private AppGUI() {

		fileViews = new ArrayList();
		dbFileViews = new ArrayList();

		setTitle("InfoViewer");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(WIDTH, HEIGHT);
		setLocation(WIDTH / 4, HEIGHT / 5);
		setResizable(true);
		setLayout(new BorderLayout());
		init();
		setVisible(true);

		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		SwingUtilities.updateComponentTreeUI(this);
	}

	private void init() {

		// 1. (TREEs + Info)_pane <- Split pane -> TabbedPane

		/* TabbedPane */

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setMinimumSize(new Dimension(WIDTH * 2 / 3, 0));
		tabbedPane.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		tabbedPane.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {

				if (tabbedPane.getSelectedComponent() instanceof FileView)
					currentFileView = (FileView) tabbedPane.getSelectedComponent();
			}
		});

		/* Path_TreesPanel_Info:Panel */

		// a) Path field

		pathField = new JTextField("Path of the selected file");
		pathField.setEditable(false);
		pathField.setForeground(Color.gray);

		// b) TreesPane

		fileTree = new FileTree();
		fileTree.addMouseListener(new FileTreeMouseListener());
		JScrollPane scrollPane = new JScrollPane(fileTree);
		scrollPane.setBorder(BorderFactory
				.createTitledBorder(BorderFactory.createSoftBevelBorder(SoftBevelBorder.LOWERED), "File Explorer"));

		dbTree = new DBTree();
		dbTree.addMouseListener(new DBTreeMouseListener());
		JScrollPane dbScrollPane = new JScrollPane(dbTree);

		reportTree = new ReportTree();
		reportTree.addMouseListener(new DBTreeMouseListener());
		JScrollPane reportScrollPane = new JScrollPane(reportTree);

		JTabbedPane dbPane = new JTabbedPane();
		dbPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createSoftBevelBorder(SoftBevelBorder.RAISED),
				"DB / Report Explorer"));

		dbPane.add("Database", dbScrollPane);
		dbPane.add("Report", reportScrollPane);

		JSplitPane treesPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane, dbPane);
		treesPane.setResizeWeight(0.5);

		// c) Info field

		JPanel fileInfo = new JPanel();
		fileInfo.setLayout(new BorderLayout());
		fileInfo.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

		date = new JTextArea("Date of creation");
		date.setEditable(false);
		date.setForeground(Color.gray);
		fileInfo.add(date, BorderLayout.NORTH);
		size = new JTextArea("Size of the file");
		size.setEditable(false);
		size.setForeground(Color.gray);
		fileInfo.add(size, BorderLayout.SOUTH);

		// d) Path/scrollPane/info panel

		JPanel PSIpanel = new JPanel();
		PSIpanel.setLayout(new BorderLayout());
		PSIpanel.setMinimumSize(new Dimension(WIDTH / 5, 0));

		PSIpanel.add(pathField, BorderLayout.NORTH);
		PSIpanel.add(treesPane, BorderLayout.CENTER);
		PSIpanel.add(fileInfo, BorderLayout.SOUTH);

		/* SplitPane */

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, PSIpanel, tabbedPane);
		splitPane.setDividerLocation(WIDTH * 29 / 100);

		add(splitPane, BorderLayout.CENTER);

		// 2. MENUBAR + Menus

		menuBar = new JMenuBar();

		Font font = new Font("Fontana", Font.BOLD, 12);

		JMenu file = new JMenu("File ");
		newFile = new JMenuItem(ActionManager.getInstance().getAction("newFile"));
		file.add(newFile);
		close = new JMenuItem(ActionManager.getInstance().getAction("close"));
		close.setMnemonic(KeyEvent.VK_C);
		file.add(close);
		delete = new JMenuItem(ActionManager.getInstance().getAction("delete"));
		delete.setMnemonic(KeyEvent.VK_D);
		file.add(delete);
		closeAll = new JMenuItem(ActionManager.getInstance().getAction("closeAll"));
		file.add(closeAll);
		save = new JMenuItem(ActionManager.getInstance().getAction("save"));
		save.setMnemonic(KeyEvent.VK_S);
		file.add(save);
		file.setFont(font);

		JMenu database = new JMenu("Database ");
		database.setMnemonic(KeyEvent.VK_Q);
		connect = new JMenuItem(ActionManager.getInstance().getAction("connect"));
		database.add(connect);
		disconnect = new JMenuItem(ActionManager.getInstance().getAction("disconnect"));
		database.add(disconnect);
		readReport = new JMenuItem(ActionManager.getInstance().getAction("readReport"));
		database.add(readReport);
		database.setFont(font);

		JMenu help = new JMenu("Help ");
		help.setMnemonic(KeyEvent.VK_H);
		about = new JMenuItem(ActionManager.getInstance().getAction("about"));
		about.setMnemonic(KeyEvent.VK_A);
		help.add(about);
		help.setFont(font);

		menuBar.add(file);
		menuBar.add(database);
		menuBar.add(help);

		setJMenuBar(menuBar);

		// 3. TOOLBAR + Actions

		toolBar = new JToolBar();
		toolBar.setFloatable(false);

		toolBar.add(new JLabel(" "));
		toolBar.add(ActionManager.getInstance().getAction("newFile"));
		toolBar.add(new JLabel("  "));
		toolBar.add(ActionManager.getInstance().getAction("save"));
		toolBar.add(new JLabel("  "));
		toolBar.add(ActionManager.getInstance().getAction("close"));
		toolBar.add(new JLabel("  "));
		toolBar.add(ActionManager.getInstance().getAction("closeAll"));
		toolBar.add(new JLabel("  "));
		toolBar.add(ActionManager.getInstance().getAction("delete"));
		toolBar.add(new JLabel("  "));
		toolBar.add(ActionManager.getInstance().getAction("about"));

		add(toolBar, BorderLayout.NORTH);
	}

	public boolean containsFileView(FileView fileView) {

		for (FileView anotherView : fileViews)
			if (!anotherView.isNew())
				if (anotherView.getModel().getName().equals(fileView.getModel().getName()))
					return true;
		return false;
	}

	public boolean containsDBFileView(DBFileView dbFileView) {

		for (DBFileView anotherView : dbFileViews)
			if (anotherView.getModel().getName().equals(dbFileView.getModel().getName()))
				return true;
		return false;
	}

	public boolean viewNameCheck(FileView fileView) {

		for (FileView anotherView : fileViews)
			if (anotherView.getName().equals(fileView.getName()))
				return true;
		return false;
	}

	public static AppGUI getInstance() {

		if (instance == null)
			instance = new AppGUI();
		return instance;
	}

	public FileTree getFileTree() {

		return fileTree;
	}

	public DBTree getDBTree() {

		return dbTree;
	}

	public ReportTree getReportTree() {

		return reportTree;
	}

	public JTabbedPane getTabbedPane() {

		return tabbedPane;
	}

	public ArrayList<FileView> getFileViews() {

		return fileViews;
	}

	public void addFileView(FileView fileView) {

		tabbedPane.add(fileView, fileView.getModel().getName());
		fileViews.add(fileView);
		tabbedPane.setSelectedComponent(fileView);

		SwingUtilities.updateComponentTreeUI(this);
	}

	public void setCurrentFileView(FileView fileView) {

		currentFileView = fileView;
	}

	public FileView getCurrentFileView() {

		return currentFileView;
	}

	public void updatePathField(String path) {

		pathField.setText(path);
		pathField.setForeground(Color.BLACK);
	}

	public JTextField getPathField() {

		return pathField;
	}

	public void updateInfo(String dateInfo, String sizeInfo) {

		String dateString = dateInfo.substring(8, 10) + "/" + dateInfo.substring(5, 7) + "/" + dateInfo.substring(0, 4);

		char[] hour = dateInfo.substring(11, 13).toCharArray();
		int hourFix = 0;
		for (int i = 0; i < hour.length; i++)
			hourFix += (hour[i] - '0') * (int) Math.pow(10, hour.length - i - 1);

		hourFix = hourFix % 24;

		String timeString = " (" + (hourFix + 2) + dateInfo.substring(13, 19) + ")";

		// Rade i datum i vrijeme, ali nekako sa datumom izgleda pretrpano. Ako
		// vi mislite da treba vraticu!
		timeString = ""; // Samo se ovo zakomentarise!

		date.setText("Date created: " + dateString + timeString);
		size.setText("File size: " + sizeInfo + " (bytes)");

		date.setFont(new Font("Fontana", Font.BOLD + Font.ITALIC, 12));
		date.setForeground(Color.black);
		size.setFont(new Font("Fontana", Font.BOLD + Font.ITALIC, 12));
		size.setForeground(Color.black);
	}

	public JTextArea[] getDateSize() {

		JTextArea[] dateSize = { date, size };

		return dateSize;
	}

	public void connectToDB() throws SQLException {

		login = new DBLoginDialog();
	}

	public boolean openConnection(String serverName, String databaseName, String userName, String password) {

		try {

			Class.forName("net.sourceforge.jtds.jdbc.Driver");

			String url = "jdbc:jtds:sqlserver://" + serverName + "/" + databaseName;
			connection = DriverManager.getConnection(url, userName, password);

			return true;

		} catch (Exception e) {

			JOptionPane.showMessageDialog(null, "Konekcija sa bazom nije uspostavljena!");
		}
		return false;
	}

	public Connection getConnection() {

		return connection;
	}

	public void disconnect() throws SQLException {

		connection.close();
		connection = null;
	}

	public ArrayList<DBFileView> getDBFileViews() {

		return dbFileViews;
	}

	public void setInfoFieldsDefault() {

		pathField.setText("Path of the selected file");
		pathField.setForeground(Color.gray);

		date.setText("Date of creation");
		date.setForeground(Color.gray);

		size.setText("Size of the file");
		size.setForeground(Color.gray);
	}
}
