package com.uniovi.sdi2223entrega2test.n.mongo;

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.conversions.Bson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MongoDB {
	// cliente de mongo
	private MongoClient mongoClient;

	// base de datos
	private MongoDatabase mongodb;

	// inicializa la base de datos
	public void resetMongo() {
		mongoClient = new MongoClient("localhost", 27017);
		mongodb = mongoClient.getDatabase("entrega2");
		// eliminamos los datos que había
		deleteData();
		// insertamos los que nos interesa
		insertData();
	}

	// cierra el cliente de mongo
	public void close() {
		mongoClient.close();
	}

	// obtiene la coleccion de la base de datos pasada por parámetro
	public MongoCollection<Document> getCollection(String collection) {
		return mongodb.getCollection(collection);
	}

	// Busca el saldo del usuario pasado por parámetro
	public double getSaldo(String user) {
		MongoCollection<Document> users = getCollection("users");
		Bson filter = Filters.eq("email", user);
		Document document = users.find(filter).first();
		Number money = (Number) document.get("money");

		return money.doubleValue();
	}

	// Busca el numero de compras de un usuario en concreto
	public double getBuys(String user) {
		MongoCollection<Document> buys = getCollection("buys");
		Bson filter = Filters.eq("user", user);
		FindIterable<Document> documents = buys.find(filter);
		int size = 0;
		for (Document document : documents) { // vamos sumando el número de elementos que hay en la colección
			size++;
		}
		return size;
	}

	// Busca el precio de la oferta por su título
	public double getPrice(String title) {
		MongoCollection<Document> offers = getCollection("offers");
		Bson filter = Filters.eq("title", title);
		Document document = offers.find(filter).first();
		Number price = (Number) document.get("price");

		return price.doubleValue();
	}

	//Busca el número de ofertas de un usuario
	public int getOffers(String user) {
		MongoCollection<Document> users = getCollection("offers");
		Bson filter = Filters.eq("seller", user);
		FindIterable<Document> document = users.find(filter);
		int size = 0;
		for (Document docum : document) {
      size++;
		}
		return size;
	}
  
	// Busca el número de ofertas con ese titulo
	public int getOffer(String title) {
		MongoCollection<Document> offers = getCollection("offers");
		Bson filter = Filters.eq("title", title);
		// Realizar la consulta
		FindIterable<Document> ofertas = offers.find(filter);
		int size = 0;
		for (Document offer : ofertas) { // vamos sumando el número de elementos que hay en la colección
			size++;
		}
		return size;
	}

	/**
	 * Vaciamos todas las colecciones de la base de datos
	 */
	private void deleteData() {
		mongodb.getCollection("users").drop();
		mongodb.getCollection("offers").drop();
		mongodb.getCollection("buys").drop();
		mongodb.getCollection("messages").drop();
		mongodb.getCollection("conversations").drop();
	}

	/**
	 * Introducimos los datos de las colecciones en la base de datos
	 */
	private void insertData() {
		// usuarios
		MongoCollection<Document> users = mongodb.getCollection("users");
		List<String> passwords = new ArrayList<String>();
		// añadimos las contraseñas encriptadas
		passwords.add("783a825ecf667676312bdd5e76a138e61e55d5061ef541f445e0c22e671eba9f");
		passwords.add("9812db3294b48b41aa0ea4cbe44453280286c0a089a07f3f6f4a313759012ab9");
		passwords.add("66e93521a9447f5082b957a3bc07dcd5841608a0a2a951cbb30a21d83f13d83a");
		passwords.add("049141fc2f265c205539050c6ebc6a9a3d81c87fbf8f936030bc108330280fef");
		passwords.add("bbd89bd814f8c02da5534ee62781701285aa3d586158e9c857618f9a36d18ce3");
		passwords.add("308f5766b85b7842f62e0bd96873a80cad47cf9fb76ba1061957ad0f1ae4dc1f");
		passwords.add("1a44cfeba26b0d6245a787a87a39e90308bcc6445916951d2cb9a80881c7d1c6");
		passwords.add("60f45cdcacc671c9bc3cddc0e4cf4d5aebe65d5a674fdb7498632a2427638df3");
		passwords.add("c7b08c4af12093cbf088edac0b8c9e394f8486cdb267f5950f363bd5adb93e77");
		passwords.add("c4307e1f4f7a11e99a5e4bfeaf598cadcb99fc536b6eeb668ebdff87df88cff5");
		passwords.add("b9809187151d1b2ada397b7c6ecfdedcaeeaa401568f7abf4706b7eab2f7f8b6");
		passwords.add("6fc8f37f8afb07a2aadeb9d6a81df0c4f3fe8d62ed7f41d8a6b0d797011c310c");
		passwords.add("46ef749ceaff17681fcbcf4f5637387421db020f80a3624459e2702187283527");
		passwords.add("c76a26044ac42d88b2154b69e5f7006d38140ccbf91c1a93679ba78e0cc54a17");
		passwords.add("5d6c0f50beb06679b619f7ef2aa46ca23caae057b21b1621ba5ae9692a9192fb");
		passwords.add("d2aec6988857be3f6ba6781ce63670513864190eb7e88a225ae80be03e6faee8");

		for (int i = 1; i <= passwords.size(); i++) {
			// insertamos los usuarios estandar
			 if (i < 10) {
				 users.insertOne(new Document()
						 .append("email", "user0" + i + "@email.com")
						 .append("rol", "STANDARD")
						 .append("name", "user0" + i)
						 .append("surname", "user0" + i)
						 .append("money", 100)
						 .append("password", passwords.get(i-1)));
			 } else {
				 users.insertOne(new Document()
						 .append("email", "user" + i + "@email.com")
						 .append("rol", "STANDARD")
						 .append("name", "user" + i)
						 .append("surname", "user" + i)
						 .append("money", 100)
						 .append("password", passwords.get(i-1)));
			 }

		}
		// insertamos el admin
		users.insertOne(new Document()
				.append("email", "admin@email.com")
				.append("rol", "ADMIN")
				.append("name", "admin")
				.append("surname", "admin")
				.append("money", 100)
				.append("password", "ebd5359e500475700c6cc3dd4af89cfd0569aa31724a1bf10ed1e3019dcfdb11"));

		String name = ""; // nombre del usuario
		String title = "Oferta"; // título de la oferta
		String detail = "Detalle"; // detalle de la oferta
		Date fecha = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd yyyy", Locale.ENGLISH);
		String date = formatter.format(fecha);
		// ofertas
		MongoCollection<Document> offers = mongodb.getCollection("offers");

		for(int i=1; i<161; i++) {
			if(i < 11)
				name = "user01";
			else if (i < 21)
				name = "user02";
			else if (i < 31)
				name = "user03";
			else if (i < 41)
				name = "user04";
			else if (i < 51)
				name = "user05";
			else if (i < 61)
				name = "user06";
			else if (i < 71)
				name = "user07";
			else if (i < 81)
				name = "user08";
			else if (i < 91)
				name = "user09";
			else if (i < 101)
				name = "user10";
			else if (i < 111)
				name = "user11";
			else if (i < 121)
				name = "user12";
			else if (i < 131)
				name = "user13";
			else if (i < 141)
				name = "user14";
			else if (i < 151)
				name = "user15";
			else if (i < 161)
				name = "user16";

			// insertamos las ofertas
			offers.insertOne(new Document()
					.append("title", title + i)
					.append("detail", detail + i)
					.append("date", date)
					.append("price", i)
					.append("seller", name + "@email.com")
					.append("isBuy", false));
		}
	}
}
