package infoViewer.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.sql.SQLException;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.tree.DefaultMutableTreeNode;

import infoViewer.model.trees.dbTree.DBNode;
import infoViewer.view.AppGUI;

public class Disconnect extends AbstractAction {

	public Disconnect() {

		putValue(NAME, "Disconnect");
		putValue(SMALL_ICON, new ImageIcon("src/resources/icons/disconnect.png"));
		putValue(SHORT_DESCRIPTION, "Disconnect from the database.");
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK));
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

		if (AppGUI.getInstance().getConnection() == null) {

			JOptionPane.showMessageDialog(null, "There are no connections to close!");
			return;
		}

		int option = JOptionPane.showOptionDialog(null, "Are you sure you want to disconnect?", "Disconnect",
				JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[] { "Yes", "No" }, 1);

		if (option == 0) {

			try {

				AppGUI.getInstance().disconnect();

				// DBTree root
				((DefaultMutableTreeNode) AppGUI.getInstance().getDBTree().getModel().getRoot()).removeAllChildren();
				((DBNode) AppGUI.getInstance().getDBTree().getModel().getRoot()).setName("No database");
				((DBNode) AppGUI.getInstance().getDBTree().getModel().getRoot()).setType(-1);
				AppGUI.getInstance().getDBTree().collapseRow(0);
				// ReportTree root
				((DefaultMutableTreeNode) AppGUI.getInstance().getReportTree().getModel().getRoot())
						.removeAllChildren();
				((DBNode) AppGUI.getInstance().getReportTree().getModel().getRoot()).setName("No database");
				AppGUI.getInstance().getReportTree().collapseRow(0);

			} catch (SQLException e) {

				JOptionPane.showMessageDialog(null, "Disconnect action failed!", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
