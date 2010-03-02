/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.whitewashing.php.cs.ui.options;

import de.whitewashing.php.cs.command.CodeSniffer;
import de.whitewashing.php.cs.command.CodeSnifferBuilder;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JComponent;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

public final class CodeSnifferOptionsPanelController extends OptionsPanelController {

    private CodeSnifferOptionsPanel panel;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private boolean changed;

    public void update() {
        getPanel().load();
        changed = false;
    }

    public void applyChanges() {
        getPanel().store();
        changed = false;

        // Reset currently used CodeSniffer instance
        CodeSnifferBuilder.create();
    }

    public void cancel() {
        // need not do anything special, if no changes have been persisted yet
    }

    public boolean isValid() {
        return getPanel().valid();
    }

    public boolean isChanged() {
        return changed;
    }

    public HelpCtx getHelpCtx() {
        return null; // new HelpCtx("...ID") if you have a help set
    }

    public JComponent getComponent(Lookup masterLookup) {
        return getPanel();
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }

    private CodeSnifferOptionsPanel getPanel() {
        if (panel == null) {
            CodeSniffer command = CodeSnifferBuilder.createOrReturn();
            CodeSnifferOptions options = new CodeSnifferOptions(command.getAvailableStandards());
            
            panel = new CodeSnifferOptionsPanel(options);
        }
        return panel;
    }

    void changed() {
        if (!changed) {
            changed = true;
            pcs.firePropertyChange(OptionsPanelController.PROP_CHANGED, false, true);
        }
        pcs.firePropertyChange(OptionsPanelController.PROP_VALID, null, null);
    }
}
