package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.Scanner;

public class TGWmodExporter {
	private static String CURRENT_DIR = ".";
	private static String MOD_DIR = CURRENT_DIR + "\\mod\\thegreatwar";
	private static String EXPORT_DIR = CURRENT_DIR + "\\export";

	public static void main(String[] args) throws IOException {
		// Delete export directory
		Path path = new File(EXPORT_DIR).toPath();
		try {
			Files.walk(path).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
		} catch (Exception e) {
		}

		// Create export directory
		createDir(EXPORT_DIR);

		// Read files to export
		FileInputStream f = null;
		try {
			f = new FileInputStream(CURRENT_DIR + "\\files_to_export.txt");
			@SuppressWarnings("resource")
			Scanner line = new Scanner(f);
			line.useDelimiter("[\n\r]");
			while (line.hasNext()) {
				String sLine = line.next();
				if ("".equals(sLine)) {
					continue;
				}
				if (sLine.endsWith("\\")) {
					copyDirectory(sLine);
				} else {
					copyFile(sLine);
				}
			}

		} catch (

		FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (f != null)
					f.close();
			} catch (IOException e) {
				throw new IllegalArgumentException(e.getMessage());
			}
		}
	}

	private static void createDir(String dir)
	{
		if (!new File(dir).exists()) {
			if (!new File(dir).mkdir()) {
				throw new RuntimeException("directory not created!");
			}
		}
	}
	
	private static void copyFile(String fileName) throws IOException {
		String[] dirs = fileName.split("\\\\");
		if (dirs.length > 1)
		{
			String dirName = "";
			for (int i = 0; i < dirs.length - 1; i++ )
			{
				dirName += dirs[i];
				createDir(EXPORT_DIR + "\\" + dirName);
				dirName += "\\";
			}			
		}
		Files.copy(new File(MOD_DIR + "\\" + fileName).toPath(), new File(EXPORT_DIR + "\\" + fileName).toPath(),
				StandardCopyOption.REPLACE_EXISTING);
	}

	private static void copyDirectory(String directoryName) throws IOException {
		File dir = new File(MOD_DIR + "\\" + directoryName);
		createDir(EXPORT_DIR + "\\" + directoryName);
		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				copyDirectory(directoryName + "\\" + file.getName() + "\\");
			} else {
				copyFile(directoryName + file.getName());
			}
		}
	}
}
