/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.whitewashing.php.cs;

import javax.swing.JMenuItem;
import org.openide.cookies.EditorCookie;
import org.openide.nodes.Node;
import org.openide.loaders.DataObject;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;
import org.openide.filesystems.FileObject;

public final class CodeSnifferAction extends CookieAction {

    private CodeSnifferBinary binary = null;

    protected void performAction(Node[] activatedNodes) {
        if(activatedNodes.length != 1) {
            return;
        }

        if (this.binary.exists() == false) {
            return;
        }

        FileObject fo = getFileObject(activatedNodes[0]);

        CodeSniffer cs = new CodeSniffer(this.binary.getPath());
        cs.execute(fo);
    }

    /**
     * Copied from org.netbeans.modules.php.project.ui.actions.support.CommandUtils
     * 
     * @param node
     * @return
     */
    private FileObject getFileObject(Node node)
    {
        assert node != null;

        FileObject fileObj = node.getLookup().lookup(FileObject.class);
        if (fileObj != null && fileObj.isValid()) {
            return fileObj;
        }
        DataObject dataObj = node.getCookie(DataObject.class);
        if (dataObj == null) {
            return null;
        }
        fileObj = dataObj.getPrimaryFile();
        if (fileObj != null && fileObj.isValid()) {
            return fileObj;
        }
        return null;
    }

    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }

    public String getName() {
        return NbBundle.getMessage(CodeSnifferAction.class, "CTL_CodeSnifferAction");
    }

    protected Class[] cookieClasses() {
        return new Class[]{EditorCookie.class};
    }

    @Override
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() Javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE);

        this.binary = new CodeSnifferBinary();
    }

    @Override
    public JMenuItem getPopupPresenter()
    {
        return this.setEnabledForExistingBinary(super.getPopupPresenter());
    }

    @Override
    public JMenuItem getMenuPresenter()
    {
        return this.setEnabledForExistingBinary(super.getMenuPresenter());
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }

    protected JMenuItem setEnabledForExistingBinary(JMenuItem item)
    {
        item.setEnabled(this.binary.exists());
        return item;
    }
}

