package infoViewer.listeners;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import infoViewer.model.trees.dbTree.DBNode;
import infoViewer.view.AppGUI;
import infoViewer.view.database.DBFileView;

public class DBTreeMouseListener extends MouseAdapter {

	@Override
	public void mouseClicked(MouseEvent e) {

		TreePath treePath = AppGUI.getInstance().getDBTree().getLeadSelectionPath();
		
		if (treePath != null)
			if (e.getButton() == MouseEvent.BUTTON1)
				if (e.getClickCount() == 2) {

					DBNode dbNode = (DBNode) treePath.getLastPathComponent();
					if (dbNode.getType() == DBNode.TABLE) {

						DBFileView dbFileView = new DBFileView(dbNode.getName());
						
						if (!AppGUI.getInstance().containsDBFileView(dbFileView)) {

							AppGUI.getInstance().getDBFileViews().add(dbFileView);
							AppGUI.getInstance().getTabbedPane().add(dbFileView);
							AppGUI.getInstance().getTabbedPane().setSelectedComponent(dbFileView);
						}
					}
				}
	}
}
