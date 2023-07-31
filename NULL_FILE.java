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

public class NULL_FILE extends FILE {
	Byte[] Header = new Byte[12];
	ArrayList<String> Strings = new ArrayList<String>();

	public NULL_FILE(String filename, int headId, int bodyId) {
		super(filename, headId, bodyId);
		this.Strings.clear();
		Byte[] Body = new Byte[this.active.size() - 12];
		this.Header = Arrays.<Byte>copyOfRange(this.active.<Byte>toArray(this.Header), 0, 12);
		Body = Arrays.<Byte>copyOfRange(this.active.<Byte>toArray(Body), 12, this.active.size());

		ArrayList<Byte> temp = new ArrayList<Byte>();
		int b;
		int i;
		Byte[] arrayOfByte1;
		for (i = (arrayOfByte1 = Body).length, b = 0; b < i;) {
			Byte byte_ = arrayOfByte1[b];
			if (byte_.byteValue() == 0) {
				this.Strings.add(Decode(temp.<Byte>toArray(new Byte[0])));
				temp.clear();
			} else {
				temp.add(byte_);
			}
			b++;
		}

		if (temp.size() > 0) {
			this.Strings.add(Decode(temp.<Byte>toArray(new Byte[0])));
			temp.clear();
		}
	}

	public void recreate() {
		ArrayList<Byte> result = new ArrayList<Byte>();
		result.addAll(Arrays.asList(this.Header));

		for (String i : this.Strings) {
			result.addAll(Arrays.asList(Encode(i)));
			result.add(Byte.valueOf((byte) 0));
		}
		result.remove(result.size() - 1);

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

	public boolean createPatch(String name) {
		File location = new File(name);
		try {
			FileOutputStream out = new FileOutputStream(location);

			for (String string : this.Strings) {
				out.write(string.getBytes("UTF8"));
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

		int minlength = (tokenizedInputs.length < this.Strings.size()) ? tokenizedInputs.length : this.Strings.size();

		for (int i = 0; i < minlength; i++) {
			if (this.Strings.get(i) != "" && tokenizedInputs[i] != "") {
				this.Strings.set(i, tokenizedInputs[i]);
			} else {
				if (this.Strings.get(i) == "" && tokenizedInputs[i] != "") {
					System.out.printf("Too many String in patch %s\n", new Object[] { name });
					return false;
				}
				if (this.Strings.get(i) != "" && tokenizedInputs[i] == "") {
					System.out.printf("Too few String in patch %s\n", new Object[] { name });
					return false;
				}
				break;
			}

		}
		System.out.printf("Patch %s applied successfully\n", new Object[] { name });
		return true;
	}
}