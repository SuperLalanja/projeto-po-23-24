package xxl.cellcontent;

public class Lstr extends Content {
    private String type = "Lstr";
    
    /**
     * @param content
     */
    public Lstr(String content){
        super(content);
    }

    @Override
    public String getType(){
        return type;
    }

    @Override
    public String toString(){
        return "|" + super.toString();
    }
}