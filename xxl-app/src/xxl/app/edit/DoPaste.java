package xxl.app.edit;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import xxl.Spreadsheet;
import xxl.exceptions.UnrecognizedEntryException;

/**
 * Paste command.
 */
class DoPaste extends Command<Spreadsheet> {

    DoPaste(Spreadsheet receiver) {
        super(Label.PASTE, receiver);
        addStringField("rangeSpecification", Prompt.address());
    }

    @Override
    protected final void execute() throws CommandException {
        try{
            _receiver.delete(stringField("rangeSpecification"));
            _receiver.paste(stringField("rangeSpecification"));
        } catch (UnrecognizedEntryException e){
            throw new InvalidCellRangeException(stringField("rangeSpecification"));
        }
    }
}
