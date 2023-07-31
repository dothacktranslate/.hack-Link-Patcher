package dothacklink_Patcher_2023;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class FILE {
	static Byte[] Blank = new Byte[] { Byte.valueOf((byte) 0), Byte.valueOf((byte) 0), Byte.valueOf((byte) 0),
			Byte.valueOf((byte) 0), Byte.valueOf((byte) 0), Byte.valueOf((byte) 0), Byte.valueOf((byte) 0),
			Byte.valueOf((byte) 0) };
	static String Break = "\n\nEOL\n\n";

	String filename;

	ArrayList<Byte> opening = new ArrayList<Byte>();
	ArrayList<Byte> active = new ArrayList<Byte>();
	ArrayList<Byte> closing = new ArrayList<Byte>();

	public FILE(String filename, int headId, int bodyId) {
		this.filename = filename;
		this.opening.clear();
		this.active.clear();
		this.closing.clear();

		byte[] body = new byte[0];

		try {
			FileInputStream in = new FileInputStream(filename);

			while (in.available() > 10) {
				byte[] head = new byte[4];
				in.read(head);
				byte[] size = new byte[4];
				in.read(size);
				int length = LittleEndian(size) * 4;
				body = new byte[length];
				in.read(body);

				int testHeadId = LittleEndian(head);
				int testBodyId = (length > 0) ? LittleEndian(body) : 0;

				if (testHeadId == headId && testBodyId == bodyId) {
					int b1;
					int j;
					byte[] arrayOfByte;
					for (j = (arrayOfByte = head).length, b1 = 0; b1 < j;) {
						byte b2 = arrayOfByte[b1];
						this.active.add(Byte.valueOf(b2));
						b1++;
					}
					for (j = (arrayOfByte = size).length, b1 = 0; b1 < j;) {
						byte b2 = arrayOfByte[b1];
						this.active.add(Byte.valueOf(b2));
						b1++;
					}
					for (j = (arrayOfByte = body).length, b1 = 0; b1 < j;) {
						byte b2 = arrayOfByte[b1];
						this.active.add(Byte.valueOf(b2));
						b1++;
					}
					break;
				}
				int b;
				int i;
				byte[] arrayOfByte1;
				for (i = (arrayOfByte1 = head).length, b = 0; b < i;) {
					byte b1 = arrayOfByte1[b];
					this.opening.add(Byte.valueOf(b1));
					b++;
				}
				for (i = (arrayOfByte1 = size).length, b = 0; b < i;) {
					byte b1 = arrayOfByte1[b];
					this.opening.add(Byte.valueOf(b1));
					b++;
				}
				for (i = (arrayOfByte1 = body).length, b = 0; b < i;) {
					byte b1 = arrayOfByte1[b];
					this.opening.add(Byte.valueOf(b1));
					b++;
				}

			}
			if (in.available() > 0) {
				byte[] temp = new byte[in.available()];
				in.read(temp);
				int b;
				int i;
				byte[] arrayOfByte1;
				for (i = (arrayOfByte1 = temp).length, b = 0; b < i;) {
					byte b1 = arrayOfByte1[b];
					this.closing.add(Byte.valueOf(b1));
					b++;
				}

			}
			in.close();
		} catch (IOException Ex) {
			Ex.printStackTrace();
		}
	}

	protected static int LittleEndian(Byte[] input) {
		int result = 0;
		result = 0xFF & input[3].byteValue();
		result = (result << 8) + (0xFF & input[2].byteValue());
		result = (result << 8) + (0xFF & input[1].byteValue());
		result = (result << 8) + (0xFF & input[0].byteValue());
		return result;
	}

	static int LittleEndian(byte[] input) {
		int result = 0;
		result = 0xFF & input[3];
		result = (result << 8) + (0xFF & input[2]);
		result = (result << 8) + (0xFF & input[1]);
		result = (result << 8) + (0xFF & input[0]);
		return result;
	}

	Byte[] BigEndian(int data) {
		Byte[] b = new Byte[4];
		b[0] = Byte.valueOf((byte) (data >> 0 & 0xFF));
		b[1] = Byte.valueOf((byte) (data >> 8 & 0xFF));
		b[2] = Byte.valueOf((byte) (data >> 16 & 0xFF));
		b[3] = Byte.valueOf((byte) (data >> 24 & 0xFF));
		return b;
	}

	protected String Decode(Byte[] in1) {
		byte[] in2 = new byte[in1.length];
		for (int i = 0; i < in1.length; i++)
			in2[i] = in1[i].byteValue();
		try {
			return new String(in2, "UTF8");
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();

			return null;
		}
	}

	protected Byte[] Encode(String in1) {
		try {
			byte[] in2 = in1.getBytes("UTF8");
			Byte[] response = new Byte[in2.length];

			for (int i = 0; i < in2.length; i++) {
				response[i] = Byte.valueOf(in2[i]);
			}
			return response;
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();

			return null;
		}
	}

	public void saveFile() {
		byte[] toWrite = new byte[this.opening.size() + this.active.size() + this.closing.size()];

		int offset = 0;
		int i;
		for (i = 0; i < this.opening.size(); i++) {
			toWrite[offset + i] = ((Byte) this.opening.get(i)).byteValue();
		}
		offset += this.opening.size();
		for (i = 0; i < this.active.size(); i++) {
			toWrite[offset + i] = ((Byte) this.active.get(i)).byteValue();
		}
		offset += this.active.size();
		for (i = 0; i < this.closing.size(); i++) {
			toWrite[offset + i] = ((Byte) this.closing.get(i)).byteValue();
		}
		try {
			FileOutputStream output = new FileOutputStream(this.filename);
			output.write(toWrite);
			output.close();
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public String testBlocks() {
		int count = 0;
		String out = "";
		for (Byte i : this.active) {
			out = String.valueOf(out) + String.format("%02x", new Object[] { i });
			count++;
			if (count == 8) {
				out = String.valueOf(out) + "\n";
				count = 0;
			}
		}

		return out;
	}
}