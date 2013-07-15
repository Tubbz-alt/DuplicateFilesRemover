package com.asascience.duplicatefileremover.gui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class SarmapDuplRemover {
	
	private static final String FILE_EXT_FRM = ".frm";
	private static final String FILE_EXT_FRX = ".frx";
	private static final String FILE_EXT_CLS = ".cls";
	private static final String FILE_EXT_BAS = ".bas";
	// ASA share folder
	private static final String ASA_SHARE_FOLDER_PATH = "C:\\MapApps\\Apps\\SARMAP69\\ASAShared";
	
	// original .vbp file
	private static final String VBP_BACKUP_PATH = "C:\\MapApps\\Apps\\SARMAP69\\Sarmapv6_backup.vbp";	
	
	// modified .vbp file
	private static final String VBP_PATH = "C:\\MapApps\\Apps\\SARMAP69\\Sarmapv6.vbp";	
	
	private static final String PARENT_FOLDER = "ASAShared\\";
	
	// Sarmap path
	private static final String SARMAP_PATH = "C:\\MapApps\\Apps\\SARMAP69";
	
	/**
	 * get a list of file names from share folder (in lower cases)
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public static List<String> getFileList() throws IOException{
		File dir = new File(ASA_SHARE_FOLDER_PATH);
		if(!dir.isDirectory()){
			throw new IOException("Source path should be a directory!");
		}
		
		// get list of file with certain extension name
		File[] dataFiles = dir.listFiles(new FilenameFilter() {
	  	@Override
	    public boolean accept(File dir, String name) {
	          return ((name.toLowerCase().endsWith(FILE_EXT_FRM) ||   
	              name.toLowerCase().endsWith(FILE_EXT_FRX) ||  
	              name.toLowerCase().endsWith(FILE_EXT_CLS) ||  
	              name.toLowerCase().endsWith(FILE_EXT_BAS)));
	      }
	  });
		List<String> fileList = new ArrayList<>();
		
		for(File file: dataFiles){
			fileList.add(file.getName());
		}
		return fileList;
	}
	
	public static void deleteDuplicateFile(List<String> scFileNameList) throws IOException{
		File dir = new File(SARMAP_PATH);
		if(!dir.isDirectory()){
			throw new IOException("target Path should be a directory!");
		}
		
		// make a shallow copy of str list
		List<String> lowerStrListCopy = new ArrayList<String>(scFileNameList);
		
		// convert it to lower case
		for(int i=0; i < lowerStrListCopy.size(); i++) {
			lowerStrListCopy.set(i, scFileNameList.get(i).toLowerCase());
		}
		
		
		// get all the files from sarmap folder with some constrains
		File[] dataFiles = dir.listFiles(new FilenameFilter() {
	  	@Override
	    public boolean accept(File dir, String name) {
	          return ((name.toLowerCase().endsWith(FILE_EXT_FRM) ||   
	              name.toLowerCase().endsWith(FILE_EXT_FRX) ||  
	              name.toLowerCase().endsWith(FILE_EXT_CLS) ||  
	              name.toLowerCase().endsWith(FILE_EXT_BAS)));
	      }
	  });
		
		// will delete files when it is in the share folder
		for(File file: dataFiles){
			if(lowerStrListCopy.contains(file.getName().toLowerCase())){				
				if(file.delete()){
    			System.out.println(file.getName() + "\t is deleted!");
    		}else{
    			System.out.println("Delete operation is failed.");
    		}
			}
		}
	}
	
	public static void modifyVBPFile() throws IOException {
		// grab all the files name form asashare folder
		List<String> fileList = getFileList();
		
		// delete those files from sarmap
		deleteDuplicateFile(fileList);
		
		BufferedReader vbpBr = null;
		BufferedWriter vbpBw = null;
		// get all the deleted file names from this text file
		// because the file name is not sensitive, we have to first get the
		// subString.
		// and then we can replace it.
		try {
			String sCurrentLine2;
			vbpBr = new BufferedReader(new FileReader(VBP_BACKUP_PATH));
			vbpBw = new BufferedWriter(new FileWriter(VBP_PATH));
			while ((sCurrentLine2 = vbpBr.readLine()) != null) {

				for (String fileName : fileList) {
					if (sCurrentLine2.toLowerCase()
					    .contains(" " + fileName.toLowerCase())
					    || sCurrentLine2.toLowerCase().contains(
					        "=" + fileName.toLowerCase())) {
						int startIdx = sCurrentLine2.toLowerCase().indexOf(
						    fileName.toLowerCase());
						int length = fileName.length();
						String subStr = sCurrentLine2
						    .substring(startIdx, startIdx + length);

						sCurrentLine2 = sCurrentLine2.replace(subStr, PARENT_FOLDER
						    + fileName);
					}
				}
				vbpBw.write(sCurrentLine2 + "\n");
			}
			System.out.println(fileList.size());
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				if (vbpBr != null)
					vbpBr.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			try {
				if (vbpBw != null)
					vbpBw.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args){
		
		try {
	    modifyVBPFile();
    } catch (Exception e) {
	    e.printStackTrace();
    }
	}
}
