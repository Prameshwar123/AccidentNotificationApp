const express = require('express');
const router = express.Router();
const {registerUser, loginUser, logoutUser, addContact, getContacts} = require('../controllers/authController')

router.post('/register', registerUser)
router.post('/login', loginUser)
router.post('/logout', logoutUser);
router.post('/contacts', addContact);
router.get('/contacts', getContacts);

module.exports = router