package fileReaderPlayGround;

import java.io.BufferedInputStream;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import com.fasterxml.jackson.databind.ObjectMapper;

public class fileCurReader {

	

	public static void main(String[] args) { //匯入檔案
		File dir = new File("C:\\Users\\Tony Chi\\Desktop\\Programming\\Amazon Billing\\amazon-billing\\CUR");
		fileCurReader reader = new fileCurReader();
		reader.showDirectoryFiles(dir);
	}

	Map<String, Integer> map = new HashMap<>(); //儲存CUR結果用
	
	public void showDirectoryFiles(File dir) {
		try {
			File[] files = dir.listFiles(); //檢查為目錄或檔案
			for (File file : files) {
				if (file.isDirectory()) {   
					showDirectoryFiles(file);
				} else {                        //找到壓縮檔後開始讀取

					ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file));
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(zipInputStream));

					while ((zipInputStream.getNextEntry() != null)) {
						String contentLine = "";
						int valueCounter = 0;

						while ((contentLine = bufferedReader.readLine()) != null) { //將Key值相同的Value疊加
							String[] resultSet = contentLine.split(","); 
							String key = resultSet[8];
							valueCounter = map.containsKey(key) ? map.get(key) : 0;
							map.put(key, valueCounter + 1);
						}
						map.remove("lineItem/UsageAccountId");               //移除Header後其餘資料轉為JSON格式輸出
						ObjectMapper mapper = new ObjectMapper();
						String finalResult = mapper.writeValueAsString(map);
						String filePath = "C:\\Users\\Tony Chi\\output.json";
						FileOutputStream fos = new FileOutputStream(filePath);
						BufferedWriter output = new BufferedWriter(new OutputStreamWriter(fos));
						output.write(finalResult);
						output.close();		
					}
					zipInputStream.close();  //工作完成關閉輸入流
					bufferedReader.close();   
					
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}