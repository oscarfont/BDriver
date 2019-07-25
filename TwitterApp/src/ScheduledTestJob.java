import java.util.Date;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class ScheduledTestJob implements Job {
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		String consumer_key =  "DDG2KsFSMaiza1w1QkRGySR8U";
		String consumer_secret = "uX7oQqJoR8fvaAmNYVgV6tL4TddgmnL4u7l75UKItdp48Opom6";
		String access_token = "935448704802226177-DnVBSxS98pImTztRgd94hyOUGpH6hYT";
		String access_token_secret = "xv1nKCSayygzv6ucZJzdhHNRxgDsI04EDi84xGFwkjlAw";
		
		// Add 
		Client client = ClientBuilder.newClient();
		WebTarget targetGet = client.target("http://localhost:15000").path("/stations/get/statistics");
		
		String info = targetGet.request(
		MediaType.APPLICATION_JSON_TYPE).get(new GenericType<String>() {});
		
		TwitterFactory factory = new TwitterFactory();
		Twitter twitter = factory.getInstance();
		twitter.setOAuthConsumer(consumer_key, consumer_secret);
		twitter.setOAuthAccessToken(new AccessToken(access_token, access_token_secret));
		try {
			// Dividimos las estadísticas en dos tweets diferentes
			String info1 = info.substring(0, info.indexOf("#BicingNews"));
			String info2 = info.substring(info.indexOf("#BicingNews"), info.length());
			Status status;
			for(int i = 0; i < 2; i++){
				if(i == 0){ status = twitter.updateStatus(info1); } 
				else { status = twitter.updateStatus(info2); }
			}
		} catch (TwitterException e) {
			System.out.println(e.getMessage());
		}
		
		System.out.println("Executing ScheduledTestJob at " + new Date());
	}
}