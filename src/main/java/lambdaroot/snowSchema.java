package lambdaroot;

public class snowSchema {

    public final long sensorId;
    public final long currentTemperature;
    public final String status;


    public snowSchema(){
        sensorId =0;
        currentTemperature=0;
        status ="OK";

    }


    @Override
    public String toString(){
        return "{ \"sensor\" :" + sensorId +
                ", \"currentTemperature\" : "+currentTemperature+
                ", \"status\" :"+status+"}";
    }

}
