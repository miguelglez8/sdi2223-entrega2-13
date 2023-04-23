const {getConnection} = require("./db");
module.exports = {
    mongoClient: null,
    app: null,
    init: function (app, mongoClient) {
        this.mongoClient = mongoClient;
        this.app = app;
    },
    getOffers: async function (filter, options) {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("entrega2");
            const collectionName = 'offers';
            const offersCollection = database.collection(collectionName);
            const offers = await offersCollection.find(filter, options).toArray();
            return offers;
        } catch (error) {
            throw (error);
        }
    },
    getOffersPg: async function (filter, options, page) {
        try {
            const limit = 5;
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("entrega2");
            const collectionName = 'offers';
            const offersCollection = database.collection(collectionName);
            const offersCollectionCount = await offersCollection.count();
            const cursor = offersCollection.find(filter, options).skip((page - 1) * limit).limit(limit)
            const offers = await cursor.toArray();
            const result = {offers: offers, total: offersCollectionCount};
            return result;
        } catch (error) {
            throw (error);
        }
    },
    findOffer: async function (filter, options) {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("entrega2");
            const collectionName = 'offers';
            const offersCollection = database.collection(collectionName);
            const offer = await offersCollection.find(filter, options);
            return offer;
        } catch (error) {
            throw (error);
        }
    },
    insertOffer: function (offer, callbackFunction) {
        this.mongoClient.connect(this.app.get('connectionStrings'), function (err, dbClient) {
            if (err) {
                callbackFunction(null)
            } else {
                const database = dbClient.db("entrega2");
                const collectionName = 'offers';
                const offersCollection = database.collection(collectionName);
                offersCollection.insertOne(offer)
                    .then(result => callbackFunction(result.insertedId))
                    .then(() => dbClient.close())
                    .catch(err => callbackFunction({error: err.message}));
            }
        });
    },
    resetOffers: async function (offers) {
        try {
            const client = await getConnection(this.mongoClient,this.app.get('connectionStrings'))
            const database = client.db("entrega2");
            const collectionName = 'offers';
            const usersCollection = database.collection(collectionName);
            await usersCollection.deleteMany();
            await usersCollection.insertMany(offers);
            return true;
        } catch(error){
            throw error;
        }
    },
    buyOffer: function (shop, callbackFunction) {
        this.mongoClient.connect(this.app.get('connectionStrings'), function (err, dbClient) {
            if (err) {
                callbackFunction(null)
            } else {
                const database = dbClient.db("entrega2");
                const collectionName = 'buys';
                const buysCollection = database.collection(collectionName);
                buysCollection.insertOne(shop)
                    .then(result => callbackFunction(result.insertedId))
                    .then(() => dbClient.close())
                    .catch(err => callbackFunction({error: err.message}));
            }
        });
    },
    getBuys: async function (filter, options) {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("entrega2");
            const collectionName = 'buys';
            const buysCollection = database.collection(collectionName);
            const buys = await buysCollection.find(filter, options).toArray();
            return buys;
        } catch (error) {
            throw (error);
        }
    },
    getBuysPg: async function (filter, options, page) {
        try {
            const limit = 5;
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("entrega2");
            const collectionName = 'buys';
            const buysCollection = database.collection(collectionName);
            const buysCollectionCount = await buysCollection.count();
            const cursor = buysCollection.find(filter, options).skip((page - 1) * limit).limit(limit)
            const buys = await cursor.toArray();
            const result = {buys: buys, total: buysCollectionCount};
            return result;
        } catch (error) {
            throw (error);
        }
    }
};