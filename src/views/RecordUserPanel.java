package views;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import controller.RecordController;
import controller.UserController;
import model.HealthSystemUser;
import model.Observation;
import model.Recommendation;

public class RecordUserPanel extends JFrame {

	private JPanel contentPane;
	private JTextField txtValue;
	private JTextField txtDateOfObs;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RecordUserPanel frame = new RecordUserPanel();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public RecordUserPanel() {
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0, 0, 0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JLabel lblObservationType = new JLabel("Obesrvation Type");
		GridBagConstraints gbc_lblObservationType = new GridBagConstraints();
		gbc_lblObservationType.insets = new Insets(0, 0, 5, 5);
		gbc_lblObservationType.gridx = 1;
		gbc_lblObservationType.gridy = 1;
		contentPane.add(lblObservationType, gbc_lblObservationType);
		
		UserController obsCont = new UserController();
		Map<Observation,Recommendation> obsList = obsCont.getRecommendations(Main.currentUser);
		
		JComboBox<String> cmbxObsType = new JComboBox<String>();
		List<Observation> observationList = new ArrayList<Observation>();
		for(Observation obs : obsList.keySet())
		{
			cmbxObsType.addItem(obs.getType());
			observationList.add(obs);
		}
		GridBagConstraints gbc_cmbxObsType = new GridBagConstraints();
		gbc_cmbxObsType.insets = new Insets(0, 0, 5, 0);
		gbc_cmbxObsType.anchor = GridBagConstraints.WEST;
		gbc_cmbxObsType.gridx = 3;
		gbc_cmbxObsType.gridy = 1;
		contentPane.add(cmbxObsType, gbc_cmbxObsType);
		
		JLabel lblValue = new JLabel("Value");
		GridBagConstraints gbc_lblValue = new GridBagConstraints();
		gbc_lblValue.insets = new Insets(0, 0, 5, 5);
		gbc_lblValue.gridx = 1;
		gbc_lblValue.gridy = 2;
		contentPane.add(lblValue, gbc_lblValue);
		
		txtValue = new JTextField();
		GridBagConstraints gbc_txtValue = new GridBagConstraints();
		gbc_txtValue.insets = new Insets(0, 0, 5, 0);
		gbc_txtValue.anchor = GridBagConstraints.WEST;
		gbc_txtValue.gridx = 3;
		gbc_txtValue.gridy = 2;
		contentPane.add(txtValue, gbc_txtValue);
		txtValue.setColumns(10);
		
		JLabel lblDateOfObservation = new JLabel("Date of Observation");
		GridBagConstraints gbc_lblDateOfObservation = new GridBagConstraints();
		gbc_lblDateOfObservation.insets = new Insets(0, 0, 5, 5);
		gbc_lblDateOfObservation.gridx = 1;
		gbc_lblDateOfObservation.gridy = 3;
		contentPane.add(lblDateOfObservation, gbc_lblDateOfObservation);
		
		txtDateOfObs = new JTextField();
		GridBagConstraints gbc_txtDateOfObs = new GridBagConstraints();
		gbc_txtDateOfObs.insets = new Insets(0, 0, 5, 0);
		gbc_txtDateOfObs.anchor = GridBagConstraints.WEST;
		gbc_txtDateOfObs.gridx = 3;
		gbc_txtDateOfObs.gridy = 3;
		contentPane.add(txtDateOfObs, gbc_txtDateOfObs);
		txtDateOfObs.setColumns(10);
		
		JButton btnAddObservation = new JButton("Add Observation");
		btnAddObservation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selectedIndex = cmbxObsType.getSelectedIndex();
				Observation observation = observationList.get(selectedIndex);
				String value = txtValue.getText();
				String date = txtDateOfObs.getText();
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				Date temp = null;
				try {
				temp=sdf.parse(date);
				java.sql.Date recordingTime = new java.sql.Date(Calendar.getInstance().getTime().getTime());
	        	java.sql.Date sql_temp = new java.sql.Date(temp.getTime());
	        	RecordController record = new RecordController();
				int count = record.insertRecord(Main.currentUser,observation, value, sql_temp,recordingTime);
				if(count == 1)
						JOptionPane.showMessageDialog(null, "Observation Record added successfully!","Add Record",JOptionPane.INFORMATION_MESSAGE);
				else
					JOptionPane.showMessageDialog(null, "Error in adding observation record!","Add Record",JOptionPane.ERROR_MESSAGE);
				} catch (ParseException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null, "Error in adding observation record!","Add Record",JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		JLabel lblEnterDateIn = new JLabel("(Enter date in yyyy-MM-dd hh:mm:ss)");
		GridBagConstraints gbc_lblEnterDateIn = new GridBagConstraints();
		gbc_lblEnterDateIn.insets = new Insets(0, 0, 5, 0);
		gbc_lblEnterDateIn.gridx = 3;
		gbc_lblEnterDateIn.gridy = 4;
		contentPane.add(lblEnterDateIn, gbc_lblEnterDateIn);
		GridBagConstraints gbc_btnAddObservation = new GridBagConstraints();
		gbc_btnAddObservation.insets = new Insets(0, 0, 0, 5);
		gbc_btnAddObservation.gridx = 1;
		gbc_btnAddObservation.gridy = 5;
		contentPane.add(btnAddObservation, gbc_btnAddObservation);
	}

}
