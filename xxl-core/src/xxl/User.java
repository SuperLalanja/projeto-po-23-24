package xxl;

import java.util.List;
import java.util.ArrayList;

/**
 * Class representing a user.
 */
public class User {
    private String _name;
    private List<Spreadsheet> _spreadsheet = new ArrayList<>();

    /**
     * @param name
     */
    public User (String name){
        _name = name;
    }
}
