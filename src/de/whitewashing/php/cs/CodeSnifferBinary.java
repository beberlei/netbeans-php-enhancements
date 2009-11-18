/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.whitewashing.php.cs;

import java.io.File;
import java.io.FilePermission;

/**
 *
 * @author manu
 */
public class CodeSnifferBinary {

    public static final String BINARY = "phpcs";    //NOI18N

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
            if (!f.exists() || !checkExec(f.getAbsolutePath())) {
                continue;
            }

            this.pathName = f.getAbsoluteFile().getPath();
            break;
        }
        return this.pathName;
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
