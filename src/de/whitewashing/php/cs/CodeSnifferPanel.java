/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * CodeSnifferPanel2.java
 *
 * Created on Nov 17, 2009, 1:45:25 PM
 */
package de.whitewashing.php.cs;

import java.util.Iterator;
import org.openide.util.NbPreferences;

/**
 *
 * @author petr
 */
public class CodeSnifferPanel extends javax.swing.JPanel {

    private Iterator<String> standards;

    /** Creates new form CodeSnifferPanel2 */
    public CodeSnifferPanel(Iterator<String> standards) {
        initComponents();

        this.standards = standards;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelStandard = new javax.swing.JLabel();
        inputBoxStandard = new javax.swing.JComboBox();
        checkBoxShowWarnings = new javax.swing.JCheckBox();

        labelStandard.setText(org.openide.util.NbBundle.getMessage(CodeSnifferPanel.class, "LBL_STANDARD")); // NOI18N

        inputBoxStandard.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Zend", "PEAR", "PHPCS", "Squiz", "MySource" }));

        checkBoxShowWarnings.setText(org.openide.util.NbBundle.getMessage(CodeSnifferPanel.class, "LBL_SHOW_WORNINGS")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(labelStandard)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, checkBoxShowWarnings, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, inputBoxStandard, 0, 236, Short.MAX_VALUE))
                .addContainerGap(93, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(labelStandard)
                    .add(inputBoxStandard, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(checkBoxShowWarnings)
                .addContainerGap(23, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    void load() {
        inputBoxStandard.removeAllItems();
        while (this.standards.hasNext()) {
            inputBoxStandard.addItem(this.standards.next());
        }
        inputBoxStandard.setSelectedItem(
                NbPreferences.forModule(CodeSniffer.class).get("phpcs.codingStandard", "Zend"));

        checkBoxShowWarnings.setSelected(
                NbPreferences.forModule(CodeSniffer.class).getBoolean("phpcs.showWarnings", true));
    }

    void store() {
        NbPreferences.forModule(CodeSniffer.class).put(
                "phpcs.codingStandard",
                inputBoxStandard.getSelectedItem().toString());

        NbPreferences.forModule(CodeSniffer.class).putBoolean(
                "phpcs.showWarnings",
                checkBoxShowWarnings.isSelected());

        // or:
        // SomeSystemOption.getDefault().setSomeStringProperty(someTextField.getText());

    }

    boolean valid() {
        // TODO check whether form is consistent and complete
        return true;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox checkBoxShowWarnings;
    private javax.swing.JComboBox inputBoxStandard;
    private javax.swing.JLabel labelStandard;
    // End of variables declaration//GEN-END:variables
}
