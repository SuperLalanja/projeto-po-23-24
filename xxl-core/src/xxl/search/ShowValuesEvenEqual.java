package xxl.search;

public class ShowValuesEvenEqual implements PredicateContent{

    public ShowValuesEvenEqual(){}

    @Override
    public String equalsTo(String result){
        String[] parts = result.split("[\\|\\=]");
        String value; 

        if (parts.length <= 1) {
            return "";
        } 

        value = parts[1];
        char firstChar = value.charAt(0);
        if ((Character.isDigit(firstChar) || firstChar == '-') && (Integer.parseInt(value) % 2 == 0)){
            return result + "\n";
        } 
        return "";
    }
}