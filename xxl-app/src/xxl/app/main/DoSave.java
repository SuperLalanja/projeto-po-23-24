package xxl.app.main;

import pt.tecnico.uilib.forms.Form;
import pt.tecnico.uilib.menus.Command;
import xxl.Calculator;
import xxl.exceptions.MissingFileAssociationException;
import java.io.IOException;
import java.io.FileNotFoundException;

/**
 * Save to file under current name (if unnamed, query for name).
 */
class DoSave extends Command<Calculator> {

    DoSave(Calculator receiver) {
        super(Label.SAVE, receiver, xxl -> xxl.getSpreadsheet() != null);
    }

    @Override
    protected final void execute() {
        try {
			try {
				_receiver.save();
			}
			catch (MissingFileAssociationException e) {
				try{
					_receiver.saveAs(Form.requestString(Prompt.newSaveAs()));
				}
				catch(MissingFileAssociationException e1){
					e1.printStackTrace();
				}
				catch(IOException e1){
					e1.printStackTrace();
				}
			}
		} catch(FileNotFoundException e){
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		} 
	}
}