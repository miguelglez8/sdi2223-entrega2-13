const {ObjectId} = require("mongodb");
module.exports = function (app, offersRepository) {

    /**
     *
     */
    app.get('/myoffers', function (req, res) {
        let filter = {author : req.session.user};
        let options = {};
        offersRepository.getOffers(filter, options).then(offers => {
            res.render("offers/myoffers.twig", {offers: offers});
        }).catch(error => {
            res.send("Se ha producido un error al listar las publicaciones del usuario:" + error)
        });
    })

    /**
     *
     */
    app.get('/offers/add', function (req, res) {
        res.render("offers/add.twig");
    });

    /**
     *
     */
    app.post('/offers/add', function(req, res) {
        let offer = {
            title: req.body.title,
            detail: req.body.detail,
            date: new Date().getDate(),
            price: req.body.price,
            seller: req.session.user
        }
        offersRepository.insertOffer(offer, function (offerId) {
            if (offerId == null) {
                res.send("Error al insertar la oferta");
            } else {
                res.send("Agregada la oferta ID: " + offerId)
            }
        });
    });
}