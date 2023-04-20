module.exports = function (app, usersRepository) {


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
                password:app.get("crypto").createHmac('sha256', app.get('clave'))
                    .update(name).digest('hex')
            });
        }

        users.push({
            email: "admin@email.com",
            rol: "ADMIN",
            name: "admin",
            surname: "admin",
            password: app.get("crypto").createHmac('sha256', app.get('clave'))
                .update("admin").digest('hex')
        })


        await usersRepository.resetUsers(users);

        res.render("login.twig");
    });



}