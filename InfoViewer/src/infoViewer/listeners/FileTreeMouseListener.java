package infoViewer.listeners;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import infoViewer.actions.ActionManager;
import infoViewer.actions.Save;
import infoViewer.view.AppGUI;
import infoViewer.view.files.FileView;

public class FileTreeMouseListener extends MouseAdapter {

	@Override
	public void mouseClicked(MouseEvent e) {

		super.mouseClicked(e);

		TreePath treePath = AppGUI.getInstance().getFileTree().getLeadSelectionPath();

		if (treePath != null)
			if (e.getButton() == MouseEvent.BUTTON1)
				if (e.getClickCount() == 2) {

					Path path = Paths.get(treePath.getLastPathComponent().toString());

					if (treePath.getLastPathComponent().toString().endsWith(".txt")) {

						try {

							BasicFileAttributes basicFileAttributes = Files.readAttributes(path,
									BasicFileAttributes.class);

							AppGUI.getInstance().updateInfo(basicFileAttributes.creationTime() + "",
									basicFileAttributes.size() + "");

						} catch (IOException Batman) {
							// TODO Auto-generated catch block
							Batman.printStackTrace();
						}

						AppGUI.getInstance()
								.updatePathField(treePath.getParentPath().getLastPathComponent().toString());

						try {

							FileReader fileReader = new FileReader(treePath.getLastPathComponent().toString());
							BufferedReader bufferedReader = new BufferedReader(fileReader);

							String text = "", line;

							while ((line = bufferedReader.readLine()) != null)
								text = text + line + '\n';

							FileView fileView = new FileView();
							fileView.setPath(treePath.getLastPathComponent().toString());

							if (!AppGUI.getInstance().containsFileView(fileView)) {

								AppGUI.getInstance().getTabbedPane().add(fileView);
								AppGUI.getInstance().getTabbedPane().setSelectedComponent(fileView);
								AppGUI.getInstance().setCurrentFileView(fileView);
								AppGUI.getInstance().getFileViews().add(fileView);

								fileView.getModel().setText(text);
								fileView.setIsEdited(false);
								fileView.setIsNew(false);
							}
							bufferedReader.close();

						} catch (FileNotFoundException Superman) {
							// TODO Auto-generated catch block
							Superman.printStackTrace();
						} catch (IOException Superman) {
							// TODO Auto-generated catch block
							Superman.printStackTrace();
						}

					} else {

						if (!AppGUI.getInstance().getFileViews().isEmpty())
							AppGUI.getInstance().setCurrentFileView(AppGUI.getInstance().getFileViews().get(0));

						if (((FileView) AppGUI.getInstance().getCurrentFileView()) != null)
							if (((FileView) AppGUI.getInstance().getCurrentFileView()).isNew()) {

								String wayToTheFile = treePath.getLastPathComponent() + "\\"
										+ AppGUI.getInstance().getCurrentFileView().getName() + ".txt";

								System.out.println("WAY_TO_THE_FILE: " + wayToTheFile);
								((Save) ActionManager.getInstance().getAction("save")).setBackupPath(wayToTheFile);
							}

						if (AppGUI.getInstance().getFileTree().isExpanded(treePath))
							AppGUI.getInstance().updatePathField(treePath.getLastPathComponent().toString());
						else
							AppGUI.getInstance()
									.updatePathField(treePath.getParentPath().getLastPathComponent().toString());
					}
				}
	}
}
