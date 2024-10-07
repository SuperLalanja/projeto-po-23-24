package xxl.search;

public class ShowReferencesEqual implements PredicateContent{

    public ShowReferencesEqual(){}

    @Override
    public String equalsTo(String result){
        String[] parts = result.split("[\\|\\=]");
        String value; 

        if (parts.length <= 2) {
            return "";
        } 

        value = parts[2];
        if (value.contains(";") && Character.isDigit(value.charAt(value.length() - 1))){
            return result + "\n";
        } 
        return "";
    }
}