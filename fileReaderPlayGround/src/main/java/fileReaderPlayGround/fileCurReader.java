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
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import com.opencsv.CSVReader;

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
				System.out.println("files:" + file.getName());
				System.out.println("belongs to:" + file.getParent());
				
			}
		}
	}

	public static int calculateRows(File file) {
		int count = 0;
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			while((in.readLine())!=null) {
				count++;
			}
			System.out.println("count="+count);
			in.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return count -1;
	}
}