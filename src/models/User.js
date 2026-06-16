const { getDB } = require('../config/db');
const { v4: uuidv4 } = require('uuid');

const User = {
  findByEmail(email) {
    const db = getDB();
    return db.prepare('SELECT * FROM users WHERE email = ?').get(email);
  },

  findById(id) {
    const db = getDB();
    return db.prepare('SELECT * FROM users WHERE id = ?').get(id);
  },

  create({ name, email, password }) {
    const db = getDB();
    const id = uuidv4();
    const createdAt = new Date().toISOString();

    db.prepare(
      'INSERT INTO users (id, name, email, password, createdAt) VALUES (?, ?, ?, ?, ?)'
    ).run(id, name, email.toLowerCase().trim(), password, createdAt);

    return { id, name, email: email.toLowerCase().trim(), createdAt };
  }
};

module.exports = User;
