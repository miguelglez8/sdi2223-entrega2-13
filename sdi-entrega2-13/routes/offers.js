const {ObjectId} = require("mongodb");
module.exports = function (app, usersRepository, offersRepository) {

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
            date: new Date().getDate().toLocaleString(),
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

    app.get('/buy', function (req, res) {
        let filter = {};
        let options = {};
        let page = parseInt(req.query.page); // Es String !!!
        if (typeof req.query.page === "undefined" || req.query.page === null || req.query.page === "0") { // Puede no venir el param
            page = 1;
        }
        offersRepository.getBuys(filter, options).then(buys => {
            let lastPage = buys.total / 5;
            if (buys.total % 5 > 0) { // Sobran decimales
                lastPage = lastPage + 1;
            }
            let pages = []; // paginas mostrar
            for (let i = page - 2; i <= page + 2; i++) {
                if (i > 0 && i <= lastPage) {
                    pages.push(i);
                }
            }
            let response = {
                buys: buys,
                pages: pages,
                currentPage: page
            }
            res.render("offers/buy.twig", response);
        }).catch(error => {
            res.send("Se ha producido un error al listar las compras del usuario:" + error)
        });
    })

    app.get('/offers', function (req, res) {
        let filter = {};
        let options = {sort: { title: 1}}
        if(req.query.search != null && typeof(req.query.search) != "undefined" && req.query.search != ""){
            filter = {"title": {$regex: ".*" + req.query.search + ".*"}};
        }
        let page = parseInt(req.query.page); // Es String !!!
        if (typeof req.query.page === "undefined" || req.query.page === null || req.query.page === "0") { // Puede no venir el param
            page = 1;
        }
        offersRepository.getOffersPg(filter, options, page).then(result => {
            let lastPage = result.total / 4;
            if (result.total % 4 > 0) { // Sobran decimales
                lastPage = lastPage + 1;
            }
            let pages = []; // paginas mostrar
            for (let i = page - 2; i <= page + 2; i++) {
                if (i > 0 && i <= lastPage) {
                    pages.push(i);
                }
            }
            let response = {
                offers: result.offers,
                pages: pages,
                currentPage: page
            }
            res.render("offers/list.twig", response);
        }).catch(error => {
            res.send("Se ha producido un error al buscar las ofertas " + error)
        });
    });

    app.post('/offers/buy/:title', function (req, res) {
        let title = ObjectId(req.params.title);
        let shop = {
            user: req.session.user,
            title: title
        }
        let filter = {_id: ObjectId(req.params.id)};
        let options = {};
        offersRepository.findOffer(filter, options).then(song => {
            compruebaBuy(song, req.session.user, function (result, errors) {
                if (result == false) {
                    offersRepository.buyOffer(shop, function (offerId) {
                        if (offerId == null) {
                            res.send("Error al realizar la compra");
                        } else {
                            res.redirect("/offers/list", errors);
                        }
                    })
                }
            })
        }).catch(error => {
            res.send("Se ha producido un error al buscar la oferta " + error)
        });
    });

    function compruebaBuy(offer, user, callback) {
        let errors = new Array();
        let options = {};
        let filter = {
            user: user
        }
        let isBought = false;
        if (user == offer.seller) { // la oferta ha sido creada por el usuario
            isBought = true;
            errors.push("ERROR: la oferta ha sido creada por el usuario")
        }
        usersRepository.findUser(filter, options).then(user => {
            if (user != null && user.money < offer.price) { // no tenemos suficiente saldo para comprar la oferta
                isBought = true;
                errors.push("ERROR: no tenemos suficiente saldo para comprar la oferta")
            }
        })

        offersRepository.getBuys({ $and: [ { seller: user}, { offerId: offer._id} ]}, options).then(users => {
            if (users.length > 0) { // la oferta ya ha sido comprada por un usuario
                isBought = true
                errors.push("ERROR: la oferta ya ha sido comprada por un usuario")
            }
            callback(isBought, errors);
        })
    };
}