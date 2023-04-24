const {ObjectId} = require("mongodb");
module.exports = function (app, usersRepository, offersRepository) {

    /**
     *
     */
    app.get('/offers/myoffers', function (req, res) {
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
            seller: req.session.user,
            isBuy: false
        }
        offersRepository.insertOffer(offer, function (offerId) {
            if (offerId == null) {
                res.send("Error al insertar la oferta");
            } else {
                res.send("Agregada la oferta ID: " + offerId)
            }
        });
    });

    app.get('/offers/buy', function (req, res) {
        let filter = {};
        let options = {};
        let page = parseInt(req.query.page); // Es String !!!
        if (typeof req.query.page === "undefined" || req.query.page === null || req.query.page === "0") { // Puede no venir el param
            page = 1;
        }
        offersRepository.getBuysPg(filter, options, page).then(buys => {
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
            let offers = [];
            for (let i = 0; i < buys.buys.length; i++) {
                offers.push(buys.buys[i].offerId)
            }
            let filter = {"_id": {$in: offers}};
            let options = {sort: {title: 1}};
            offersRepository.getOffers(filter, options).then(offers => {
                usersRepository.findUser({email: req.session.user}, options).then(user => {
                    let response = {
                        offers: offers,
                        pages: pages,
                        currentPage: page,
                        session: req.session,
                        money: user.money
                    }
                    res.render("offers/buy.twig", response);
                })
            }).catch(error => {
                res.send("Se ha producido un error al listar las ofertas del usuario: " + error)
            });
        }).catch(error => {
            res.send("Se ha producido un error al listar las compras del usuario:" + error)
        });
    })

    app.get('/offers/searchOffers', function (req, res) {
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
            usersRepository.findUser({email: req.session.user}, options).then(user => {
                let lastPage = result.total / 5;
                if (result.total % 5 > 0) { // Sobran decimales
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
                    currentPage: page,
                    session: req.session,
                    money: user.money,
                    search: req.query.search
                }
                res.render("offers/list.twig", response);
            })
        }).catch(error => {
            res.send("Se ha producido un error al buscar las ofertas " + error)
        });
    });

    app.post('/offers/buy/:_id', function (req, res) {
        let filter = {_id: ObjectId(req.params._id)};
        let shop = {
            user: req.session.user,
            offerId: ObjectId(req.params._id)
        }
        let options = {};
        offersRepository.findOffer(filter, options).then(offer => {
            compruebaBuy(offer, req.session.user, function (result, errors, money) {
                if (result == false) {
                    offersRepository.buyOffer(shop, function (offerId) {
                        const rest = money - offer.price;
                        offersRepository.updateOffer({seller: offer.seller}, { $set: { isBuy: true}}).then(offer => {
                            usersRepository.updateUser({email: req.session.user}, { $set: { money: rest}}).then(user => {
                                if (offerId == null) {
                                    res.send("Error al realizar la compra");
                                } else {
                                    let response = {
                                        offers: result.offers,
                                        session: req.session,
                                        money: user.money,
                                        errors: errors
                                    }
                                    res.redirect("/offers/list", response);
                                }
                            })
                        })
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
        let money;
        let isBought = false;
        if (user == offer.seller) { // la oferta ha sido creada por el usuario
            isBought = true;
            errors.push("ERROR: la oferta ha sido creada por el usuario")
        }
        usersRepository.findUser(filter, options).then(user => {
            money = user.money;
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
            callback(isBought, errors, money);
        })
    };
}