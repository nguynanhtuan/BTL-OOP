package util;

import javax.swing.table.DefaultTableModel;


public class ReadOnlyTableModel extends DefaultTableModel {

    public ReadOnlyTableModel(Object... columnNames) {
        super(columnNames, 0);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}