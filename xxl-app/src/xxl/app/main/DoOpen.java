package xxl.app.main;

import xxl.exceptions.UnavailableFileException;

import pt.tecnico.uilib.forms.Form;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import xxl.Calculator;

/**
 * Open existing file.
 */
class DoOpen extends Command<Calculator> {

    DoOpen(Calculator receiver) {
        super(Label.OPEN, receiver);
    }

    @Override
    protected final void execute() throws CommandException {
        if(_receiver.getSpreadsheet() != null && _receiver.getSpreadsheet().isThereChange() && Form.confirm(Prompt.saveBeforeExit())){
            DoSave save = new DoSave(_receiver);
            save.execute();
        }
        try {
            _receiver.load(Form.requestString(Prompt.openFile()));

        } catch (UnavailableFileException e) {
            throw new FileOpenFailedException(e);
        }
    }
}
