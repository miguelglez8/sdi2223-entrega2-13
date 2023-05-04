const express = require('express');
const logsRepository = require("../repositories/logRepository");
const logRouter = express.Router();

logRouter.use(function(req, res, next) {
    log = {
        type: "PET",
        date: Date.now(),
        description: req.originalUrl
    };
    logsRepository.insertLog(log);
    next();
});
module.exports = logRouter;