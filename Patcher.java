package dothacklink_Patcher_2023;

import java.io.File;
import java.util.ArrayList;

public class Patcher {
	public static void main(String[] argv) {

		File source = new File("I:\\Projects\\.hack_Link\\Patch_Test\\data\\");
		File patch = new File("I:\\Projects\\.hack_Link\\Patch_Test\\patch\\");
		String operation = "patch";

		if (source.isDirectory() || patch.isDirectory()) {
			patchCM(source, patch, operation);
			System.out.println("CM Files patched!");
			patchSC(source, patch, operation);
			System.out.println("SC Files patched!");
			patchNULL(source, patch, operation);
			System.out.println("NULL Files patched!");
			System.out.println("Patch complete!");
		}
	}

	public static void patchCM(File source, File patch, String operation) {
		ArrayList<String> cm_files = CCSFile.cm_file_list();
		File cm_patch = new File(String.valueOf(patch.getPath()) + "/demo/cm");
		cm_patch.mkdirs();

		for (String cm : cm_files) {
			System.out.println(cm);
			File temp_source = new File(String.valueOf(source.getPath()) + cm);
			File temp_patch = new File(String.valueOf(patch.getPath()) + cm);

			if (temp_source.exists()) {

				CM_FILE current = new CM_FILE(temp_source.getAbsolutePath());
				if (operation.equalsIgnoreCase("patch")) {
					if (current.applyPatch(temp_patch.getAbsolutePath())) {
						current.recreate();
						current.saveFile();
					}
					continue;
				}
				if (operation.equalsIgnoreCase("extract")) {

					current.createPatch(temp_patch.getAbsolutePath());
					continue;
				}
				if (operation.equalsIgnoreCase("test")) {
					String test_1 = current.testBlocks();
					String test_2 = "";
					if (current.applyPatch(temp_patch.getAbsolutePath())) {
						current.recreate();
						test_2 = current.testBlocks();
					}
					if (test_1.equals(test_2)) {
						System.out.println("Test Passed");
						continue;
					}
					System.out.println("Test Failed");
				}
			}
		}
	}

	public static void patchSC(File source, File patch, String operation) {
		ArrayList<String> sc_files = CCSFile.sc_file_list();
		File sc_patch = new File(String.valueOf(patch.getPath()) + "/demo/sc");
		sc_patch.mkdirs();

		for (String sc : sc_files) {
			System.out.println(sc);
			File temp_source = new File(String.valueOf(source.getPath()) + sc);
			File temp_patch = new File(String.valueOf(patch.getPath()) + sc);

			if (temp_source.exists()) {

				SC_FILE current = new SC_FILE(temp_source.getAbsolutePath());
				if (operation.equalsIgnoreCase("patch")) {
					if (current.applyPatch(temp_patch.getAbsolutePath())) {
						current.recreate();
						current.saveFile();
					}
					continue;
				}
				if (operation.equalsIgnoreCase("extract")) {

					if (!temp_patch.exists())
						current.createPatch(temp_patch.getAbsolutePath());
					continue;
				}
				if (operation.equalsIgnoreCase("test")) {
					String test_1 = current.testBlocks();
					String test_2 = "";
					if (current.applyPatch(temp_patch.getAbsolutePath())) {
						current.recreate();
						test_2 = current.testBlocks();
						System.out.println(test_1);
						System.out.println(test_2);
					}
					if (test_1.equals(test_2)) {
						System.out.println("Test Passed");
						continue;
					}
					System.out.println("Test Failed");
				}
			}
		}
	}

	public static void patchNULL(File source, File patch, String operation) {
		ArrayList<FileDescriptor> null_files = CCSFile.null_file_list();
		File null_patch = new File(String.valueOf(patch.getAbsolutePath()) + "/str");
		null_patch.mkdirs();

		for (FileDescriptor file : null_files) {
			System.out.println(file.Patch);
			File temp_source = new File(String.valueOf(source.getAbsolutePath()) + file.Source);
			File temp_patch = new File(String.valueOf(patch.getAbsolutePath()) + file.Patch);

			if (temp_source.exists()) {

				NULL_FILE current = new NULL_FILE(temp_source.getAbsolutePath(), file.headID, file.bodyID);
				if (operation.equalsIgnoreCase("patch")) {
					if (current.applyPatch(temp_patch.getAbsolutePath())) {
						current.recreate();
						current.saveFile();
					}
					continue;
				}
				if (operation.equalsIgnoreCase("extract")) {

					if (!temp_patch.exists())
						current.createPatch(temp_patch.getAbsolutePath());
					continue;
				}
				if (operation.equalsIgnoreCase("test")) {
					String test_1 = current.testBlocks();
					String test_2 = "";
					if (current.applyPatch(temp_patch.getAbsolutePath())) {
						current.recreate();
						test_2 = current.testBlocks();
					}
					if (test_1.equals(test_2)) {
						System.out.println("Test Passed");
						continue;
					}
					System.out.println("Test Failed");
					String[] a = test_1.split("\n");
					String[] b = test_2.split("\n");
					int max = (a.length > b.length) ? a.length : b.length;
					for (int i = 0; i < max; i++) {
						if (i >= a.length) {
							System.out.print("                        ");
						} else {
							System.out.print(a[i]);
						}
						System.out.print('\t');
						if (i >= b.length) {
							System.out.print("                        ");
						} else {
							System.out.print(b[i]);
						}
						System.out.print('\n');
					}

				}
			}
		}
	}
}