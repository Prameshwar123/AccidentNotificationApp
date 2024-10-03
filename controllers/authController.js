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
                error: 'Email is taken already'
            });
        }
        const hashedPassword = await hashPassword(password)
        const user = await User.create({
            name, email, password: hashedPassword
        });
        return res.json(user);
    } catch (error) {
        console.log('error');
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
                error: 'No User Found'
            });
        }
        // check if passwords match
        const match = await comparePassword(password, user.password);
        if (match) {
            res.json('passwords match');
        }
        else {
            error: res.json('passwords do not match');
        }
        // return res.json(user);
    } catch (error) {
        console.log('error');
    }
}

module.exports = {
    test,
    registerUser,
    loginUser
}