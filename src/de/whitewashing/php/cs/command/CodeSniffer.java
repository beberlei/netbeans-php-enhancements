/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.whitewashing.php.cs.command;

import java.io.BufferedReader;
import java.util.concurrent.ExecutionException;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.api.extexecution.ExecutionService;
import org.netbeans.api.extexecution.ExternalProcessBuilder;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
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

    public void setShellScript(String shellScript) {
        this.shellScript = shellScript;
    }
    

    public CodeSniffer(String shellScript, String codingStandard, boolean showWarnings) {
        this.shellScript = shellScript;
        this.codingStandard = codingStandard;
        this.showWarnings = showWarnings;
    }

    public boolean isEnabled() {
        if(this.shellScript == null || this.shellScript.equals("")) {
            return false;
        } else {
            File shellFile = new File(this.shellScript);
            return shellFile.isFile() && shellFile.canExecute();
        }
    }
    
    public String getVersion() {
        if(!isEnabled()) {
            return "?";
        }
        
        ExternalProcessBuilder procBuilder = new ExternalProcessBuilder(this.shellScript)
                .addArgument("--version");

        try {
            ProcessExecutor executor = new ProcessExecutor();
            Reader executedProcess = executor.execute(procBuilder);
            
            // Handle PHP Exceptions
            handlePhpExceptions(executedProcess);
            
            String versionLine = this.getStringFromReader(executedProcess);
            Pattern pattern = Pattern.compile("(?:CodeSniffer.*?)(?:v\\.?(?:ersion)?\\s+)([0-9]+\\.[0-9]+(?:\\.[0-9]+)?)", Pattern.CASE_INSENSITIVE);
            Matcher m = pattern.matcher(versionLine);
            if(!m.find()) {
                return "?";
            }
            
            return m.group(1);
        } catch(CodeSnifferPhpException e) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(e.getMessage(), NotifyDescriptor.ERROR_MESSAGE));
            return "?";
        } catch(java.io.IOException e) {
            return "?";
        }
    }

    public List<String> getAvailableStandards() {
        if (this.isEnabled() == false) {
            return new ArrayList<String>();
        }
        
        try {
            ExternalProcessBuilder procBuilder = new ExternalProcessBuilder(this.shellScript)
                    .addArgument("-i");

            ProcessExecutor executor = new ProcessExecutor();
            StupidStandardsOutputParser parser = new StupidStandardsOutputParser();

            Reader executedProcess = executor.execute(procBuilder);

            // Handle PHP Exceptions
            handlePhpExceptions(executedProcess);
            
            return parser.parse(executedProcess);
        } catch(java.io.IOException e) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(e.getMessage(), NotifyDescriptor.ERROR_MESSAGE));
        }
        
        return new ArrayList<String>();
    }

    public CodeSnifferXmlLogResult execute(FileObject fo) {
        return execute(fo, false);
    }

    public CodeSnifferXmlLogResult execute(FileObject fo, boolean annotateLines) {
        final File parent = FileUtil.toFile(fo.getParent());

        if(parent == null || this.isEnabled() == false) {
            return CodeSnifferXmlLogResult.empty();
        }
        
        // Executes PHPCS
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
        Reader executedProcess = new ProcessExecutor().execute(externalProcessBuilder);

        CodeSnifferXmlLogResult rs = CodeSnifferXmlLogResult.empty();
        try {
            // Handle PHP Exceptions
            handlePhpExceptions(executedProcess);

            // Parse response
            rs = parser.parse(executedProcess);
            if(annotateLines) {
                annotateWithCodingStandardHints(fo, rs);
            }

            // Check if we have no errors at all
            if(rs.getCsErrors().isEmpty() && rs.getCsWarnings().isEmpty())
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message("Great!\nNo errors or warnings found.", NotifyDescriptor.INFORMATION_MESSAGE));
        } catch(java.io.IOException e) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(e.getMessage(), NotifyDescriptor.ERROR_MESSAGE));
        }

        // Progress bar finish
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
     * Creates a simple string from the given input reader. 
     * This method will not handle exceptions.
     *
     * @param reader The raw input stream.
     *
     * @return String
     */
    private String getStringFromReader(Reader reader)
        throws IOException
    {
        return new BufferedReader(reader).readLine();
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
            builder = builder.redirectErrorStream(true);
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
     * Reads the stream searching for fatal errors when executing PHPCS
     */
    public void handlePhpExceptions(Reader reader)
        throws CodeSnifferPhpException, IOException
    {
        BufferedReader output = new BufferedReader(reader);
        String ln;
        while((ln = output.readLine()) != null) {
            String message = "";

            // Treats fatal errors and parse errors that PHP may throw
            if(ln.indexOf("Fatal error:") >= 0 || ln.indexOf("Parse error:") >= 0) {
                if(ln.indexOf("Fatal error:") >= 0) {
                    Matcher m = Pattern.compile(
                            "Fatal error:\\s+ (.*?:[0-9]+)",
                            Pattern.CASE_INSENSITIVE
                    ).matcher(ln);
                    if(m.find())
                        message = m.group(1);
                } else if(ln.indexOf("Parse error:") >= 0) {
                    Matcher m = Pattern.compile(
                            "Parse error:\\s+(.*line [0-9]+)",
                            Pattern.CASE_INSENSITIVE
                    ).matcher(ln);
                    if(m.find())
                        message = m.group(1);
                }

                String finalMessage = "A fatal error occured, check your PHPCS installation.";
                if(!message.equals(""))
                    finalMessage += "\nError:\n" + message;

                reader.reset();
                throw new CodeSnifferPhpException(finalMessage);
            }
        }
        
        reader.reset();
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
                return CodeSniffer.this.getStringFromReader(reader);
            } catch (IOException e) {
                return this.DEFAULT_STANDARDS;
            }
        }
    }
}
