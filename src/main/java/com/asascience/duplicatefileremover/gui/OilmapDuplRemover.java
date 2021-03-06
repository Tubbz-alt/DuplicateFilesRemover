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


public class OilmapDuplRemover {
	
	private static final String FILE_EXT_FRM = ".frm";
	private static final String FILE_EXT_FRX = ".frx";
	private static final String FILE_EXT_CLS = ".cls";
	private static final String FILE_EXT_BAS = ".bas";
	
	private static final String PARENT_FOLDER = "ASAShared\\";
	
	public static List<String> getFileListLCase(String path) throws Exception{
		File dir = new File(path);
		if(!dir.isDirectory()){
			throw new Exception("Source path should be a directory!");
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
			fileList.add(file.getName().toLowerCase());
		}
		return fileList;
	}
	
	public static void deleteDuplicateFile(String targetPath, List<String> scFileNameList) throws Exception{
		File dir = new File(targetPath);
		if(!dir.isDirectory()){
			throw new Exception("target Path should be a directory!");
		}
		File[] dataFiles = dir.listFiles(new FilenameFilter() {
	  	@Override
	    public boolean accept(File dir, String name) {
	          return ((name.toLowerCase().endsWith(FILE_EXT_FRM) ||   
	              name.toLowerCase().endsWith(FILE_EXT_FRX) ||  
	              name.toLowerCase().endsWith(FILE_EXT_CLS) ||  
	              name.toLowerCase().endsWith(FILE_EXT_BAS)));
	      }
	  });
		
		for(File file: dataFiles){
			if(scFileNameList.contains(file.getName().toLowerCase())){
				if(file.delete()){
    			System.out.println(file.getName() + "\t is deleted!");
    		}else{
    			System.out.println("Delete operation is failed.");
    		}
			}
		}
	}
	
	public static void modifyVBPFile(String vbpPath, String changeList) throws IOException{
		
		BufferedReader listBr = null;
		List<String> fileNameList = new ArrayList<>();
		
		// get all the deleted file names from this text file
		try{
			String sCurrentLine;
			listBr = new BufferedReader(new FileReader(changeList));
			while((sCurrentLine = listBr.readLine()) != null){
				fileNameList.add(sCurrentLine.trim());
				
			}
			System.out.println(fileNameList.size());
		}catch (IOException e){
			throw e;
		} finally {
			try {
				if (listBr != null)
					listBr.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		BufferedReader vbpBr = null;
		BufferedWriter vbpBw = null;
		// get all the deleted file names from this text file
			try{
				String sCurrentLine2;
				vbpBr = new BufferedReader(new FileReader(vbpPath));
				vbpBw = new BufferedWriter(new FileWriter("C:\\MapApps\\Apps\\CHEMMAP69\\Chemmapv6.vbpp"));
				while((sCurrentLine2 = vbpBr.readLine()) != null){
					
					for(String fileName: fileNameList){
						if(sCurrentLine2.contains(fileName)){
							sCurrentLine2 = sCurrentLine2.replace(fileName, PARENT_FOLDER + fileName);
						}
					}
					vbpBw.write(sCurrentLine2 + "\n");
				}
				System.out.println(fileNameList.size());
			}catch (IOException e){
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
	
	public static void modifyVBPFileByFolder(String vbpPath,
	    String shareFolderPath) throws IOException {

		File dir = new File(shareFolderPath);
		if (!dir.isDirectory()) {
			throw new IOException("Share folder path should be a directory!");
		}
		File[] dataFiles = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return ((name.toLowerCase().endsWith(".frm")
				    || name.toLowerCase().endsWith(".frx")
				    || name.toLowerCase().endsWith(".cls") || name.toLowerCase()
				    .endsWith(".bas")));
			}
		});
		List<String> fileList = new ArrayList<>();

		for (File file : dataFiles) {
			fileList.add(file.getName());
		}
		
		BufferedReader vbpBr = null;
		BufferedWriter vbpBw = null;
		// get all the deleted file names from this text file
		// because the file name is not sensitive, we have to first get the subString.
		// and then we can replace it.
			try{
				String sCurrentLine2;
				vbpBr = new BufferedReader(new FileReader(vbpPath));
				vbpBw = new BufferedWriter(new FileWriter("C:\\MapApps\\Apps\\CHEMMAP69\\Chemmapv6.vbpp"));
				while((sCurrentLine2 = vbpBr.readLine()) != null){
					
					for(String fileName: fileList){
						if(sCurrentLine2.toLowerCase().contains(" "+ fileName.toLowerCase())
								|| sCurrentLine2.toLowerCase().contains("="+ fileName.toLowerCase())){
							int startIdx = sCurrentLine2.toLowerCase().indexOf(fileName.toLowerCase());
							int length = fileName.length();
							String subStr = sCurrentLine2.substring(startIdx, startIdx + length);
							
							sCurrentLine2 = sCurrentLine2.replace(subStr, PARENT_FOLDER + fileName);
						}
					}
					vbpBw.write(sCurrentLine2 + "\n");
				}
				System.out.println(fileList.size());
			}catch (IOException e){
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
		String path = "C:\\projects\\Oilmap\\ASAShared";
		String path2 = "C:\\MapApps\\Apps\\CHEMMAP69";
		String txtPath = "C:\\Users\\XZheng\\Desktop\\chemMapDeletedFileList.txt";
		String vbpPath = "C:\\MapApps\\Apps\\CHEMMAP69\\Chemmapv6.vbp";	
				
		
		try {
	    //List<String> fileList = getFileListLCase(path);
	    //deleteDuplicateFile(path2, fileList);
	    //modifyVBPFile(vbpPath, txtPath);
	    modifyVBPFileByFolder(vbpPath, path);
    } catch (Exception e) {
	    e.printStackTrace();
    }
	}
}
