const User = require('../models/user')
const {hashPassword, comparePassword} = require('../helpers/auth')
const { compare } = require('bcrypt')

const test = (req, res) => {
    res.json('test is working')
}

//Register Endpoint
const registerUser = async (req, res) => {
    try {
        const { name, email, password } = req.body;
        const exist = await User.findOne({ email });
        if (exist) {
            return res.json({
                success: false,
                message: 'Email is taken already',
                user: null
            });
        }
        const hashedPassword = await hashPassword(password)
        const user = await User.create({
            name, email, password: hashedPassword
        });
        return res.json({
            success: true,
            message: 'User registered successfully',
            user
        });
    } catch (error) {
        console.log('error');
        res.status(500).json({
            success: false,
            message: 'Something went wrong. Please try again later.',
            user: null
        });
    }
}

//Login endpoint

const loginUser = async (req, res) => {
    try {
        const { email, password } = req.body;
        // check if user exists
        const user = await User.findOne({ email });
        if (!user) {
            return res.json({
                success: false,
                message: 'No User Found',
                user: null
            });
        }
        // check if passwords match
        const match = await comparePassword(password, user.password);
        if (match) {
            return res.json({
                success: true,
                message: 'Login successful',
                user
            });
        }
        else {
            return res.json({
                success: false,
                message: 'Incorrect password',
                user: null
            });
        }
        // return res.json(user);
    } catch (error) {
        console.log('error');
        res.status(500).json({
            success: false,
            message: 'Something went wrong. Please try again later.',
            user: null
        });
    }
}

module.exports = {
    test,
    registerUser,
    loginUser
}
