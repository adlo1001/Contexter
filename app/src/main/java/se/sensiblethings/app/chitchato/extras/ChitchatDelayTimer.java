package se.sensiblethings.app.chitchato.extras;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;

public class ChitchatDelayTimer extends Thread {
	protected Context context_;
	protected Dialog dialog_win;
	public boolean SHOW_ERROR_DAILOG = false;
	public boolean SHOW_BUSY_DAILOG = true;

	public ChitchatDelayTimer(Context c) {
		this.context_ = c;
	}

	public ChitchatDelayTimer(Context c, Dialog d) {
		this.context_ = c;
		this.dialog_win = d;
	}

	@Override
	public void run() {
		Log.v("Delay Timer", "Delay Time ");

		while (SHOW_BUSY_DAILOG) {

			try {
				// check connections for 1/2 minutes
				this.sleep(30 * 1000);
				this.SHOW_ERROR_DAILOG = true;
				this.interrupt();

			} catch (Exception e) {
				Log.v("Chitchat Delay Timer", "Thread Interepted!");
				//e.printStackTrace();
				return;
			}

		}
	}

	public Dialog getDialog_win() {
		return dialog_win;
	}

	public void setDialog_win(Dialog dialog_win) {
		this.dialog_win = dialog_win;
	}

	public boolean isSHOW_ERROR_DAILOG() {
		return SHOW_ERROR_DAILOG;
	}

	public void setSHOW_ERROR_DAILOG(boolean sHOW_ERROR_DAILOG) {
		SHOW_ERROR_DAILOG = sHOW_ERROR_DAILOG;
	}

	public boolean isSHOW_BUSY_DAILOG() {
		return SHOW_BUSY_DAILOG;
	}

	public void setSHOW_BUSY_DAILOG(boolean sHOW_BUSY_DAILOG) {
		SHOW_ERROR_DAILOG = sHOW_BUSY_DAILOG;
	}

}
