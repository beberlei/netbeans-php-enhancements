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
public class CodingStandardWarning extends Annotation {

    private String warning = null;
    private int lineNum = 0;

    public CodingStandardWarning(String warning, int lineNum)
    {
        this.warning = warning;
        this.lineNum = lineNum;
    }

    public CodingStandardWarning(String warning)
    {
        this.warning = warning;
    }

    public String getAnnotationType() {
        return "de-whitewashing-php-cs-annotation-warning";
    }

    public String getShortDescription() {
        return warning;
    }

    public int getLineNum() {
        return lineNum;
    }
}

