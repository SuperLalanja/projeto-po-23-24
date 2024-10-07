package xxl.app.edit;

import pt.tecnico.uilib.forms.Form;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import xxl.Spreadsheet;
import xxl.exceptions.UnrecognizedEntryException;

/**
 * Class for inserting data.
 */
class DoInsert extends Command<Spreadsheet> {

    DoInsert(Spreadsheet receiver) {
        super(Label.INSERT, receiver);
        addStringField("cell", Prompt.address());
        addStringField("content", Prompt.content());
    }

    @Override
    protected final void execute() throws CommandException {
        try{
            _receiver.insertContents(stringField("cell"), stringField("content"));
        } catch(UnrecognizedEntryException e){
            throw new InvalidCellRangeException(stringField("cell"));
        }
    }

}
