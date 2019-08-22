package Utils;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
import org.testng.Reporter;

/**
 * Utils class will have some common utility methods required
 * 
 * @author Cognizant
 *
 */

public abstract class WebUtils {

	public static String twoStringsWithNewLine(String one, String two) {

		String separator = System.getProperty("line.separator");
		StringBuilder lines = new StringBuilder(one);
		lines.append(separator);
		lines.append(two);
		return lines.toString();

	}

	/**
	 * Windows Only method
	 * 
	 * @param serviceName
	 * @return true if process status is running, false otherwise
	 */

	/**
	 * Kills the specified process (Windows Only method)
	 * 
	 * @param serviceName   od for capturing video
	 * @param videofilename
	 * @param recorder
	 */


	public static void createFolder(String strDirectoy) {
		boolean success = (new File(strDirectoy)).mkdirs();
		if (success) {
		}
	}

	/**
	 * Method to find the relative path from 2 absolute paths
	 * 
	 * @param absPath
	 * @param basePath
	 * @return relative path to the file/folder. If an exception occurs absPath will
	 *         be returned
	 */
	public static String getRelativePath(String absPath, String basePath) {
		try {
			Path pathAbsolute = Paths.get(absPath);
			Path pathBase = Paths.get(basePath);
			Path pathRelative = pathBase.relativize(pathAbsolute);
			return pathRelative.toString();
		} catch (Exception e) {
			return absPath;
		}
	}

	public static File lastFileModified(String dir) {
		File fl = new File(dir);
		File[] files = fl.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.isFile();
			}
		});
		long lastMod = Long.MIN_VALUE;
		File choice = null;
		for (File file : files) {
			if (file.lastModified() > lastMod) {
				choice = file;
				lastMod = file.lastModified();
			}
		}
		return choice;
	}

	public static boolean renameFile(File oldFile, File newFile) {

		if (oldFile.renameTo(newFile)) {
			return true;
		} else {
			return false;
		}

	}

	public static String generateid1() {

		char[] chars = "012345".toCharArray();
		Random rnd = new Random();
		StringBuilder sb = new StringBuilder((100000 + rnd.nextInt(900000)));
		for (int i = 0; i < 10; i++)
			sb.append(chars[rnd.nextInt(chars.length)]);

		return sb.toString();

	}

	public static String generateid2() {

		char[] chars = "0123".toCharArray();
		Random rnd = new Random();
		StringBuilder sb = new StringBuilder((1000 + rnd.nextInt(9000)));
		for (int i = 0; i < 5; i++)
			sb.append(chars[rnd.nextInt(chars.length)]);

		return sb.toString();

	}

	/** Randon textgen() **/

	public static String TextrandomGen() {

		String RandomText = RandomStringUtils.randomAlphanumeric(5).toLowerCase();
		return RandomText;
	}

	public static String generateid() {
		return Long.toString(System.currentTimeMillis());
	}

}
