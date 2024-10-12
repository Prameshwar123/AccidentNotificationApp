const mongoose = require('mongoose');
const { Schema } = mongoose;

const contactSchema = new Schema({
    userId: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User',
        required: true,
    },
    name: String,
    phoneNumber: String
});

const Contact = mongoose.model('Contact', contactSchema);

module.exports = Contact;
