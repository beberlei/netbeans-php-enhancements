/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.whitewashing.php.cs.command;

import de.whitewashing.php.cs.ui.options.CodeSnifferOptions;
import java.util.ArrayList;

/**
 * Simple factory/singleton that creates a unique CodeSniffer instance based
 * on the user's preferences and the underlying system settings.
 *
 * @author manu
 */
public class CodeSnifferBuilder {

    private static CodeSniffer codeSniffer = null;

    public static CodeSniffer createOrReturn() {
        if (codeSniffer == null) {
            return create();
        }
        return codeSniffer;
    }

    public static CodeSniffer create() {
        CodeSnifferOptions options = new CodeSnifferOptions();

        return (codeSniffer = new CodeSniffer(options.getShellScript(), options.getCodingStandard(), options.hasShowWarnings()));
    }
}
