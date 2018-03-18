package com.umbrella.ubsdk.rebuild;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;


public class ReBulidApkTool {
	private static String LINE_SEPARATOR=System.getProperty("line.separator");
	private static String BASE_PATH;//当前工程根目录
	private static String WORK_PATH;//work目录
	private static String BAK_PATH;//bak目录
	private static String TEMP_PATH;//temp目录
	private static String TOOLS_PATH;//tools目录
	private static String OUT_PATH;//out目录
	
	private static String originApkName="";
	private static String unSignApkPath;
	private static String signedApkPath;
	private static File originApkFile;
	private static File tempApkFile;
	

	public static void main(String[] args) throws Exception {

//		设置log输出路径，注释即为输出到console
		LogManager.getInstance().setLogFile();
		
//		1.初始化路径
		initPath();
//		2.获取母包apk
		getOriginApk(args);
//		3.拷贝母包到work/tempApk.apk并且重命名
		copyOriginApk2WorkAndRename();
//		4.反编译到bak目录
		decodeApk2Bak( );
//		5.拷贝到temp目录
		copyBak2Temp();
//		6.修改AndroidManifest.xml文件
		modifyTheManifest();
//		7.生成未签名的apk
		generateUnsignedApk();
//		8.生成签名apk
		generateSignedApk();
//		9.对签名apk进行优化
		zipalignSignedApk();
		
		System.out.println("ReBulidApk Success!!");
	}


	/**
	 * 初始化路径
	 */
	private static void initPath() {
		System.out.println("ReBulidApk Start!!");
		System.out.println("----------------------------");
		System.out.println(LINE_SEPARATOR);
		
		System.out.println("Step one:Initialization path");
		BASE_PATH=System.getProperty("user.dir");
		TOOLS_PATH= BASE_PATH+File.separator+"tool";
		WORK_PATH = BASE_PATH+File.separator+"work";
		BAK_PATH =  WORK_PATH+File.separator+"bak";
		TEMP_PATH = WORK_PATH+File.separator+"temp";
		OUT_PATH=BASE_PATH+File.separator+"out";
		
		unSignApkPath = BASE_PATH+File.separator+"unSignApk.apk";
		signedApkPath =BASE_PATH+File.separator+"signedApk.apk";
		
//		清空work目录
		FileUtil.delete(WORK_PATH);
		
		System.out.println("Step one:Initialization path success!!");
		System.out.println("----------------------------");
		System.out.println(LINE_SEPARATOR);
	}
	
	/**
	 * 获取母包apk
	 * @param args
	 */
	private static void getOriginApk(String[] args){
		System.out.println("Step two:Get origin apk");
		if (args!=null&&args.length>0) {
			originApkFile = new File(args[0]);
			int lastPointIndex = originApkFile.getName().lastIndexOf(".");
			originApkName=originApkFile.getName().substring(0, lastPointIndex);
		}
		
		if (originApkFile==null) {
			File baseDir = new File(BASE_PATH);
			File[] fileList = baseDir.listFiles(new FileFilter() {
				@Override
				public boolean accept(File file) {
					if (file.isFile()&&file.getName().endsWith(".apk")) {
						return true;
					}
					return false;
				}
			});
			if (fileList!=null&&fileList.length>0) {
				originApkFile=fileList[0];
				int lastPointIndex = originApkFile.getName().lastIndexOf(".");
				originApkName=originApkFile.getName().substring(0, lastPointIndex);
			}
		}
		
		if (originApkFile==null) {
			throw new RuntimeException("error----->Get origin apk fail!!");
		}else{
			System.out.println("Step two:Get origin apk success!!");
			System.out.println("----------------------------");
			System.out.println(LINE_SEPARATOR);
		}
		
	}
	

	/**
	 * 拷贝母包到work目录并且重命名为tempApk.apk
	 * @throws IOException
	 */
	private static void copyOriginApk2WorkAndRename() throws IOException {
		System.out.println("Step three:Copy origin apk to work dir and rename");
		String tempApkPath=WORK_PATH+File.separator+"tempApk.apk";
		tempApkFile = new File(tempApkPath);
		if (!tempApkFile.exists()) {
			tempApkFile.getParentFile().mkdirs();
		}else{
			FileUtil.delete(tempApkPath);
		}
		tempApkFile.createNewFile();
		
		FileUtil.copyFile(originApkFile, tempApkFile);
		System.out.println("Step three:Copy origin apk to work dir and rename success!!");
		System.out.println("----------------------------");
		System.out.println(LINE_SEPARATOR);
	}
	
	/**
	 * 反编译apk到Bak目录
	 * @param apktoolVersion
	 * @param apkFile
	 * @throws Exception
	 */
	private static void decodeApk2Bak( ) throws Exception {
		System.out.println("Step four:Decompile game package to work/bak directory");

		FileUtil.delete(BAK_PATH);
		File bakFile = new File(BAK_PATH);
		bakFile.mkdirs();

		String decodeApkCompileCommand = String.format("java -jar -Xms512m -Xmx512m apktool.jar d -f -o %s %s", new String[] {BAK_PATH, tempApkFile.getAbsolutePath()});
		System.out.println("decodeApkCompileCommand:" + decodeApkCompileCommand);
		CommandUtil.exeCmd(decodeApkCompileCommand, new File(TOOLS_PATH));
		System.out.println("Step four:Decompile game package to work/bak directory success!!");
		System.out.println("----------------------------");
		System.out.println(LINE_SEPARATOR);
	}
	
	/**
	 * 拷贝Bak目录到Temp目录
	 * @throws Exception
	 */
	private static void copyBak2Temp() throws Exception {
		System.out.println("Step five:Copy work/bak directory to work/temp directory");
		FileUtil.delete(TEMP_PATH);
		File tempFile = new File(TEMP_PATH);
		tempFile.mkdirs();
		FileUtil.copyDirectiory(BAK_PATH, TEMP_PATH);
		System.out.println("Step five:Copy work/bak directory to work/temp directory success!!");
		System.out.println("----------------------------");
		System.out.println(LINE_SEPARATOR);
	}
	
	/**
	 * 修改AndroidManifest.xml清单文件
	 * @throws DocumentException
	 * @throws IOException
	 */
	private static void modifyTheManifest() throws DocumentException, IOException{
		System.out.println("Step fix:Modify the AndroidManifest.xml");
		OutputFormat outputFormat=new OutputFormat("",true,"UTF-8");
		String manifestPath=TEMP_PATH+File.separator+"AndroidManifest.xml";
		Document document = new SAXReader().read(new File(manifestPath));
		Element application = document.getRootElement().element("application");
		Attribute bannerAttribute = application.attribute("banner");
		Attribute isGameAttribute = application.attribute("isGame");
		if (bannerAttribute!=null) {
			application.remove(bannerAttribute);
		}
		if (isGameAttribute!=null) {
			application.remove(isGameAttribute);
		}
		Attribute nameAttribute = application.attribute("name");
		if (nameAttribute!=null) {
			application.remove(nameAttribute);
		}
		application.addAttribute("android:name","com.umbrella.game.ubsdk.ui.UBApplication");
//		修改主活动
		List<Element> activities = application.elements("activity");
		 String gameOldMainActivityFullName=null;
		 Element mainActivity=null;
		 Element mainActivityIntentFilter=null;
		 Element mainAction=null;
		 Element mainLauncher=null;
		 for (Element activity : activities) {
			Element intentFilter = activity.element("intent-filter");
			if (intentFilter!=null) {//找到游戏原有MainActivity
				Element action = intentFilter.element("action");
				Element category = intentFilter.element("category");
				if (action==null) continue;
				if (category==null) continue;
				boolean isMainAction = TextUtil.equals("android.intent.action.MAIN",action.attributeValue("name"));
				boolean isMainLauncher = TextUtil.equals("android.intent.category.LAUNCHER", category.attributeValue("name"));
				if (isMainAction&&isMainLauncher) {
					mainActivity=activity;
					gameOldMainActivityFullName = activity.attributeValue("name");
					mainActivityIntentFilter=intentFilter;
					mainAction=action;
					mainLauncher=category;
//								System.out.println("		mainActivity:"+mainActivity);
					System.out.println("gameOldMainActivityFullName:"+gameOldMainActivityFullName);
//								System.out.println("		mainActivityIntentFilter:"+mainActivityIntentFilter);
//								System.out.println("		mainAction:"+mainAction);
//								System.out.println("		mainLauncher:"+mainLauncher);
				}
			}

		}
		
		if (mainActivity==null||mainActivityIntentFilter==null||mainAction==null||mainLauncher==null) {
			throw new RuntimeException("error----->Game main activity configuration error!!");
		}
//		移除原MainActivity的启动节点
		if (mainActivityIntentFilter.elements().size()==2) {
			mainActivity.remove(mainActivityIntentFilter);
		}else{
			mainActivityIntentFilter.remove(mainAction);
			mainActivityIntentFilter.remove(mainLauncher);
		}
//		mainActivity.elements().
		
		application.remove(mainActivity);//移除原来的主Activity
		
//		动态创建UBGameWrapActivity节点
		Element ubGameWrapActivity=DocumentHelper.createElement("activity");
		ubGameWrapActivity.addAttribute("android:name","com.umbrella.plugin.storebridge.UmbrellaActivity");
		ubGameWrapActivity.addAttribute("android:configChanges", "keyboardHidden|orientation|screenSize");
		
		Element intentFilter = DocumentHelper.createElement("intent-filter");
		intentFilter.add(mainAction.createCopy());
		intentFilter.add(mainLauncher.createCopy());
		ubGameWrapActivity.add(intentFilter);
		
		application.add(ubGameWrapActivity);
		
//		DOM操作完毕，更新文档
		XMLWriter xmlWrite = new XMLWriter(new FileWriter(manifestPath), outputFormat);
		xmlWrite.write(document);
		xmlWrite.flush();
		xmlWrite.close();
		System.out.println("Step fix:Modify the AndroidManifest.xml success!!");
		System.out.println("----------------------------");
		System.out.println(LINE_SEPARATOR);
	}
	
	/**
	 * 生成未签名的apk
	 * @throws Exception
	 */
	private static void generateUnsignedApk( ) throws Exception {
//		回编成apk的过程
//		输出路径
		System.out.println("Step seven:Generate unsigned apk");
		
		String generateUnsignedApkCommand=String.format("java -jar %s b -r -o %s %s ", new String[]{"apktool.jar",unSignApkPath,TEMP_PATH});
		System.out.println("generateUnsignedApkCommand:"+generateUnsignedApkCommand);
		CommandUtil.exeCmd(generateUnsignedApkCommand, new File(TOOLS_PATH));
		File unSignApkFile = new File(unSignApkPath);
		if (!unSignApkFile.exists()) {
			throw new RuntimeException("error----->Generate unsigned apk fail!!");
		}else{
			System.out.println("Step seven:Generate unsigned apk success!!");
			System.out.println("----------------------------");
			System.out.println(LINE_SEPARATOR);
		}
	}
	
	/**
	 * 生成签名apk
	 * @return
	 * @throws Exception
	 */
	private static String generateSignedApk( ) throws Exception {
		System.out.println("Step eight:Generate signed apk");
//		给unsigned.apk签名
		String keystorePath=BASE_PATH+File.separator+"keystore"+File.separator+"ubsdk.keystore";
		String keystoreConfigPath=BASE_PATH+File.separator+"keystore"+File.separator+"config.xml";
		Keystore keystore = KeystoreXMLParser.parser(keystoreConfigPath);
		
//		String jarSignerPath=TOOLS_PATH+File.separator+"jarsigner"+File.separator+"jarsigner";
		String generateSignedApkCommand = String.format(
		            "jarsigner -digestalg SHA1 -sigalg SHA1withRSA -keystore %s -storepass %s -keypass %s -signedjar %s %s %s",
		            new String[] {keystorePath, keystore.getPasword(), keystore.getAliasPwd(), signedApkPath,
		            		unSignApkPath, keystore.getAlias()});
		System.out.println("generateSignedApkCommand:"+generateSignedApkCommand);
		CommandUtil.exeCmd(generateSignedApkCommand, new File(TOOLS_PATH));
		FileUtil.delete(unSignApkPath);
		
		File signedApkFile = new File(signedApkPath); 
		if (!signedApkFile.exists()) {
			throw new RuntimeException("error----->Generate signed apk fail!!");
		}else{
			System.out.println("Step eight:Generate signed apk success!!");
			System.out.println("----------------------------");
			System.out.println(LINE_SEPARATOR);
		}
		return signedApkPath;
	}

	/**
	 * 对签名apk进行优化
	 * @throws Exception
	 */
	private static void zipalignSignedApk( ) throws Exception {
		System.out.println("Step nine:Optimize signature apk");
//		对已生成的签名包进行优化
//		最终的生成渠道包路径和命令
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm",Locale.getDefault());
		Date date = new Date();
		String timestamp = simpleDateFormat.format(date);
		String finalChannelApkPath=OUT_PATH+File.separator+originApkName+"_"+timestamp+".apk";
		File finalChannelApk = new File(finalChannelApkPath);
		if (!finalChannelApk.getParentFile().exists()) {
			finalChannelApk.getParentFile().mkdirs();
		}
		if (finalChannelApk.exists()) {
			finalChannelApk.delete();
		}
		
		String zipalignPath=TOOLS_PATH+File.separator+"zipalign";
		String generateFinalChannelApkCommand=String.format("%s -v 4 %s %s",new String[]{zipalignPath,signedApkPath,finalChannelApkPath});
		System.out.println("generateFinalChannelApkCommand:"+generateFinalChannelApkCommand);
		CommandUtil.exeCmd(generateFinalChannelApkCommand,new File(TOOLS_PATH));
		FileUtil.delete(signedApkPath);
//		FileUtil.delete(WORK_PATH);
		if (!finalChannelApk.exists()) {
			throw new RuntimeException("error----->Optimize signature apk fail!!");
		}else{
			System.out.println("Step nine:Optimize signture apk success!!");
			System.out.println("----------------------------");
			System.out.println(LINE_SEPARATOR);
		}
	}
}
