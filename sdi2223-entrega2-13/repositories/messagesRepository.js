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
    insertMessage: async function (message, conversation) {
        try {
            const client = await getConnection(this.mongoClient, this.app.get('connectionStrings'))
            const database = client.db("entrega2");
            const collectionName = 'messages';
            const conversationsName = 'conversations';
            const messages = database.collection(collectionName);
            const conversations = database.collection(conversationsName);
            const result = await messages.insertOne(message);
            // Buscamos la conversación
            const conversation = await conversations.find({
                buyer: conversation.buyer,
                seller: conversation.seller,
                offer: conversation.offer
            }, {}).toArray();
            // Si lo retornado está vacío, significa que no existe. La insertamos.
            if (conversation.isEmpty()) {
                conversations.insertOne(conversation);
            }
            return result.insertedId;
        } catch (error) {
            throw (error);
        }
    },
    /**
     * Obtiene los conversaciones
     */
    getConversations: async function (filter, options) {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("entrega2");
            const collectionName = 'messages';
            const messagesCollection = database.collection(collectionName).aggregate([
                { $match: filter},
                { $group: {
                    _id: {
                        buyer: "$buyer",
                        seller: "$seller",
                        offer: "$offer"
                    }, messages: { $push: "$$ROOT" }
                }}
            ], options);
            const messages = messagesCollection.toArray()
            return messages;
        } catch (error) {
            throw (error);
        }
    },
    insertConversation: async function (conversation) {
        try {
            const client = await getConnection(this.mongoClient, this.app.get('connectionStrings'))
            const database = client.db("entrega2");
            const collectionName = 'conversations';
            const conversations = database.collection(collectionName);
            const result = await conversations.insertOne(conversation);
            return result.insertedId;
        } catch (error) {
            throw (error);
        }
    },
}