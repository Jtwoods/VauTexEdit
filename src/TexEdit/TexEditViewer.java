package TexEdit;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.swing.*;
import javax.swing.text.*;

public class TexEditViewer extends JFrame {

	/**
	 * text is where the text input will be displayed.
	 */
	private JTextArea text;

	/**
	 * saveAs is an action to be taken when the save as option is selected.
	 */
	private AbstractAction saveAs;

	/**
	 * save is an action to be taken when save is selected.
	 */
	private AbstractAction save;

	/**
	 * compileHelp helps compile a .tex file into a .pdf.
	 */
	private LateCompileJava compileHelp;

	/**
	 * fileChooser is a file chooser.
	 */
	private JFileChooser fileChooser;

	/**
	 * currentFile is the name of the current .tex file.
	 */
	private String currentFile;

	/**
	 * changed tracks whether the text area has been changed or not.
	 */
	private boolean changed;

	/**
	 * TexEditViewer initializes private variables and constructs the viewer.
	 */
	public TexEditViewer() {

		// Create the LateCompile object.
		compileHelp = new LateCompileJava();

		// Create the text area with the default size and font.
		text = new JTextArea(80, 50);
		text.setFont(new Font("Monospaced", Font.PLAIN, 12));

		// Create the JFileChooser.
		fileChooser = new JFileChooser(System.getProperty("user.dir"));

		// Set the title of the currentFile.
		currentFile = "Untitled";
		this.setTitle(currentFile);

		// Set changed to false.
		changed = false;

		// Create a scroll bar for moving the text up and down and side to side.
		JScrollPane scroll = new JScrollPane(text,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.setRowHeaderView(new Numbering());
		
		// Add the scroll bar to the JFrame.
		this.add(scroll, BorderLayout.CENTER);

		// Create a menu bar for file, edit, and compile.
		JMenuBar menu = new JMenuBar();
		this.setJMenuBar(menu);

		// Create and add the file, edit, and compile options.
		JMenu file = new JMenu("File");
		JMenu edit = new JMenu("Edit");
		JMenu compile = new JMenu("Compile");
		menu.add(file);
		menu.add(edit);
		menu.add(compile);

		// Create some Actions to place in the option menu.
		AbstractAction Open = new AbstractAction("Open") {
			public void actionPerformed(ActionEvent e) {
				saveOld();
				if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					readInFile(fileChooser.getSelectedFile().getAbsolutePath());
				}
				saveAs.setEnabled(true);
			}
		};

		// Create an action listener for the Save option.
		save = new AbstractAction("Save") {
			public void actionPerformed(ActionEvent e) {
				if (!currentFile.equals("Untitled"))
					saveFile(currentFile);
				else
					saveFileAs();
			}
		};

		// Create an action listener for the Save as option.
		saveAs = new AbstractAction("Save as...") {
			public void actionPerformed(ActionEvent e) {
				saveFileAs();
			}
		};

		// Create an action listener for the Quit option.
		AbstractAction Quit = new AbstractAction("Quit") {
			public void actionPerformed(ActionEvent e) {
				saveOld();
				System.exit(0);
			}
		};

		// Create an abstract action that will allow for compilation of a .tex
		// file
		// When the compile button is pressed.
		AbstractAction Compile = new AbstractAction("Compile") {
			@SuppressWarnings("unchecked")
			/**
			 * actionPerformed is the action that will be taken when the compile option is selected.
			 */
			public void actionPerformed(ActionEvent e) {

				// If there is a path to a file on record
				// perform the compilation.
				if (currentFile != null) {
					// Get the name of the file without the path.
					// Note that the '/' is platform dependent.
					String fileName = currentFile.substring(
							currentFile.lastIndexOf('/'),
							currentFile.indexOf('.'));
					// Get the path to the folder containing the current file.
					// Note that the '/' is platform dependent.
					String currentPath = currentFile.substring(0,
							currentFile.lastIndexOf('/'));
					try {
						// Call the lateCompile method to perform the
						// compilation
						// of the .tex file.
						compileHelp.lateCompile(currentPath, fileName);
					} catch (IOException e1) {
						// If this operation fails print the stack.
						e1.printStackTrace();
					}

				} else {
					saveFileAs();
				}
			}

		};

		// Create some default actions that will allow cut and paste operations.
		ActionMap m = text.getActionMap();
		Action cut = m.get(DefaultEditorKit.cutAction);
		Action copy = m.get(DefaultEditorKit.copyAction);
		Action paste = m.get(DefaultEditorKit.pasteAction);

		// Add options new, save, and save as to the File menu.
		// file.add(New);
		file.add(Open);
		file.add(save);
		file.add(saveAs);

		// Add options cut, copy, paste to the Edit menu.
		edit.add(cut);
		edit.add(copy);
		edit.add(paste);
		// Add Compile to the compile option.
		compile.add(Compile);
		// Set the default close operation.
		this.pack();
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		// Make the JFrame visible.
		this.setVisible(true);

		// Add a key listener that will track whether the
		// current text has been changed or not.
		KeyListener k1 = new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				// Update the changed variable to true.
				changed = true;
				// Enable saving.
				save.setEnabled(true);
				saveAs.setEnabled(true);
			}
		};

	}

	/**
	 * saveFileAs opens a file chooser and allows the user to select a name and
	 * location for the current file to be saved.
	 */
	private void saveFileAs() {
		if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
			saveFile(fileChooser.getSelectedFile().getAbsolutePath());
	}

	/**
	 * saveOld performs save operation on a file that has been saved already and
	 * needs to be saved again.
	 */
	private void saveOld() {
		if (changed) {
			if (JOptionPane.showConfirmDialog(this, "Would you like to save "
					+ currentFile + " ?", "Save", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
				saveFile(currentFile);
		}
	}

	/**
	 * readInFile reads the given file into the text field for editing.
	 * 
	 * @param fileName
	 *            the name of the file including path.
	 */
	private void readInFile(String fileName) {
		try {
			FileReader r = new FileReader(fileName);
			text.read(r, null);
			r.close();
			currentFile = fileName;
			setTitle(currentFile);
			changed = false;
		} catch (IOException e) {
			Toolkit.getDefaultToolkit().beep();
			JOptionPane.showMessageDialog(this,
					"Editor can't find the file called " + fileName);
		}
	}

	/**
	 * saveFile performs file saving on the current file.
	 * 
	 * @param fileName
	 *            the name of the file to be saved.
	 */
	private void saveFile(String fileName) {
		try {
			FileWriter w = new FileWriter(fileName);
			text.write(w);
			w.close();
			currentFile = fileName;
			setTitle(currentFile);
			changed = false;
			save.setEnabled(false);
		} catch (IOException e) {
		}
	}

	/**
	 * main creates a TexEditViewer.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new TexEditViewer();
	}

}
