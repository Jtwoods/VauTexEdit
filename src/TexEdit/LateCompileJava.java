package TexEdit;

import java.io.IOException;

/**
 * LateCompile provides a system dependent interface that compiles a 
 * .tex file through the use of dependent programs: latex, dvips, and pstopdf.
 * The current version works on mac osx with the previously mentioned programs 
 * installed in the path: /usr/local/texlive/2013/bin/universal-darwin/.
 *
 * 
 * @author James Woods
 *
 */
public class LateCompileJava {
	
	/**
	 * extensions is an array of file extensions that will be used to construct 
	 * system commands to be performed on files with the given file types.
	 */
	private String[] extensions = {".tex", ".dvi", ".ps", ".pdf"};
	
	/**
	 * commands is an array of commands with path extensions to the locations of the programs
	 * being called. Each performs a specific operation in order to produce a pdf from a tex file.
	 */
	private String[] commands = {"/usr/local/texlive/2013/bin/universal-darwin/latex --output-directory=",
			"/usr/local/texlive/2013/bin/universal-darwin/dvips ",
			"pstopdf "};
	
	/**
	 * TO_DO is the number of operations that must be performed to produce a pdf from the tex file.
	 */
	private static final int TO_DO = 3;


	/**
	 * lateCompile performs the compilation of a .tex file to .dvi, then .dvi to .ps, and from .ps to .pdf
	 * then opens the produced .pdf file for viewing.
	 *  
	 * @param path is the path to the .tex file to be compiled.
	 * @param file is the name of the .tex file without the file extension.
	 * @throws IOException is thrown when the .exec command fails. 
	 */
	void lateCompile(String path, String file) throws IOException {
	
		//Create a string to contain the current command to be performed.
		String command = "";
		
		//Perform three operations.
		for(int i = 0; i < TO_DO; i++) {
			
			//Let the current command begin with the first in the array of commands.
		    command = commands[i];
			
		    //If this is the first command being performed then there is special syntax required to 
		    //perform the latex operation and produce a file in the given path.
			if(i == 0) {
				//Add the path to the folder where the output will be forwarded and the path to the .tex file.
				command += path + " " + path + file + extensions[i];
			}
			//Otherwise the syntax should be normal.
			else {
				//Add the path to the file with extension.
				command += path + file + extensions[i];
				
				//If this is the second operation add the "-o" option to the 
				//command. This forwards the output to the path given later.
				if(i == 1) {
					command += " -o ";
				}
				//Otherwise just add a space.
				else {
					command += " ";
				}
				//Add the path, filename, and extension of the output file.
				command += path + file + extensions[i + 1];
					
			}
				//Create a process that will run the given program with the desired options
				//in the runtime environment.
				Process todo = Runtime.getRuntime().exec(command);
				//Wait for the operation to finish before moving on.
				try {
					todo.waitFor();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				//Destroy the process that was created.
				todo.destroy();
		
		}
		//Build the command to open the pdf produced by the compilation.
		command = "open " + path + file + extensions[3];
		//Use the runtime environment to open the pdf. 
		Runtime.getRuntime().exec(command);
	}
}
