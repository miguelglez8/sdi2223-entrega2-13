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
            // Comprueba si el comprador es igual al vendedor
            else if (checkBuyer(req, res)) {
                res.status(422);
                res.json({
                    "type": "field",
                    "value": res.user,
                    "msg": "El vendedor debe ser distinto del comprador",
                    "path": "seller",
                    "location": "body"
                })
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
                    buyer: res.user,
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
            let conversationId = new ObjectId(req.params.id)
            let filter = {_id: conversationId, $or: [ {seller: res.user}, {buyer: res.user} ]}
            messagesRepository.deleteConversation(filter, {}).then(result => {
                if (result === null || result.deletedCount === 0) {
                    res.status(404);
                    res.json({error: "ID inválido o no existe. No se ha borrado el registro."});
                } else {
                    res.status(200);
                    res.send(JSON.stringify(result));
                }
            }).catch(error => {
                res.status(500);
                res.json({error: "Se ha producido un error al eliminar la conversación."})
            });
        } catch (e) {
            res.status(500);
            res.json({error: "Se ha producido un error, revise que el ID sea válido."})
        }
    });

    /**
     * S7: Dada la ID de un mensaje, marca dicho mensaje como leído.
     */
    app.put('/api/v1.0/conversations/:id', function (req, res) {
        try {
            let messageId = new ObjectId(req.params.id);
            let filter = {_id: messageId};
            // Si la _id NO no existe, no crea un nuevo documento.
            const options = {upsert: false};
            let message = {
                read: true
            }
            messagesRepository.updateMessage(message, filter, options).then(result => {
                if (result === null) {
                    res.status(404);
                    res.json({error: "ID inválido o no existe, no se ha actualizado el mensaje."});
                }
                // La _id No existe o los datos enviados no difieren de los ya almacenados.
                else if (result.modifiedCount == 0) {
                    res.status(409);
                    res.json({error: "No se ha modificado el mensaje."});
                } else {
                    res.status(200);
                    res.json({
                        message: "Mensaje actualizado a leído correctamente.",
                        result: result
                    })
                }
            }).catch(error => {
                res.status(500);
                res.json({error: "Se ha producido un error al modificar el mensaje."})
            });
        } catch (e) {
            res.status(500);
            res.json({error: "Se ha producido un error al intentar modificar el mensaje: " + e})
        }
    })

    function checkBuyer(req, res) {
        if (res.user === req.body.seller) {
            return true;
        }
    }
}

