package xxl.search;

import java.util.Comparator;

/**
 * Class that orders cells and their content in showFunctions.
 */
public class ResultComparator implements Comparator<String> {

    @Override
    public int compare(String result1, String result2) {
        //Divides the Strings to obtain the relevant information.
        String[] parts1 = result1.split("[|\\=\\(\\),]");
        String[] parts2 = result2.split("[|\\=\\(\\),]");

        //Obtains the functions' names.
        String functionName1 = parts1[2];
        String functionName2 = parts2[2];

        //Obtains coordinates.
        String[] coordinates1 = parts1[0].split(";");
        String[] coordinates2 = parts2[0].split(";");

        int line1 = Integer.parseInt(coordinates1[0]);
        int line2 = Integer.parseInt(coordinates2[0]);
        int column1 = Integer.parseInt(coordinates1[1]);
        int column2 = Integer.parseInt(coordinates2[1]);
        
        //First, compares the functions' names and orders them alphabetically.
        int compareFunctionName = functionName1.compareTo(functionName2);
        if (compareFunctionName != 0) {
            return compareFunctionName;
        }

        //Then, compares the lines and orders them in an ascending order
        if (line1 != line2) {
            return line1 - line2;
        }

        //Finally, compares the columns and orders them in an ascending order.
        return column1 - column2;
    }
}

