package xxl.app.edit;

import pt.tecnico.uilib.menus.Command;
import xxl.Spreadsheet;

/**
 * Show cut buffer command.
 */
class DoShowCutBuffer extends Command<Spreadsheet> {

    DoShowCutBuffer(Spreadsheet receiver) {
        super(Label.SHOW_CUT_BUFFER, receiver);
    }

    @Override
    protected final void execute() {
        _display.popup(_receiver.showCutBuffer());
    }

}
