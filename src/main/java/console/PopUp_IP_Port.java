package console;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import connection.SocketConnector;
import utilities.Constants;

/**
 * Class that creates the IP / Port popup
 *
 * @author Matthias Mack 3316380
 */
public class PopUp_IP_Port {
	/**
	 * flag for the dialog, has to be voltaile 
	 */
	private static volatile boolean dialogVisible = true;
	/**
	 * the main frame for the popup
	 */
	private static JFrame popupframe;

	/**
	 * Shows the dialog for the ip and the port
	 */
	public static void showPopup() {
		// create the jframe
		popupframe = new JFrame(Constants.POPUP_TITLE);
		// set the default close operation to exit
		popupframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// add the border
		JPanel p = new JPanel();
		// new layout for the Jpanel
		FlowLayout flay = new FlowLayout();
		// align the things on the left
		flay.setAlignment(FlowLayout.LEFT);
		p.setLayout(flay);
		// boxlayout for the form
		p.setBorder(new EmptyBorder(10, 10, 10, 10));
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		// adds the lbl for the IP-----------------------------------
		JLabel lbl_IP = new JLabel("IP [v4]");
		lbl_IP.setBorder(new EmptyBorder(2, 0, 6, 0));
		p.add(lbl_IP);
		// add the panel to the frame
		popupframe.add(p);
		// -----------------------------------------------------------------
		// textbox for the IP
		JTextField txtIp = new JTextField();
		txtIp.setText(SocketConnector.getIp());
		p.add(txtIp);
		// ------------------------------------------------------------
		// adds the lbl for the Port-----------------------------------
		JLabel lbl_port = new JLabel("Port");
		lbl_port.setBorder(new EmptyBorder(6, 0, 6, 0));
		p.add(lbl_port);
		// textbox for the Port
		JTextField txtPort = new JTextField();
		txtPort.setText(String.valueOf((SocketConnector.getPort())));
		p.add(txtPort);
		// ------------------------------------------------------------
		// placeholder for the  button, to move it downwards
		JLabel lblPlaceholder = new JLabel();
		// set the padding
		lblPlaceholder.setBorder(new EmptyBorder(6, 0, 6, 0));
		p.add(lblPlaceholder);
		// ---------button for the submit
		JButton submit = new JButton(Constants.POPUP_SUBMIT);
		p.add(submit);
		// add the event listener
		submit.addActionListener(new ActionListener() {
			// wait for the buttonclick
			public void actionPerformed(ActionEvent e) {
				// check if anything changed
				try {
					// check if the ip  or the port has changed
					if (!txtIp.getText().toString().equals(SocketConnector.getIp())
							|| !(Integer.parseInt(txtPort.getText()) == SocketConnector.getPort())) {
						// if it is different, test the ip
						// the adresses from 0-255
						String addressSpace = "(\\d{1,2}|(0|1)\\" + "d{2}|2[0-4]\\d|25[0-5])";

						// Regex for a digit from 0 to 255 and
						// followed by a dot, repeat 4 times.
						// this is the regex to validate an IP address.
						String checkIp = addressSpace + "\\." + addressSpace + "\\." + addressSpace + "\\."
								+ addressSpace;
						// if IP_V4 is valid put it in
						if (txtIp.getText().matches(checkIp)) {
							// adds the values to the socket Connector
							SocketConnector.setIp(txtIp.getText().toString());
						} else { // else print out the error message
							throw new Exception();
						}
						// if the port is valid put it in as well
						if (txtPort.getText().matches("^[1-9]+[0-9]*$")) {
							SocketConnector.setPort(Integer.parseInt(txtPort.getText().toString()));
						} else { // else print out the error message
							throw new NumberFormatException();
						}
						// hide the form
						popupframe.setVisible(false);
						dialogVisible = false;
						// print out the choosen ip and port combination
						printOutIP(SocketConnector.getIp(), SocketConnector.getPort());
					} else { // if the things are normal, set mainBoolean
						// print out the choosen ip and port combination
						printOutIP(SocketConnector.getIp(), SocketConnector.getPort());
						// hide the form
						popupframe.setVisible(false);
						dialogVisible = false;
					}
				} catch (NumberFormatException e1) {
					System.err.println(Constants.POPUP_UNVALID_PORT);
					// hide form
					popupframe.setVisible(false);
					// restart
					PopUp_IP_Port.showPopup();
				} catch (Exception e2) {
					System.err.println(Constants.POPUP_UNVALID_IP);
					// hide form
					popupframe.setVisible(false);
					// restart
					PopUp_IP_Port.showPopup();
				}
			}
		});
		// set resizeable
		popupframe.setResizable(false);
		// pack the frame
		popupframe.pack();
		// set the visibility
		popupframe.setVisible(true);
		// size of the frame
		popupframe.setSize(200, 180);
	}

	/**
	 * Getter for the dialog ready
	 *
	 * @return the dialogReady boolean
	 */
	public static boolean isDialogReady() {
		return dialogVisible;
	}

	/**
	 * Setter for the dialogReady
	 * 
	 * @param dialogReady the dialogReady to set
	 */
	public static void setDialogReady(boolean dialogReady) {
		PopUp_IP_Port.dialogVisible = dialogReady;
	}

	/**
	 * Method that checks if the popupframe is displayed
	 * 
	 * @return
	 */
	public static boolean isDisplayed() {
		return dialogVisible;
	}

	/**
	 * Method that prints out the choosen ip and the port
	 * 
	 * @param ip   - as string 255.255.255
	 * @param port - as int 1-99999
	 */
	public static void printOutIP(String ip, int port) {
		System.out.println("--------------------------------------");
		System.out.println("Server: " + ip + " Port: " + port);
		System.out.println("--------------------------------------");
	}
}