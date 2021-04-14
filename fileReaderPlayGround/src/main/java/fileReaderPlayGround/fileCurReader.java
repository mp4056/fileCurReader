package fileReaderPlayGround;

import java.io.BufferedInputStream;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

	public static void main(String[] args) {
		File dir = new File("C:\\Users\\Tony Chi\\Desktop\\Programming\\Amazon Billing\\amazon-billing\\CUR");
		showDirectoryFiles(dir);
	}

	public static void showDirectoryFiles(File dir) {
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				showDirectoryFiles(file);
			} else {
				calculateRows(file);
			}

		}
	}

	public static void calculateRows(File file) {
	
		try {
			ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file));
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(zipInputStream));
			while ((zipInputStream.getNextEntry() != null)) {				
				String line = "";
				int usageAccountId = 0;
				while ((line = bufferedReader.readLine()) != null) {
					String[] resultSet = line.split(",");
					Map<String, Integer> map = new HashMap<>();
					usageAccountId = map.containsKey(resultSet[8]) ? map.get(resultSet[8]) : 0;
					map.remove("lineItem/UsageAccountId");
					map.put(resultSet[8], usageAccountId + 1);
					valueAdder(map);
					ObjectMapper objectMapper = new ObjectMapper();
					String finalResult = objectMapper.writeValueAsString(map);
					
				}		
			}	
			zipInputStream.close();
			bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	public static Map<String,Integer> valueAdder(Map<String,Integer> map){
		
		Set<Map.Entry<String, Integer>>entrySet = map.entrySet();
		Iterator<Map.Entry<String, Integer>> it = entrySet.iterator();
		Map <String,Integer> resultSet = new HashMap<>();	
		while(it.hasNext()) {	
			Map.Entry<String, Integer> me = it.next();	
			String key = me.getKey();
			int value = me.getValue();
			if(resultSet.containsKey(key)) {
				//resultSet現在是空值，但Key是有東西的
				resultSet.put(key, resultSet.get(key)+value);
			}else {
				resultSet.put(key, value);				
			}
			System.out.println("resultSet="+resultSet);
		}
		return map;
	}
	
	
}