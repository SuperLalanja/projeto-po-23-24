package xxl.app.search;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import xxl.Spreadsheet;
import xxl.search.PredicateContent;
import xxl.search.ShowValuesEvenEqual;

/**
 * Command for searching content values.
 */
class DoShowIntEven extends Command<Spreadsheet> {

    DoShowIntEven(Spreadsheet receiver) {
        super(Label.SEARCH_INT_EVEN, receiver);
    }

    @Override
    protected final void execute() throws CommandException{
        PredicateContent p1 = new ShowValuesEvenEqual();
        _display.popup(_receiver.showValues(p1));
    }
}