const User = require('../models/user');
const Contact = require('../models/contact');
const { hashPassword, comparePassword } = require('../helpers/auth');

const registerUser = async (req, res) => {
    try {
        const { name, email, password } = req.body;
        const exist = await User.findOne({ email });
        if (exist) {
            return res.json({
                success: false,
                message: 'Email is already taken',
                user: null
            });
        }
        const hashedPassword = await hashPassword(password);
        const user = await User.create({ name, email, password: hashedPassword });
        req.session.userId = user._id;
        return res.json({
            success: true,
            message: 'User registered successfully',
            user
        });
    } catch (error) {
        console.error('Error during registration:', error);
        res.status(500).json({
            success: false,
            message: 'Something went wrong. Please try again later.',
            user: null
        });
    }
};

const loginUser = async (req, res) => {
    try {
        const { email, password } = req.body;
        const user = await User.findOne({ email });
        if (!user) {
            return res.json({
                success: false,
                message: 'No User Found',
                user: null
            });
        }

        const match = await comparePassword(password, user.password);
        if (match) {
            req.session.userId = user._id;
            return res.json({
                success: true,
                message: 'Login successful',
                user
            });
        } else {
            return res.json({
                success: false,
                message: 'Incorrect password',
                user: null
            });
        }
    } catch (error) {
        console.error('Error during login:', error);
        res.status(500).json({
            success: false,
            message: 'Something went wrong. Please try again later.',
            user: null
        });
    }
};

const logoutUser = (req, res) => {
    req.session.destroy((err) => {
        if (err) {
            return res.status(500).json({ success: false, message: 'Failed to logout' });
        }
        res.clearCookie('connect.sid');
        res.status(200).json({ success: true, message: 'Logged out successfully' });
    });
};

const addContact = async (req, res) => {
    if (!req.session.userId) {
        return res.status(401).json({ message: 'Unauthorized' });
    }

    try {
        const { name, phoneNumber } = req.body;
        const userId = req.session.userId;

        const newContact = new Contact({
            userId,
            name,
            phoneNumber
        });

        await newContact.save();

        return res.status(200).json({
            success: true,
            message: 'Contact added successfully',
            contact: newContact
        });
    } catch (error) {
        console.error('Error adding contact:', error);
        return res.status(500).json({
            success: false,
            message: 'Failed to add contact',
        });
    }
};

const getContacts = async (req, res) => {
    if (!req.session.userId) {
        return res.status(401).json({ message: 'Unauthorized' });
    }

    try {
        const userId = req.session.userId;
        const contacts = await Contact.find({ userId });
        return res.status(200).json({
            success: true,
            contacts
        });
    } catch (error) {
        console.error('Error fetching contacts:', error);
        return res.status(500).json({
            success: false,
            message: 'Failed to fetch contacts',
        });
    }
};

module.exports = {
    registerUser,
    loginUser,
    logoutUser,
    addContact,
    getContacts
};
