const {ObjectId} = require("mongodb");
module.exports = function (app, offersRepository) {
    app.get("/api/v1.0/offers", function (req, res) {
        let filter = {
            seller: { $ne: res.user}
        };
        let options = {};
        offersRepository.getOffers(filter, options).then(offers => {
            res.status(200);
            res.send({offers: offers})
        }).catch(error => {
            res.status(500);
            res.json({ error: "Se ha producido un error al recuperar las ofertas." })
        });
    });

    app.get("/api/v1.0/offers/:id", function (req, res) {
        try {
            let offerId = new ObjectId(req.params.id)
            let filter = {_id: offerId};
            let options = {};
            offersRepository.findOffer(filter, options).then(song => {
                if (song === null) {
                    res.status(404);
                    res.json({error: "ID inválido o no existe"})
                } else {
                    res.status(200);
                    res.json({song: song})
                }
            }).catch(error => {
                res.status(500);
                res.json({error: "Se ha producido un error a recuperar la canción."})
            });
        } catch (e) {
            res.status(500);
            res.json({error: "Se ha producido un error :" + e})
        }
    });

}

