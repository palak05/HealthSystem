package views;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import controller.UserController;
import model.HealthSystemUser;

public class EditProfile extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JFormattedTextField textField_4;
	private HealthSystemUser user;
	/*private JLabel lblPassword;
	private JPasswordField passwordField;*/

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					EditProfile frame = new EditProfile(Main.currentUser);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * @throws ParseException 
	 */

	public EditProfile(HealthSystemUser user) {
		this.user = user;
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblUid = new JLabel("UID");
		lblUid.setBounds(10, 11, 46, 14);
		contentPane.add(lblUid);
		
		textField = new JTextField();
		textField.setText(user.getId());
		textField.setBounds(115, 8, 110, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JLabel lblName = new JLabel("Name");
		lblName.setBounds(10, 36, 46, 14);
		contentPane.add(lblName);
		
		textField_1 = new JTextField();
		textField_1.setText(user.getName());
		textField_1.setBounds(115, 33, 110, 20);
		contentPane.add(textField_1);
		textField_1.setColumns(10);
		
		JLabel lblAddress = new JLabel("Address");
		lblAddress.setBounds(10, 61, 86, 14);
		contentPane.add(lblAddress);
		
		textField_2 = new JTextField();
		textField_2.setText(user.getAddress());
		textField_2.setBounds(115, 58, 110, 20);
		contentPane.add(textField_2);
		textField_2.setColumns(10);
		
		JLabel lblGender = new JLabel("Gender");
		lblGender.setBounds(10, 86, 46, 14);
		contentPane.add(lblGender);
		
		textField_3 = new JTextField();
		textField_3.setText(user.getGender());
		textField_3.setBounds(115, 83, 110, 20);
		contentPane.add(textField_3);
		textField_3.setColumns(10);
		
		JLabel lblDateOfBirth = new JLabel("Date of Birth");
		lblDateOfBirth.setBounds(10, 112, 86, 14);
		contentPane.add(lblDateOfBirth);
		
		SimpleDateFormat format = new SimpleDateFormat("dd-mm-yy");
		textField_4 = new JFormattedTextField(format);
		textField_4.setValue(user.getDateOfBirth());
		textField_4.setBounds(115, 109, 110, 20);
		contentPane.add(textField_4);
		textField_4.setColumns(10);
		/*
		lblPassword = new JLabel("Password");
		lblPassword.setBounds(10, 137, 86, 14);
		contentPane.add(lblPassword);
		
		passwordField = new JPasswordField();
		passwordField.setText(user.getPassword());
		passwordField.setBounds(115, 134, 110, 20);
		contentPane.add(passwordField);*/
		
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UserController userController = new UserController();
				userController.updateUser(textField.getText(), textField_1.getText(), 
						textField_2.getText(), textField_3.getText(), (Date) textField_4.getValue(), "password");
				System.out.println( (Date) textField_4.getValue());
				JOptionPane.showMessageDialog(null, "Profile is updated successfully!","Success",
                        JOptionPane.INFORMATION_MESSAGE);
			}
		});
		btnSave.setBounds(10, 179, 89, 23);
		contentPane.add(btnSave);
		
		
	}
}
