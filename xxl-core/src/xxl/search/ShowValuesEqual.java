package xxl.search;

public class ShowValuesEqual implements PredicateContent{
    private String _value;

    /**
     * @param value
     */
    public ShowValuesEqual(String value){
        _value = value;
    }

    /**
     * Verifies if two cells have the same value.
     */
    @Override
    public String equalsTo(String result){
        String[] parts = result.split("[\\|\\=]");
        String value; 

        if (parts.length <= 1) {
            return "";
        } 

        value = parts[1];
        if (value.equals(_value)){
            return result + "\n";
        } 
        return "";
    }
}