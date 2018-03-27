package se.sensiblethings.app.chitchato.kernel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import se.sensiblethings.app.chitchato.activities.ErrorActivity;
import se.sensiblethings.app.chitchato.kernel.RESTHandler.RequestMethod;

public class Groups {

    protected ArrayList<String> mGroups = new ArrayList<String>();
    protected ArrayList<String> mPeers = null;
    protected String xml_string = "";
    protected String response = null;
    protected Context context_;
    protected FileInputStream file_input_stream = null;
    protected FileOutputStream file_output_stream = null;
    protected SharedPreferences mPrefs;
    protected String mGroups_temp = "#none#";
    protected ArrayList<String> myProfile = null;
    protected String myGroups = "#none#";
    ;
    ChitchatConfig chitchat_config_0;

    protected Set<String> fav_groups;

    public String file_url = "http://10.47.232.55:8080/thewebpage/webresources/generic/";

    protected URL url;

    public Groups() {

    }

    public Groups(Context context) {
        this.context_ = context;
        try {
            ArrayList<String> tmp_arr_list = new ArrayList<String>();
            chitchat_config_0 = new ChitchatConfig("groups", "mygroups");
            chitchat_config_0.loadProperties();
            mGroups = chitchat_config_0.getPropertiesArrayList();
            chitchat_config_0 = new ChitchatConfig("groups", "groups");
            chitchat_config_0.loadProperties();
            tmp_arr_list = chitchat_config_0.getPropertiesArrayList();

            for (String str : tmp_arr_list) {
                mGroups.add(str);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public Groups(Context context, String nm) {
        try {
            this.context_ = context;
            chitchat_config_0 = new ChitchatConfig("groups", "passwords");
            chitchat_config_0.loadProperties();
            mGroups = chitchat_config_0.getPropertiesArrayList();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Groups(Context context, String file_url_, String groupOwner) {
        try {
            this.context_ = context;
            mPrefs = context_.getSharedPreferences("mygroups_cache", 2);
            myGroups = mPrefs.getString("groups", myGroups);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

            Intent intent = new Intent(this.context_, ErrorActivity.class);
            intent.putExtra("error", e.getLocalizedMessage());
            context_.startActivity(intent);

        }

    }

    public Groups(Context context, String group_name, boolean mygroup) {
        this.context_ = context;
        // list of my groups
        if (mygroup)
            chitchat_config_0 = new ChitchatConfig("groups", "mygroups");
        else
            chitchat_config_0 = new ChitchatConfig("groups", "groups");
        try {
            chitchat_config_0.loadProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mGroups = chitchat_config_0.getPropertiesArrayList();
    }

    public ArrayList<String> getGroupsList(String xmldoc) {
        // HashMap items_;//= new HashMap();
        ArrayList<String> al = new ArrayList<String>();
        int i = 0;
        String itemID = "";
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            org.w3c.dom.Document doc = db.parse(new InputSource(
                    new StringReader(xmldoc)));
            Element root_Element = doc.getDocumentElement();
            Element el_;
            NodeList nl = root_Element.getElementsByTagName("GroupName");

            // el_ = (Element) nl.item(0);

            if (nl != null && nl.getLength() > 0) {
                // Log.i("Number of Items:", nl.getLength()+"");
                for (int ii = 0; ii < nl.getLength(); ii++) {
                    el_ = (Element) nl.item(ii);
                    al.add(el_.getFirstChild().getNodeValue());
                    // Log.i("Item" + ii, el_.getFirstChild().getNodeValue());
                    // System.out.print(ell_.getFirstChild().getNodeValue());

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return al;
    }

    public ArrayList getPeerList(String xmldoc) {
        // HashMap items_;//= new HashMap();
        ArrayList<String> al = new ArrayList<String>();
        int i = 0;
        String itemID = "";
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            org.w3c.dom.Document doc = db.parse(new InputSource(
                    new StringReader(xmldoc)));
            Element root_Element = doc.getDocumentElement();
            Element el_;
            NodeList nl = root_Element.getElementsByTagName("Peer");

            // el_ = (Element) nl.item(0);

            if (nl != null && nl.getLength() > 0) {
                Log.i("Number of Peers:", nl.getLength() + "");
                for (int ii = 0; ii < nl.getLength(); ii++) {
                    el_ = (Element) nl.item(ii);
                    NodeList nl_ = el_.getElementsByTagName("peerName");
                    Node node = (Node) nl_.item(0);

                    al.add(node.getFirstChild().getNodeValue());
                    Log.i("Peer " + ii, node.getFirstChild().getNodeValue());
                    // System.out.print(ell_.getFirstChild().getNodeValue());

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return al;
    }

    public String getFormattedItemsList(String xmldoc, String groupName) {
        // HashMap items_;//= new HashMap();

        Log.w("XML DOC-->Groups", xmldoc);
        String groupsFormattedString = "nothing";
        String g_Name = null, g_interest = null, g_age_limit = null, g_leader = null, g_ip = null, g_port = null;
        int i = 0;
        String itemID = "";
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            org.w3c.dom.Document doc = db.parse(new InputSource(
                    new StringReader(xmldoc)));
            Element root_Element = doc.getDocumentElement();
            Element el_;
            NodeList nl = root_Element.getElementsByTagName("Group");

            // el_ = (Element) nl.item(0);

            // if (nl != null && nl.getLength() > 0) {
            // Log.i("Number of Items:", nl.getLength()+"");
            // for (int ii = 0; ii < nl.getLength(); ii++) {
            // el_ = (Element) nl.item(ii);
            g_Name = root_Element.getAttribute("GroupName");
            g_leader = root_Element.getAttribute("groupLeader");
            g_interest = root_Element.getAttribute("GroupInterest");
            g_age_limit = root_Element.getAttribute("groupAgeLimit");
            g_ip = root_Element.getAttribute("IPAddress");
            g_port = root_Element.getAttribute("port");

            // Add to Local cache of favorite groups
            // if (g_Name.contains(groupName))
            groupsFormattedString = this.groupsFormatLocal(g_Name, g_interest,
                    g_age_limit, g_leader, g_ip, g_port);
            // al.add(el_.getFirstChild().getNodeValue());

            // System.out.print(ell_.getFirstChild().getNodeValue());

            // }

            // }
            Log.i("groupsFormattedString", groupsFormattedString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return groupsFormattedString;
    }

    public void updateGroupsOnLocalNode(Context context, String groups_, boolean _mygroup) {

        try {

            {
                if (!mGroups.contains(groups_)) {
                    mGroups.add(groups_);
                }
                ;

                if (_mygroup)
                    saveNewGroup(groups_, "mygroups", mGroups);
                else
                    saveNewGroup(groups_, "groups", mGroups);
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Intent intent = new Intent(context.getApplicationContext(),
                    ErrorActivity.class);
            intent.putExtra("error", "System Error!");
            context.startActivity(intent);

        }

    }

    public void saveNewGroup(String _group_name_, String ext, ArrayList<String> ar) {
        chitchat_config_0.saveArrayList(_group_name_, ext, ar);
    }


    public void updateGroupsOnLocalNode(Context context, ArrayList<String> al,
                                        String file_name) {

        ArrayList<String> temp_arr = getGroupsN("");
        for (String temp : temp_arr) {
            al.add(temp);
        }

        try {
            // System.out.println(groups_);
            SharedPreferences.Editor ed = mPrefs.edit();
            Set<String> set = new HashSet<String>(al);
            ed.putStringSet(file_name, set);
            ed.commit();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Intent intent = new Intent(context.getApplicationContext(),
                    ErrorActivity.class);
            intent.putExtra("error", "System Error!");
            context.startActivity(intent);

        }

    }

    public ArrayList<String> retrieveGroupsFromLocalNode() {
        ArrayList<String> al = new ArrayList<String>();
        String groups_temp = "#none#";
        String[] temp_1, temp_2;
        mGroups_temp = mPrefs.getString("groups", mGroups_temp);
        try {
            al = chitchat_config_0.getPropertiesArrayList();
        } catch (Exception e) {
            e.printStackTrace();
            Intent intent = new Intent(this.context_, ErrorActivity.class);
            intent.putExtra("error", "System Error!");
            context_.startActivity(intent);
        }

        return al;
    }

    public String retrieveFormattedGroupsFromLocalNode() {

        String groups_temp = "#none#";
        String[] temp_1, temp_2;

        mGroups_temp = mPrefs.getString("groups", mGroups_temp);
        try {

            if (mPrefs.contains("groups") == true)
            // Log.i("Files Directory:>>",
            // context_.getFilesDir().getCanonicalPath());
            // context_.getFilesDir().createTempFile("groups", "log");
            {
                groups_temp = mPrefs.getString("groups", groups_temp);

            } else
                ;

        } catch (Exception e) {
            e.printStackTrace();
            Intent intent = new Intent(this.context_, ErrorActivity.class);
            intent.putExtra("error", "System Error!");
            context_.startActivity(intent);
        }

        return groups_temp;
    }

    public ArrayList<String> retrieveGroupsFromBT(Context context) {

        ArrayList<String> al_ = new ArrayList<String>();
        RESTHandler resthandler = new RESTHandler(this.file_url + "groups");

        try {
            resthandler.Execute(RequestMethod.GET, null);

            response = resthandler.getResponse();

            Log.i("Chitchato Server Test", response);
            this.xml_string = response;
            this.mGroups = this.getGroupsList(response);
            al_ = this.mGroups;
            if (al_.isEmpty())
                al_.add("None!");

        } catch (Exception e) {
            e.printStackTrace();
            Intent intent = new Intent(context.getApplicationContext(),
                    ErrorActivity.class);
            intent.putExtra("error", "System Error!");
            context.startActivity(intent);

        }
        return al_;
    }

    public String retrieveGroupFromBT(Context context, String groupName) {

        // ArrayList<String> al_ = new ArrayList<String>();
        String group = "";
        RESTHandler resthandler = new RESTHandler(this.file_url + "peer/");

        try {
            resthandler.AddParam("group", groupName);
            resthandler.Execute(RequestMethod.GET, null);

            response = resthandler.getResponse();

            Log.i("Chitchato Server Test", response);
            this.xml_string = response;
            // /this.mGroups = this.getItemsList(response);
            // al_ = this.mGroups;
            // if (al_.isEmpty())
            // al_.add("None!");
            group = response;

        } catch (Exception e) {
            e.printStackTrace();
            Intent intent = new Intent(context.getApplicationContext(),
                    ErrorActivity.class);
            intent.putExtra("error", "System Error!");
            context.startActivity(intent);

        }
        return group;
    }

    public ArrayList<String> retrievePeersFromBT(Context context,
                                                 String groupName) {

        ArrayList<String> peers = new ArrayList<String>();
        // String group = "";
        RESTHandler resthandler = new RESTHandler(this.file_url + "peer/");

        try {
            resthandler.AddParam("group", groupName);
            resthandler.Execute(RequestMethod.GET, null);
            response = resthandler.getResponse();
            Log.i("Chitchato Server Test", response);
            this.xml_string = response;
            // /this.mGroups = this.getItemsList(response);
            // al_ = this.mGroups;
            // if (al_.isEmpty())
            // al_.add("None!");

        } catch (Exception e) {
            e.printStackTrace();
            Intent intent = new Intent(context.getApplicationContext(),
                    ErrorActivity.class);
            intent.putExtra("error", "System Error!");
            context.startActivity(intent);

        }
        return peers;
    }

    public void updateGroupsOnBT(Context context, String data) {
        try {
            RESTHandler resthandler = new RESTHandler(this.file_url + "group");

            data = data + InetAddress.getLocalHost().getAddress() + "::"
                    + "9009" + "::~~";
            resthandler.Execute(RequestMethod.POST, data);
            String response = resthandler.getResponse();
            // Log.i("Chitchato Server Test", response);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Intent intent = new Intent(context.getApplicationContext(),
                    ErrorActivity.class);
            intent.putExtra("error",
                    "Connection to the Bootstrap Server Failed!! ");
            context.startActivity(intent);
        }

    }

    public ArrayList<String> searchGroupsOnCache(Context context,
                                                 String keyword, String interest, int age, String group_leader) {
        ArrayList<String> al = new ArrayList<String>();
        ArrayList<String> temp_al = this.retrieveGroupsFromLocalNode();
        String temp = "";
        Iterator itr = temp_al.listIterator();

        while (itr.hasNext()) {
            temp = itr.next().toString();
            if (temp.contains(keyword) || temp.contains(interest)
                    || temp.contains(group_leader)) {
                al.add(temp);
            }
        }

        return al;
    }

    public ArrayList<String> searchGroupsOnBT(Context context, String keyword,
                                              String interest, int age, String group_leader) {
        ArrayList<String> al = new ArrayList<String>();
        ArrayList<String> temp_al = this.retrieveGroupsFromBT(context);
        String temp = "";
        Iterator itr = temp_al.listIterator();

        while (itr.hasNext()) {
            temp = itr.next().toString();
            if (temp.contains(keyword) || temp.contains(interest)
                    || temp.contains(group_leader)) {
                al.add(temp);
            }
        }

        return al;
    }

    public String groupsFormatLocal(String groupName, String interest,
                                    String age, String groupLeader, String ip, String port) {
        String str_group_format, g_Name, g_interest, g_age_limit, g_leader, g_ip, g_port;
        g_Name = groupName;
        g_interest = interest;
        g_age_limit = age;
        g_leader = groupLeader;
        g_ip = ip;
        g_port = port;

        str_group_format = "~~::" + g_Name + "::" + g_leader + "::"
                + g_interest + "::" + g_age_limit + "::" + g_ip + "::" + g_port
                + "::~~";
        return str_group_format;
    }

    public String removeGroupFromLocal(String groups, String groupName) {
        String updatedGroups = "", first_piece = "", second_piece = "";
        String data = groups;
        String temp_string = "";
        String[] data_1 = data.split("~~");
        int i = 0;
        for (String group : data_1) {
            System.out.println(group);
            String[] data_2 = group.split("::");

            for (String peer : data_2) {

                if (!group.startsWith("::")) {
                    break;
                }

                if (!peer.isEmpty()) {

                    if (!(0 == data_2[1].trim().compareTo(groupName))) {

                        temp_string = "~~" + data_1[i] + "~~";

                        updatedGroups = temp_string + updatedGroups;
                        break;
                    }
                }

            }

            i++;
        }

        // this.updateGroupsOnLocalNode(this.context_, updatedGroups);
        return updatedGroups;
    }

    public class BKGroundTask extends AsyncTask<URL, Integer, Long> {

        public ArrayList<String> mGroups = null;
        int progress_val = 0;
        Context context;
        ArrayList<String> al_;
        // ListAdapter listAdapter;

        @Override
        protected Long doInBackground(URL... urls) {

            int count = urls.length;
            String response = null;
            al_ = null;
            try {
                Groups groups = new Groups();

                for (int i = 0; i < count; i++) {
                    publishProgress((int) ((i / (float) count) * 100));
                    if (isCancelled())
                        break;
                }

                al_ = groups.retrieveGroupsFromBT(context);

                this.mGroups = al_;
                for (String temp : mGroups) {
                    Log.w("Get Groups >> groups", temp);
                }
                // publishProgess("");
                // RESTHandler resthandler = new RESTHandler(
                // "http://192.168.0.30:8080/thewebpage/webresources/generic/groups");

                // resthandler.Execute(RequestMethod.GET, null);
                // response = resthandler.getResponse();
                // Log.i("Chitchato Server Test", response);

            } catch (Exception e) {
                e.printStackTrace();
                Intent intent = new Intent(context.getApplicationContext(),
                        ErrorActivity.class);
                intent.putExtra("error",
                        "Connection to the Bootstrap Server Failed!");
                context.startActivity(intent);

            }
            return null;
        }

        @Override
        protected void onPostExecute(Long result) {
            try {

                // this.mGroups = this.getItemsList(result);
                // prgDialog.showDialog("Done groups loading!" + result +"" );
                // dismissDialog(0);
                this.mGroups = al_;
                setGroups(mGroups);
            } catch (Exception e) {
                Intent intent = new Intent(context.getApplicationContext(),
                        ErrorActivity.class);
                intent.putExtra("error",
                        "Connection to the Bootstrap Server Failed!");
                context.startActivity(intent);
            }
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // if(progress[0]<100)
            // {

            // }
            // progress_val = progress_val + 5;

        }

        public ArrayList<String> getGroups() {

            return mGroups;

        }

        public void setContext(Context c) {
            this.context = c;
        }

    }

    public ArrayList<String> getGroups() {

        mGroups = this.retrieveGroupsFromLocalNode();

        return mGroups;
    }

    public ArrayList<String> getPeers(String doc) {

        mPeers = getPeerList(doc);

        return mPeers;
    }

    public ArrayList<String> getGroups(String file_url) {

        mGroups = this.retrieveGroupsFromBT(this.context_);
        return mGroups;
    }

    public ArrayList<String> getGroupsN(String _file_url) {

        ArrayList<String> al = new ArrayList<String>();
        Set<String> set_ = new HashSet<String>();
        String groups_temp = "#none#";
        String temp;
        set_ = mPrefs.getStringSet("FAV_GROUPS", null);

        try {
            Map.Entry node;
            Iterator itr = set_.iterator();

            while (itr.hasNext()) {
                temp = itr.next().toString();
                al.add(temp);
            }

        } catch (Exception e) {
            e.printStackTrace();

        }

        return al;

    }

    public ArrayList<String> getMyGroupsN(String _file_url) {

        ArrayList<String> al = new ArrayList<String>();
        String groups_temp = "#none#";
        groups_temp = mPrefs.getString("groups", mGroups_temp);
        String[] temp_str = groups_temp.split("::");
        int i = temp_str.length - 1;

        try {

            while (i > 0) {

                al.add(temp_str[i]);
                System.out.println(temp_str[i]);
                i--;
            }

        } catch (Exception e) {
            e.printStackTrace();

        }

        return al;

    }

    public void getDeleteImportedGroups(Context context) {

        try {
            SharedPreferences.Editor ed = mPrefs.edit();
            Set<String> set = new HashSet<String>(new ArrayList<String>());
            ed.putStringSet("FAV_GROUPS", set);
            ed.commit();

        } catch (Exception e) { // TODO Auto-generated catch block
            e.printStackTrace();
            Intent intent = new Intent(context.getApplicationContext(),
                    ErrorActivity.class);
            intent.putExtra("error", "System Error!");
            context.startActivity(intent);

        }

    }

    public void getDeleteMyGroups(Context context) {

        try {
            SharedPreferences.Editor ed = mPrefs.edit();
            ed.putString("groups", mGroups_temp);
            ed.commit();

        } catch (Exception e) { // TODO Auto-generated catch block
            e.printStackTrace();
            Intent intent = new Intent(context.getApplicationContext(),
                    ErrorActivity.class);
            intent.putExtra("error", "System Error!");
            context.startActivity(intent);

        }

    }

    public void setGroups(ArrayList<String> al_) {
        this.mGroups = al_;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public String getXML() {
        return this.xml_string;
    }

    public ArrayList<String> getmGroups() {
        return mGroups;
    }

    public ArrayList<String> getmGroups(Context c) {
        return mGroups;
    }

    public void setmGroups(ArrayList<String> mGroups) {
        this.mGroups = mGroups;
    }

    public void addGroup(String group_uci) {
        if (!mGroups.contains(group_uci))
            mGroups.add(group_uci);
        saveNewGroup(group_uci, "groups", mGroups);
    }

    public void addGroup(String group_uci, boolean my_groups) {
        if (!mGroups.contains(group_uci))
            mGroups.add(group_uci);

        if (my_groups) {
            chitchat_config_0 = new ChitchatConfig("groups", "mygroups");
            saveNewGroup(group_uci, "mygroups", mGroups);
        } else {
            chitchat_config_0 = new ChitchatConfig("groups", "groups");
            saveNewGroup(group_uci, "groups", mGroups);
        }

    }

    public void addGroupParam(String param)
    {
        if (!mGroups.contains(param))
            mGroups.add(param);
        saveNewGroup(param, "params", mGroups);
    }

    public void removeGroup(String group_uci, boolean ismygroup) {
        if (!mGroups.contains(group_uci))
            mGroups.remove(group_uci);
        if (ismygroup) {
            chitchat_config_0 = new ChitchatConfig("groups", "mygroups");
            saveNewGroup(group_uci, "mygroups", mGroups);

        } else {
            chitchat_config_0 = new ChitchatConfig("groups", "groups");
            saveNewGroup(group_uci, "groups", mGroups);
        }

    }


    synchronized public void updateGroupPW(String group_name, String pw, boolean my_groups) {

        String group_uci = "";
        ArrayList<String> _ar_ = mGroups;

        for (String tmp : _ar_) {
            System.out.println(group_name + "______________________________________________________group_name" + tmp);
            if (tmp.contains(":::" + group_name)) {
                _ar_.remove(tmp);
                tmp = tmp + "~::~" + pw;
                group_uci = tmp;
                _ar_.add(group_uci);
                addGroup(group_uci, my_groups);
                System.out.println("______________________________________________________value" + group_uci);
                break;
            } else {
                // Register the group
            }

        }
/*
        if (my_groups) {
            chitchat_config_0 = new ChitchatConfig("groups", "mygroups");
            saveNewGroup(group_uci, "mygroups", _ar_);
        } else {
            chitchat_config_0 = new ChitchatConfig("groups", "groups");
            saveNewGroup(group_uci, "groups", _ar_);
        }*/

    }


    public void addGroup(String pr_uci_string, int index) {
        String temp = pr_uci_string;
        String[] temp_ = temp.split("~::~");
        for (int i = 1; i < temp_.length; i++) {
            if (!mGroups.contains(temp_[i]))
                mGroups.add(temp_[i]);
        }


    }

    public void clearGroups() {
        chitchat_config_0.deleteProperties();
    }


    public String getGroupPassword(String _group_name_) {
        String _password_ = "#";
        for (String str : mGroups) {
            if (str.contains("~::~")) {
                String[] _mtemp_str = str.split("~::~");
                if (_mtemp_str[0].contains(_group_name_) && _mtemp_str.length > 3)
                    _password_ = _mtemp_str[3];
            }
        }

        return _password_;

    }

}
