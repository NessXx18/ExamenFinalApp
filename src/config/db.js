const Database = require('better-sqlite3');
const path = require('path');

const DB_PATH = path.join(__dirname, '..', '..', 'mindguard.db');

let db;

const connectDB = () => {
  try {
    db = new Database(DB_PATH);

    // Activar WAL para mejor rendimiento concurrente
    db.pragma('journal_mode = WAL');
    db.pragma('foreign_keys = ON');

    // Crear tablas si no existen
    db.exec(`
      CREATE TABLE IF NOT EXISTS users (
        id TEXT PRIMARY KEY,
        name TEXT NOT NULL,
        email TEXT NOT NULL UNIQUE COLLATE NOCASE,
        password TEXT NOT NULL,
        createdAt TEXT DEFAULT (datetime('now'))
      );

      CREATE TABLE IF NOT EXISTS chat_sessions (
        id TEXT PRIMARY KEY,
        userId TEXT NOT NULL,
        title TEXT NOT NULL,
        createdAt TEXT DEFAULT (datetime('now')),
        updatedAt TEXT DEFAULT (datetime('now')),
        FOREIGN KEY (userId) REFERENCES users(id)
      );

      CREATE TABLE IF NOT EXISTS messages (
        id TEXT PRIMARY KEY,
        sessionId TEXT NOT NULL,
        role TEXT NOT NULL CHECK(role IN ('user', 'assistant')),
        content TEXT NOT NULL,
        hasRiskSignal INTEGER DEFAULT 0,
        timestamp TEXT DEFAULT (datetime('now')),
        FOREIGN KEY (sessionId) REFERENCES chat_sessions(id)
      );
    `);

    console.log(`SQLite Connected: ${DB_PATH}`);
  } catch (error) {
    console.error(`SQLite Connection Error: ${error.message}`);
    process.exit(1);
  }
};

const getDB = () => {
  if (!db) {
    throw new Error('Database not initialized. Call connectDB() first.');
  }
  return db;
};

module.exports = connectDB;
module.exports.getDB = getDB;
