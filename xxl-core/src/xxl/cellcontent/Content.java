package xxl.cellcontent;

import java.io.Serial;
import java.io.Serializable;

/**
 * Class representing a cell's content.
 */
public abstract class Content implements Serializable{
    
    @Serial
    private static final long serialVersionUID = 202308312359L;

    private String _content;

    /**
     * @param content
     */
    public Content(String content){
        _content = content;
    }

    /**
     * Obtains the content's type.
     * 
     * @return
     */
    public abstract String getType();

    /**
     * Gets the stored content.
     * 
     * @return
     */
    public String getContent(){
        return _content;
    }

    @Override
    public String toString(){
        return _content;
    }
}