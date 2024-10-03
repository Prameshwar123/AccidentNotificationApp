const express = require('express');
const dotenv = require('dotenv').config();
const { mongoose }  = require('mongoose');
const app = express();
const PORT = 5000;

//db connection
mongoose.connect(process.env.MONGO_URL)
    .then(() => console.log('Database Connected'))
    .catch((err) => console.log('Database not connected', err));

//middleware
app.use(express.json());

app.use('/', require('./routes/authRoute'));

app.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
});