package console;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

import Utilities.Constants;
import connection.SocketConnector;

/**
 * Class that represents a console for the Output!
 *
 * @author Matthias Mack 3316380
 */
public class Console extends OutputStream {
	// Singleton-Pattern START -----------------------------------------

	/**
	 * Singleton instance of Console
	 */
	private static Console instance = null;

	/**
	 * private constructor to prevent instantiation
	 */
	private Console() {

	}

	/**
	 * Returns singleton BusDepot instance
	 *
	 * @return Console Singleton instance
	 */
	public static synchronized Console getConsole() {
		if (instance == null) {
			instance = new Console();
		}
		return instance;
	}

	// Singleton-Pattern END ________________________________________________

	/**
	 * Appender for the recent actions
	 */
	private Append append;

	/**
	 * Bytearray for the data
	 */
	private byte[] byteArr;

	/**
	 * Constructor with init Textarea
	 * 
	 * @param jtxtarea - JTextArea with 1500 lines
	 */
	public Console(JTextArea jtxtarea) {
		this(jtxtarea, 1500);
	}

	/**
	 * Constructor
	 * 
	 * @param jtxtarea - the textarea
	 * @param maxLines - maximum lines
	 */
	public Console(JTextArea jtxtarea, int maxLines) {
		if (maxLines < 1) {
			throw new IllegalArgumentException("Maximale Zeilen müssen positiv sein! " + maxLines);
		}
		// set the array to length 1
		byteArr = new byte[1];
		// initiate the new Textarea
		append = new Append(jtxtarea, maxLines);
	}

	/**
	 * Method that runs the console
	 */
	public void runConsole() {
		// create the frame
		JFrame jFrame = new JFrame("Smart-RMX");
		// add the top description with IP and Port
		jFrame.add(new JLabel(
				"Smart-RMX Console | Server: " + SocketConnector.getIp() + " | Port: " + SocketConnector.getPort()),
				BorderLayout.NORTH);
		// create the JTExtArea
		JTextArea jTextArea = new JTextArea();
		// create a new Console from the JTExtArea
		Console console = new Console(jTextArea, 60);

		// the stream for the console -> redirect syserr & sysout
		PrintStream printStream = new PrintStream(console);
		System.setOut(printStream);
		System.setErr(printStream);

		// add the frame
		jFrame.add(new JScrollPane(jTextArea));
		// read only
		jTextArea.setEditable(false);
		jFrame.pack();
		// visibility
		jFrame.setVisible(true);
		// size of the frame
		jFrame.setSize(800, 550);
		// exit on close
		jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		// bigger font size 12 pt
		jTextArea.setFont(jTextArea.getFont().deriveFont(14f));
	}

	/**
	 * Clear the console
	 */
	public synchronized void clear() {
		// if there are no more lines clear
		if (append != null) {
			append.clear();
		}
	}

	/**
	 * write a single byte to JtextBox
	 */
	public synchronized void write(int val) {
		byteArr[0] = (byte) val;
		write(byteArr, 0, 1);
	}

	/**
	 * write an byte Array to textBox
	 */
	public synchronized void write(byte[] byteArr) {
		write(byteArr, 0, byteArr.length);
	}

	/**
	 * Method to write out a byteArray by the runnable Append class
	 */
	public synchronized void write(byte[] byteArr, int startPoint, int arrLenght) {
		if (append != null) {
			append.append(bytesToString(byteArr, startPoint, arrLenght));
		}
	}

	/**
	 * Method that passes a byteArray to an String with local UTF-8 Encoding
	 * 
	 * @param byteArr    - gets a Array with bytes
	 * @param startPoint - the startpoint of the Array
	 * @param arrLength  - the lenght of the Array
	 * @return
	 */
	static private String bytesToString(byte[] byteArr, int startPoint, int arrLength) {
		try {
			// format the String as UTF-8
			return new String(byteArr, startPoint, arrLength, "UTF-8");
		} catch (UnsupportedEncodingException thr) {
			// if the Encoding is not supported, return the "normal" string
			return new String(byteArr, startPoint, arrLength);
		}
	}

	/**
	 * Closes the console
	 */
	public synchronized void close() {
		append = null;
	}

	/**
	 * Class for the runnable Append
	 *
	 * @author Matthias Mack 3316380
	 */
	static class Append implements Runnable {
		/*
		 * the text Area
		 */
		private final JTextArea jTextArea;

		/*
		 * maximum lines of code
		 */
		private final int maxLines;

		/**
		 * Lists with the lengs and the vals of the textArea
		 */
		private final LinkedList<Integer> lengths;

		/**
		 * List for the values
		 */
		private final List<String> vals;
		/**
		 * Integer for the current Length
		 */
		private int currentLen;
		/**
		 * Boolean which says when to clear the console
		 */
		private boolean clear;
		/**
		 * Boolean for the queue
		 */
		private boolean queueBool;

		/**
		 * Constructor for the append
		 * 
		 * @param jTxtArea - pass the textArea
		 * @param maxLines - maximum lines
		 */
		Append(JTextArea jTxtArea, int maxLines) {
			this.jTextArea = jTxtArea;
			this.maxLines = maxLines;
			this.lengths = new LinkedList<Integer>();
			this.vals = new ArrayList<String>();
			this.currentLen = 0;
			this.clear = false;
			this.queueBool = true;
		}

		/**
		 * Appends a val
		 * 
		 * @param val
		 */
		synchronized void append(String val) {
			vals.add(val);
			if (queueBool) {
				queueBool = false;
				EventQueue.invokeLater(this);
			}
		}

		/**
		 * Clears the console
		 */
		synchronized void clear() {
			clear = true;
			currentLen = 0;
			lengths.clear();
			vals.clear();
			// if queue is true
			if (queueBool) {
				// set it to false
				queueBool = false;
				EventQueue.invokeLater(this);
			}
		}

		// regular run method for Runnable
		public synchronized void run() {
			if (clear) {
				jTextArea.setText("");
			}
			// for each String loop
			for (String val : vals) {
				// increment the lenght by the value length
				currentLen += val.length();
				// if it is the end of the line do
				if (val.endsWith(Constants.EOF) || val.endsWith(Constants.EOF_SYS)) {
					// if the lines expand the maximum line size do
					if (lengths.size() >= maxLines) {
						// replace the textrange
						jTextArea.replaceRange("", 0, lengths.removeFirst());
					}
					lengths.addLast(currentLen);
					currentLen = 0;
				}
				jTextArea.append(val);
			}
			// reset Variables
			vals.clear();
			clear = false;
			queueBool = true;
		}
	}
}