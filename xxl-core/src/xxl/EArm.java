package xxl;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;
import xxl.exceptions.UnrecognizedEntryException;

/**
 * Class representing the spreadsheet's structure.
 */
public class EArm implements Serializable{
    private int _linesTotal;
    private int _columnsTotal;
    private Map<String, Cell> _cellMap;

    /**
     * @param lines
     * @param columns
     */
    public EArm(int lines, int columns){
        _linesTotal = lines;
        _columnsTotal = columns;
        _cellMap = new TreeMap<String, Cell>();
        initializeCells();
    }

    /**
     * Creates the cells of a new spreadsheet.
     */
    private void initializeCells() {
        for (int i = 1; i <= _linesTotal; i++) {
            for (int j = 1; j <= _columnsTotal; j++) {
                _cellMap.put(""+i+";"+j, new Cell());
            }
        }
    }
    
    /**
     * Returns the amount of lines in the spreadsheet.
     * 
     * @return
     */
    public int getLinesTotal(){
        return _linesTotal;
    }

    /**
     * Returns the amount of columns in the spreadsheet.
     * 
     * @return
     */
    public int getColumnsTotal(){
        return _columnsTotal;
    }

    /**
     * Gets the specified cell.
     * 
     * @param rangeSpecification
     * @return
     * @throws UnrecognizedEntryException
     */
    public Cell getCell(String rangeSpecification) throws UnrecognizedEntryException{
        if (_cellMap.get(rangeSpecification) == null){
            throw new UnrecognizedEntryException(rangeSpecification);
        }
        return _cellMap.get(rangeSpecification);
    }
}

