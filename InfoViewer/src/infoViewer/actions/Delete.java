package infoViewer.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import infoViewer.view.AppGUI;

public class Delete extends AbstractAction {

	public Delete() {

		putValue(NAME, "Delete");
		putValue(SMALL_ICON, new ImageIcon("src/resources/icons/trash-icon.png"));
		putValue(SHORT_DESCRIPTION, "Delete file.");
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_DOWN_MASK));
	}

	public void actionPerformed(ActionEvent arg0) {

		TreePath treePath = AppGUI.getInstance().getFileTree().getSelectionModel().getLeadSelectionPath();
		if(treePath != null) {
			
			File fileNew = new File(treePath.getLastPathComponent().toString());

			if (fileNew.exists()) {
				fileNew.delete();
			} else if (fileNew.isDirectory() && fileNew.getTotalSpace() == 0) {
				fileNew.delete();
			}

			DefaultMutableTreeNode root = new DefaultMutableTreeNode();

			DefaultTreeModel treeModel = new DefaultTreeModel(root);

			FileSystemView fileSystemView = FileSystemView.getFileSystemView();

			// Grananje
			File[] roots = fileSystemView.getRoots();
			for (File fileSystemRoot : roots) {

				DefaultMutableTreeNode node = new DefaultMutableTreeNode(fileSystemRoot);
				root.add(node);
				File[] files = fileSystemView.getFiles(fileSystemRoot, true);

				for (File file : files) {

					if (file.isDirectory())
						node.add(new DefaultMutableTreeNode(file));
				}
			}
			AppGUI.getInstance().getFileTree().setModel(treeModel);
			AppGUI.getInstance().getFileTree().expandRow(0);
			
		} else 
			JOptionPane.showMessageDialog(null, "You must select a file (-> tree) in order to delete it.");
	}
}