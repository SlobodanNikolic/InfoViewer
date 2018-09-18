package infoViewer.view.database.dialogs;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;

import infoViewer.model.database.DBFileModel;
import infoViewer.view.AppGUI;
import net.miginfocom.swing.MigLayout;

public class SortMDIDialog extends JDialog {

	public static final int TEXT_FIELD_SIZE = 30;

	private boolean descending = false;

	private ArrayList<String> labelNames;
	private ArrayList<String> checkedColumns;

	private DBFileModel dbFileModel;

	public SortMDIDialog(DBFileModel dbFileModel) {

		this.dbFileModel = dbFileModel;
		labelNames = dbFileModel.getColumnNames();

		checkedColumns = new ArrayList();

		setTitle("Sort the table");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		init();
		setVisible(true);
		pack();
		setLocation(AppGUI.WIDTH / 2 + AppGUI.WIDTH / 5, AppGUI.HEIGHT / 2);
	}

	private void init() {

		String rows = "[]";

		for (int i = 0; i <= labelNames.size(); i++)
			rows += "10[]";

		setLayout(new MigLayout("", "25[]25[]27", rows + "10[]25"));

		initRadioButons();

		add(new JLabel(" "), "wrap");

		for (int i = 0; i < labelNames.size(); i++) {

			JCheckBox checkBox = new JCheckBox(labelNames.get(i));
			checkBox.setFont(new Font("Fontana", Font.BOLD, 12));
			checkBox.setForeground(new Color(55, 55, 55));
			add(checkBox, "wrap");

			checkBox.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {

					if (checkBox.isSelected()) {

						if (!checkedColumns.contains(checkBox.getText()))
							checkedColumns.add(checkBox.getText());

					} else {

						if (checkedColumns.contains(checkBox.getText()))
							checkedColumns.remove(checkBox.getText());
					}
				}
			});
		}

		JButton addNewRecord = new JButton(" Sort ");

		addNewRecord.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				try {

					if (checkedColumns.size() == 0) {

						JOptionPane.showMessageDialog(null, "No sort parameters checked!", "Warning",
								JOptionPane.WARNING_MESSAGE);
						return;
					}

					dbFileModel.sortMDI(checkedColumns, descending);

				} catch (SQLException e1) {
					e1.printStackTrace();
				}

				dispose();
			}
		});

		String cell = "cell 3 " + (labelNames.size() + 3);
		add(addNewRecord, cell);
	}

	private void initRadioButons() {

		add(new JLabel(" "), "wrap");

		JRadioButton asc = new JRadioButton("Ascending");
		JRadioButton desc = new JRadioButton("Descending");

		asc.setSelected(true);
		asc.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				asc.setSelected(true);
				desc.setSelected(false);
				descending = false;
			}
		});

		add(asc);
		add(desc, "wrap");

		desc.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				desc.setSelected(true);
				asc.setSelected(false);
				descending = true;
			}
		});
	}
}
