/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.whitewashing.php.cs.command;

import java.util.concurrent.ExecutionException;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.api.extexecution.ExecutionService;
import org.netbeans.api.extexecution.ExternalProcessBuilder;
import org.openide.cookies.LineCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.openide.loaders.DataObject;
import org.openide.text.Line;

/**
 *
 * @author benny
 */
public class CodeSniffer {

    public static final String PARAM_STANDARD = "--standard=%s";
    public static final String PARAM_REPORT = "--report=xml";

    private String shellScript;
    private String codingStandard;
    private boolean showWarnings;

    public CodeSniffer(String shellScript, String codingStandard, boolean showWarnings) {
        this.shellScript = shellScript;
        this.codingStandard = codingStandard;
        this.showWarnings = showWarnings;
    }

    public boolean isEnabled() {
        return this.shellScript !=null && new File(this.shellScript).exists();
    }

    public List<String> getAvailableStandards() {
        if (this.isEnabled() == false) {
            return new ArrayList<String>();
        }
        
        ExternalProcessBuilder procBuilder = new ExternalProcessBuilder(this.shellScript)
                .addArgument("-i");

        ProcessExecutor executor = new ProcessExecutor();
        StupidStandardsOutputParser parser = new StupidStandardsOutputParser();
        return parser.parse(executor.execute(procBuilder));
    }

    public CodeSnifferXmlLogResult execute(FileObject fo) {
        return execute(fo, false);
    }

    public CodeSnifferXmlLogResult execute(FileObject fo, boolean annotateLines) {
        final File parent = FileUtil.toFile(fo.getParent());

        if(parent == null || this.isEnabled() == false) {
            return CodeSnifferXmlLogResult.empty();
        }

        ExternalProcessBuilder externalProcessBuilder;

        if (this.showWarnings) {
            externalProcessBuilder = new ExternalProcessBuilder(this.shellScript)
                .workingDirectory(parent)
                .addArgument(String.format(PARAM_STANDARD, this.codingStandard))
                .addArgument(PARAM_REPORT)
                .addArgument(fo.getNameExt());
        } else {
            externalProcessBuilder = new ExternalProcessBuilder(this.shellScript)
                .workingDirectory(parent)
                .addArgument(String.format(PARAM_STANDARD, this.codingStandard))
                .addArgument(PARAM_REPORT)
                .addArgument("-n")
                .addArgument(fo.getNameExt());
        }
        
        CodeSnifferXmlLogParser parser = new CodeSnifferXmlLogParser();
        CodeSnifferXmlLogResult rs = parser.parse(new ProcessExecutor().execute(externalProcessBuilder));

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
            LineCookie cookie = d.getCookie(LineCookie.class);

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

    /**
     * Small utility class that is used to execute an external CodeSniffer process.
     */
    class ProcessExecutor {

        /**
         * Executes the given process and returns a reader instance with the
         * STDOUT result of the process.
         *
         * @param builder A configured process builder instance.
         *
         * @return Reader
         */
        public Reader execute(ExternalProcessBuilder builder) {
            CodeSnifferOutput output = new CodeSnifferOutput();

            ExecutionDescriptor descriptor = new ExecutionDescriptor()
                    .frontWindow(false)
                    .controllable(false)
                    .outProcessorFactory(output);

            ExecutionService service = ExecutionService.newService(builder, descriptor, "PHP Coding Standards");
            Future<Integer> task = service.run();
            try {
                task.get();
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            } catch (ExecutionException ex) {
                Exceptions.printStackTrace(ex);
            }

            return output.getReader();
        }
    }

    /**
     * Utility class that parses the textual output of the CodeSniffer -i option
     * and creates a list of available coding standards.
     */
    class StupidStandardsOutputParser {

        private final String DEFAULT_STANDARDS = "Zend, PEAR, PHPCS, Squiz and MySource";

        private List<String> parse(Reader reader) {
            String output = this.getStringFromReader(reader);
            
            List<String> standards = new ArrayList<String>();

            String[] parts = output.split(" and ");
            standards.add(parts[1].trim());

            parts = parts[0].split(",");
            for (int i = 1; i < parts.length; ++i) {
                standards.add(parts[i].trim());
            }

            parts = parts[0].split(" ");
            standards.add(parts[parts.length - 1].trim());

            return standards;
        }

        /**
         * Creates a simple string from the given input reader. This method will
         * return a default set of CodeSniffer standards when an IOException
         * occures during the parsing process.
         *
         * @param reader The raw input stream with codesniffer data.
         *
         * @return String
         */
        private String getStringFromReader(Reader reader) {
            try {
                char[] chars = new char[1024];
                reader.read(chars);
                StringBuilder sb = new StringBuilder();
                sb.append(chars);

                return sb.toString();
            } catch (IOException e) {
                return this.DEFAULT_STANDARDS;
            }
        }
    }
}
