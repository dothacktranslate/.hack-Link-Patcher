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

public class CM_FILE extends FILE {
	Byte[] Header = new Byte[12];

	Byte[] Name;

	int Count;
	//ArrayList<Byte[]> Names = (ArrayList) new ArrayList<Byte>();
	ArrayList<Byte[]> Names = new ArrayList<Byte[]>();
	ArrayList<Integer> Sizes = new ArrayList<Integer>();
	ArrayList<Integer> Offsets = new ArrayList<Integer>();
	ArrayList<String> Values = new ArrayList<String>();

	CM_FILE(String filename) {
		super(filename, -859036672, 2);
		this.Names.clear();
		this.Sizes.clear();
		this.Offsets.clear();
		this.Values.clear();

		this.Header = Arrays.<Byte>copyOfRange(this.active.<Byte>toArray(this.Header), 0, 12);
		Byte[] temp = new Byte[8];

		temp = Arrays.<Byte>copyOfRange(this.active.<Byte>toArray(temp), 12, 20);
		this.Name = temp;

		temp = new Byte[4];
		temp = Arrays.<Byte>copyOfRange(this.active.<Byte>toArray(temp), 20, 24);
		this.Count = LittleEndian(temp);
		if (this.Count <= 1) {
			return;
		}

		for (int i = 0; i < this.Count; i++) {
			int start = 24 + i * 20;
			int end = start + 20;

			temp = new Byte[20];
			temp = Arrays.<Byte>copyOfRange(this.active.<Byte>toArray(temp), start, end);

			Byte[] temp2 = new Byte[8];
			temp2 = Arrays.<Byte>copyOfRange(temp, 0, 8);
			this.Names.add(temp2);

			temp2 = new Byte[4];
			temp2 = Arrays.<Byte>copyOfRange(temp, 8, 12);
			int size = LittleEndian(temp2);
			this.Sizes.add(Integer.valueOf(size));

			temp2 = new Byte[4];
			temp2 = Arrays.<Byte>copyOfRange(temp, 12, 16);
			int offset = LittleEndian(temp2);
			this.Offsets.add(Integer.valueOf(offset));

			temp2 = new Byte[size];
			temp2 = Arrays.<Byte>copyOfRange(this.active.<Byte>toArray(temp2), 12 + offset, 12 + offset + size);

			this.Values.add(Decode(temp2).replaceAll("\r\n", "\n"));
		}
	}

	public void recreate() {
		ArrayList<Byte> result = new ArrayList<Byte>();
		result.addAll(Arrays.asList(this.Header));
		result.addAll(Arrays.asList(this.Name));
		result.addAll(Arrays.asList(BigEndian(this.Count)));
		int i;
		for (i = 1; i < this.Count; i++) {

			this.Sizes.set(i - 1, Integer.valueOf((Encode((String) this.Values.get(i - 1))).length));
			this.Offsets.set(i, Integer.valueOf(
					((Integer) this.Offsets.get(i - 1)).intValue() + ((Integer) this.Sizes.get(i - 1)).intValue()));
		}
		if (this.Count > 1) {

			for (i = 0; i < this.Count; i++) {
				result.addAll(Arrays.asList(this.Names.get(i)));

				if (!Arrays.equals((Object[]) this.Names.get(i), (Object[]) Blank)) {

					result.addAll(Arrays.asList(BigEndian(((Integer) this.Sizes.get(i)).intValue())));
					result.addAll(Arrays.asList(BigEndian(((Integer) this.Offsets.get(i)).intValue())));

					result.add(Byte.valueOf((byte) -1));
					result.add(Byte.valueOf((byte) -1));
					result.add(Byte.valueOf((byte) -1));
					result.add(Byte.valueOf((byte) -1));
				} else {

					result.addAll(Arrays.asList(BigEndian(0)));
					result.addAll(Arrays.asList(BigEndian(0)));
					result.add(Byte.valueOf((byte) 0));
					result.add(Byte.valueOf((byte) 0));
					result.add(Byte.valueOf((byte) 0));
					result.add(Byte.valueOf((byte) 0));
				}
			}

			for (i = 0; i < this.Count; i++) {
				result.addAll(Arrays.asList(Encode(this.Values.get(i))));
			}
		}
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
			if (this.Values.get(i) != "" && tokenizedInputs[i] != "")
			{
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
}