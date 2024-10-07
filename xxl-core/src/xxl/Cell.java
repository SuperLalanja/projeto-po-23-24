package xxl;

import java.io.Serializable;
import xxl.cellcontent.Content;

/**
 * Class representing a cell.
 */
public class Cell implements Serializable{
    private Content _content;

    public Cell() {
        _content = null;
    }

    /**
     * Inserts the specified content into the cell.
     * 
     * @param content
     */
    public void insertContent(Content content) {
        _content = content;
    }

    /**
     * Returns the cell's content as a String.
     * 
     * @return
     */
    public String getContentStr() {
        if (_content == null) {
            return "";
        }
        return _content.getContent();
    }

    /**
     * Returns the cell's content.
     * 
     * @return
     */
    public Content getContent(){
        return _content;
    }

    @Override
    public String toString(){
        return  _content.toString();
    }
}