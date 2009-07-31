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

    public static final String BINARY = "phpcs";

    private String fileName = null;
    
    private String pathName = null;

    private boolean initialized = false;

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

        String env = System.getenv("PATH");
        if (env == null || env.trim().equals("")) {
            return null;
        }

        String[] paths = env.split(File.pathSeparator);
        for (String path: paths) {
            File f = new File(path + File.separator + this.fileName);
            if (!f.exists() || !f.canExecute()) {
                continue;
            }

            this.pathName = f.getAbsoluteFile().getPath();
            break;
        }
        return this.pathName;
    }

    public static void main(String[] args)
    {
        CodeSnifferBinary cs = new CodeSnifferBinary();
        cs.findBinary();
    }
}
