package com.julianEngine.misc;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class UIChatWindow extends JFrame{
	private static final long serialVersionUID = 3734330531134861177L;
	Socket m_peer;
	BufferedReader in;
	PrintWriter out;
	public UIChatWindow(Socket peer){
		m_peer = peer;
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(500, 500);
		
		JTextArea chatBox = new JTextArea();
		chatBox.setEditable(false);
		this.getContentPane().add(chatBox, BorderLayout.CENTER);
		
		JPanel msgPanel = new JPanel();
		JTextField msg = new JTextField();
		JButton send = new JButton("Send");
		ActionListener onSend = (ActionEvent e) -> {
			out.println("[them] "+msg.getText());
			out.flush();
			chatBox.setText(chatBox.getText()+"[you] "+msg.getText()+"\n");
			msg.setText("");
		};
		send.addActionListener(onSend);
		msg.addActionListener(onSend);
		msgPanel.setLayout(new BoxLayout(msgPanel, BoxLayout.X_AXIS));
		msgPanel.add(msg);
		msgPanel.add(send);
		this.getContentPane().add(msgPanel, BorderLayout.SOUTH);
		
		try {
			in = new BufferedReader(new InputStreamReader(peer.getInputStream()));
			out = new PrintWriter(peer.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		new Thread(){
			public void run(){
				while(true){
					try {
						String newLine = in.readLine();
						chatBox.setText(chatBox.getText()+newLine+"\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
		this.setTitle("Chat");
	}
	
	/*
	public void show(){
		super.setVisible(true);
	}
	*/
}
