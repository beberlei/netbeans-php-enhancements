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
abstract public class CodingStandardViolation extends Annotation {

    private String violationMessage = null;
    private int lineNum = 0;

    public CodingStandardViolation(String msg, int lineNum)
    {
        this.violationMessage = msg;
        this.lineNum = lineNum;
    }
    public int getLineNum() {
        return lineNum;
    }

    public String getShortDescription() {
        return violationMessage;
    }

}
