import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.awt.desktop.SystemEventListener;
import java.net.MalformedURLException;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class main {
	public static void main(String[]args) throws MalformedURLException {
		java.util.logging.Logger.getLogger("org.mongodb.driver").setLevel(java.util.logging.Level.WARNING);
		String[] urls = {"https://www.nytimes.com/",
				"https://www.bbc.com/",
				"https://www.cnn.com/",
				"https://www.theguardian.com/",
				"https://www.npr.org/",
				"https://www.reuters.com/",
				"https://www.aljazeera.com/",
				"https://www.bloomberg.com/",
				"https://www.wsj.com/",
				"https://www.ft.com/",
				"https://www.usatoday.com/",
				"https://www.latimes.com/",
				"https://www.chicagotribune.com/",
				"https://www.washingtonpost.com/",
				"https://www.huffpost.com/",
				"https://www.nationalgeographic.com/",
				"https://www.smithsonianmag.com/",
				"https://www.scientificamerican.com/",
				"https://www.newscientist.com/",
				"https://www.sciencedaily.com/",
				"https://www.nasa.gov/",
				"https://www.nature.com/",
				"https://www.theatlantic.com/",
				"https://www.vox.com/",
				"https://www.economist.com/",
				"https://www.hbr.org/",
				"https://www.businessinsider.com/",
				"https://www.forbes.com/",
				"https://www.entrepreneur.com/",
				"https://www.inc.com/",
				"https://www.fastcompany.com/",
				"https://www.wired.com/",
				"https://www.theverge.com/",
				"https://www.techcrunch.com/",
				"https://www.engadget.com/",
				"https://www.tomsguide.com/",
				"https://www.gamespot.com/",
				"https://www.polygon.com/",
				"https://www.ign.com/",
				"https://www.giantbomb.com/",
				"https://www.imdb.com/",
				"https://www.rottentomatoes.com/",
				"https://www.metacritic.com/",
				"https://www.allmusic.com/",
				"https://www.billboard.com/",
				"https://www.nme.com/",
				"https://www.thrillist.com/",
				"https://www.eater.com/",
				"https://www.foodandwine.com/",
				"https://www.epicurious.com/",
				"https://www.cookinglight.com/",
				"https://www.foodnetwork.com/",
				"https://www.allrecipes.com/",
				"https://www.buzzfeed.com/",
				"https://www.vox.com/",
				"https://www.pbs.org/",
				"https://www.history.com/",
				"https://www.biography.com/",
				"https://www.nationalreview.com/",
				"https://www.theamericanconservative.com/",
				"https://www.politico.com/",
				"https://www.realclearpolitics.com/",
				"https://www.fivethirtyeight.com/",
				"https://www.thedailybeast.com/",
				"https://www.salon.com/",
				"https://www.slate.com/",
				"https://www.motherjones.com/",
				"https://www.buzzfeednews.com/",
				"https://www.propublica.org/",
				"https://www.nbcnews.com/",
				"https://www.cbsnews.com/",
				"https://www.abcnews.go.com/",
				"https://www.foxnews.com/",
				"https://www.nba.com/",
				"https://www.nfl.com/",
				"https://www.mlb.com/",
				"https://www.nhl.com/",
				"https://www.si.com/",
				"https://www.espn.com/",
				"https://www.nbcsports.com/",
				"https://www.olympic.org/",
				"https://www.fifa.com/",
				"https://www.uefa.com/",
				"https://www.pgatour.com/",
				"https://www.nbcolympics.com/",
				"https://www.usatoday.com/sports/",
				"https://www.si.com/nba/",
				"https://www.menshealth.com/",
				"https://www.self.com/",
				"https://www.womenshealthmag.com/",
				"https://www.runnersworld.com/",
				"https://www.outsideonline.com/",
				"https://www.backpacker.com/",
				"https://www.yogajournal.com/",
				"https://www.shape.com/",
				"https://www.bicycling.com/",
				"https://www.cnn.com/travel/",
				"https://www.lonelyplanet.com/",
				"https://www.afar.com/",
				"https://www.cntraveler.com/",
				"https://www.tripadvisor.com/",
				"https://www.booking.com/",
				"https://www.airbnb.com/",
				"https://www.nationalparkstraveler.org/",
				"https://www.fodors.com/",
				"https://www.lonelyplanet.com/",
				"https://www.roughguides.com/",
				"https://www.frommers.com/",
				"https://www.businessinsider.com/travel",
				"https://www.cnbc.com/travel/",
				"https://www.travelandleisure.com/",
				"https://www.nytimes.com/travel/",
				"https://www.architecturaldigest.com/travel/",
				"https://www.theguardian.com/travel/",
				"https://www.nationalgeographic.com/travel/",
				"https://www.vanityfair.com/style/",
				"https://www.elle.com/fashion/",
				"https://www.harpersbazaar.com/fashion/",
				"https://www.vogue.com/fashion/",
				"https://www.gq.com/style/",
				"https://www.esquire.com/style/",
				"https://www.thecut.com/fashion/",
				"https://www.businessoffashion.com/",
				"https://www.highsnobiety.com/",
				"https://www.fashionista.com/",
				"https://www.racked.com/",
				"https://www.refinery29.com/",
				"https://www.apartmenttherapy.com/",
				"https://www.architecturaldigest.com/",
				"https://www.dwell.com/",
				"https://www.housebeautiful.com/",
				"https://www.domino.com/",
				"https://www.thespruce.com/",
				"https://www.goodhousekeeping.com/",
				"https://www.countryliving.com/",
				"https://www.food52.com/",
				"https://www.yummly.com/",
				"https://www.thespruceeats.com/",
				"https://www.simplyrecipes.com/",
				"https://www.bonappetit.com/",
				"https://www.delish.com/",
				"https://www.myrecipes.com/"};
//		Crawler c=new Crawler(urls,urls.length,1,15);
		Indexer I=new Indexer();

//		MongoClient mongoClient = MongoClients.create("mongodb+srv://nouraymanh:3tAM0HS52jDjQ5wQ@cluster0.skdggct.mongodb.net");
//
//		MongoDatabase database = mongoClient.getDatabase("search-engine-db");
//		MongoCollection<Document> collection = database.getCollection("Test");
		//collection.createIndex(Indexes.ascending("key"), new IndexOptions().unique(true));
//		try {
//			collection.insertOne(new Document("url","daniel"));
//			System.out.println("Document inserted successfully!");
//		} catch (MongoWriteException e) {
//			Document filter = new Document("url", "daniel");
//
//
//			//do logic
//			// Define the update operation
//			Document update = new Document("$push", new Document("dl3 daniel", "kda at3ml update tmm tany"));
//			// Update the document
//			UpdateResult result = collection.updateOne(filter, update);
//
//
//
//			System.err.println("Error: " + e.getMessage());
//		}
//		org.bson.Document filter=new org.bson.Document("url","daniel");
//
//		boolean documentExists = collection.find(filter).iterator().hasNext();
//
//		if (documentExists) {
//			System.out.println("Document found!");
//			org.bson.Document doc=collection.find(filter).first();
//			System.out.println(doc);
//		} else {
//			System.out.println("Document not found.");
//		}


//		collection.insertOne(new Document("url","https://edition.cnn.com/"));
//		collection.insertOne(new Document("url","https://www.bbc.co.uk"));
//		List<String > daniel=new ArrayList<>();
//		daniel.add("dan");
//		daniel.add("daniola");
//		daniel.add("danielo");
//		daniel.add("dandon");
//		daniel.add("dananeno");
//		collection.insertOne(new Document("url","daniel").append("dl3 daniel",daniel));
//
//		Document filter = new Document("url", "daniel");
//
//		// Define the update operation
//		Document update = new Document("$push", new Document("dl3 daniel", "kda at3ml update tmm"));
//
////		// Update the document
//		UpdateResult result = collection.updateOne(filter, update);


		}
}
