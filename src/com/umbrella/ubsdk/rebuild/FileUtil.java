package com.umbrella.ubsdk.rebuild;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class FileUtil {
	
	/**
	 * 替换文件内容
	 * @param oldWord
	 * @param newWord
	 * @param dir
	 * @throws Exception
	 */
	public static void replaceDirectioryContent(String oldWord, String newWord, File dir) throws Exception {
		if (!dir.exists()) return;
		for(File file : dir.listFiles()) {
			if(file.isDirectory()) {
				replaceDirectioryContent(oldWord, newWord, file);
			} else {
				replaceFileContent(oldWord, newWord, file);
			}
		}
	}
	
	public static void replaceFileContent(String oldWord, String newWord, File file) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		String temp = reader.readLine();
		StringBuffer buffer = new StringBuffer();
		while(temp != null) {
			temp = temp.replaceAll(oldWord, newWord);
			buffer.append(temp);
			buffer.append("\r\n");
			temp = reader.readLine();
		}
		reader.close();
		
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
//		System.out.println("buffer:"+buffer.toString());
		writer.write(buffer.toString().toCharArray());
		writer.flush();
		writer.close();
	}
	
	/**
	 * 删除文件
	 */
	public static void delete(String path) {
		File file = new File(path);
		if(!file.exists()) {
			return;
		}
		
		if(file.isDirectory()) {
			File[] files = file.listFiles();
			for(File f : files) {
				delete(f.getPath());
			}
		}
		file.delete();
	}
	
	/**
	 * 复制目录
	 * @param sourceDir
	 * @param targetDir
	 */
	public static void copyDirectiory(String sourceDir, String targetDir) throws Exception {
		File source = new File(sourceDir);
		if(!source.exists()) {
			return;
		}
		
		//新建目标目录
        (new File(targetDir)).mkdirs();
        //获取源文件夹当下的文件或目录
        File[] file = source.listFiles();
    	for(int i=0; i < file.length; i++) {
            if(file[i].isFile()){
                //源文件
                File sourceFile=file[i];
                //目标文件
                File targetFile = new File(new File(targetDir).getAbsolutePath()+File.separator+file[i].getName());
                copyFile(sourceFile, targetFile);
            }
            
            if(file[i].isDirectory()){
                //准备复制的源文件夹
                String dir1 = sourceDir + File.separator + file[i].getName();
                //准备复制的目标文件夹
                String dir2 = targetDir + File.separator + file[i].getName();
                copyDirectiory(dir1, dir2);
            }
        }
    }
	/**
	 * 复制目录lib的
	 * @param sourceDir
	 * @param targetDir
	 */
	public static void copyDirectioryForLib(String sourceDir, String targetDir) throws Exception {
		File source = new File(sourceDir);
		File target = new File(targetDir);
		if(!source.exists()) {
			return;
		}
		
		//新建目标目录
        target.mkdirs();
        //获取源文件夹当下的文件或目录
        File[] file = source.listFiles();
        File[] tfile = target.listFiles();
        int directoryCount = 0;
        int sLENGTH = file.length;
        int tLENGTH = tfile.length;
        int sFolderCount = 0;//渠道支持机型文件个数
        int tFolderCount = 0;//游戏支持机型文件个数
        
        //渠道支持机型文件夹的个数
        for(int i=0; i < sLENGTH; i++) {
        	if(file[i].isDirectory()){
        		sFolderCount++;
        	}
        }
        
      //游戏支持机型文件夹的个数
        for(int i=0; i < tLENGTH; i++) {
        	if(tfile[i].isDirectory()){
        		tFolderCount++;
        	}
        }
        ///////////////////////////////////////////////////////////////////////////////////////////////
        for(int i=0; i < sLENGTH; i++) {//先拷贝渠道中的jar文件
            if(file[i].isFile()){
                //源文件
                File sourceFile=file[i];
                //目标文件
                File targetFile = new File(new File(targetDir).getAbsolutePath()+File.separator+file[i].getName());
                System.out.println("单独进行jar文件的拷贝，》》》》》》》》》》"+file[i].getName());
                copyFile(sourceFile, targetFile);
            }
        }
        
        System.out.println("\n jar文件的拷贝完成，开始进行文件夹的拷贝..............\n");
        
  /*      
        if(tFolderCount>0){//游戏有支持机型文件夹
        	
        	if(sFolderCount>0){//渠道也存在支持机型文件夹
        		for(int i=0; i < tLENGTH; i++) {
            		if(tfile[i].isDirectory()){
                        //准备复制的源文件夹
                        String dir1 = sourceDir + File.separator + tfile[i].getName();
                        
                        //准备复制的目标文件夹
                        String dir2 = targetDir + File.separator + tfile[i].getName();
                        
                        File dirfile = new File(dir1);
                        
                        if (dirfile.exists()){
                        	System.out.println("渠道支持的机型文件夹“>>>>>存在<<<<”："+tfile[i].getName()+">>直接拷贝！>>>>>>>>>>>");
                        	copyDirectiory(dir1, dir2);
                        } else{
                        	String dir_base_bi = sourceDir + File.separator + "armeabi";//这里写死了，需要注意
                        	String dir_base_7a = sourceDir + File.separator + "armeabi-v7a";//这里写死了，需要注意
                        	File dir_bi = new File(dir_base_bi);
                        	File dir_7a = new File(dir_base_7a);
                        	if(dir_bi.exists()){
                        		copyDirectiory(dir_base_bi, dir2);
                        		System.out.println("渠道支持的机型文件夹 “>>>>>不存在<<<<”："+tfile[i].getName()+">>>>>>不存在，使用         armeabi        代替拷贝！");
                        	} else if(!dir_bi.exists() && dir_7a.exists()){
                        		copyDirectiory(dir_base_7a, dir2);
                        		System.out.println("渠道支持的机型文件夹 “>>>>>不存在<<<<”："+tfile[i].getName()+">>>>>>不存在，使用             armeabi-v7a        代替拷贝！");
                        	} else {
                        		System.out.println("渠道支持的机型文件夹极端情况下都 “>>armeabi和armeabi-v7a>>>不存在<<所以<<”：！！！！！！！尴尬了！！！！！！！！！！！！！！！！！");
                        	}
                        	
                        }
                    }
            		
            	}
        		
        	} else { //渠道   没有      支持机型文件夹
        		
        		//渠道没有支持的机型文件夹，所以不需要拷贝
        		System.out.println("-------渠道--没有支持的机型文件夹，所以--------不需要拷贝");
        	}
        	
        } else {//游戏方                 没有                       支持机型文件夹
        	System.out.println("------------游戏方-- 没有--支持机型文件夹，所以准备将 渠道到的支持机型文件夹                     全包拷贝过去到游戏中！>>>>>>>>>>>\n");
        	for(int i=0; i < sLENGTH; i++) {
                if(file[i].isDirectory()){
                    //准备复制的源文件夹
                    String dir1 = sourceDir + File.separator + file[i].getName();
                    //准备复制的目标文件夹
                    String dir2 = targetDir + File.separator + file[i].getName();
                    System.out.println(file[i].getName()+">> 文件                            正在进行全包拷贝到游戏中，>>>>>>>>>>>");
                    copyDirectiory(dir1, dir2);
                }
            }
        	
        }
    	*/
        ///////////////////////////////////////////////////////////////////////////////
    }
	
	/**
	 * 复制文件
	 * @param sourcefile
	 * @param targetFile
	 * @throws IOException
	 */
	public static void copyFile(File sourcefile,File targetFile) throws IOException{
        //新建文件输入流并对它进行缓冲
        FileInputStream input = new FileInputStream(sourcefile);
        BufferedInputStream inbuff = new BufferedInputStream(input);
        
        //新建文件输出流并对它进行缓冲
        FileOutputStream out = new FileOutputStream(targetFile);
        BufferedOutputStream outbuff = new BufferedOutputStream(out);
        
        //缓冲数组
        byte[] b=new byte[1024*5];
        int len=0;
        while((len=inbuff.read(b))!=-1){
            outbuff.write(b, 0, len);
        }
        
        //刷新此缓冲的输出流
        outbuff.flush();
        
        //关闭流
        inbuff.close();
        outbuff.close();
        out.close();
        input.close();
    }
	
}
