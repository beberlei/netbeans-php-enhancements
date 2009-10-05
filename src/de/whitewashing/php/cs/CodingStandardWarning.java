/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.whitewashing.php.cs;

/**
 *
 * @author benny
 */
public class CodingStandardWarning extends CodingStandardViolation {

    public CodingStandardWarning(String warning, int lineNum)
    {
        super(warning, lineNum);
    }

    public String getAnnotationType() {
        return "de-whitewashing-php-cs-annotation-warning";
    }
}

