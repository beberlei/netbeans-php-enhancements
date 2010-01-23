/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.whitewashing.php.cs.command;

import de.whitewashing.php.cs.CodingStandardError;
import de.whitewashing.php.cs.CodingStandardWarning;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;

/**
 *
 * @author benny
 */
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileRenameEvent;

/**
 *
 * @author benny
 */
public class CodeSnifferFileListener implements FileChangeListener
{
    private CodeSnifferXmlLogResult rs = null;

    void setLogResult(CodeSnifferXmlLogResult rs)
    {
        this.rs = rs;
    }

    public void fileFolderCreated(FileEvent fileEvent) {
        
    }

    public void fileDataCreated(FileEvent fileEvent) {
        
    }

    public void fileChanged(FileEvent fileEvent) {
        for(CodingStandardError e: rs.getCsErrors()) {
            e.detach();
        }
        for(CodingStandardWarning w: rs.getCsWarnings()) {
            w.detach();
        }
        fileEvent.getFile().removeFileChangeListener(this);
    }

    public void fileDeleted(FileEvent fileEvent) {
        
    }

    public void fileRenamed(FileRenameEvent fileEvent) {
        
    }

    public void fileAttributeChanged(FileAttributeEvent fileEvent) {
        
    }

}
