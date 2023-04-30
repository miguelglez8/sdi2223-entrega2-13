const {getConnection} = require("./db");
module.exports = {
    mongoClient: null,
    app: null,
    init: function (app, mongoClient) {
        this.mongoClient = mongoClient;
        this.app = app;
    },
    /**
     * Obtiene los mensajes
     */
    getMessages: async function (filter, options) {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("entrega2");
            const collectionName = 'messages';
            const messagesCollection = database.collection(collectionName);
            const messages = await messagesCollection.find(filter, options).toArray();
            return messages;
        } catch (error) {
            throw (error);
        }
    },
    /**
     * Obtiene un mensaje dado un filtro
     */
    findMessage: async function (filter, options) {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("entrega2");
            const collectionName = 'messages';
            const messagesCollection = database.collection(collectionName);
            const message = await messagesCollection.findOne(filter, options);
            return message;
        } catch (error) {
            throw (error);
        }
    },
    insertMessage: async function (message) {
        try {
            const client = await getConnection(this.mongoClient, this.app.get('connectionStrings'))
            const database = client.db("entrega2");
            const collectionName = 'messages';
            const messages = database.collection(collectionName);
            const result = await messages.insertOne(message);
            return result.insertedId;
        } catch (error) {
            throw (error);
        }
    }
}