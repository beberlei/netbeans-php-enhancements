
Netbeans PHP Editor Extensions:
==============================

No Warranty whatsoever for this, it might break your Netbeans ;-)
Tested only on Netbeans 7, but, should work with 6.7+.
The module automatically scans for the "phpcs" executable inside the enviroment path.

## Features:

1. PHP Coding Standards Support for the Tasks Window.
1. Highlighting of Violations (in editor):
  * Right click on editor > __"Show Coding Standards Violations"__ or;
  * Shift + F12 hotkey.
1. Error stripe violations highlight: Every error appears as a little horizontal line at the right side of editor scrollbar.

TASK WINDOW
==============

Open up the Tasks Window from "Window - Tasks" or CTRL+6.

To enable the Todo List CS Scanning you have to right click on the Filter Icon and Select "Edit". Add a New Filter "Coding Standards" and activate only "PHP Coding Standards" as task group and confirm. Then you have to click the uppermost icon on the left of the tasks window that enables "Current File Scope".

For performance reasons the PHPCS plugin will only work in this mode, which means only the coding standard violations of the current file are shown in the task window.

CONFIGURE CODING-STANDARD
=========================

In the Advanced/Miscellaneous Tab of the Options Dialog you can choose which coding standard you want to use and where the PHPCS binary (script) is located.

TODO'S
=====

* Add project level Coding Standard Configuration

EXTENDING THIS PLUGIN
=====================

If you git clone this project you have to modify the nbproject/private/platform-private.properties file and fill it with the following two properties:

    user.properties.file=/home/benny/.netbeans/6.8/build.properties
    nbplatform.default.harness.dir=/home/benny/programs/netbeans-6.8/harness

THANK YOU NOTES
===============

We thank the following individuals for contributing code and knowledge to this project:

Petr Pisl, tprochazka, Alexandre Haguiar

CHANGELOG
===============
### Version __1.2.0__:
* __Added__: Shortcut, press Shift+F12 to analyse current file;
* __Added__: Validation to the form/panel. Validates if PHPCS is working and if there are any valid Code Standards;
* __Added__: Validation on preferences storage, avoids invalid settings to be saved;
* __Added__: Refresh button on configuration panel. This button revalidates PHPCS binary and refresh code standards list at user will;
* __Added__: Warnings/Errors are also added to the error stripe (column right to the editor scrollbar). Now one won't need to scroll all the file to find CS warnings/errors;
* __Added__: Context menu for JS and CSS files (both also supported by PHPCS tokenizer);
* __Added__: Show information dialog if no error/warnings were found;
* __Added__: If PHPCS fails with a fatal error, show an information dialog;
* __Fixed__: Exception handling if no CodeStandard is selected (NullPointerException).
