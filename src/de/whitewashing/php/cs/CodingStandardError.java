/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.whitewashing.php.cs;

import org.openide.text.Annotation;

/**
 *
 * @author benny
 */
public class CodingStandardError extends Annotation {

    private String error = null;
    private int lineNum = 0;

    public CodingStandardError(String error, int lineNum)
    {
        this.error = error;
        this.lineNum = lineNum;
    }

    public CodingStandardError(String error)
    {
        this.error = error;
    }

    public String getAnnotationType() {
        return "de-whitewashing-php-cs-annotation-error";
    }

    public String getShortDescription() {
        return error;
    }

    public int getLineNum() {
        return lineNum;
    }
}

