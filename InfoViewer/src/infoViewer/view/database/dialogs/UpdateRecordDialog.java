package infoViewer.view.database.dialogs;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import infoViewer.model.database.DBFileModel;
import infoViewer.model.trees.dbTree.DBNode;
import infoViewer.view.AppGUI;
import net.miginfocom.swing.MigLayout;

public class UpdateRecordDialog extends JDialog {

	public static final int TEXT_FIELD_SIZE = 30;

	private ArrayList<JTextField> inputFields;
	private ArrayList<String> labelNames, oldValues;

	private DBFileModel dbFileModel;

	public UpdateRecordDialog(DBFileModel dbFileModel, int row) {

		if(row == -1) {
			
			JOptionPane.showMessageDialog(null, "Please select a record first!");
			return;
		}
		
		this.dbFileModel = dbFileModel;
		labelNames = dbFileModel.getColumnNames();

		setTitle("Update a record");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocation(AppGUI.WIDTH * 4 / 9, AppGUI.HEIGHT / 2);
		init(row);
		setVisible(true);
		pack();
	}

	private void init(int row) {

		String rows = "[]";

		for (int i = 0; i <= labelNames.size(); i++)
			rows += "5[]";

		setLayout(new MigLayout("", "[][]27", rows + "[]25"));

		inputFields = new ArrayList();
		oldValues = new ArrayList();
		
		for(int i = 0; i < labelNames.size(); i++)
			oldValues.add(((String) dbFileModel.getData()[row][i]).trim());

		add(new JLabel(" "), "wrap");

		for (int i = 0; i < labelNames.size(); i++) {

			add(new JLabel("<html>&nbsp;&nbsp;&nbsp;</html>"));
			JLabel label = new JLabel(labelNames.get(i));
			label.setFont(new Font("Fontana", Font.BOLD, 12));
			label.setForeground(new Color(55, 55, 55));
			add(label);
			JTextField inputField;

			inputField = new JTextField(TEXT_FIELD_SIZE);
			inputField.setText(oldValues.get(i));

			inputFields.add((JTextField) inputField);
			add(inputField, "wrap");
		}

		JButton addNewRecord = new JButton(" Update ");

		addNewRecord.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				ArrayList<String> inputValues = new ArrayList();

				for (int i = 0; i < inputFields.size(); i++)
					inputValues.add(inputFields.get(i).getText());

				dbFileModel.updateRecord(inputValues, oldValues);

				dispose();
			}
		});

		String cell = "cell 3 " + (labelNames.size() + 2);
		add(addNewRecord, cell);
	}
}
