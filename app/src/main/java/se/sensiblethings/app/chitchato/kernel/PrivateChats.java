package se.sensiblethings.app.chitchato.kernel;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import se.sensiblethings.app.chitchato.extras.Constants;
import se.sensiblethings.app.chitchato.extras.Customizations;

public class PrivateChats {

    protected ArrayList<String> mPeers = null;
    protected ArrayList<String> mPublicChat = null;
    protected String xml_string = "";
    protected String response = null;
    protected Context context_;
    protected FileInputStream file_input_stream = null;
    protected FileOutputStream file_output_stream = null;
    protected SharedPreferences mPrefs;
    protected String mGroups_temp = "#none#";
    protected Groups groups = new Groups();
    protected String _selected_group_ = "*ALL#";
    protected String _selected_peer_ = "none";
    protected ArrayList<String> private_chat_thread = new ArrayList<String>();
    protected int PREFFERED_MAX_THREAD_LINES = 99;
    protected int PRI_PREFFERED_MAX_THREAD_LINES = 99;
    protected int PUB_PREFFERED_MAX_THREAD_LINES = 99;

    Customizations custom;

    protected String file_url = groups.file_url;

    ChitchatConfig chitchat_config_0;

    public PrivateChats() {

    }

    public PrivateChats(Context context) {
        this.context_ = context;
    }

    public PrivateChats(Context context, String groupname, String friend_uci) {
        this.context_ = context;
        this._selected_group_ = groupname;
        this._selected_peer_ = friend_uci;
        custom = new Customizations(context, -1);
        PRI_PREFFERED_MAX_THREAD_LINES = custom.getPri_message_thread_len();
        PUB_PREFFERED_MAX_THREAD_LINES = custom.getPub_message_thread_len();
        // list of public messages --- based on the valuue PREFFERED_MAX_THREAD_LINES
        chitchat_config_0 = new ChitchatConfig(groupname + "." + friend_uci, "prchat");
        try {
            chitchat_config_0.loadProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.private_chat_thread = chitchat_config_0.getPropertiesArrayList();
    }

    public ArrayList<String> getmPrivateChat(String _group_name_)

    {
        private_chat_thread = chitchat_config_0.getPropertiesArrayList();
        return private_chat_thread;
    }

    public ArrayList<String> getmPrivateChat(String _group_name_, int len) {
        private_chat_thread = chitchat_config_0.getPropertiesArrayList();

        ArrayList ar_list_lim = new ArrayList<String>();
        for (String str : private_chat_thread) {
            if (private_chat_thread.size() - private_chat_thread.indexOf(str) < 15)
                ar_list_lim.add(str);
        }
        return ar_list_lim;
    }


    public int getmPrivateChatSize() {
        return private_chat_thread.size();
    }


    public ArrayList<String> getmPrivateChat()

    {
        return private_chat_thread;
    }


    public void setmPrivateChat(ArrayList<String> al) {
        this.private_chat_thread = al;
    }

    public void addChat(String group_name_, String friend_uci, String _thread_line) {
        if (_selected_peer_.equalsIgnoreCase(friend_uci)) {
            if (this.PREFFERED_MAX_THREAD_LINES != -1) {
                while (this.PREFFERED_MAX_THREAD_LINES >= private_chat_thread.size()) {
                    private_chat_thread.remove(0);
                }
                private_chat_thread.add(_thread_line);
                try {
                    chitchat_config_0.addPair(_thread_line, group_name_ + "." + friend_uci);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                while (Constants.PREFFERED_MAX_THREAD_LINES >= private_chat_thread.size()) {
                    private_chat_thread.remove(0);
                }
                private_chat_thread.add(_thread_line);
            }
        } else ;
    }


    public synchronized void savePrivateChats(String _group_name_, String friend_uci, String ext, ArrayList<String> ar) {
        try {
            chitchat_config_0.loadProperties();
            chitchat_config_0.saveArrayList(this.context_, _group_name_ + "." + friend_uci, ext, ar);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clearPrivateChats(String _group_name_, String friend_uci, String ext) {
        chitchat_config_0.deleteProperties();
    }


    public int getPREFFERED_MAX_THREAD_LINES() {
        return PREFFERED_MAX_THREAD_LINES;
    }

    public void setPREFFERED_MAX_THREAD_LINES(int PREFFERED_MAX_THREAD_LINES) {
        this.PREFFERED_MAX_THREAD_LINES = PREFFERED_MAX_THREAD_LINES;
    }
}
