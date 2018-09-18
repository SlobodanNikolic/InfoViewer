package infoViewer.view.database;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Observable;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import infoViewer.model.database.DBFileModel;
import infoViewer.model.database.TableModel;
import infoViewer.view.Observer;
import infoViewer.view.database.dialogs.AddRecordDialog;
import infoViewer.view.database.dialogs.SortMDIDialog;
import infoViewer.view.database.dialogs.UpdateRecordDialog;
import net.miginfocom.swing.MigLayout;

public class DBFileView extends Observer {

	private DBFileModel dbFileModel;
	private JTable table;

	public DBFileView(String name) {

		setName(name);
		setLayout(new BorderLayout());

		dbFileModel = new DBFileModel(name);
		dbFileModel.addObserver(this);

		try {

			dbFileModel.readHeader();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		formButtons();

		table = new JTable();
		table.setModel(new TableModel(dbFileModel.getData(), dbFileModel.getColumnNames().toArray()));

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBorder(BorderFactory.createTitledBorder(""));
		add(scrollPane, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout());
		for (int i = 0; i < 3; i++)
			panel.add(new JLabel(""), "wrap");
		add(panel, BorderLayout.SOUTH);
	}

	public DBFileModel getModel() {

		return dbFileModel;
	}

	private void formButtons() {

		String[] buttons = new String[] { "Refresh", "Add Record", "Update Record", "Delete Record", "Sort MDI",
				"Filter Find Record" };

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new MigLayout());
		buttonPanel.add(new JLabel("<html>&nbsp;&nbsp;&nbsp;</html>"));
		for (int i = 0; i < 6; i++) {

			JButton button = new JButton(buttons[i]);

			activateButton(button, buttons[i]);

			buttonPanel.add(button);
			buttonPanel.add(new JLabel("<html>&emsp;</html>"));
		}
		add(buttonPanel, BorderLayout.NORTH);
	}

	private void activateButton(JButton button, String name) {

		switch (name) {

		case "Refresh":
			button.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					try {

						dbFileModel.readData();
						table.setModel(new TableModel(dbFileModel.getData(), dbFileModel.getColumnNames().toArray()));

					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
			});
			break;

		case "Add Record":
			button.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					new AddRecordDialog(dbFileModel);
				}
			});
			break;

		case "Update Record":
			button.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					if (table.getSelectedRow() == -1) {

						JOptionPane.showMessageDialog(null, "Please select the record again.");
						return;
					}
					new UpdateRecordDialog(dbFileModel, table.getSelectedRow());
				}
			});
			break;

		case "Delete Record":
			button.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					try {
						dbFileModel.deleteRecord(table.getValueAt(table.getSelectedRow(), table.getSelectedColumn()),
								table.getSelectedRow());
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
			break;

		case "Sort MDI":
			button.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					new SortMDIDialog(dbFileModel);
				}
			});
			break;

		case "Filter Find Record":
			button.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					JOptionPane.showMessageDialog(null,
							"You need to purchase the PREMIUM_VERSION in order to use this option.   " + "\n"
									+ "         (in order to do so, please contact the CREATORS of this app)");
				}
			});
			break;
		}
	}

	@Override
	public void update(Observable arg0, Object arg1) {

		try {
			dbFileModel.readData(); 
		} catch (SQLException e) {
			e.printStackTrace();
		}
		table.setModel(new TableModel(dbFileModel.getData(), dbFileModel.getColumnNames().toArray()));
	}

	public JTable getTable() {

		return table;
	}
}
