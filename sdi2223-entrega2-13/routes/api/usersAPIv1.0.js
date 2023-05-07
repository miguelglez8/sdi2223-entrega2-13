module.exports = function (app, songsRepository, usersRepository) {
    /**
     * Permite al usuario loguearse con un token
     */
    app.post('/api/v1.0/users/login', function (req, res) {
        try {
            let securePassword = app.get("crypto").createHmac('sha256', app.get('clave'))
                .update(req.body.password).digest('hex');
            let filter = {
                email: req.body.email,
                password: securePassword
            }
            let options = {};
            usersRepository.findUser(filter, options).then(user => {
                if (user == null) {
                    res.status(401);
                    res.json({
                        message: "usuario no autorizado",
                        authenticated: false
                    })
                } else {
                    let token = app.get('jwt').sign(
                        {user: user.email, time: Date.now() / 1000},
                        "secreto");
                    res.status(200);
                    res.json({
                        message: "usuario autorizado",
                        authenticated: true,
                        token: token
                    })
                }
            }).catch(error => {
                res.status(401);
                res.json({
                    message: "Se ha producido un error al verificar credenciales",
                    authenticated: false
                })
            })
        } catch (e) {
            res.status(500);
            res.json({
                message: "Se ha producido un error al verificar credenciales",
                authenticated: false
            })
        }
    });

    /**
     * Retorna el usuario actualmente logueado
     */
    app.get("/api/v1.0/users/current", function (req, res) {
        let filter = {
            email: res.user
        };
        let options = {};
        usersRepository.findUser(filter, options).then(user => {
            res.status(200);
            res.send({user: user})
        }).catch(error => {
            res.status(500);
            res.json({ error: "Se ha producido un error inesperado." })
        });
    });
}