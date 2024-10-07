package xxl.app.search;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import xxl.Spreadsheet;
import xxl.search.PredicateContent;
import xxl.search.ShowValuesEqual;

/**
 * Command for searching content values.
 */
class DoShowValues extends Command<Spreadsheet> {

    DoShowValues(Spreadsheet receiver) {
        super(Label.SEARCH_VALUES, receiver);
        addStringField("value", Prompt.searchValue());
    }

    @Override
    protected final void execute() throws CommandException{
        PredicateContent p1 = new ShowValuesEqual(stringField("value"));
        _display.popup(_receiver.showValues(p1));
    }
}