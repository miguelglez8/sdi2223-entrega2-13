const express = require('express');
const usersRepository = require("../repositories/usersRepository");
const userSessionRouter = express.Router();

userSessionRouter.use(function(req, res, next) {
    console.log("routerUsuarioSession");
    if ( req.session.user ) {
        usersRepository.findUser({ email: req.session.user}, {}).then(result =>
        {
            if(result.rol == "ADMIN") {
                res.redirect("/users/admin/list");
            }
            if(result.rol == "STANDARD"){
                res.redirect("/offers/myoffers");
            }else {
                next();
            }
        }).catch(error => res.redirect("/users/login"));

    } else {
        res.redirect("/users/login");
    }
});
module.exports = userSessionRouter;