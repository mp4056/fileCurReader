package fileReaderPlayGround;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.zip.ZipInputStream;
import com.fasterxml.jackson.databind.ObjectMapper;

public class fileCurReader {

	public static void main(String[] args) throws ExecutionException, InterruptedException{ // 匯入檔案
		
		CompletableFuture<Void> mapCreate = CompletableFuture.runAsync(()->{

				File dir = new File("C:\\Users\\Tony Chi\\Desktop\\Programming\\Amazon Billing\\amazon-billing\\CUR");
			    fileCurReader reader = new fileCurReader();
				ConcurrentHashMap<String,AtomicInteger> resultMap = reader.getCurResultMap(dir);
		}).whenComplete((ok,e)->{
			if(e!=null) {
				e.printStackTrace();
			}
			mapToJsonConvertor(ok);
		});	    		    
	}

	
		

	ConcurrentHashMap<String, AtomicInteger> map = new ConcurrentHashMap<String,AtomicInteger>(); // 儲存CUR結果用

	public ConcurrentHashMap<String, AtomicInteger> getCurResultMap(File dir) {
		try {
			File[] files = dir.listFiles(); // 檢查為目錄或檔案
			for (File file : files) {
				if (file.isDirectory()) {
					getCurResultMap(file);
				} else { // 找到壓縮檔後開始讀取

					ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file));
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(zipInputStream));

					while ((zipInputStream.getNextEntry() != null)) {
						String contentLine = "";

						while ((contentLine = bufferedReader.readLine()) != null) { // 將Key值相同的Value疊加
							String[] resultSet = contentLine.split(",");
							String key = resultSet[8];
							map.putIfAbsent(key, new AtomicInteger(0));
							map.get(key).incrementAndGet();
						}
						map.remove("lineItem/UsageAccountId"); // 移除Header後其餘資料轉為JSON格式輸出
					}
					zipInputStream.close(); // 工作完成關閉輸入流
					bufferedReader.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	public static void mapToJsonConvertor(ConcurrentHashMap<String, AtomicInteger> resultMap) { // map轉換成JSON
		try {
			ObjectMapper mapper = new ObjectMapper();
			String finalResult = mapper.writeValueAsString(resultMap);
			String filePath = "C:\\Users\\Tony Chi\\output.json";
			FileOutputStream fos = new FileOutputStream(filePath);
			BufferedWriter output = new BufferedWriter(new OutputStreamWriter(fos));
			output.write(finalResult);
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}