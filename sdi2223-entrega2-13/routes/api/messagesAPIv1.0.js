const {ObjectId} = require("mongodb");
const {validationResult} = require('express-validator')
const {messageValidatorInsert} = require('./messagesValidator')
module.exports = function (app, offersRepository, messagesRepository) {
    /**
     * S3: Envía mensajes a una oferta con los parámetros del body de la petición. Estos parámetros deben ser:
     *     - "seller": Vendedor de la oferta
     *     - "offer": Id de oferta
     *     - "text": Texto del mensaje
     */
    app.post('/api/v1.0/offers/messages', messageValidatorInsert, function(req, res) {
        try {
            const errors = validationResult(req);
            if (!errors.isEmpty()) {
                res.status(422);
                res.json({errors: errors.array()})
            }
            else {
                // Creamos el mensaje
                let message = {
                    buyer: res.user,
                    seller: req.body.seller,
                    offer: req.body.offer,
                    text: req.body.text,
                    date: Date.now(),
                    read: false
                }

                // Creamos la conversación
                let conversation = {
                    buyer: res.buyer,
                    seller: req.body.seller,
                    offer: req.body.offer
                }

                messagesRepository.insertMessage(message, conversation).then((messageId) => {
                    if (messageId === null) {
                        res.status(409);
                        res.json({error: "No se ha podido enviar el mensaje."});
                    } else {
                        res.status(201);
                        res.json({
                            message: "Mensaje enviado correctamente.",
                            _id: messageId
                        })
                    }
                });
            }
        } catch (e) {
            res.status(500);
            res.json({error: "Se ha producido un error al intentar crear el mensaje: " + e})
        }
    })

    /**
     * S4: Dada una id de oferta y un usuario, retorna el listado de mensajes vinculados a dicha oferta en los que
     *     el usuario sea el vendedor o el interesado.
     */
    app.get("/api/v1.0/offers/messages/:id", function (req, res) {
        let filter = {
            $or: [ {seller: res.user}, {buyer: res.user} ],
            offer: req.params.id
        };
        let options = {};
        messagesRepository.getMessages(filter, options).then(messages => {
            res.status(200);
            res.send({messages: messages})
        }).catch(error => {
            res.status(500);
            res.json({ error: "Se ha producido un error al recuperar los mensajes." })
        });
    });

    /**
     * S5: Dado un usuario identificado, retorna todas las conversaciones vinculadas a dicho usuario, ya sea el
     *     vendedor o el interesado.
     */
    app.get("/api/v1.0/conversations", function (req, res) {
        let filter = {$or: [ {seller: res.user}, {buyer: res.user} ]};
        let options = {};
        messagesRepository.getConversations(filter, options).then(conversations => {
            res.status(200);
            res.send({conversations: conversations})
        }).catch(error => {
            res.status(500);
            res.json({ error: "Se ha producido un error al recuperar las conversaciones." })
        });
    });

    /**
     * S6: Dada la ID de una conversación, elimina dicha conversación junto a todos los mensajes relacionados a ella.
     */
    app.delete('/api/v1.0/conversations/:id', function (req, res) {
        try {
            let conversationId = ObjectId(req.params.id)
            let filter = {conversationId: conversationId}
            messagesRepository.deleteConversation(filter, {}).then(result => {
                if (result === null || result.deletedCount === 0) {
                    res.status(404);
                    res.json({error: "ID inválido o no existe, no se ha borrado el registro."});
                } else {
                    res.status(200);
                    res.send(JSON.stringify(result));
                }
            }).catch(error => {
                res.status(500);
                res.json({error: "Se ha producido un error al eliminar la canción."})
            });
        } catch (e) {
            res.status(500);
            res.json({error: "Se ha producido un error, revise que el ID sea válido."})
        }
    });
}