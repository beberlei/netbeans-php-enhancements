/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.whitewashing.php.cs;

import java.io.File;

/**
 *
 * @author manu
 */
public class CodeSnifferBinary {

    public static final String BINARY = "phpcs";    //NOI18N

    private String fileName = null;
    
    private String pathName = null;

    private boolean initialized = false;

    /**
     * List of possible cli tool extensions. Note that the order of the
     * extensions is important, otherwise the cli tool detection coould fail
     * on windows.
     */
    private String[] extensions = {".bat", ".php"};

    public CodeSnifferBinary()
    {
        this(BINARY);
    }

    public CodeSnifferBinary(String fileName)
    {
        this.fileName = fileName;
    }

    protected String getPath()
    {
        return this.findBinary();
    }

    protected boolean exists()
    {
        return (this.findBinary() != null);
    }

    private String findBinary()
    {
        if (initialized == true) {
            return this.pathName;
        }
        this.initialized = true;

        for (String path: this.getPossiblePaths()) {
            if (this.isExecutable(path + File.separator + this.fileName)) {
                return this.pathName;
            }
        }
        return this.pathName;
    }

    /**
     * Returns an array with all possible system paths where the CodeSniffer
     * binary could exist.
     *
     * @return String[]
     */
    private String[] getPossiblePaths() {
        String env = System.getenv("PATH");
        if (env == null || env.trim().equals("")) {
            return new String[0];
        }
        return env.split(File.pathSeparator);
    }

    /**
     * Tests if the CodeSniffer executable exists for various operation system.
     *
     * @param path The raw executable path without system dependent extension.
     *
     * @return boolean
     */
    private boolean isExecutable(String path) {
        for (String extension : this.extensions) {
            if (this.isExecutable(path, extension)) {
                return true;
            }
        }
        return this.isExecutable(path, "");
    }

    /**
     * Checks if the given path + extension is an existent and executable
     * CodeSniffer binary.
     *
     * @param path The raw executable path without system dependent extension.
     * @param extension A possible extension of the CodeSniffer executable.
     *
     * @return boolean
     */
    private boolean isExecutable(String path, String extension) {
        File f = new File(path + extension);
        if (f.exists() && checkExec(f.getAbsolutePath())) {
            this.pathName = f.getAbsoluteFile().getPath();
            return true;
        }
        return false;
    }

    private boolean checkExec(String path) {
        boolean value = true;
        // Check the SecurityManager
        SecurityManager sm = System.getSecurityManager();

        if (sm != null) {
            try {
                sm.checkExec(path);
            }
            catch (SecurityException se) {
                value = false;
            }
        }
        return value;
    }

}
