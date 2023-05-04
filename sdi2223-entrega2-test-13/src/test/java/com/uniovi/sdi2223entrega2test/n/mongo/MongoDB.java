package com.uniovi.sdi2223entrega2test.n.mongo;

import org.bson.Document;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

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

}
