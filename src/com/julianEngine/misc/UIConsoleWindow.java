package com.julianEngine.misc;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.julianEngine.utility.Log;

public class UIConsoleWindow extends JFrame{
	private static final long serialVersionUID = 3734330531134861177L;
	
	PipedInputStream sysIn = new PipedInputStream(); //this should become the system input, can be written to by out
	PipedOutputStream out; //this is what we write to to send data to the system
	
	PipedOutputStream sysOut = new PipedOutputStream(); //this becomes the system output, hooked to in to be read
	PipedInputStream in;
	
	public UIConsoleWindow(){
		try {
			out = new PipedOutputStream(sysIn);
			in = new PipedInputStream(sysOut);
		} catch (IOException e2) {
			Log.error("Error connecting streams while creating a console window");
			e2.printStackTrace();
		}
		
		System.setIn(sysIn);
		System.setOut(new PrintStream(sysOut));
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(500, 500);
		
		JTextArea consoleOut = new JTextArea();
		consoleOut.setEditable(false);
		this.getContentPane().add(consoleOut, BorderLayout.CENTER);
		
		JPanel cmdPanel = new JPanel();
		JTextField cmdBox = new JTextField();
		JButton runButton = new JButton("Run");
		ActionListener onSend = (ActionEvent e) -> {
			try {
				out.write(cmdBox.getText().getBytes());
				out.flush();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			cmdBox.setText("");
		};
		runButton.addActionListener(onSend);
		cmdBox.addActionListener(onSend);
		
		cmdPanel.setLayout(new BoxLayout(cmdPanel, BoxLayout.X_AXIS));
		cmdPanel.add(cmdBox);
		cmdPanel.add(runButton);
		this.getContentPane().add(cmdPanel, BorderLayout.SOUTH);
	
		new Thread(){
			public void run(){
				BufferedReader in = new BufferedReader(new InputStreamReader(UIConsoleWindow.this.in));
				while(true){
					try {
						String sysData = in.readLine();
						consoleOut.setText(consoleOut.getText()+sysData+"\n");
					} catch (IOException e) {
						Log.error("Error reading from system output");
						e.printStackTrace();
					}
				}
			}
		}.start();
		
		//GlobalizationMain.reloadCIN();
		
		this.setTitle("Console");
	}
}
