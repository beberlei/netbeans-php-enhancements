package de.whitewashing.php.cs.command;

/**
 * Default exception when
 * @author Willy Barro
 */
public class CodeSnifferPhpException extends java.io.IOException {

    CodeSnifferPhpException(String finalMessage) {
        super(finalMessage);
    }
    
}