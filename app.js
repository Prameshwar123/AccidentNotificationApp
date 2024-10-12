const express = require('express');
const dotenv = require('dotenv').config();
const { mongoose } = require('mongoose');
const session = require('express-session');
const app = express();
const PORT = process.env.PORT || 5000;

mongoose.connect(process.env.MONGO_URL)
    .then(() => console.log('Database Connected'))
    .catch((err) => console.log('Database not connected', err));

app.use(express.json());

const crypto = require('crypto');
const secret = crypto.randomBytes(64).toString('hex');

app.use(session({
    secret: secret, 
    resave: false,
    saveUninitialized: true,
    cookie: { secure: false } 
}));

app.use('/', require('./routes/authRoute'));

app.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
});
