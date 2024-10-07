package xxl.search;

public class ShowFunctionsEqual implements PredicateContent{
    private String _function;

    /**
     * @param function
     */
    public ShowFunctionsEqual(String function){
        _function = function;
    }

    /**
     * Verifies if two cells have the same function.
     */
    @Override
    public String equalsTo(String result){
        String[] parts = result.split("[\\=\\(]");
        String function; 

        if (parts.length <= 1 || _function.equals("")) {
            return "";
        }

        function = parts[1];
        if (function.contains(_function)) {
            return result + "\n";
        }
        return "";
    }
}