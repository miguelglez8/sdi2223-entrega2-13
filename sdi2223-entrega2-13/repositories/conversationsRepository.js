const {getConnection} = require("./db");
module.exports = {
    mongoClient: null,
    app: null,
    init: function (app, mongoClient) {
        this.mongoClient = mongoClient;
        this.app = app;
    },
    /**
     * Obtiene las conversaciones
     */
    getConversations: async function (filter, options) {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("entrega2");
            const collectionName = 'conversations';
            const conversationsCollection = database.collection(collectionName);
            const conversations = await conversationsCollection.find(filter, options).toArray();
            return conversations;
        } catch (error) {
            throw (error);
        }
    },
    /**
     * Obtiene una conversación dado un filtro
     */
    findConversation: async function (filter, options) {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("entrega2");
            const collectionName = 'conversations';
            const conversationsCollection = database.collection(collectionName);
            const conversation = await conversationsCollection.findOne(filter, options);
            return conversation;
        } catch (error) {
            throw (error);
        }
    },
    /**
     * Obtiene las conversaciones con paginación
     */
    getConversationsPg: async function (filter, options, page) {
        try {
            const limit = 5;
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("entrega2");
            const collectionName = 'conversations';
            const conversationsCollection = database.collection(collectionName);
            const conversationsCollectionCount = await conversationsCollection.count();
            const cursor = conversationsCollection.find(filter, options).skip((page - 1) * limit).limit(limit)
            const conversations = await cursor.toArray();
            const result = {conversations: conversations, total: conversationsCollectionCount};
            return result;
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
    /**
     * Actualiza la conversación
     */
    updateConversation: async function (filter, options) {
        try {
            const client = await getConnection(this.mongoClient,this.app.get('connectionStrings'))
            const database = client.db("entrega2");
            const collectionName = 'conversations';
            const conversationsCollection = database.collection(collectionName);
            return await conversationsCollection.updateOne(filter, options);
        } catch (error) {
            throw (error);
        }
    }
};