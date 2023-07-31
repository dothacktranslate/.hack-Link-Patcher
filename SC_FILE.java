package dothacklink_Patcher_2023;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SC_FILE extends FILE {
	Byte[] Header = new Byte[12];
	String Count;
	ArrayList<String> Titles = new ArrayList<String>();
	ArrayList<String> Values = new ArrayList<String>();

	SC_FILE(String filename) {
		super(filename, -859036672, 2);

		this.Header = Arrays.<Byte>copyOfRange(this.active.<Byte>toArray(this.Header), 0, 12);

		Byte[] temp = new Byte[this.active.size() - 12];
		temp = Arrays.<Byte>copyOfRange(this.active.<Byte>toArray(this.Header), 12, this.active.size());

		String Value = Decode(temp);
		Value = Value.replaceAll("\r\n", "\n");

		Pattern p_count = Pattern.compile("([0-9]+)#");
		Matcher m_count = p_count.matcher(Value);

		if (m_count.find()) {
			this.Count = m_count.group(1);
		}

		Pattern p_str = Pattern.compile("(#(TXT_)?[0-9]+#\\-?[0-9]+#\\-?[0-9]+#)([^#]+)");
		Matcher m_str = p_str.matcher(Value);

		while (m_str.find()) {
			this.Titles.add(m_str.group(1));
			this.Values.add(m_str.group(3));
		}
	}

	public boolean createPatch(String name) {
		File location = new File(name);
		try {
			FileOutputStream out = new FileOutputStream(location);

			for (String value : this.Values) {
				out.write(value.getBytes("UTF8"));
				out.write(Break.getBytes("UTF8"));
			}

			out.close();
			return true;
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		return false;
	}

	public boolean applyPatch(String name) {
		FileInputStream stream;
		MappedByteBuffer bb;
		File location = new File(name);

		try {
			stream = new FileInputStream(location);
		} catch (FileNotFoundException e) {

			System.out.printf("Patch %s does not exist\n", new Object[] { name });
			return false;
		}

		FileChannel fc = stream.getChannel();

		try {
			bb = fc.map(FileChannel.MapMode.READ_ONLY, 0L, fc.size());
		} catch (IOException e) {
			try {
				stream.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
			return false;
		}

		String fulltext = Charset.forName("UTF8").decode(bb).toString();
		
		try {
			stream.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		String[] tokenizedInputs = fulltext.split(Break);

		int minlength = (tokenizedInputs.length < this.Values.size()) ? tokenizedInputs.length : this.Values.size();

		for (int i = 0; i < minlength; i++) {
			if (this.Values.get(i) != "" && tokenizedInputs[i] != "") {
				this.Values.set(i, tokenizedInputs[i]);
			} else {
				if (this.Values.get(i) == "" && tokenizedInputs[i] != "") {
					System.out.printf("Too many String in patch %s\n", new Object[] { name });
					return false;
				}
				if (this.Values.get(i) != "" && tokenizedInputs[i] == "") {
					System.out.printf("Too few String in patch %s\n", new Object[] { name });
					return false;
				}
				break;
			}
		}
		System.out.printf("Patch %s applied successfully\n", new Object[] { name });
		return true;
	}

	public void recreate() {
		ArrayList<Byte> result = new ArrayList<Byte>();
		result.addAll(Arrays.asList(this.Header));
		result.addAll(Arrays.asList(Encode(this.Count)));

		for (int i = 0; i < this.Titles.size(); i++) {
			result.addAll(Arrays.asList(Encode(this.Titles.get(i))));
			result.addAll(Arrays.asList(Encode(this.Values.get(i))));
		}
		result.addAll(Arrays.asList(Encode("#")));

		while (result.size() % 4 != 0) {
			result.add(Byte.valueOf((byte) 0));
		}
		int size = result.size() / 4 - 2;
		Byte[] b_size = BigEndian(size);
		result.set(4, b_size[0]);
		result.set(5, b_size[1]);
		result.set(6, b_size[2]);
		result.set(7, b_size[3]);

		this.active = result;
	}
}