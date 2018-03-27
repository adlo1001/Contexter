package se.sensiblethings.app.chitchato.context;

public class UserContext implements MyContext {

    private String user_acc = "0.0", limunity = "0.0", device_ort = "0.0",
            pressure = "0", temperature = "-1000", sound_level = "0.0";
    private String user_location = "Address not detected";
    private String _mlocation_address = "Address not detected";


    private float gravity_x = 9.81f, gravity_y = 9.81f, gravity_z = 9.81f, alpha = 0.8f;
    private float acce_x, acce_y, acce_z;

    public UserContext(String id) {

    }

    @Override
    public String getLocation() {
        // TODO Auto-generated method stub
        return user_location;
    }

    @Override
    public void setLocation(double long_, double lat_) {
        this.user_location = long_ + "," + lat_;

    }

    @Override
    public String getLimunosity() {
        // TODO Auto-generated method stub
        return limunity;
    }

    @Override
    public void setLimunosity(float l) {
        // TODO Auto-generated method stub
        this.limunity = l + "";

    }

    @Override
    public String getAcce() {
        // TODO Auto-generated method stub
        return user_acc;
    }

    @Override
    public void setAcce(float x, float y, float z) {


        gravity_x = alpha * gravity_x + (1 - alpha) * x;
        gravity_y = alpha * gravity_y + (1 - alpha) * y;
        gravity_z = alpha * gravity_z + (1 - alpha) * z;

        //this.user_acc = x + "";
        this.acce_x = x - gravity_x;
        this.acce_y = y - gravity_y;
        this.acce_z = z - gravity_z;

        user_acc = String.valueOf((acce_x + acce_y + acce_z) / 3);


    }

    @Override
    public String getOrientation() {
        // TODO Auto-generated method stub
        return device_ort;
    }

    @Override
    public void setOrientation(float yaw, float pitch, float roll) {
        // TODO Auto-generated method stub

        this.device_ort = yaw + "," + pitch + "," + roll;
    }

    @Override
    public String getPressure() {
        // TODO Auto-generated method stub
        return pressure;
    }

    @Override
    public void setPressure(float p) {
        // TODO Auto-generated method stub
        this.pressure = p + "";

    }

    @Override
    public String getTemprature() {
        // TODO Auto-generated method stub
        return temperature;
    }

    @Override
    public void setTemprature(float temp) {
        // TODO Auto-generated method stub
        this.temperature = temp + "";
    }

    @Override
    public long getTime() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setTime(long time) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getSoundLevel() {
        // TODO Auto-generated method stub
        return this.sound_level;
    }

    @Override
    public void setSoundLevel(float l) {
        this.sound_level = l + "";

    }

    @Override
    public String getAddress() {
        // TODO Auto-generated method stub
        return this._mlocation_address;
    }

    @Override
    public void setAddress(String address) {
        this._mlocation_address = address;

    }

}
