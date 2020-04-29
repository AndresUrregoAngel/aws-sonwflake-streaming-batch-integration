package lambdaroot;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.google.gson.*;
import java.sql.*;
import java.util.*;


//Handler value: example.HandlerWeatherData
public class snowflakehandler implements RequestHandler<Map<String,ArrayList<snowSchema>>, String>{
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public String handleRequest(Map<String,ArrayList<snowSchema>> event, Context context)
    {
        LambdaLogger logger = context.getLogger();
        String response = new String("200 OK");
        // log execution details
        logger.log("Calling snowflake method");
        snowflakedb sf = new snowflakedb();

        ArrayList<snowSchema> content = event.get("content");
        //content.forEach(item->System.out.println(item));

        for (snowSchema item:content) {
            try{
                sf.insertNewRecords(item);
            }catch(SQLException error){logger.log("The insertion process has failed due to : "+error);}
        }



        return response;
    }

}
