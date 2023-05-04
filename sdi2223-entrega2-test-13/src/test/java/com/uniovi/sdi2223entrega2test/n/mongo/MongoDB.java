package com.uniovi.sdi2223entrega2test.n.mongo;

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.conversions.Bson;

public class MongoDB {
	// cliente de mongo
	private MongoClient mongoClient;

	// base de datos
	private MongoDatabase mongodb;

	// inicializa la base de datos
	public void resetMongo() {
		mongoClient = new MongoClient("localhost", 27017);
		mongodb = mongoClient.getDatabase("entrega2");
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
		MongoCollection<Document> users = getCollection("buys");
		Bson filter = Filters.eq("user", user);
		FindIterable<Document> document = users.find(filter);
		int size = 0;
		for (Document docum : document) { // vamos sumando el número de elementos que hay en la colección
			size++;
		}
		return size;
	}

	// Busca el precio de la oferta por su título
	public double getPrice(String title) {
		MongoCollection<Document> users = getCollection("offers");
		Bson filter = Filters.eq("title", title);
		Document document = users.find(filter).first();
		Number price = (Number) document.get("price");

		return price.doubleValue();
	}
}
