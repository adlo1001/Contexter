package se.sensiblethings.app.chitchato.extras;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

public class LocalStorageGroup {

	String file1 = "active_groups";
	BufferedWriter bw = null;
	String file2 = "peer";
	FileOutputStream fos = null;
	FileInputStream fis = null;
	public Context contxt = null;
	private String split[];

	public LocalStorageGroup(Context context) {
		this.contxt = context;
	}

	public LocalStorageGroup() {
	}

	public synchronized void writeToNewChatList(Context cntxt, String file) {

		try {

			fos = cntxt.openFileOutput(file, Context.MODE_PRIVATE);
			cntxt.deleteFile(file);
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		notifyAll();
	}

	public synchronized void writeToChatList(String group_name, String message,
			Context cntxt) {

		try {
			fos = cntxt.openFileOutput(group_name, Context.MODE_APPEND);
			fos.write(message.getBytes());
			fos.close();
			Log.e("CHITCHAT -O", "savedto " + group_name + "&\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		notifyAll();
	}

	public synchronized void WriteToChatList(String message, Context cntxt,
			String file) {

		try {
			fos = cntxt.openFileOutput(file.trim(), Context.MODE_APPEND);
			fos.write(("\n" + message + "& \n").getBytes());
			fos.close();
			Log.e("saved to" + file, message + "\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		notifyAll();
	}

	public synchronized LinkedList<String> ReadFromChatList(Context cntxt,
			String group_name) {

		LinkedList<String> ll = new LinkedList<String>();
		try {

			fis = cntxt.openFileInput(group_name.trim());
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);
			String data = br.readLine();

			while (br.readLine() != null) {

				if (!data.trim().isEmpty()) {
					data = br.readLine();
					if (!ll.contains(data.trim()))
						ll.add(data);
					Log.e("Read From" + group_name, data + " ");
				}

			}
			Log.e("Read From" + group_name, data + " ");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		notifyAll();
		return ll;
	}

	public synchronized byte[] getBytesForImage(Context context, String uci)
	{
		//try{}catch(IOException e){e.printStackTrace();}
		return null;
	}

}
