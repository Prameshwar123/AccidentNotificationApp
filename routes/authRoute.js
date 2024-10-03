const express = require('express');
const router = express.Router();
const {test, registerUser, loginUser} = require('../controllers/authController')

router.get('/', test)
router.post('/register', registerUser)
router.post('/login', loginUser)

module.exports = router