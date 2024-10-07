package xxl.app.search;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import xxl.Spreadsheet;
import xxl.search.PredicateContent;
import xxl.search.ShowFunctionsEqual;

/**
 * Command for searching function names.
 */
class DoShowFunctions extends Command<Spreadsheet> {

    DoShowFunctions(Spreadsheet receiver) {
        super(Label.SEARCH_FUNCTIONS, receiver);
        addStringField("value", Prompt.searchFunction());
    }

    @Override
    protected final void execute() throws CommandException{
        PredicateContent p1 = new ShowFunctionsEqual(stringField("value"));
        _display.popup(_receiver.showFunctions(p1));
    }
}
