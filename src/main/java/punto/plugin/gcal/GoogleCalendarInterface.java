package punto.plugin.gcal;




import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class GoogleCalendarInterface {
	
//	public static void main(String args[]) throws Exception{
//		buildService("266739900252-cpam8e781hd331r6dun25rbp7p7ovdus@developer.gserviceaccount.com",true, new File("C:\\Users\\MattUpstairs\\git\\perthparkourwebapp\\PerthParkourWeb\\conf\\oauth.p12"));
//		listEvents("gf17vivdso64h4hrd7acke5vkc@group.calendar.google.com");
//	}
	
	static Calendar client;
	static String calId;

	public static Calendar buildService(String accId, boolean readOnly, File p12, String calId) throws Exception{
	    
		GoogleCredential credentials = new GoogleCredential.Builder().setTransport(GoogleNetHttpTransport.newTrustedTransport())
				  .setJsonFactory(new JacksonFactory())
				  
				  .setServiceAccountId(accId)
				  .setServiceAccountScopes(Arrays.asList("https://www.googleapis.com/auth/calendar.readonly"))
				  .setServiceAccountPrivateKeyFromP12File(p12)
				  
				.build();
				client = new Calendar.Builder(
                        GoogleNetHttpTransport.newTrustedTransport(),
                        new JacksonFactory(),
                        credentials
                ).setApplicationName("API Project").build();
				GoogleCalendarInterface.calId=calId;
				return client;
	  }
	public static Calendar getClient(){
		return client;
	}

    static List<Event> events = null;

    static Date time = new Date();

    public static void getEvents() throws IOException{
        String pageToken=null;
        time = new Date();
        events =
                getClient().events().list(calId)
                        .setPageToken(pageToken)
                        .setMaxResults(10)
                        .setTimeMin(new DateTime(new Date()))
                        .setSingleEvents(true)
                        .setOrderBy("startTime")
                        .execute()
                        .getItems();

    }
    static long interval = 5L;
    public static long timeDiff(Date d1, Date d2){
        long diff = d1.getTime()-d2.getTime();
        if(diff<0)diff=0-diff;

        return diff;
    }
	public static List<Event> listEvents() throws IOException{
        Date now = new Date();

        if(events==null || timeDiff(now, time)>interval){
            getEvents();
        }
//		for(Event e: events){
//			if(e.getStart()!=null)
//				Debug.test(e.getStart().toPrettyString());
//		}
		return events;
	}
}
