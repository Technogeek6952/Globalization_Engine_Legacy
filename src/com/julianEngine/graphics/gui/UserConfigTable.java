package com.julianEngine.graphics.gui;

import java.util.Arrays;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

import com.julianEngine.config.UserConfiguration;
import com.julianEngine.utility.Log;

public class UserConfigTable extends JTable{
	private static final long serialVersionUID = 614405708574731703L;
	
	JComboBox<String> typeSelector = new JComboBox<String>();
	JComboBox<String> booleanSelector = new JComboBox<String>();
	
	public UserConfigTable(){
		super();
		this.setModel(new VariableEditorTableModel());
		this.getModel().getRowCount();
		this.setRowSelectionAllowed(false);
		this.setColumnSelectionAllowed(false);
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		typeSelector.addItem("Boolean");
		typeSelector.addItem("String");
		typeSelector.addItem("Integer");
		typeSelector.addItem("Double");
		typeSelector.addItem("Float");
		typeSelector.addItem("Character");
		
		booleanSelector.addItem("true");
		booleanSelector.addItem("false");
	}
	
	@Override
	public TableCellEditor getCellEditor(int row, int col){
		if (col==1){
			//if this is the second column, return the type selector combo box
			return new DefaultCellEditor(typeSelector);
		}else if (col==2){
			//if this is the third column
			if (((String)this.getValueAt(row, col-1)).equals("Boolean")){
				//if the type of the variable is boolean, give a selector for true/false
				return new DefaultCellEditor(booleanSelector);
			}else{
				return new DefaultCellEditor(new JTextField());
			}
		}
		
		//all other cells should be a default text editor
		return new DefaultCellEditor(new JTextField());
	}
	
	@Override
	public boolean isCellEditable(int row, int col){
		return true;
	}
	
	private class VariableEditorTableModel extends DefaultTableModel{
		private static final long serialVersionUID = 4477690304646246001L;

		private String[][] data;
		private int rows;
		
		@SuppressWarnings("unchecked")
		public void update(){
			data = UserConfiguration.getRawData();
			rows = data.length;
			
			this.dataVector = new Vector<Vector<Object>>();
			for (String[] row:data){
				Vector<Object> vectorRow = new Vector<Object>(row.length);
				vectorRow.addAll(Arrays.asList(row));
				this.dataVector.add(vectorRow);
			}
			UserConfigTable.this.repaint();
		}
		
		public VariableEditorTableModel(){
			super(new Object[1][3], new String[]{"Variable", "Type", "Value"});
			update();
		}
		
		@Override
		public boolean isCellEditable(int row, int col){
			
			if (col<0){
				return true;
			}
			
			return false;
		}
		
		@Override
		public Object getValueAt(int row, int col){
			if(row>=rows){
				return null;
			}
			return data[row][col];
		}
		
		@Override
		public int getRowCount(){
			return rows;
		}
		
		@Override
		public void setValueAt(Object newValue, int row, int col){
			if (col==0){
				//changing name
				switch((String)this.getValueAt(row, 1)){ //switch based on type
				case "Boolean":
					UserConfiguration.removeBool((String)this.getValueAt(row, 0));
					UserConfiguration.addBool((String) newValue, Boolean.parseBoolean((String)this.getValueAt(row, 2)));
					break;
				case "String":
					UserConfiguration.removeString((String)this.getValueAt(row, 0));
					UserConfiguration.addString((String) newValue, (String)this.getValueAt(row, 2));
					break;
				case "Integer":
					UserConfiguration.removeInt((String)this.getValueAt(row, 0));
					UserConfiguration.addInt((String) newValue, Integer.parseInt((String)this.getValueAt(row, 2)));
					break;
				case "Double":
					UserConfiguration.removeDouble((String)this.getValueAt(row, 0));
					UserConfiguration.addDouble((String) newValue, Double.parseDouble((String)this.getValueAt(row, 2)));
					break;
				case "Float":
					UserConfiguration.removeFloat((String)this.getValueAt(row, 0));
					UserConfiguration.addFloat((String) newValue, Float.parseFloat((String)this.getValueAt(row, 2)));
					break;
				case "Character":
					UserConfiguration.removeChar((String)this.getValueAt(row, 0));
					UserConfiguration.addChar((String) newValue, ((String)this.getValueAt(row, 2)).charAt(0));
					break;
				default:
					Log.error("Invalid type: "+(String)this.getValueAt(row, 1));
					break;
				}
				
				update();
			}else if(col==1){
				//changing type
				//first remove old value
				switch((String)this.getValueAt(row, 1)){ //switch based on type
				case "Boolean":
					UserConfiguration.removeBool((String)this.getValueAt(row, 0));
					break;
				case "String":
					UserConfiguration.removeString((String)this.getValueAt(row, 0));
					break;
				case "Integer":
					UserConfiguration.removeInt((String)this.getValueAt(row, 0));
					break;
				case "Double":
					UserConfiguration.removeDouble((String)this.getValueAt(row, 0));
					break;
				case "Float":
					UserConfiguration.removeFloat((String)this.getValueAt(row, 0));
					break;
				case "Character":
					UserConfiguration.removeChar((String)this.getValueAt(row, 0));
					break;
				default:
					Log.error("Invalid type: "+(String)this.getValueAt(row, 1));
					break;
				}
				
				//second add to list of new types (put in default values, in case the existing value is not compatible)
				switch((String)newValue){ //switch based on new type
				case "Boolean":
					UserConfiguration.addBool((String)this.getValueAt(row, 0), true);
					break;
				case "String":
					UserConfiguration.addString((String)this.getValueAt(row, 0), "");
					break;
				case "Integer":
					UserConfiguration.addInt((String)this.getValueAt(row, 0), 0);
					break;
				case "Double":
					UserConfiguration.addDouble((String)this.getValueAt(row, 0), 0.0);
					break;
				case "Float":
					UserConfiguration.addFloat((String)this.getValueAt(row, 0), 0f);
					break;
				case "Character":
					UserConfiguration.addChar((String)this.getValueAt(row, 0), ' ');
					break;
				default:
					Log.error("Invalid type: "+(String)this.getValueAt(row, 1));
					break;
				}
				
				//once the new values are in the user configuration table, we can update our own table
				update();
			}else{
				//changing value
				switch((String)this.getValueAt(row, 1)){ //switch based on type
				case "Boolean":
					UserConfiguration.addBool((String)this.getValueAt(row, 0), Boolean.parseBoolean((String)newValue));
					break;
				case "String":
					UserConfiguration.addString((String)this.getValueAt(row, 0), (String)newValue);
					break;
				case "Integer":
					UserConfiguration.addInt((String)this.getValueAt(row, 0), Integer.parseInt((String)newValue));
					break;
				case "Double":
					UserConfiguration.addDouble((String)this.getValueAt(row, 0), Double.parseDouble((String)newValue));
					break;
				case "Float":
					UserConfiguration.addFloat((String)this.getValueAt(row, 0), Float.parseFloat((String)newValue));
					break;
				case "Character":
					UserConfiguration.addChar((String)this.getValueAt(row, 0), ((String)newValue).charAt(0));
					break;
				default:
					Log.error("Invalid type: "+(String)this.getValueAt(row, 1));
					break;
				}
				
				update();
			}
			
			super.setValueAt(newValue, row, col); //after we have done all we need, we should call the super method to make suer all original functionality is kept
		}
	}
}
