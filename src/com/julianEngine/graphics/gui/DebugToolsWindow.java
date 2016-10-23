package com.julianEngine.graphics.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.julianEngine.utility.TextAreaOutputStream;

import java.awt.Color;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.JButton;
import javax.swing.JTabbedPane;
import java.awt.SystemColor;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JTable;

public class DebugToolsWindow extends JFrame {
	private static final long serialVersionUID = -6305585163373783555L;
	
	private JPanel contentPane;
	private PrintStream consoleOut;
	private JTextField textField;
	private PipedInputStream consoleIn = new PipedInputStream();
	private PipedOutputStream out = null;
	private JTable table;
	/**
	 * Create the frame.
	 */
	public DebugToolsWindow() {
		try {
			out = new PipedOutputStream(consoleIn);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		setBackground(Color.BLACK);
		setTitle("Debug Tools");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		try{UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}catch (Exception e){e.printStackTrace();}
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBackground(SystemColor.control);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		
		JPanel consolePanel = new JPanel();
		tabbedPane.addTab("Console", null, consolePanel, null);
		consolePanel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		consolePanel.add(scrollPane, BorderLayout.CENTER);
		
		JTextArea textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		
		JPanel panel_2 = new JPanel();
		consolePanel.add(panel_2, BorderLayout.SOUTH);
		panel_2.setLayout(new BorderLayout(0, 0));
		
		textField = new JTextField();
		panel_2.add(textField);
		textField.setColumns(10);
		
		JButton btnNewButton = new JButton("Enter");
		panel_2.add(btnNewButton, BorderLayout.EAST);
		
		consoleOut = new PrintStream(new TextAreaOutputStream(textArea, 100_000));
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("User Variables", null, panel, null);
		
		table = new JTable();
		panel.add(table);
		
		ActionListener sendAction = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					out.write((textField.getText()+"\n").getBytes());
					textField.setText("");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		btnNewButton.addActionListener(sendAction);
		textField.addActionListener(sendAction);
	}
	
	public PrintStream getPrintStream(){
		return consoleOut;
	}
	
	public InputStream getInputStream(){
		return consoleIn;
	}

}
