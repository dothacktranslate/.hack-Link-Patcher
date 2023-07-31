package dothacklink_Patcher_2023;

public class FileDescriptor {
	public String Source;
	public String Patch;
	public int headID;
	public int bodyID;

	FileDescriptor(String Source, String Patch, int headID, int bodyID) {
		this.Source = Source;
		this.Patch = Patch;
		this.headID = headID;
		this.bodyID = bodyID;
	}
}