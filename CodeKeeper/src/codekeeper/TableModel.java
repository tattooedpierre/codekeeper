/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package codekeeper;

import javax.swing.table.AbstractTableModel;

/**
 *
 * @author tattooedpierre
 */
public class TableModel extends AbstractTableModel {

    String[] columnNames =
    {
        "first name", "last name", "sports"
    };

    Object[][] data;
    
    @Override
    public int getColumnCount()
    {
        return columnNames.length;
    }

    @Override
    public int getRowCount()
    {
        return data.length;
    }

    @Override
    public String getColumnName(int col)
    {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int row, int col)
    {
        return data[row][col];
    }

    public boolean isCellEditable()
    {
        return true;
    }
}
