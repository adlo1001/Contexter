package se.sensiblethings.app.chitchato.kernel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.ImageView;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import se.sensiblethings.app.chitchato.activities.ErrorActivity;
import se.sensiblethings.app.chitchato.kernel.RESTHandler.RequestMethod;

public class Peers {

    protected ArrayList<String> mPeers = null;
    protected String response = null;
    protected Context context_;
    protected FileInputStream file_input_stream = null;
    protected FileOutputStream file_output_stream = null;
    protected SharedPreferences mPrefs;
    protected String mPeers_temp = "#none#";
    protected String file_url = "";
    protected Groups groups;
    public ProfileImage profileImage;
    ChitchatConfig chitchat_config_0;

    public Peers() {

    }

    public Peers(Context context, String groupName) {
        this.context_ = context;
        // list of uci
        chitchat_config_0 = new ChitchatConfig(groupName);
        try {
            chitchat_config_0.loadProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mPeers = chitchat_config_0.getPropertiesArrayList();

    }




    public Peers(Context context, String groupName, String ext) {
        this.context_ = context;
        // list of uci
        chitchat_config_0 = new ChitchatConfig(groupName, ext);
        try {
            chitchat_config_0.loadProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mPeers = chitchat_config_0.getPropertiesArrayList();
    }

    public Peers(Context context) {
        this.context_ = context;
        groups = new Groups();
        profileImage = new ProfileImage(context, "");
        this.file_url = groups.file_url;
        mPrefs = context_.getSharedPreferences("peers_cache", 0);
        mPeers_temp = mPrefs.getString("peers", mPeers_temp);
    }

    public ArrayList getPeerList(String xmldoc) {
        // HashMap items_;//= new HashMap();
        ArrayList al = new ArrayList<String>();
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
                    al.add(el_.getFirstChild().getNodeValue());
                    Log.i("Peer " + ii, el_.getFirstChild().getNodeValue());
                    // System.out.print(ell_.getFirstChild().getNodeValue());

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return al;
    }

    public synchronized void updatePeersOnLocalNode(Context context,
                                                    String group_name, String peers) {

        try {

            SharedPreferences.Editor ed = mPrefs.edit();
            ed.putString(group_name, peers);

            ed.commit();

            // Log.e("saved to" + file, message+ "\n");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Intent intent = new Intent(context.getApplicationContext(),
                    ErrorActivity.class);
            intent.putExtra("error", "System Error!");
            context.startActivity(intent);

        }
        notifyAll();
    }

    public synchronized ArrayList<String> retrievePeersFromLocalNode(
            String group_name) {
        ArrayList<String> al = new ArrayList<String>();
        String peers_temp = "#none#";
        String[] temp_1, temp_2;
        try {

            peers_temp = mPrefs.getString("", peers_temp);
            // Log.i("Files Directory:>>",
            // context_.getFilesDir().getCanonicalPath());
            // context_.getFilesDir().createTempFile("groups", "log");
            temp_1 = peers_temp.split("~~");
            if (temp_1 != null)
                for (String t : temp_1) {
                    temp_2 = t.split("::");
                    if (temp_2.length > 1)
                        al.add(temp_2[2]);
                    else
                        ;// al.add("#none#");

                }
            else
                al.add("None!");

            // Log.e("ReadFrom" + file, data + " ");

        } catch (Exception e) {
            e.printStackTrace();
            Intent intent = new Intent(this.context_, ErrorActivity.class);
            intent.putExtra("error", "System Error!");
            context_.startActivity(intent);
        }
        notifyAll();
        return al;
    }

    public ArrayList<String> retriveGroupsFromBT(Context context,
                                                 String groupName) {
        ArrayList<String> al_ = new ArrayList<String>();
        RESTHandler _handler = new RESTHandler(
                "http://185.102.215.188:8080/ChitchatoWBS/webresources/_mentityclasses.peerholder");
        TreeMap treemap = _handler.getPeersByGroup(groupName);
        Set set_ = treemap.entrySet();

        Map.Entry node;
        Iterator itr_ = set_.iterator();
        while (itr_.hasNext()) {
            node = (Map.Entry) itr_.next();
            if (node.getKey().toString().contains("fullName"))
                al_.add(node.getValue().toString());
        }

        return al_;
    }

    public void updatePeers(Context context, String data) {
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

    public ArrayList<String> searchPeersOnCache(Context context,
                                                String keyword, String interest, int age, String group_leader) {
        ArrayList<String> al = new ArrayList<String>();
        ArrayList<String> temp_al = this.retrievePeersFromLocalNode(keyword);
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
        ArrayList<String> temp_al = this.retriveGroupsFromBT(context, keyword);
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

    public ArrayList<String> getPeers() {

        return mPeers;
    }

    public class ProfileImage {
        private ImageView profile_image;
        private String file_path = "/DCIM/Camera";
        private Context context;

        public ProfileImage(Context c, String str) {
            this.context = c;
            // this.profile_image;
            this.file_path = str;

        }

        public ImageView getProfilePic() {

            return this.profile_image;

        }
    }

    public ArrayList<String> getmPeers() {
        return mPeers;
    }

    public void setmPeers(ArrayList<String> mPeers) {
        this.mPeers = mPeers;
    }

    public void addPeer(String pr_uci) {
        if (!mPeers.contains(pr_uci))
            mPeers.add(pr_uci);
    }

    public void addPeer(String pr_uci_string, int index) {
        String temp = pr_uci_string;
        String[] temp_ = temp.split("~::~");
        if(index==0)
        for (int i = 1; i < temp_.length; i++) {
            if (!mPeers.contains(temp_[i]))
                mPeers.add(temp_[i]);
        }else if(index==1)
        {
            mPeers.add(pr_uci_string);
        }

    }
    public void addPublicBS(String pr_uci_string, int index) {
        String temp = pr_uci_string;
        String[] temp_ = temp.split("~::~");
        if(index==0)
            for (int i = 1; i < temp_.length; i++) {
                if (!mPeers.contains(temp_[i]))
                    mPeers.add(temp_[i]);
            }else if(index==1)
        {
            mPeers.add(pr_uci_string);
        }

    }


    public void clearPeers() {
        chitchat_config_0.deleteProperties();
    }
    public void savePeers(String _fname_, String ext, ArrayList<String> ar)
    {
        chitchat_config_0.saveArrayList(_fname_,ext, ar);
    }

}

