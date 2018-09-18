package infoViewer.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import infoViewer.view.AppGUI;
import infoViewer.view.files.FileView;

public class NewFile extends AbstractAction {

	public NewFile() {

		putValue(NAME, "New file");
		putValue(SMALL_ICON, new ImageIcon("src/resources/icons/New file.png"));
		putValue(SHORT_DESCRIPTION, "Create a new file.");
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

		String name = "New file ";
		int number = 1;

		AppGUI gui = AppGUI.getInstance();

		while (true) {

			FileView fileView = new FileView();
			fileView.setName(name + number);

			if (gui.viewNameCheck(fileView))
				number++;

			else {

				fileView.setIsNew(true);
				gui.getTabbedPane().add(fileView);
				gui.getTabbedPane().setSelectedComponent(fileView);
				gui.setCurrentFileView(fileView);
				gui.getFileViews().add(fileView);
				break;
			}
		}
	}
}
