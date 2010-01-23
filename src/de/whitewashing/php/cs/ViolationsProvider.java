/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.whitewashing.php.cs;

import java.util.List;
import java.util.ArrayList;
import org.netbeans.spi.tasklist.PushTaskScanner;
import org.netbeans.spi.tasklist.Task;
import org.netbeans.spi.tasklist.TaskScanningScope;
import org.openide.filesystems.FileObject;

/**
 *
 * @author benny
 */
public class ViolationsProvider extends PushTaskScanner {

    public ViolationsProvider()
    {
        super("PHP Coding Standards", "Check PHP Coding Standards", null);
    }

    public List<? extends Task> scan(FileObject fo) {
        List<Task> violations = new ArrayList<Task>();

        if(!fo.hasExt("php") && !fo.hasExt("php5") && !fo.hasExt("phtml")) {
            return violations;
        }

        CodeSnifferXmlLogResult rs = CodeSnifferBuilder.create().execute(fo);

        for(int i = 0; i < rs.getCsErrors().size(); i++) {
            violations.add(
                Task.create(fo, "error", rs.getCsErrors().get(i).getShortDescription(), rs.getCsErrors().get(i).getLineNum()+1)
            );
        }
        for(int i = 0; i < rs.getCsWarnings().size(); i++) {
            violations.add(
                Task.create(fo, "warning", rs.getCsWarnings().get(i).getShortDescription(), rs.getCsWarnings().get(i).getLineNum()+1)
            );
        }

        return violations;
    }

    public void setScope(TaskScanningScope scope, Callback callback) {
        if (scope == null || callback == null) {
            return;
        }

        int i = 0;
        for (FileObject file : scope.getLookup().lookupAll(FileObject.class)) {
            if(i >= 1) {
                return;
            }

            callback.setTasks(file, scan(file));
            i++;
        }
    }
}
