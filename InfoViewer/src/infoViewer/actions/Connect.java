package infoViewer.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.sql.SQLException;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import infoViewer.model.trees.dbTree.DBNode;
import infoViewer.view.AppGUI;

public class Connect extends AbstractAction {

	public Connect() {

		putValue(NAME, "Connect");
		putValue(SMALL_ICON, new ImageIcon("src/resources/icons/connect.png"));
		putValue(SHORT_DESCRIPTION, "Connect to a database.");
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK));
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

		try {

			AppGUI.getInstance().connectToDB();
			((DBNode) AppGUI.getInstance().getDBTree().getModel().getRoot()).setType(DBNode.DATABASE);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
