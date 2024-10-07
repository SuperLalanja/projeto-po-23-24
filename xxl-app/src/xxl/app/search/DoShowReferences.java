package xxl.app.search;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import xxl.Spreadsheet;
import xxl.search.PredicateContent;
import xxl.search.ShowReferencesEqual;

/**
 * Command for searching content values.
 */
class DoShowReferences extends Command<Spreadsheet> {

    DoShowReferences(Spreadsheet receiver) {
        super(Label.SEARCH_REFERENCES, receiver);
    }

    @Override
    protected final void execute() throws CommandException{
        PredicateContent p1 = new ShowReferencesEqual();
        _display.popup(_receiver.showValues(p1));
    }
}