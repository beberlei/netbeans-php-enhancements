/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.whitewashing.php.cs;

/**
 *
 * @author benny
 */
public class CodingStandardError extends CodingStandardViolation {

    public CodingStandardError(String msg, int lineNum)
    {
        super(msg, lineNum);
    }

    public String getAnnotationType() {
        return "de-whitewashing-php-cs-annotation-error";
    }
}

