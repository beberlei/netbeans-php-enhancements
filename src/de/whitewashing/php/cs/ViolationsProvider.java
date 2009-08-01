/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.whitewashing.php.cs;

import java.util.List;
import java.util.ArrayList;
import org.netbeans.spi.tasklist.FileTaskScanner;
import org.netbeans.spi.tasklist.Task;
import org.openide.filesystems.FileObject;

/**
 *
 * @author benny
 */
public class ViolationsProvider extends FileTaskScanner {

    private CodeSnifferBinary codeSnifferBinary = null;
    private CodeSniffer cs = null;

    public ViolationsProvider()
    {
        super("PHP Coding Standards", "Check PHP Coding Standards", null);

        codeSnifferBinary = new CodeSnifferBinary();
    }

    private CodeSniffer getCodeSniffer()
    {
        if(cs == null) {
            cs = new CodeSniffer(this.codeSnifferBinary.getPath());
        }
        return cs;
    }

    public List<? extends Task> scan(FileObject fo) {
        List<Task> violations = new ArrayList<Task>();

        if(!fo.getMIMEType().equals("x-php5")) {
            return violations;
        }

        if(codeSnifferBinary.exists() == true) {
            CodeSnifferXmlLogResult rs = getCodeSniffer().execute(fo);

            for(int i = 0; i < rs.getCsErrors().size(); i++) {
                violations.add(
                    Task.create(fo, "error", rs.getCsErrors().get(i).getShortDescription(), rs.getCsErrors().get(i).getLineNum())
                );
            }
            for(int i = 0; i < rs.getCsWarnings().size(); i++) {
                violations.add(
                    Task.create(fo, "warning", rs.getCsWarnings().get(i).getShortDescription(), rs.getCsWarnings().get(i).getLineNum())
                );
            }
        }

        return violations;
    }

    public void attach(Callback arg0) {
        
    }

}
