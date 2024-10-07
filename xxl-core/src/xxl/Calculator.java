package xxl;

import java.io.IOException;
import java.io.FileNotFoundException;

import java.util.Map;
import java.util.TreeMap;

import xxl.exceptions.ImportFileException;
import xxl.exceptions.MissingFileAssociationException;
import xxl.exceptions.UnavailableFileException;
import xxl.exceptions.UnrecognizedEntryException;

import java.io.ObjectOutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Class representing a spreadsheet application.
 */
public class Calculator {
    private Spreadsheet _spreadsheet = null;
    private String _filename = "";
    private int _lines;
    private int _columns;
    private Map<String, User> _user = new TreeMap<String, User>();


    /**
     * Serializes and saves the state of the current spreadsheet into the file
     * associated with it, after checks if a valid file association exists. If
     * the spreadsheet contains unsaved changes, it serializes the state to the
     * file, overwriting the previous contents. After saving, the change flag
     * is reset to indicate that the data is up-to-date and saved.
     *
     * @throws FileNotFoundException if the associated file cannot be created
     *                               or opened.
     * @throws MissingFileAssociationException if there is no associated file
     *                                         with the current spreadsheet.
     * @throws IOException if there is an error while serializing the state of
     *                     the spreadsheet to disk.
     */
    public void save() throws FileNotFoundException, MissingFileAssociationException, IOException {
 		if (_filename == null || _filename.equals(""))
			throw new MissingFileAssociationException();
		try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(_filename)))) {
			oos.writeObject(_spreadsheet);
            _spreadsheet.setChange(false);
		}
	}
	

    /**
     * Works identically to the function "save", however, it sets the specified
     * file name as the new associated file for the calculator and then
     * triggers the saving process.
     *
     * @param filename The name of the file to save the spreadsheet's state to.
     * @throws FileNotFoundException if the specified file cannot be created or
     *                               opened.
     * @throws MissingFileAssociationException if there is no associated file
     *                                         with the current spreadsheet.
     * @throws IOException if there is an error while serializing the state of
     *                     the spreadsheet to the specified file.
     */
    public void saveAs(String filename) throws FileNotFoundException, MissingFileAssociationException, IOException {
   		_filename = filename;
		save();
    }

    /**
     * Loads the serialized application state from a specified file and
     * updates the current spreadsheet.
     * 
     * @param filename The name of the file containing the serialized
     *                 application's state to load.
     * @throws UnavailableFileException if the specified file does not exist or
     *                                  there is an error while processing this
     *                                  file.
     */
    public void load(String filename) throws UnavailableFileException {
        _filename = filename;
        try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)))) {
			_spreadsheet = (Spreadsheet) ois.readObject();
            _spreadsheet.setChange(false);
		} catch (IOException | ClassNotFoundException e) {
			throw new UnavailableFileException(filename);
		}
    }

    /**
     * Reads the specified text input file, interprets its contents, and
     * inserts data into the current spreadsheet. The file format should follow
     * specific rules, including defining the number of lines and columns at
     * the beginning of the file and providing cell data in subsequent lines.
     *
     * @param filename The name of the text input file to import data from.
     * @throws ImportFileException if there is an issue with reading the file
     *                             or processing its contents.
     */
    public void importFile(String filename) throws ImportFileException {
		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			String line;
            String[] thisline;
            int lineNumber = 0;
			while ((line = reader.readLine()) != null) {//read the file's lines;
                if (lineNumber < 2) {
                    thisline = line.split("=");
                    registerEntry(thisline);//save the number of lines and columns
                    if (lineNumber == 1) {
                        createSpreadsheetImport();//create the spreadsheet using the saved measurements
                    }
                } else {
                    thisline = line.split("\\|");
                    if (thisline.length == 2){
                        _spreadsheet.insertContents(thisline[0],thisline[1]);//save the specified cell contents
                    }
                }
                lineNumber++;
            }
        } catch ( IOException | UnrecognizedEntryException e) {
            throw new ImportFileException(filename, e);//handle exceptions that may occur during the import process
        }
    }

    /**
     * @return
     */
    public Spreadsheet getSpreadsheet() {
        return _spreadsheet;
    }

    /**
     * This method interprets an entry from a line of input and determines its
     * type. It can handle two types of entries: lines and columns. Depending
     * on the type of entry, it either sets the number of lines or columns for
     * the current spreadsheet or raises an exception if the entry is
     * unrecognized.
     * 
     * @param line An array containing the entry, where the first line
     *             represents the entry type, and the second line contains its
     *             value.
     * @throws UnrecognizedEntryException if the entry type is not a line or a
     *                                    column or if there is an issue with
     *                                    the entry format.
     */
    public void registerEntry(String[] line) throws UnrecognizedEntryException{
        switch (line[0]) {
            case "linhas" -> putLine(line[1]);
            case "colunas" -> putColumn(line[1]);
            default -> throw new UnrecognizedEntryException(line[0]);
        }
    }

    /**
     * This method takes a string that represents the number of lines and
     * converts it to an integer. It then assigns the result to the current
     * spreadsheet's line count, which will be used to create it.
     * 
     * @param line A string containing the number of lines to be set for the
     *             spreadsheet.
     * @throws UnrecognizedEntryException if there is an issue with the
     *                                    provided line value or its format.
     */
    public void putLine(String line) throws UnrecognizedEntryException{
        _lines = TransformInt(line);
    }

    /**
     * This method takes a string that represents the number of columns and
     * converts it to an integer. It then assigns the result to the current
     * spreadsheet's column count, which will be used to create it.
     * 
     * @param column A string containing the number of columns to be set for
     *               the spreadsheet.
     * @throws UnrecognizedEntryException if there is an issue with the
     *                                    provided column value or its format.
     */
    public void putColumn(String column) throws UnrecognizedEntryException{
        _columns = TransformInt(column);
    }

    /**
     * This method takes a string that represents an integer and attempts to
     * convert it to an integer. If the conversion is successful and the
     * integer is valid and positive, it returns the integer value.
     * 
     * @param number A string that represents an integer.
     * @return The integer value after successful conversion and validation.
     * @throws UnrecognizedEntryException if the provided string is not a valid
     *                                    integer or is not positive.
     */
    public int TransformInt(String number) throws UnrecognizedEntryException{
        int intValue;
        try{
            intValue = Integer.parseInt(number);
            if (intValue < 1){
                throw new UnrecognizedEntryException(number);
            }
        } catch(NumberFormatException e){
            throw new UnrecognizedEntryException(number, e);
        }
        return intValue;
    }

    /**
     * This method initializes a new spreadsheet with number of lines and
     * columns defined by the imported file, sets it as the current
     * spreadsheet, and marks it as changed, which indicates unsaved changes.
     */
    public void createSpreadsheetImport(){
        _spreadsheet = new Spreadsheet(_lines, _columns);
        _spreadsheet.setChange(true);
    }

    /**
     * This method initializes a new spreadsheet with the provided number of
     * lines and columns, sets it as the current spreadsheet, and marks it as
     * changed, which indicates unsaved changes.
     *
     * @param lines The number of lines for the new spreadsheet.
     * @param columns The number of columns for the new spreadsheet.
     */
    public void createSpreadsheet(int lines, int columns){
        _spreadsheet = new Spreadsheet(lines, columns);
        _spreadsheet.setChange(true);
    }
}