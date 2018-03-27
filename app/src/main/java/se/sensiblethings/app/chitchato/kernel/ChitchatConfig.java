package se.sensiblethings.app.chitchato.kernel;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import se.sensiblethings.app.chitchato.extras.Customizations;
import se.sensiblethings.interfacelayer.SensibleThingsNode;

/**
 * Created by user on 10/24/2016.
 */
public class ChitchatConfig {
    //
    public static boolean PRIVATE = false;
    public static boolean CONTEXT_MODE = true;
    private Properties _config_table;
    private String _config_name;
    private TreeMap<String, String> hash_map;
    private TreeMap<String, SensibleThingsNode> hash_map_nodes;
    protected int PRI_PREFFERED_MAX_THREAD_LINES = 10;
    protected int PUB_PREFFERED_MAX_THREAD_LINES = 10;
    protected long last_modified= 0;

    ChitchatConfig() {
    }

    public ChitchatConfig(String file_name) {

        this._config_name = file_name + ".config";
        _config_table = new Properties();
        hash_map = new TreeMap<String, String>();

    }

    public ChitchatConfig(String file_name, String ext) {
        this._config_name = file_name + "." + ext;
        _config_table = new Properties();
        hash_map = new TreeMap<String, String>();

    }



    public void saveProperties() {
        try {

            setAppFolder(this._config_name);
            FileOutputStream fos = new FileOutputStream(getAppFolder("") + "" + this._config_name);
            File file= new File(getAppFolder("") + "" + this._config_name);
            last_modified = file.lastModified();
            this._config_table.store(fos, _config_name);

            fos.close();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void deleteProperties() {
        try {

            FileOutputStream fos = new FileOutputStream(getAppFolder("") + "" + this._config_name);
            this._config_table.clear();
            fos.close();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public void deleteProperties(String _file_name_, String ext) {
        try {
            FileOutputStream fos = new FileOutputStream(getAppFolder("") + "" + _file_name_ + "." + ext);
            this._config_table.clear();
            fos.close();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    // private
    public void saveArrayList(String _file_name_, String ext, ArrayList<String> _array_list_) {

        int i = _config_table.size() + _array_list_.size();

        //if (i > Constants.PREFFERED_MAX_THREAD_LINES + 100) {
        //_config_table.clear();
        //} else {
        for (String tmp : _array_list_)
            _config_table.put(tmp, _file_name_);

        //}


        saveProperties();
    }


    //public
    public synchronized void saveArrayList(Context context, String _file_name_, String ext, ArrayList<String> _array_list_) {
        Customizations custom = new Customizations(context, -1);
        PRI_PREFFERED_MAX_THREAD_LINES = custom.getPri_message_thread_len();
        PUB_PREFFERED_MAX_THREAD_LINES = custom.getPub_message_thread_len();
        int i = _config_table.size() + _array_list_.size();
        if (i > PUB_PREFFERED_MAX_THREAD_LINES) {
            _config_table.clear();
        } else {
            for (String tmp : _array_list_) {
                if (_file_name_.contains("@")) {

                    _file_name_ = _file_name_.replace("@", ":");
                    _file_name_ = _file_name_.split(":")[0];
                }
                _config_table.put(tmp, _file_name_);
            }

        }
        saveProperties();
    }

    // Adv save
    public synchronized void saveArrayList(Context context, String _file_name_, ArrayList<String> _array_list_) {

        Customizations custom = new Customizations(context, -1);
        PRI_PREFFERED_MAX_THREAD_LINES = custom.getPri_message_thread_len();
        PUB_PREFFERED_MAX_THREAD_LINES = custom.getPub_message_thread_len();
        int i = _config_table.size() + _array_list_.size();
        if (i > PUB_PREFFERED_MAX_THREAD_LINES) {
            _config_table.clear();
        } else {
            for (String tmp : _array_list_) {
                if (tmp.contains(":")) {
                    _config_table.put(tmp.split(":")[0], tmp.split(":")[1]);
                }
            }

        }
        saveProperties();
    }


    public void loadProperties() throws IOException {
        FileInputStream fis;
        try {
            fis = new FileInputStream(getAppFolder("") + "" + _config_name);
            _config_table.load(fis);
            fis.close();
        } catch (FileNotFoundException e) {
            saveProperties();

            // e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void loadPropertiesV2() {

        FileInputStream fis;
        try {
            fis = new FileInputStream(getAppFolder("") + "" + _config_name);
            _config_table.load(fis);
            fis.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public TreeMap<String, String> getTreeMapCopy() {
        try {
            loadProperties();

            Set<Object> keys = _config_table.keySet();
            TreeSet<Object> sortedKeys = new TreeSet<Object>(keys);
            for (Object key : sortedKeys) {
                hash_map.put((String) key,
                        _config_table.getProperty((String) key));

            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return hash_map;
    }

    public TreeMap<String, SensibleThingsNode> getTreeMapCopyNodes() {
        Set<Object> keys = _config_table.keySet();
        TreeSet<Object> sortedKeys = new TreeSet<Object>(keys);
        SensibleThingsNode node = null;
        for (Object key : sortedKeys) {

            // node = _config_table.getProperty((String) key);
            hash_map_nodes.put((String) key, node);
        }
        return hash_map_nodes;
    }

    public Set<Object> getPropertiesList() {
        Set<Object> keys = _config_table.keySet();
        return keys;
    }

    public ArrayList<String> getPropertiesArrayList() {
        ArrayList<String> list_of_keys = new ArrayList<String>();
        Set<Object> keys = _config_table.keySet();
        TreeSet<Object> sortedKeys = new TreeSet<Object>(keys);
        for (Object key : sortedKeys) {
            list_of_keys.add((String) key);

        }
        return list_of_keys;
    }

    public String getProperty(String key) {
        String value = (String) _config_table.get(key);
        return value;
    }

    public String getPropertyV2(String key) {
        String value = (String) _config_table.getProperty((String) key);
        return value;
    }

    public String getKey(String str) {

        String key = "", temp = "";
        Set<Object> keys = _config_table.keySet();
        TreeSet<Object> sortedKeys = new TreeSet<Object>(keys);
        for (Object obj : sortedKeys) {
            temp = (String) (_config_table.get(obj));
            if (temp.contains(":" + str + ":")) {
                key = (String) obj;
                break;
            }

        }
        return key;

    }

    public String getValue(String str) {
        String key;
        String value = "", temp = "";
        Set<Object> keys = _config_table.keySet();
        TreeSet<Object> sortedKeys = new TreeSet<Object>(keys);
        for (Object obj : sortedKeys) {
            temp = (String) (_config_table.get(obj));
            if (temp.contains(":" + str + ":")) {
                value = temp;
                break;
            }
        }
        return value;

    }

    public SensibleThingsNode getNode(String uci) {
        String key;
        SensibleThingsNode node;
        String temp = "";
        Set<Object> keys = _config_table.keySet();
        TreeSet<Object> sortedKeys = new TreeSet<Object>(keys);
        node = (SensibleThingsNode) (_config_table.get(uci));
        return node;

    }

    public void clearProperties() {
        _config_table.clear();
        saveProperties();

    }

    public void addPair(String uci, String value) throws IOException {
        loadProperties();

        Properties new_tr_mp = (Properties) _config_table.clone();
        _config_table.clear();
        _config_table.put(uci, value);
        _config_table.putAll(new_tr_mp);

        saveProperties();
    }

    public void addPair(String uci, SensibleThingsNode node) throws IOException {
        loadProperties();

        Properties new_tr_mp = (Properties) _config_table.clone();
        _config_table.clear();
        _config_table.put(uci, node);
        _config_table.putAll(new_tr_mp);

        saveProperties();
    }

    public void removePair(String uci) {
        _config_table.remove(uci);
        saveProperties();
    }

    //Get Path for Home Folder
    public static String getAppFolder(String file_name) {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/local/";
    }

    // Set Path for Home Folder
    public void setAppFolder(String file_name) {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/local");
        if (!file.exists()) {
            new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/local").mkdir();
            //System.out.println("Chitchato Home Directory Created.");
        } else {

        }
    }

    public static boolean DeleteAppFolder() {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato");
        return file.delete();
    }

    public long getLast_modified() {
        return last_modified;
    }

    public void setLast_modified(long last_modified) {
        this.last_modified = last_modified;
    }
}
