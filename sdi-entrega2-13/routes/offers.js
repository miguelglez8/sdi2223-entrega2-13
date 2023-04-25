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
        offersRepository.insertOffer(offer).then((offerId) => {
            if (offerId == null) {
                res.send("Error al insertar la oferta");
            } else {
                res.send("Agregada la oferta ID: " + offerId)
            }
        });

    });

    /**
     * Obtiene las ofertas que han sido compradas
     */
    app.get('/offers/buy', function (req, res) {
        let filter = {};
        let options = {};
        // establecemos la página
        let page = parseInt(req.query.page); // Es String !!!
        if (typeof req.query.page === "undefined" || req.query.page === null || req.query.page === "0") { // Puede no venir el param
            page = 1;

            // buscamos las ofertas por paginación
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
                // sacamos las ids de las ofertas
                let offers = [];
                for (let i = 0; i < buys.buys.length; i++) {
                    offers.push(buys.buys[i].offerId)
                }
                let filter = {"_id": {$in: offers}};
                let options = {sort: {title: 1}};
                // obtenemos las ofertas
                offersRepository.getOffers(filter, options).then(offers => {
                    // buscamos el usuario en sesión
                    usersRepository.findUser({email: req.session.user}, options).then(user => {
                        let response = {
                            offers: offers,
                            pages: pages,
                            currentPage: page,
                            session: req.session,
                            money: user.money
                        }
                        // volvemos a la vista de ofertas compradas
                        res.render("offers/buy.twig", response);
                    }).catch(error => {
                        res.send("Se ha producido un error al encontrar el usuario en sesión: " + error)
                    });
                }).catch(error => {
                    res.send("Se ha producido un error al listar las ofertas del usuario: " + error)
                });
            }).catch(error => {
                res.send("Se ha producido un error al listar las compras del usuario:" + error)
            });
        }
    })

    /**
     * Busca las ofertas por título
     */
    app.get('/offers/searchOffers', function (req, res) {
        let filter = {};
        let options = {sort: { title: 1}}
        // establecemos el filtro
        if(req.query.search != null && typeof(req.query.search) != "undefined" && req.query.search != ""){
            filter = {"title": {$regex: ".*" + req.query.search + ".*"}};
        }
        // establecemos la página
        let page = parseInt(req.query.page); // Es String !!!
        if (typeof req.query.page === "undefined" || req.query.page === null || req.query.page === "0") { // Puede no venir el param
            page = 1;
        }
        // buscamos las oferta con paginación
        offersRepository.getOffersPg(filter, options, page).then(result => {
            // buscamos el usuario
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
                // volvemos a la vista de listar las ofertas
                res.render("offers/list.twig", response);
            }).catch(error => {
                res.send("Se ha producido un error al encontrar el usuario en sesión: " + error)
            });
        }).catch(error => {
            res.send("Se ha producido un error al buscar las ofertas " + error)
        });
    });

    /**
     * Compra la oferta a través de la id
     */
    app.get('/offers/buy/:id', function (req, res) {
        let id = new ObjectId(req.params.id);
        let user = req.session.user;
        let filter = {_id: id};
        let shop = {
            user: req.session.user,
            offerId: id
        }
        let options = {};
        // filtramos oferta por título
        offersRepository.getOffers(filter, options).then(offer => {
            if (offer==null) {
                res.send("No hay ninguna oferta");
            } else {
                // comprobamos si es válida para comprar
                compruebaBuy(offer, user, function (result, errors, money, offer) {
                    let response = {
                        session: req.session,
                        money: money,
                        errors: errors
                    }
                    if (result == false) {
                        // compramos la oferta
                        offersRepository.buyOffer(shop).then(offerId => {
                            const rest = money - offer.price;
                            // actualizamos la oferta (el campo isBuy a true)
                            offersRepository.updateOffer({_id: new ObjectId(req.params.id)}, { $set: { isBuy: true}}).then(offer => {
                                if (offer == null) {
                                    res.send("Error al actualizar la oferta");
                                } else {
                                    // actualizamos el saldo del usuario
                                    usersRepository.updateUser({email: req.session.user}, { $set: { money: rest}}).then(user => {
                                        if (user == null) {
                                            res.send("Error al actualizar el usuario");
                                        } else {
                                            // volvemos a la vista de listar todas las ofertas
                                            res.render("/offers/list", response);
                                        }
                                    }).catch(error => {
                                        res.send("Se ha producido un error al actualizar el usuario en sesión: " + error)
                                    });
                                }
                            }).catch(error => {
                                res.send("Se ha producido un error al actualizar la oferta: " + error)
                            });
                        }).catch(error => {
                            res.send("Se ha producido un error al comprar: " + error)
                        });
                    }
                }).catch(error => {
                    res.send("Se ha producido un error al buscar si es válida la compra: " + error)
                });
            }
        }).catch(error => {
            res.send("Se ha producido un error al buscar la oferta " + error)
        });
    });

    /**
     * Devuelve false si no está comprada (válida) y true en otro caso
     */
    function compruebaBuy(offer, user, callback) {
        let errors = new Array();
        let options = {};
        let filter = {
            email: user
        }
        let oferta = offer[0]
        let money;
        let isBought = false;
        if (user == oferta.seller) { // la oferta ha sido creada por el usuario
            isBought = true;
            errors.push("ERROR: la oferta ha sido creada por el usuario")
        }
        usersRepository.findUser(filter, options).then(user => {
            money = user.money;
            if (user != null && user.money < oferta.price) { // no tenemos suficiente saldo para comprar la oferta
                isBought = true;
                errors.push("ERROR: no tenemos suficiente saldo para comprar la oferta")
            }
            offersRepository.getBuys({ $and: [ { seller: user}, { offerId: oferta._id} ]}, options).then(users => {
                if (users.length > 0) { // la oferta ya ha sido comprada por un usuario
                    isBought = true
                    errors.push("ERROR: la oferta ya ha sido comprada por un usuario")
                }
                callback(isBought, errors, money, oferta);
            }).catch(error => {
                res.send("Se ha producido un error al obtener las compras: " + error)
            });
        }).catch(error => {
            res.send("Se ha producido un buscar el usuario en sesión: " + error)
        });


    };
}