package xxl;

import xxl.exceptions.UnrecognizedEntryException;
import xxl.cellcontent.*;
import xxl.search.*;

import java.util.Map;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import java.io.Serial;
import java.io.Serializable;

/**
 * Class representing a spreadsheet.
 */
public class Spreadsheet implements Serializable {

    @Serial
    private static final long serialVersionUID = 202308312359L;

    private Map<String, User> _user = new TreeMap<String, User>();
    private boolean _change = false;
    private EArm _cells;
    private Spreadsheet _cutBuffer;
    private List<String> _copiedContent = new ArrayList<>();

    
    /**
     * @param lines
     * @param columns
     */
    public Spreadsheet(int lines, int columns){
        _cells = new EArm(lines, columns);
    }

	/**
     * Defines if there have been any changes to the spreadsheet.
     * 
	 * @param bool
	 */
	public void setChange(boolean bool) {
		_change = bool;
	}

	/**
     * Verifies if there have been any changes to the spreadsheet.
     * 
	 * @return
	 */
	public boolean isThereChange() {
		return _change;
	}

    /**
     * Insert specified content in specified range.
     *
     * @param rangeSpecification
     * @param contentSpecification
     * @throws UnrecognizedEntryException
     */
    public void insertContents(String rangeSpecification, String contentSpecification) throws UnrecognizedEntryException{  
        Content content = contentParser(contentSpecification);
        if (rangeSpecification.contains(":")){
            int[] range = parseRangeSpecification(rangeSpecification);

            for (int line = Math.min(range[0], range[2]); line <= Math.max(range[0], range[2]); line++) {
                for (int col = Math.min(range[1], range[3]); col <= Math.max(range[1], range[3]); col++) {
                    Cell cell = _cells.getCell(line + ";" + col);
                    cell.insertContent(content);
                }
            }
        } else {
            Cell cell = _cells.getCell(rangeSpecification);
            cell.insertContent(content);
        }
    }

    /**
     * Turns specified content of String class into an object of Content class.
     * 
     * @param contentSpecification
     * @return
     * @throws UnrecognizedEntryException
     */
    public Content contentParser(String contentSpecification) throws UnrecognizedEntryException{
        char firstChar = contentSpecification.charAt(0);
        if (Character.isDigit(firstChar)) {
            return new Lint(contentSpecification);
        } else {
            switch (firstChar) {
                case '-'  : return parseContentInt(contentSpecification);
                case '='  : return parseContentRefFunc(contentSpecification);
                case '\'' : return new Lstr(contentSpecification);
                default : throw new UnrecognizedEntryException(contentSpecification);
            }
        }
    }

    /**
     * Turns specified content of String class into content that represents an Integer.
     * 
     * @param contentSpecification
     * @return
     * @throws UnrecognizedEntryException
     */
    public Content parseContentInt(String contentSpecification) throws UnrecognizedEntryException {
        char secondChar = contentSpecification.charAt(1);
        if (Character.isDigit(secondChar)) {
            return new Lint(contentSpecification);
        } else {
            throw new UnrecognizedEntryException(contentSpecification);
        }
    }

    /**
     * Turns specified content of String class into content that represents a reference that points to a function.
     * 
     * @param contentSpecification
     * @return
     * @throws UnrecognizedEntryException
     */
    public Content parseContentRefFunc(String contentSpecification) throws UnrecognizedEntryException {
        String[] parts = contentSpecification.split("\\(");
        String functionName = parts[0].substring(1);

        if ((functionName.equals("ADD") || functionName.equals("SUB") || 
          functionName.equals("DIV") || functionName.equals("MUL")) &&
          (parts.length > 1)) {//verifies if we're handling a binary function.
            String[] arguments = parts[1].substring(0, parts[1].length() - 1).split(",");

            if (arguments[0].contains(";")){
                arguments[0] = "=" + arguments[0];
            }
            if (arguments[1].contains(";")){
                arguments[1] = "=" + arguments[1];
            }

            // analyzes the argument's content.
            Content arg1 = contentParser(arguments[0]);
            Content arg2 = contentParser(arguments[1]);

            if (arg1 == null || arg2 == null){
                throw new UnrecognizedEntryException(contentSpecification);
            }

            if(arg1.getType().contains("Reference") || 
               arg2.getType().contains("Reference") ||
               arg1.getType().contains("Lint") || arg2.getType().contains("Lint")){
                switch(functionName){
                    case "ADD" : return new Add(contentSpecification);
                    case "DIV" : return new Div(contentSpecification);
                    case "MUL" : return new Mul(contentSpecification);
                    case "SUB" : return new Sub(contentSpecification);
                    default : throw new UnrecognizedEntryException(contentSpecification);
                }
            } else{
                throw new UnrecognizedEntryException(contentSpecification);
            } 
        } else if ((functionName.equals("AVERAGE") || functionName.equals("PRODUCT") ||
          functionName.equals("CONCAT") || functionName.equals("COALESCE")) && 
          (parts.length > 1)){// verifies if we're handling an interval function.
            String[] arguments = parts[1].substring(0, parts[1].length() - 1).split(":");

            String[] linColStart = arguments[0].split(";");
            String[] linColEnd = arguments[1].split(";");
            
            int startLine = Integer.parseInt(linColStart[0]);
            int startColumn = Integer.parseInt(linColStart[1]);
            int endLine = Integer.parseInt(linColEnd[0]);
            int endColumn = Integer.parseInt(linColEnd[1]);
            
            if ((startLine != endLine) && (startColumn != endColumn)){
                throw new UnrecognizedEntryException(contentSpecification);
            }

            switch(functionName){
                case "AVERAGE"  : return new Average(contentSpecification);
                case "PRODUCT"  : return new Product(contentSpecification);
                case "CONCAT"   : return new Concat(contentSpecification);
                case "COALESCE" : return new Coalesce(contentSpecification);
                default : throw new UnrecognizedEntryException(contentSpecification);
            }

        } else if (isCoordinate(contentSpecification.substring(1))){//verifies if we're handling a reference
            return new Reference(contentSpecification);
        } else {
            throw new UnrecognizedEntryException(contentSpecification);
        }
    }

    /**
     * Shows the content of the specified cell or cells.
     * 
     * @param rangeSpecification
     * @return
     * @throws UnrecognizedEntryException
     */
    public String visualize(String rangeSpecification) throws UnrecognizedEntryException {
        if (rangeSpecification.contains(":")) {
            return searchRange(rangeSpecification);
        } else {
            return searchNoRange(rangeSpecification) + "\n1";
        }
    }

    /**
     * Shows the content of the specified cells.
     * 
     * @param rangeSpecification
     * @return
     * @throws UnrecognizedEntryException
     */
    public String searchRange(String rangeSpecification) throws UnrecognizedEntryException {
        int[] range = parseRangeSpecification(rangeSpecification);
        List<String> resultList = new ArrayList<>();
        int i = 0;

        for (int line = Math.min(range[0], range[2]); line <= Math.max(range[0], range[2]); line++) {
            for (int col = Math.min(range[1], range[3]); col <= Math.max(range[1], range[3]); col++) {
                resultList.add(searchNoRange(line + ";" + col));
                i++;
            }
        }
        resultList.add("" + i);
        return String.join("\n", resultList);
    }

    /**
     * Generates a list of integers representing the starting and
     * ending coordinates of a cell interval.
     * 
     * @param rangeSpecification
     * @return
     * @throws UnrecognizedEntryException
     */
    public int[] parseRangeSpecification(String rangeSpecification) throws UnrecognizedEntryException {
        String[] coordinates = rangeSpecification.split(":");
        String[] linColStart = coordinates[0].split(";");
        String[] linColEnd = coordinates[1].split(";");

        if (linColStart.length < 2 || linColEnd.length < 2) {
            throw new UnrecognizedEntryException(rangeSpecification);
        }

        int startLine = Integer.parseInt(linColStart[0]);
        int startColumn = Integer.parseInt(linColStart[1]);
        int endLine = Integer.parseInt(linColEnd[0]);
        int endColumn = Integer.parseInt(linColEnd[1]);

        if ((startLine != endLine) && (startColumn != endColumn)){//determines if it's a line or a column
            throw new UnrecognizedEntryException(rangeSpecification);
        }
        return new int[]{startLine, startColumn, endLine, endColumn};
    }

    /**
     * Shows the content of the specified cell.
     * 
     * @param rangeSpecification
     * @return
     * @throws UnrecognizedEntryException
     */
    public String searchNoRange(String rangeSpecification) throws UnrecognizedEntryException {
        Cell cell = _cells.getCell(rangeSpecification);
        Content content = cell.getContent();
        if (content == null) {
            return "" + rangeSpecification + "|";
        } else if (content.getType().contains("BinaryFunction")) {
            return "" + rangeSpecification + searchBinFunction(content, cell);
        } else if (content.getType().contains("IntervalFunction")) {
            return "" + rangeSpecification + searchIntervalFunction(content, cell);
        } else if (content.getType().contains("Lint") || content.getType().contains("Lstr")) {
            return "" + rangeSpecification + cell.toString();
        } else if (content.getType().contains("Reference")) {
            return "" + rangeSpecification + "|" + refInRef(rangeSpecification) + cell.toString();
        } else {
            throw new UnrecognizedEntryException(rangeSpecification);
        }
    }

    /**
     * Shows the binary function of the specified cell.
     * 
     * @param content
     * @param cell
     * @return
     * @throws UnrecognizedEntryException
     */
    public String searchBinFunction(Content content, Cell cell) throws UnrecognizedEntryException {
        BinaryFunction binaryFunction = (BinaryFunction) content;

        String contentStr = cell.getContentStr();
        String[] parts = contentStr.split("\\(");
        parts = parts[1].substring(0, parts[1].length() - 1).split(",");
        String firstPart, secondPart;

        //obtains the content of a reference, if one exists.
        if (parts[0].contains(";")) {
            firstPart = refInRef(parts[0]);
        } else {
            firstPart = parts[0];
        }

        if (parts[1].contains(";")) {
            secondPart = refInRef(parts[1]);
        } else {
            secondPart = parts[1];
        }
        
        if (hasInvalidContent(firstPart) ||  hasInvalidContent(secondPart)) {
            return "|#VALUE" + contentStr;
        }

        int firstValue = Integer.parseInt(firstPart);
        int secondValue = Integer.parseInt(secondPart);

        binaryFunction.operation(firstValue, secondValue);
        return cell.toString();
    }

    /**
     * Verifies if a cell has invalid content.
     * 
     * @param input
     * @return
     */
    private boolean hasInvalidContent(String input) {
        return input.startsWith("'") || input.startsWith("#");
    } 

    /**
     * Shows the content of an interval function.
     * 
     * @param content
     * @param cell
     * @return
     * @throws UnrecognizedEntryException
     */
    public String searchIntervalFunction(Content content, Cell cell) throws UnrecognizedEntryException{
        String contentStr = cell.getContentStr();
        String[] parts = contentStr.split("\\(");
        String rangeSpecification = parts[1].substring(0, parts[1].length() - 1);
        String resRange = searchRange(rangeSpecification); //Retrieves all the data from the cells within the function.
        parts = resRange.split("[\\=\\\n]");
        
        if (content.getType().contains("IntervalFunctionInt")){
            searchIntervalFunctionInt(content, parts);
            return cell.toString();
        } else {
            searchIntervalFunctionStr(content, parts);
            return cell.toString();
        }
    }

    /**
     * Shows the content of an interval function that handles Integers.
     * 
     * @param content
     * @param parts
     * @throws UnrecognizedEntryException
     */
    public void searchIntervalFunctionInt(Content content, String[] parts) throws UnrecognizedEntryException {
        IntervalFunctionInt intervalFunction = (IntervalFunctionInt) content;

        String extractedValues = "";
        int length = 0;

        for (String part : parts) { //obtains the content of each cell in the function.
            String[] subParts = part.split("\\|");
            if (subParts.length > 1) {
                char firstChar = subParts[1].charAt(0);
                if (Character.isDigit(firstChar) || firstChar == '-'){
                    extractedValues += subParts[1] + "|";
                    length += 1;
                } else if (subParts[1].contains("#VALUE") || firstChar == '\''){
                    intervalFunction.setRendered("#VALUE");
                    return;
                }
            }
            else if (isCoordinate(subParts[0])){
                intervalFunction.setRendered("#VALUE");
                return;
            }
        }

        parts = extractedValues.split("\\|");
        int[] values = new int[length];
        int iterador = 0;
        for (String part : parts){
            values[iterador] = Integer.parseInt(part);
            iterador +=1;
        }

        intervalFunction.operation(values);
    }

    /**
     * Shows the content of an interval function that handles Strings.
     * 
     * @param content
     * @param parts
     * @throws UnrecognizedEntryException
     */
    public void searchIntervalFunctionStr(Content content, String[] parts) throws UnrecognizedEntryException {
        IntervalFunctionStr intervalFunction = (IntervalFunctionStr) content;

        String extractedValues = "";
        int length = 0;

        for (String part : parts) { //obtains the content of each cell in the function.
            String[] subParts = part.split("\\|");
            if (subParts.length > 1) {
                char firstChar = subParts[1].charAt(0);
                if (firstChar == '\''){
                    extractedValues += subParts[1] + "|";
                    length += 1;
                }
            }
        }

        parts = extractedValues.split("\\|");
        String[] values = new String[length];
        int iterador = 0;
        for (String part : parts){
            values[iterador] = part;
            iterador +=1;
        }

        intervalFunction.operation(values);
    }

    /**
     * In a chain of multiple cells that possess references to other cells,
     * finds the last cell, the one that isn't a reference.
     * 
     * @param rangeSpecification
     * @return
     * @throws UnrecognizedEntryException
     */
    public String refInRef(String rangeSpecification) throws UnrecognizedEntryException {
        String coordinate = rangeSpecification;
        Cell cell = _cells.getCell(coordinate);
        Content content = cell.getContent();

        while (content == null || !(content.getType().contains("Lint")
              || content.getType().contains("Lstr"))) { //finds what the reference is pointing to
            String contentStr = cell.getContentStr();

            if (contentStr == "") { //if the reference points to a cell without content.
                return "#VALUE";
            } else if (cell.getContent().getType().contains("BinaryFunction")){ //if the reference points to a binary function, returns its result.
                String[] parts = searchBinFunction(content , cell).split("\\|");
                parts = parts[1].split("=");
                return parts[0];
            } else if (cell.getContent().getType().contains("IntervalFunction")){ //if the reference points to an interval function, returns its result.
                String[] parts = searchIntervalFunction(content , cell).split("\\|");
                parts = parts[1].split("=");
                return parts[0];
            }
            cell = _cells.getCell(contentStr.substring(1));
            content = cell.getContent();
        }
        return cell.getContentStr(); //if the reference points to an Integer or a String.
    }

    /**
     * Verifies if the receive String is a coordinate.
     * 
     * @param coordinate
     * @return
     */
    public boolean isCoordinate(String coordinate){
        char firstChar = coordinate.charAt(0);
        char lastchar = coordinate.charAt(coordinate.length() -1);

        return(Character.isDigit(firstChar) && coordinate.contains(";") && 
            Character.isDigit(lastchar));
    }
    
    /**
     * Shows the cells that have the selected value, as well as their content.
     * 
     * @param p
     * @return
     */
    public String showValues(PredicateContent p){
        int columns = _cells.getColumnsTotal();
        int lines =_cells.getLinesTotal();
        String result = "";

        for (int i = 1; i <= lines; i ++){
            for(int j = 1; j <= columns; j++){
                try{
                    result += p.equalsTo(visualize(i + ";" + j));
                } catch(UnrecognizedEntryException e){
                    e.printStackTrace();
                }
            }
        }
        result = result.replaceAll("\n$", ""); //removes the last \n.
        return result;
    }

    /**
     * Shows the cells that have the selected function, as well as their content.
     * 
     * @param p
     * @return
     */
    public String showFunctions(PredicateContent p) {
        String result = showValues(p);
        String[] resultArray = result.split("\n");

        //Uses Comparator to order the String array of cells and their content.
        Arrays.sort(resultArray, new ResultComparator());
        //Joins the cells and their contents into a String.
        StringBuilder resultString = new StringBuilder();
        for (String sortedResult : resultArray) {
            resultString.append(sortedResult).append("\n");
        }
        
        //Removes the last \n.
        if (resultString.length() > 0) {
            resultString.setLength(resultString.length() - 1);
        }
        return resultString.toString();
    }

    /**
     * Copies the content of a chosen cell, line of cells or column of cells.
     * 
     * @param rangeSpecification
     * @throws UnrecognizedEntryException
     */
    public void copy(String rangeSpecification) throws UnrecognizedEntryException{
            newCutBuffer(rangeSpecification);
            _copiedContent.clear();
            String rangeContent = visualize(rangeSpecification);
            saveContentCopy(rangeContent);
            insertContentCutBuffer(rangeContent);
    }

    /**
     * Creates a new cutbuffer every time there's new copied content.
     * 
     * @param rangeSpecification
     * @throws UnrecognizedEntryException
     */
    public void newCutBuffer(String rangeSpecification) throws UnrecognizedEntryException{
        if (rangeSpecification.contains(":")){
            int[] range = parseRangeSpecification(rangeSpecification);
            if (range[0] == range[2]){ //verifies if rangeSpecification is a line.
                _cutBuffer = new Spreadsheet(1, Math.abs(range[1] - range[3]) + 1);
            } else { //if it's not a line, it's a column.
                _cutBuffer = new Spreadsheet(Math.abs(range[0] - range[2]) + 1, 1);
            }
        } else{
            _cutBuffer = new Spreadsheet(1, 1);
        }
    }

    /**
     * Saves the final result of functions and references, as well as Integers and 
     * Strings, copied into the cutbuffer.
     * 
     * @param rangeContent
     */
    public void saveContentCopy(String rangeContent){
        String[] parts = rangeContent.split("[\\=\\\n]");
        for (String part : parts) {
            String[] subParts = part.split("\\|");
            if (subParts.length > 1){
                _copiedContent.add(subParts[1]);
            } else if (isCoordinate(subParts[0])){
                _copiedContent.add("");
            }
        }
    }

    /**
     * Inserts the copied content into the cutbuffer.
     * 
     * @param rangeContent
     * @throws UnrecognizedEntryException
     */
    public void insertContentCutBuffer(String rangeContent) throws UnrecognizedEntryException{
        String[] parts = rangeContent.split("[\\n]");
        int columnsT = _cutBuffer._cells.getColumnsTotal();
        int linesT = _cutBuffer._cells.getLinesTotal();
        int i = 0;

        for (int line = 1; line <= linesT; line++) {
            for (int col = 1; col <= columnsT; col++) {
                String[] subParts = parts[i].split("\\|");
                auxInsertCutBuffer(subParts, line, col);
                i ++;
            }
        }
    }

    /**
     * Auxiliary to insertContentCutBuffer.
     * 
     * @param subParts
     * @param line
     * @param col
     * @throws UnrecognizedEntryException
     */
    private void auxInsertCutBuffer(String[] subParts, int line, int col) throws UnrecognizedEntryException{
        if (subParts.length > 1){
            if (subParts[1].contains("=")){
                _cutBuffer.insertContents(line + ";" + col, 
                    subParts[1].substring(subParts[1].indexOf("=")));
            } else {
                _cutBuffer.insertContents(line + ";" + col, subParts[1]);
            }
        }
    }

    /**
     * Shows the cutbuffer's content.
     * 
     * @return
     */
    public String showCutBuffer(){
        int columnsT = _cutBuffer._cells.getColumnsTotal();
        int linesT = _cutBuffer._cells.getLinesTotal();
        int i = 0;
        List<String> resultList = new ArrayList<>();

        try{
            for (int line = 1; line <= linesT; line++) {
                for (int col = 1; col <= columnsT; col++) {
                    Cell cell = _cutBuffer._cells.getCell(line + ";" + col);
                    if (cell.getContent() != null && 
                       (cell.getContent().getType().contains("Function") || 
                        cell.getContent().getType().contains("Reference"))){ //obtains the result of the copied function or reference.
                        resultList.add(line + ";" + col+ "|" + _copiedContent.get(i) + cell.getContentStr());
                    } else{
                        resultList.add(line + ";" + col+ "|" + cell.getContentStr());
                    }
                    i++;
                }
            }
        } catch(UnrecognizedEntryException e){
            e.printStackTrace();
        }

        return String.join("\n", resultList); //adds a \n after each cell's data.
    }

    /**
     * Deletes the content of the specified cell or cells.
     * 
     * @param rangeSpecification
     * @throws UnrecognizedEntryException
     */
    public void delete(String rangeSpecification) throws UnrecognizedEntryException{
        if (rangeSpecification.contains(":")){
            int[] range = parseRangeSpecification(rangeSpecification);
            for (int line = Math.min(range[0], range[2]); line <= Math.max(range[0], range[2]); line++) {
                for (int col = Math.min(range[1], range[3]); col <= Math.max(range[1], range[3]); col++) {
                    deleteContent(line + ";" + col);
                }
            }
        } else{
            deleteContent(rangeSpecification);
        }
    }

    /**
     * Deletes the content of the specified cell.
     * 
     * @param rangeSpecification
     * @throws UnrecognizedEntryException
     */
    public void deleteContent(String rangeSpecification) throws UnrecognizedEntryException{
        Cell cell = _cells.getCell(rangeSpecification);
        cell.insertContent(null);
    }

    /**
     * Pastes the content stored in the cutbuffer into the specified cell or cells.
     * 
     * @param rangeSpecification
     * @throws UnrecognizedEntryException
     */
    public void paste(String rangeSpecification) throws UnrecognizedEntryException{
        int[] range;
        //gets the initial coordinates for pasting on the spreadsheet.
        if (rangeSpecification.contains(":")){
            range = parseRangeSpecification(rangeSpecification);
            range[0] = Math.min(range[0], range[2]);
            range[1] = Math.min(range[1], range[3]);
        } else {
            String[] coordinate = rangeSpecification.split(";");
            range = new int[]{Integer.parseInt(coordinate[0]), 
                              Integer.parseInt(coordinate[1])};
        }

        int columnsT = _cutBuffer._cells.getColumnsTotal();
        int linesT = _cutBuffer._cells.getLinesTotal();

        for (int line = 1; line <= linesT; line++) {
            if (_cells.getLinesTotal() >= range[0]){ //verifies if we're within the vertical limits of the spreadsheet.
                for (int col = 1; col <= columnsT; col++) {
                    if (_cells.getColumnsTotal() >= range[1] ){ //verifies if we're withing the horizontal limits of the spreadsheet.
                        Cell cell = _cutBuffer._cells.getCell(line + ";" + col);
                        String targetCoordinate = range[0] + ";" + range[1];

                        if (cell.getContentStr() == ""){
                            deleteContent(targetCoordinate);
                        } else {
                            insertContents(targetCoordinate, cell.getContentStr());
                        }
                    }
                    if (columnsT != 1){ //happes only when we're pasting a column on the spreadsheet.
                        range[1]++;
                    }
                }
            }
            range[0] ++;
        }
    }
}
