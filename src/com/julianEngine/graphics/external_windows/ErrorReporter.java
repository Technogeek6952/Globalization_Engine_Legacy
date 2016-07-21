package com.julianEngine.graphics.external_windows;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;

public class ErrorReporter extends JFrame{
	public static void displayError(Exception e){
		StringBuilder sb = new StringBuilder(e.toString());
		for (StackTraceElement ste : e.getStackTrace()) {
	        sb.append("\n\tat ");
	        sb.append(ste);
	    }
	    String trace = sb.toString();
	    new ErrorReporter(e.getMessage(), trace).setVisible(true);;
	}
	
	public ErrorReporter(String error, String stackTrace){
		this.setTitle("Unexpected Error: "+error);
		this.setSize(500, 500);
		
		JTextArea errorTrace = new JTextArea(stackTrace);
		errorTrace.setEditable(false);
		this.add(errorTrace, BorderLayout.CENTER);
		
		JButton done = new JButton("Dismiss");
		done.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ErrorReporter.this.setVisible(false);
			}
		});
	}
}
