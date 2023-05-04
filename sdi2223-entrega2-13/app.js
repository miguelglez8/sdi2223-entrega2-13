let createError = require('http-errors');
let express = require('express');
let path = require('path');
const fs = require('fs');

let app = express();

/**
 * Control de acceso mas permisivo
 */
app.use(function(req, res, next) {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Credentials", "true");
    res.header("Access-Control-Allow-Methods", "POST, GET, DELETE, UPDATE, PUT");
    res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, token")
    next();
});


/**
 * Generacion de tokens
 */
let jwt = require('jsonwebtoken');
app.set('jwt', jwt);

/**
 * Para leer las peticiones POST
 */
let bodyParser = require('body-parser');
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));


/**
 * Encriptacion del sistema
 */
let crypto = require('crypto');
app.set('clave','abcdefg');
app.set('crypto', crypto);

/**
 * Cliente de MONGODB
 */
console.log(process.env)
const { MongoClient } = require("mongodb");
const url = "mongodb://localhost:27017";
app.set('connectionStrings', url);

let expressSession = require('express-session');
app.use(expressSession({
    secret: 'abcdefg',
    resave: true,
    saveUninitialized: true
}));

/**
 * Los repositorios del sistema
 */
let usersRepository = require("./repositories/usersRepository.js");
usersRepository.init(app, MongoClient);
let offersRepository = require("./repositories/offersRepository.js");
offersRepository.init(app, MongoClient);
let messagesRepository = require("./repositories/messagesRepository.js");
messagesRepository.init(app, MongoClient);

/**
 * INDEX
 */
let indexRouter = require('./routes/index');
let userSessionRouter = require('./routes/userSessionRouter');

app.use("/users/list", userSessionRouter);

app.use("/offers/add",userSessionRouter);
app.use("/offers/list",userSessionRouter);
app.use("/offers/buy",userSessionRouter);
app.use("/offers/myoffers",userSessionRouter);

const userTokenRouter = require('./routes/userTokenRouter');
app.use("/api/v1.0/offers/", userTokenRouter);
app.use("/api/v1.0/offers/messages", userTokenRouter);
app.use("/api/v1.0/conversations", userTokenRouter);

async function loadUsersData() {
    try {
        const client = await MongoClient.connect(url);
        const db = client.db("entrega2");
        const usersCollection = db.collection("users");

        const rawData = fs.readFileSync("../data/users.json");
        const users = JSON.parse(rawData);

        await usersCollection.deleteMany({});
        await usersCollection.insertMany(users);

        console.log(`Loaded ${users.length} users`);
    } catch (err) {
        console.error(`Failed to load users data: ${err}`);
    }
}


/**
 * Motor de vistas twig
 */
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'twig');


app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(express.static(path.join(__dirname, 'public')));
require("./routes/users.js")(app, usersRepository);

require("./routes/bd.js")(app, usersRepository, offersRepository);

require("./routes/offers.js")(app, usersRepository, offersRepository);

app.use('/', indexRouter);

/*
 * API Rest
 */
require("./routes/api/offersAPIv1.0.js")(app, offersRepository);
require("./routes/api/usersAPIv1.0.js")(app, offersRepository, usersRepository);
require("./routes/api/messagesAPIv1.0")(app, offersRepository, messagesRepository);

/**
 * Carga de usuarios
 */
loadUsersData();



/**
 * Manejar errores 404
 */
app.use(function(req, res, next) {
    next(createError(404));
});

/**
 * Manejar errores
 */
app.use(function(err, req, res, next) {
    // set locals, only providing error in development
    res.locals.message = err.message;
    res.locals.error = req.app.get('env') === 'development' ? err : {};

    // render the error page
    res.status(err.status || 500);
    res.render('error');
});

module.exports = app;
