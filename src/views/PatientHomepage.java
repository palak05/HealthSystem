package views;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import model.HealthSystemUser;
import net.miginfocom.swing.MigLayout;

public class PatientHomepage {
	
	private HealthSystemUser user;
	
	public PatientHomepage(HealthSystemUser user) {
		this.user = user;
	}

	public void patientLayout() {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocation(300, 400);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		JComponent panel1 = new JPanel(new MigLayout());
		tabbedPane.addTab("Profile", null, panel1,
		                  "Does nothing");
		frame.add(tabbedPane);
		JLabel uidLabel = new JLabel("UID");
		JLabel nameLabel = new JLabel("Name");
		JTextField userField = new JTextField();
		JTextField passwordField = new JTextField();
		panel1.add(uidLabel);
		panel1.add(userField, "wrap, grow, width 150:250");
		panel1.add(nameLabel);
		panel1.add(passwordField, "wrap, grow, width 150:250");
		
		//Diagnoses
		DiagnosesPanel diagnosesPanel = new DiagnosesPanel(user);
		tabbedPane.addTab("Diagnoses", diagnosesPanel);
		
		//HealthIndicators
		JComponent panel3 = new JPanel();
		tabbedPane.addTab("Health Indicators", panel3);

		JComponent panel4 = new JPanel();
		panel4.setPreferredSize(new Dimension(410, 50));
		tabbedPane.addTab("Alerts", panel4);
		
		JComponent panel5 = new JPanel();
		panel5.setPreferredSize(new Dimension(410, 50));
		tabbedPane.addTab("Health Supporters", panel5);
		
//		tabbedPane.addChangeListener(new ChangeListener() {
//		      public void stateChanged(ChangeEvent e) {
//		    	  if(tabbedPane.getSelectedComponent() instanceof DiagnosesPanel)
//		    		  diagnosesPanel.populateDiagnoses();
//		      }
//		    });
		
		frame.pack();
		frame.setVisible(true);
	}

	}
