const {check} = require('express-validator');
exports.messageValidatorInsert = [
    check('text', 'The message must not be empty').trim().not().isEmpty()
]
