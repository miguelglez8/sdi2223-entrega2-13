const {resetBuyOffers} = require("../repositories/offersRepository");
module.exports = function (app, usersRepository, offersRepository) {


    /**
     * Creacion de la base da datos con unos usuarios iniciales,
     * además de la creacion del usuario administrador
     */
    app.get("/initbd", async function(req,res){

        let users = new Array();
        let name = "";
        for(i=1; i <16; i++) {

            if(i < 10)
                name = "user0"+i
            else
                name = "user" + i

            users.push({
                email: name+"@email.com",
                rol: "STANDARD",
                name:name,
                surname:name,
                money: 100,
                password:app.get("crypto").createHmac('sha256', app.get('clave'))
                    .update(name).digest('hex')
            });
        }

        users.push({
            email: "admin@email.com",
            rol: "ADMIN",
            name: "admin",
            surname: "admin",
            money: 100,
            password: app.get("crypto").createHmac('sha256', app.get('clave'))
                .update("admin").digest('hex')
        })

        let offers = new Array();
        let title = "Oferta ";
        let detail = "Detalle ";

        for(let i=1; i<151; i++) {

            if(i < 11)
                name = "user01"
            else if (i < 21)
                name = "user02"
            else if (i < 31)
                name = "user03"
            else if (i < 41)
                name = "user04"
            else if (i < 51)
                name = "user05"
            else if (i < 61)
                name = "user06"
            else if (i < 71)
                name = "user07"
            else if (i < 81)
                name = "user08"
            else if (i < 91)
                name = "user09"
            else if (i < 101)
                name = "user10"
            else if (i < 111)
                name = "user11"
            else if (i < 121)
                name = "user12"
            else if (i < 131)
                name = "user13"
            else if (i < 141)
                name = "user14"
            else if (i < 151)
                name = "user15"

            offers.push({
                title: title + i,
                detail: detail + i,
                date: new Date().getDate().toLocaleString(),
                price: i,
                seller: name + "@email.com",
                isBuy: false
            });
        }


        await usersRepository.resetUsers(users);
        await offersRepository.resetOffers(offers);
        await offersRepository.resetBuyOffers();

        res.render("login.twig");
    });



}