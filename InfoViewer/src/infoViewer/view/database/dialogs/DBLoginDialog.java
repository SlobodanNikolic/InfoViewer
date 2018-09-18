package infoViewer.view.database.dialogs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.text.JTextComponent;

import infoViewer.model.trees.dbTree.DBNode;
import infoViewer.view.AppGUI;
import net.miginfocom.swing.MigLayout;

public class DBLoginDialog extends JDialog {

	public static final int SERVER_NAME = 0;
	public static final int DATABASE_NAME = 1;
	public static final int USER_NAME = 2;
	public static final int PASSWORD = 3;

	public static final int TEXT_FIELD_SIZE = 18;

	public static final int WIDTH = AppGUI.WIDTH / 2 + AppGUI.HEIGHT / 24;
	public static final int HEIGHT = AppGUI.HEIGHT / 3 + AppGUI.HEIGHT / 14;

	private ArrayList<JTextField> inputFields;

	public DBLoginDialog() {

		setTitle("Database login");
		setSize(WIDTH, HEIGHT);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		init();
		setVisible(true);
	}

	private void init() {

		setLayout(new MigLayout("", "[][]20", "[]5[]5[]5[]5[]"));

		inputFields = new ArrayList();

		ArrayList<String> labelNames = new ArrayList(
				Arrays.asList(" _Server_name_ ", " _Database_name_ ", " _Username_ ", " _Password_ "));

		ArrayList<String> fieldValues = new ArrayList(
				Arrays.asList("147.91.175.155", "ui-2015-tim204.3", "ui-2015-tim204.3", "ui-2015-tim204.3.00su01"));

		add(new JLabel("\n"), "wrap");

		for (int i = 0; i < 4; i++) {

			add(new JLabel("<html>&nbsp;&nbsp;&nbsp;</html>"));
			JLabel label = new JLabel(labelNames.get(i).replace("_", " "));
			label.setFont(new Font("Fontana", Font.BOLD, 12));
			label.setForeground(new Color(55, 55, 55));
			add(label);
			JTextComponent inputField;

			if (i == 3) {

				inputField = new JPasswordField(TEXT_FIELD_SIZE);
				((JPasswordField) inputField).setEchoChar('*');
			}

			else {

				inputField = new JTextField(TEXT_FIELD_SIZE);
			}

			inputField.setText(fieldValues.get(i));

			inputFields.add((JTextField) inputField);
			add(inputField, "wrap");
		}

		add(new JLabel("\n"), "wrap");

		JButton connect = new JButton("Connect to database");

		connect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				String serverName = inputFields.get(SERVER_NAME).getText();
				String databaseName = inputFields.get(DATABASE_NAME).getText();
				String userName = inputFields.get(USER_NAME).getText();
				String password = inputFields.get(PASSWORD).getText();

				AppGUI.getInstance().openConnection(serverName, databaseName, userName, password);

				try {

					AppGUI.getInstance().getDBTree().formDBTree(AppGUI.getInstance().getConnection());
					// Root name -> DatabaseName
					DBNode dbNode = (DBNode) AppGUI.getInstance().getDBTree().getModel().getRoot();
					dbNode.setName(databaseName);
					
					AppGUI.getInstance().getReportTree().formReportTree(AppGUI.getInstance().getConnection());
					// Root name -> "Sport"
					DBNode reportNode = (DBNode) AppGUI.getInstance().getReportTree().getModel().getRoot();
					reportNode.setName("Sport");
				
				} catch (SQLException e1) {
					e1.printStackTrace();
				}

				dispose();
			}
		});

		add(connect, "cell 4 6");
	}

	public ArrayList<JTextField> getInputFields() {

		return inputFields;
	}
}
