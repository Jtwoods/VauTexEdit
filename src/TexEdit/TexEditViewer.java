package TexEdit;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;

public class TexEditViewer extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * text is where the text input will be displayed.
	 */
	private JTextArea text;

	/**
	 * numbering is the text area for numbering the lines of text.
	 */
	private JTextArea numbering;

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
	 * HEIGHT is the height of the textArea
	 */
	private final int HEIGHT = 44;

	/**
	 * WIDTH is the width of the textArea
	 */
	private final int WIDTH = 40;

	/**
	 * TexEditViewer initializes private variables and constructs the viewer.
	 */
	public TexEditViewer() {

		// Create the LateCompile object.
		compileHelp = new LateCompileJava();

		// Create the text area with the default size and font.
		text = new JTextArea(HEIGHT, WIDTH);

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

		// Create a text area to display numbers for text.
		numbering = new JTextArea();
		numbering.setEditable(false);
		numbering.setBackground(Color.white);
		numbering.setBorder(new LineBorder(Color.LIGHT_GRAY));
		numbering.setText(getNumbers());

		// Create a DocumentListener that will update the
		// line numbering when the text area changes.
		text.getDocument().addDocumentListener(new ControlListener());

		// Place the line numbering into the scroll bar.
		scroll.setRowHeaderView(numbering);

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

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				saveOld();
				if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					readInFile(fileChooser.getSelectedFile().getAbsolutePath());
				}
				saveAs.setEnabled(true);
				// Insert a new document listener.
				text.getDocument().addDocumentListener(new ControlListener());
				// Set the numbering for the opened text.
				numbering.setText(getNumbers());
			}
		};

		// Create an action listener for the Save option.
		save = new AbstractAction("Save") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				if (!currentFile.equals("Untitled"))
					saveFile(currentFile);
				else
					saveFileAs();
			}
		};

		// Create an action listener for the Save as option.
		saveAs = new AbstractAction("Save as...") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				saveFileAs();
			}
		};

		// Create an abstract action that will allow for compilation of a .tex
		// file
		// When the compile button is pressed.
		AbstractAction Compile = new AbstractAction("Compile") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			/**
			 * actionPerformed is the action that will be taken when the compile
			 * option is selected.
			 */
			public void actionPerformed(ActionEvent e) {

				// If there is a path to a file on record
				// perform the compilation.
				if (currentFile != null) {
					try {
						// Call the lateCompile method to perform the
						// compilation
						// of the .tex file.
					String path = fileChooser
							.getCurrentDirectory().getPath();
					
					String name = fileChooser
							.getSelectedFile().getName();
					name = name.substring(0, name.lastIndexOf('.'));

						compileHelp.lateCompile(path, name);
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

		// Add a key listener that will track whether the
		// current text has been changed or not.
		KeyListener keys = new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				// Update the changed variable to true.
				changed = true;
				// Enable saving.
				save.setEnabled(true);
				saveAs.setEnabled(true);

			}
		};

		// Add the key listener.
		text.addKeyListener(keys);

		// Add Compile to the compile option.
		compile.add(Compile);
		// Set the default close operation.
		this.pack();
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		// Make the JFrame visible.
		this.setVisible(true);

	}

	/**
	 * getNumbers returns a string that contains a line numbering for the number
	 * of lines in the text.
	 * 
	 * @return the line numbering
	 */
	public String getNumbers() {
		// Build a string containing numbering information for the text.
		StringBuffer toReturn = new StringBuffer();
		// Create an int that represents the number of lines in the document.
		int lines = text.getLineCount() + 1;

		for (int i = 1; i < lines; i++) {
			// Add a number for each line of text.
			if (i < lines) {
				toReturn.append(i + " \n");
			} else {
				toReturn.append("  \n");
			}
		}

		// Return the string produced.
		return toReturn.toString();
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
	 * NumberingListener implements DocumentListener for the text fields
	 * document to allow the numbering field to be updated when the document is
	 * changed.
	 * 
	 * @author James Woods
	 * 
	 */
	public class ControlListener implements DocumentListener {

		/**
		 * changeUpdate sets the numbering for the text field when the document
		 * is changed.
		 */
		public void changedUpdate(DocumentEvent arg0) {
			numbering.setText(getNumbers());
		}

		/**
		 * insertUpdate sets the numbering for the text field when an insert is
		 * made to the document.
		 */
		public void insertUpdate(DocumentEvent arg0) {
			numbering.setText(getNumbers());
		}

		/**
		 * removeUpdate sets the numbering for the text field when a remove is
		 * made to th document.
		 */
		public void removeUpdate(DocumentEvent arg0) {
			numbering.setText(getNumbers());
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
