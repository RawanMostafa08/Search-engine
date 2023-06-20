import com.mongodb.client.*;
import com.mongodb.client.result.UpdateResult;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Crawler implements Runnable {
	public ArrayList<String> URLs;
	private static final int MAX_PAGES_TO_CRAWL = 2000;
	private static final int TIMEOUT_MS = 5000;
	private ConcurrentLinkedQueue<Document> MyDocs = new ConcurrentLinkedQueue<Document>();
	private ConcurrentLinkedQueue<String> visitedlinks = new ConcurrentLinkedQueue<String>();

	private static TreeMap<String,String> strings=new TreeMap<>();
	Thread arr[];
	private Thread t1;
	private String seed;
	private int id;
	private int threadnumber;
	String []urls;
	public MongoClient mongoClient;
	public MongoDatabase database;
	public MongoCollection<org.bson.Document> collection;
	public Crawler(String []urls,int size, int i,int tnum) {
		// TODO Auto-generated constructor stub
		mongoClient = MongoClients.create("mongodb+srv://nouraymanh:3tAM0HS52jDjQ5wQ@cluster0.skdggct.mongodb.net");

		database = mongoClient.getDatabase("search-engine-db");

		collection = database.getCollection("crawler");

		URLs=new ArrayList<>();

		getURLs();

		id=i;
		this.urls=urls;
		System.out.println("start crawler");
		threadnumber=tnum;
		arr=new Thread[tnum];
		multithreading();
	}

	private void multithreading() {
		for(int i=0; i<threadnumber;i++) {
			arr[i] = new Thread(this);

			arr[i].setName(Integer.toString(i));
		}
		for(int i=0; i<threadnumber;i++) {
			arr[i].start();
		}
		for(int i=0; i<threadnumber;i++) {
			try {
				arr[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		mongoClient.close();
	}

	private static int pagesCrawled = 0;

	private synchronized static void incrementPagesCrawled() {
		pagesCrawled++;
	}

	private void  crawl(int level, String url) throws MalformedURLException {
		if (level > 3 || pagesCrawled >= MAX_PAGES_TO_CRAWL) {
			return;
		}
		Document doc = getdoc(url);
		if (doc != null) {
			if (!visitedlinks.contains(url) && !URLs.contains(url)) {
				visitedlinks.add(url);
				insertDB(url,doc);
				incrementPagesCrawled();
			}
			int unvisitedLinks = 0;
			for (Element link : doc.select("a[href]")) {
				String nextlink = link.absUrl("href");
				if (!visitedlinks.contains(nextlink)&& visitedlinks.size() < MAX_PAGES_TO_CRAWL) {
					unvisitedLinks++;
					crawl(level + 1, nextlink);
				}
			}
			if (unvisitedLinks == 0) {
				System.out.println("No more unvisited links, stopping crawl.");
				return;
			}
		}
	}

	private static final int MAX_RETRIES = 3;
	private static final int RETRY_DELAY_MS = 1000;


	private Document getdoc(String url) throws MalformedURLException {
		URL urlObj= new URL(url);
		if(RobotChecker.robotSafe(urlObj)) {
			Connection con = Jsoup.connect(url).timeout(TIMEOUT_MS);
			int retries = 0;
			while (retries < MAX_RETRIES) {
				try {

					Document doc = con.get();
					if (con.response().statusCode() == 200) {
						String cmpctString = compactString(doc.body().text());
						if (checkDocument(cmpctString)) {
							strings.put(url, cmpctString);
							return doc;
						} else {
							System.out.println("I found a duplicate document");
							return null;
						}
					} else {
						return null;
					}
				} catch (UnknownHostException e) {
					System.err.println("Could not resolve host: " + url);
					return null;
				} catch (IOException e) {
					System.err.println("IOException while fetching " + url + ": " + e.getMessage());
					retries++;
					try {
						Thread.sleep(RETRY_DELAY_MS);
					} catch (InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
				}
			}
			System.err.println("Max retries exceeded, giving up on " + url);
		}
		return null;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		try {
			crawl(1,urls[Integer.parseInt(Thread.currentThread().getName())]);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	public Thread gett() {
		return t1;
	}

	public String compactString(String bodyText){
		String[] words = bodyText.split(" ");
		String cmpctString="";
		int step = (words.length<10)?1:words.length/10;
		for (int i=0; i< words.length;i+=step) {
			cmpctString+=words[i];
		}
		return cmpctString;
	}
	public boolean checkDocument(String cmpctString){
		for (Map.Entry<String, String> entry : strings.entrySet()) {
			if(entry.getValue().equals(cmpctString))
				return false;
		}
		System.out.println(cmpctString);
		return true;
	}
	public void insertDB(String url,Document doc) {
		List<String> childLinks=new ArrayList<>();
		for (Element link : doc.select("a[href]")) {
			childLinks.add(link.absUrl("href"));
		}
		collection.insertOne(new org.bson.Document("url",url).append("links",childLinks));
	}

	public void getURLs() {
		MongoCursor<org.bson.Document> cursor = collection.find().iterator();
		try {
			while (cursor.hasNext()) {
				org.bson.Document document = cursor.next();
				// Do something with the retrieved document
                System.out.println(document.get("url"));
				URLs.add((String)(document.get("url")));
			}
		} finally {
			cursor.close();
		}
	}

}
