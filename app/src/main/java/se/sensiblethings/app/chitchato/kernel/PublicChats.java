package se.sensiblethings.app.chitchato.kernel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import se.sensiblethings.app.chitchato.activities.ErrorActivity;
import se.sensiblethings.app.chitchato.extras.Constants;
import se.sensiblethings.app.chitchato.extras.Customizations;
import se.sensiblethings.app.chitchato.kernel.RESTHandler.RequestMethod;

public class PublicChats {

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
    protected ArrayList<String> public_chat_thread = new ArrayList<String>();
    protected int PREFFERED_MAX_THREAD_LINES = 99;
    protected int PRI_PREFFERED_MAX_THREAD_LINES = 99;
    protected int PUB_PREFFERED_MAX_THREAD_LINES = 99;
    protected String file_url = groups.file_url;
    Customizations custom;
    ChitchatConfig chitchat_config_0;

    public PublicChats() {

    }

    public PublicChats(Context context) {
        this.context_ = context;
    }

    public PublicChats(Context context, String groupname) {
        this.context_ = context;
        this._selected_group_ = groupname;
        custom = new Customizations(context, -1);
        PRI_PREFFERED_MAX_THREAD_LINES = custom.getPri_message_thread_len();
        PUB_PREFFERED_MAX_THREAD_LINES = custom.getPub_message_thread_len();
        // list of public messages --- based on the valuue PREFFERED_MAX_THREAD_LINES
        chitchat_config_0 = new ChitchatConfig(groupname, "pbchat");
        try {
            chitchat_config_0.loadProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.public_chat_thread = chitchat_config_0.getPropertiesArrayList();
    }

    public PublicChats(Context context, String groupname, String ext) {
        this.context_ = context;
        this._selected_group_ = groupname;
        //custom = new Customizations(context, -1);
        //PRI_PREFFERED_MAX_THREAD_LINES = custom.getPri_message_thread_len();
        //PUB_PREFFERED_MAX_THREAD_LINES = custom.getPub_message_thread_len();
        // list of public messages --- based on the valuue PREFFERED_MAX_THREAD_LINES
        chitchat_config_0 = new ChitchatConfig(groupname, ext);
        try {
            chitchat_config_0.loadProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.public_chat_thread = chitchat_config_0.getPropertiesArrayList();
    }


    public ArrayList<String> getPublicChat(String xmldoc) {


        String message = null;
        ArrayList<String> public_chat_list = new ArrayList<String>();
        int i = 0;
        String itemID = "";
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            org.w3c.dom.Document doc = db.parse(new InputSource(
                    new StringReader(xmldoc)));
            Element root_Element = doc.getDocumentElement();
            Element el_, el__;
            NodeList nl = root_Element.getElementsByTagName("Public");
            el_ = (Element) nl.item(0);
            // el_ = (Element) nl.item(0);
            NodeList nl_ = el_.getElementsByTagName("Message");
            if (nl_ != null && nl_.getLength() > 0) {
                Log.i("Number of Messages:", nl_.getLength() + "");
                for (int ii = 0; ii < nl_.getLength(); ii++) {
                    Node node = (Node) nl_.item(ii);
                    el__ = (Element) nl_.item(ii);
                    message = node.getFirstChild().getNodeValue() + "::"
                            + el__.getAttribute("peerName") + "::"
                            + el__.getAttribute("time");

                    public_chat_list.add(message);
                    Log.i("Message " + ii, message);
                    // System.out.print(ell_.getFirstChild().getNodeValue());

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return public_chat_list;
    }

    public String getPublicChat(Context context, String groupName) {
        String publicChat = "";
        RESTHandler resthandler = new RESTHandler(this.file_url
                + "public_chat/");

        try {
            resthandler.AddParam("group", groupName);
            resthandler.Execute(RequestMethod.GET, null);

            response = resthandler.getResponse();
            Log.i("Chitchato Server Test", response);
            this.xml_string = response;
            ///System.out.print(xml_string);
            publicChat = response;

        } catch (Exception e) {
            e.printStackTrace();
            Intent intent = new Intent(context.getApplicationContext(),
                    ErrorActivity.class);
            intent.putExtra("error", "System Error!");
            context.startActivity(intent);

        }
        return publicChat;
    }

    public ArrayList<String> getmPublicChat(String _group_name_) {
        public_chat_thread = chitchat_config_0.getPropertiesArrayList();

        return public_chat_thread;
    }

    public ArrayList<String> getmPublicChat(String _group_name_, int len) {
        public_chat_thread = chitchat_config_0.getPropertiesArrayList();
        ArrayList ar_list_lim = new ArrayList<String>();

        for (String str : public_chat_thread) {
            if (public_chat_thread.size() - public_chat_thread.indexOf(str) < len)
                ar_list_lim.add(str);
        }


        return ar_list_lim;
    }
    public int getmPublicChatSize()
    {
        return  public_chat_thread.size();
    }

    public ArrayList<String> getmPublicChat()

    {
        return public_chat_thread;
    }


    public void setmPublicChat(ArrayList<String> al) {
        this.public_chat_thread = al;
    }

    public void addChat(String group_name_, String _thread_line) {
        if (_selected_group_.equalsIgnoreCase(group_name_)) {
            if (this.PREFFERED_MAX_THREAD_LINES != -1) {
                while (this.PREFFERED_MAX_THREAD_LINES <= public_chat_thread.size()) {
                    //public_chat_thread.remove(0);
                }
                public_chat_thread.add(_thread_line);
                try {
                    chitchat_config_0.addPair(_thread_line, group_name_);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                while (Constants.PREFFERED_MAX_THREAD_LINES >= public_chat_thread.size()) {
                    // public_chat_thread.remove(0);
                }
                public_chat_thread.add(_thread_line);
            }
        } else ;

        //savePublicChats(group_name_, "",public_chat_thread);
    }

    public synchronized void savePublicChats(String _group_name_, String ext, ArrayList<String> ar) {

        try {

            chitchat_config_0.loadProperties();
            //chitchat_config_0.saveArrayList(_group_name_, ext, ar);
            chitchat_config_0.saveArrayList(this.context_, _group_name_, ext, ar);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public synchronized void saveADVs(String _group_name_, String ext, ArrayList<String> ar) {

        try {
            chitchat_config_0.loadProperties();
            //chitchat_config_0.saveArrayList(_group_name_, ext, ar);
            chitchat_config_0.saveArrayList(this.context_, _group_name_, ext, ar);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void clearPublicChats() {
        chitchat_config_0.deleteProperties();
    }

    public void clearPublicChats(String _group_name_, String ext) {
        chitchat_config_0.deleteProperties();
    }


    public int getPREFFERED_MAX_THREAD_LINES() {
        return PREFFERED_MAX_THREAD_LINES;
    }

    public void setPREFFERED_MAX_THREAD_LINES(int PREFFERED_MAX_THREAD_LINES) {
        this.PREFFERED_MAX_THREAD_LINES = PREFFERED_MAX_THREAD_LINES;
    }

    public long getFileModifiedTimeMills()
    {
        return chitchat_config_0.getLast_modified();
    };
}
