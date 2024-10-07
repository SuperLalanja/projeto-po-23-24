package xxl.cellcontent;

public class Lint extends Content {
    private String type = "Lint";
    
    /**
     * @param content
     */
    public Lint(String content){
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