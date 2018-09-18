package infoViewer.view.database;

import java.awt.BorderLayout;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import infoViewer.model.database.DBFileModel;
import infoViewer.model.database.TableModel;
import infoViewer.view.AppGUI;
import net.miginfocom.swing.MigLayout;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.swing.JRViewer;

public class ReportView extends DBFileView {

	public ReportView(String name, JasperPrint print) {

		super(name);

		setLayout(new BorderLayout());

		add(new JRViewer(print), BorderLayout.CENTER);

		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout());
		for (int i = 0; i < 3; i++)
			panel.add(new JLabel(""), "wrap");
		add(panel, BorderLayout.SOUTH);

		if (!AppGUI.getInstance().containsDBFileView(this)) {

			AppGUI.getInstance().getTabbedPane().add(name, this);
			AppGUI.getInstance().getDBFileViews().add(this);

		} else
			System.out.println("Report_Is_Contained_In_The_GUI!");
	}
}
