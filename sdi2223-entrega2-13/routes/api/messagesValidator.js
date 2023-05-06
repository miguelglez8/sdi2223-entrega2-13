const {check} = require('express-validator');
exports.messageValidatorInsert = [
    check('text', 'El mensaje no debe estar vacío').trim().not().isEmpty()
]
