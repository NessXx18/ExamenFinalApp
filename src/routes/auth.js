const express = require('express');
const jwt = require('jsonwebtoken');
const bcrypt = require('bcryptjs');
const User = require('../models/User');
const router = express.Router();

// Lanza un error estricto si no existe la variable process.env.JWT_SECRET
if (!process.env.JWT_SECRET) {
  throw new Error('FATAL ERROR: La variable de entorno JWT_SECRET es requerida.');
}

// Validación básica de correo electrónico
const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

router.post('/register', async (req, res) => {
  try {
    const { name, email, password } = req.body;
    
    if (!name || !email || !password) {
      return res.status(400).json({ error: 'Todos los campos son requeridos' });
    }
    
    if (!emailRegex.test(email)) {
      return res.status(400).json({ error: 'El correo electrónico no es válido' });
    }
    
    if (password.length < 8) {
      return res.status(400).json({ error: 'La contraseña debe tener un mínimo de 8 caracteres' });
    }
    
    // Guardado real en MongoDB - Búsqueda de duplicados
    const existingUser = await User.findOne({ email });
    if (existingUser) {
      return res.status(409).json({ error: 'El usuario ya existe' });
    }
    
    const hashedPassword = await bcrypt.hash(password, 10);
    const user = new User({
      name,
      email,
      password: hashedPassword
    });
    
    // Guardado real usando .save()
    await user.save();
    
    const token = jwt.sign(
      { userId: user._id, email: user.email },
      process.env.JWT_SECRET,
      { expiresIn: '7d' }
    );
    
    res.status(201).json({
      token,
      user: { id: user._id, name: user.name, email: user.email }
    });
  } catch (error) {
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

router.post('/login', async (req, res) => {
  try {
    const { email, password } = req.body;
    
    if (!email || !password) {
      return res.status(400).json({ error: 'Email y contraseña son requeridos' });
    }
    
    // Búsqueda real usando .findOne()
    const user = await User.findOne({ email });
    if (!user) {
      return res.status(401).json({ error: 'Credenciales incorrectas' });
    }
    
    const isValid = await bcrypt.compare(password, user.password);
    if (!isValid) {
      return res.status(401).json({ error: 'Credenciales incorrectas' });
    }
    
    const token = jwt.sign(
      { userId: user._id, email: user.email },
      process.env.JWT_SECRET,
      { expiresIn: '7d' }
    );
    
    res.json({
      token,
      user: { id: user._id, name: user.name, email: user.email }
    });
  } catch (error) {
    res.status(500).json({ error: 'Error interno del servidor' });
  }
});

module.exports = router;