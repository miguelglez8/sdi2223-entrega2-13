const {ObjectId} = require("mongodb");
module.exports = function (app, usersRepository, offersRepository) {

    /**
     *
     */
    app.get('/offers/myoffers', function (req, res) {
        let filter = {seller : req.session.user}
        let options = {};
        let page = parseInt(req.query.page);
        if (typeof req.query.page === "undefined" || req.query.page === null || req.query.page === "0") { // Puede no venir el param
            page = 1;
        }
        // buscamos las ofertas por paginación
        offersRepository.getOffersPg(filter, options, page).then(offers => {
            let lastPage = offers.total / 5;
            if (offers.total % 5 > 0) {
                lastPage = lastPage + 1;
            }
            let pages = [];
            for (let i = page - 2; i <= page + 2; i++) {
                if (i > 0 && i <= lastPage) {
                    pages.push(i);
                }
            }
            // sacamos las ids de las ofertas
            let result = [];
            for (let i = 0; i < offers.offers.length; i++) {
                result.push(offers.offers[i].offerId)
            }
            usersRepository.findUser({email: req.session.user}, options).then(user => {
                let response = {
                    offers: offers.offers,
                    pages: pages,
                    currentPage: page,
                    session: req.session,
                    money: user.money
                }
                res.render("offers/myoffers.twig", response);
            })
            // volvemos a la vista de ofertas compradas

        }).catch(error => {
            res.send("Se ha producido un error al listar las compras del usuario:" + error)
        });
    })

    /**
     *
     */
    app.get('/offers/add', function (req, res) {
        usersRepository.findUser({email: req.session.user}, {}).then(user => {
            let response = {
                session: req.session,
                money: user.money
            }
            res.render("offers/add.twig", response);
        })
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
                // res.send("Agregada la oferta ID: " + offerId)
                res.redirect("/offers/myoffers")
            }
        });

    });

    /**
     *
     */
    app.get('/offers/delete/:id', function (req, res) {
        let filter = {_id: new ObjectId(req.params.id)};
        offersRepository.deleteOffer(filter, {}).then(result => {
            if (result === null || result.deletedCount === 0) {
                res.send("No se ha podido eliminar el registro");
            } else {
                res.redirect("/offers/myoffers");
            }
        }).catch(error => {
            res.send("Se ha producido un error al intentar eliminar la canción: " + error)
        });
    })

    /**
     * Obtiene las ofertas que han sido compradas
     */
    app.get('/offers/buy', function (req, res) {
        let filter = {user: req.session.user};
        let options = {};
        // establecemos la página
        let page = parseInt(req.query.page); // Es String !!!
        if (typeof req.query.page === "undefined" || req.query.page === null || req.query.page === "0") { // Puede no venir el param
            page = 1;
        }
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
            // obtenemos las ofertas
            offersRepository.getOffers(filter, {}).then(offers => {
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
    })

    /**
     * Busca las ofertas por título
     */
    app.get('/offers/searchOffers', function (req, res) {
        let filter = {};
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
        offersRepository.getOffersPg(filter, {}, page).then(result => {
            // buscamos el usuario
            usersRepository.findUser({email: req.session.user}, {}).then(user => {
                let pages = []; // paginas mostrar

                let lastPage
                if (!req.query.search) {
                    lastPage = result.total / 5;
                    if (result.total % 5 > 0) { // Sobran decimales
                        lastPage = lastPage + 1;
                    }
                } else {
                    lastPage = result.offers.length/ 5;
                    if (result.offers.length % 5 > 0) { // Sobran decimales
                        lastPage = lastPage + 1;
                    }
                }

                if (lastPage < 2) {
                    for (let i = page - 1; i <= page + 1; i++) {
                        if (i > 0 && i <= lastPage) {
                            pages.push(i);
                        }
                    }
                } else {
                    for (let i = page - 2; i <= page + 2; i++) {
                        if (i > 0 && i <= lastPage) {
                            pages.push(i);
                        }
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
    app.get('/offers/buy/:id/:page', async function (req, res) {
        let id = new ObjectId(req.params.id);
        let user = req.session.user;
        let sesion = req.session
        let filter = {_id: id};
        let page = parseInt(req.params.page)
        let shop = {
            user: req.session.user,
            offerId: id
        }
        let options = {};
        // filtramos oferta por título
        offersRepository.getOffers(filter, options).then(async offer => {
            if (offer == null) {
                res.send("No hay ninguna oferta");
            } else {
                // comprueba si podemos comprar la oferta
                let canBuyResult = await compruebaBuy(offer, user);
                if (canBuyResult.errors.length != 0) {
                    res.redirect("/offers/searchOffers?errors="+canBuyResult.errors+"&page="+page)
                } else {
                    offersRepository.buyOffer(shop).then(offerId => {
                        const rest = canBuyResult.price;
                        // actualizamos la oferta (el campo isBuy a true)
                        offersRepository.updateOffer({_id: new ObjectId(req.params.id)}, {$set: {isBuy: true}}).then(offer => {
                            if (offer == null) {
                                res.send("Error al actualizar la oferta");
                            } else {
                                // actualizamos el saldo del usuario
                                usersRepository.updateUser({email: req.session.user}, {$set: {money: rest}}).then(user => {
                                    if (user == null) {
                                        res.send("Error al actualizar el usuario");
                                    } else {
                                        // volvemos a la vista de listar todas las ofertas
                                        res.redirect("/offers/searchOffers?money="+canBuyResult.price+"&page="+page)
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
            }
        }).catch(err => {
            res.send("Error al obtener la lista de ofertas " + err)
        });
    });

    /**
     * Devuelve los errores al validar la compra (si los hay) y el saldo restante del usuario
     */
    async function compruebaBuy(offer, user)
    {
        let errors = new Array();
        let oferta = offer[0];
        if(user == oferta.seller)
            // error: no puedes comprar tu oferta
            errors.push("[Titulo="+oferta.title+"]" + " ERROR: la oferta ha sido creada por el usuario")
        let filter = {
            email: user
        }
        let userResponse = await usersRepository.findUser(filter, {})
        if(userResponse == null){ return {errors: ["Ha ocurrido un error al obtener el usuario"]}}
        if(userResponse.money < oferta.price)
            // error: no tienes suficiente dinero
            errors.push("[Titulo="+oferta.title+"]" + " ERROR: no tienes suficiente saldo para comprar la oferta")
        let offersResponse = await offersRepository.getBuys({ $and: [ { user: user}, { offerId: oferta._id} ]}, {});
        if(offersResponse == null) {return {errors: ["Ha ocurrido un error al obtener las ofertas"]}}
        // error: la oferta ya está comprada
        if(offersResponse.length > 0) errors.push("[Titulo="+oferta.title+"]" + " ERROR: la oferta ya ha sido comprada por un usuario")
        let price = userResponse.money - oferta.price;
        // devolvemos los errores que haya y el precio que le queda al usuario
        return {errors, price}
    }
}