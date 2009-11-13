/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.whitewashing.php.cs;

import java.util.concurrent.ExecutionException;
import java.util.prefs.BackingStoreException;
import java.io.File;
import java.util.concurrent.Future;
import java.util.prefs.Preferences;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.api.extexecution.ExecutionService;
import org.netbeans.api.extexecution.ExternalProcessBuilder;
import org.openide.cookies.LineCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.openide.util.NbPreferences;
import org.openide.loaders.DataObject;
import org.openide.text.Line;

/**
 *
 * @author benny
 */
public class CodeSniffer {

    public static final String PARAM_STANDARD = "--standard=%s";
    public static final String PARAM_REPORT = "--report=xml";
    public CodeSnifferOutput output = null;
    protected String command = null;

    public CodeSniffer(String command) {
        this.command = command;
    }

    CodeSnifferXmlLogResult execute(FileObject fo) {
        return execute(fo, false);
    }

    CodeSnifferXmlLogResult execute(FileObject fo, boolean annotateLines) {
        final File parent = FileUtil.toFile(fo.getParent());

        if(parent == null) {
            return CodeSnifferXmlLogResult.empty();
        }

        Preferences pref = NbPreferences.forModule(CodeSniffer.class);

        String codingStandard = pref.get("phpcs.codingStandard", "Zend");

        ExternalProcessBuilder externalProcessBuilder;

        if (pref.getBoolean("phpcs.showWarnings", true)) {
            externalProcessBuilder = new ExternalProcessBuilder(this.command)
                .workingDirectory(parent)
                .addArgument(String.format(PARAM_STANDARD, codingStandard))
                .addArgument(PARAM_REPORT)
                .addArgument(fo.getNameExt());
        } else {
            externalProcessBuilder = new ExternalProcessBuilder(this.command)
                .workingDirectory(parent)
                .addArgument(String.format(PARAM_STANDARD, codingStandard))
                .addArgument(PARAM_REPORT)
                .addArgument("-n")
                .addArgument(fo.getNameExt());
        }

        this.output = new CodeSnifferOutput();

        ExecutionDescriptor descriptor = new ExecutionDescriptor()
                .frontWindow(false)
                .controllable(false)
                .outProcessorFactory(this.output);

        ExecutionService service = ExecutionService.newService(externalProcessBuilder,
                descriptor, "PHP Coding Standards");


        Future<Integer> task = service.run();
        try {
            task.get();
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        } catch (ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }

        CodeSnifferXmlLogParser parser = new CodeSnifferXmlLogParser();
        CodeSnifferXmlLogResult rs = parser.parse(this.output.getReader());

        if(annotateLines) {
            annotateWithCodingStandardHints(fo, rs);
        }
        return rs;
    }

    private void annotateWithCodingStandardHints(FileObject fo, CodeSnifferXmlLogResult rs) {
        CodeSnifferFileListener l = new CodeSnifferFileListener();
        l.setLogResult(rs);
        fo.addFileChangeListener(l);

        try {
            DataObject d = DataObject.find(fo);
            LineCookie cookie = (LineCookie) d.getCookie(LineCookie.class);

            Line.Set lineSet = null;
            Line line = null;
            for (int i = 0; i < rs.getCsErrors().size(); i++) {
                lineSet = cookie.getLineSet();
                line = lineSet.getOriginal(rs.getCsErrors().get(i).getLineNum());
                rs.getCsErrors().get(i).attach(line);
            }

            for (int i = 0; i < rs.getCsWarnings().size(); i++) {
                lineSet = cookie.getLineSet();
                line = lineSet.getOriginal(rs.getCsWarnings().get(i).getLineNum());
                rs.getCsWarnings().get(i).attach(line);
            }
        } catch (DataObjectNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
