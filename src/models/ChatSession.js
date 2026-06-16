const { getDB } = require('../config/db');
const { v4: uuidv4 } = require('uuid');

const ChatSession = {
  /**
   * Obtener todas las sesiones de un usuario, ordenadas por última actualización.
   */
  findByUserId(userId) {
    const db = getDB();
    return db.prepare(
      'SELECT * FROM chat_sessions WHERE userId = ? ORDER BY updatedAt DESC'
    ).all(userId);
  },

  /**
   * Buscar una sesión por ID verificando que pertenezca al usuario.
   */
  findById(id, userId) {
    const db = getDB();
    return db.prepare(
      'SELECT * FROM chat_sessions WHERE id = ? AND userId = ?'
    ).get(id, userId);
  },

  /**
   * Crear una nueva sesión de chat.
   */
  create({ userId, title }) {
    const db = getDB();
    const id = uuidv4();
    const now = new Date().toISOString();

    db.prepare(
      'INSERT INTO chat_sessions (id, userId, title, createdAt, updatedAt) VALUES (?, ?, ?, ?, ?)'
    ).run(id, userId, title, now, now);

    return { id, userId, title, createdAt: now, updatedAt: now };
  },

  /**
   * Contar sesiones de un usuario.
   */
  countByUserId(userId) {
    const db = getDB();
    const row = db.prepare(
      'SELECT COUNT(*) as count FROM chat_sessions WHERE userId = ?'
    ).get(userId);
    return row.count;
  },

  /**
   * Actualizar el timestamp updatedAt de una sesión.
   */
  touch(id) {
    const db = getDB();
    const now = new Date().toISOString();
    db.prepare(
      'UPDATE chat_sessions SET updatedAt = ? WHERE id = ?'
    ).run(now, id);
  }
};

const Message = {
  /**
   * Obtener los últimos N mensajes de una sesión.
   */
  getBySessionId(sessionId, limit = 50) {
    const db = getDB();
    // Subconsulta para obtener los últimos N, luego ordenar cronológicamente
    return db.prepare(`
      SELECT * FROM (
        SELECT * FROM messages
        WHERE sessionId = ?
        ORDER BY timestamp DESC
        LIMIT ?
      ) ORDER BY timestamp ASC
    `).all(sessionId, limit);
  },

  /**
   * Insertar un mensaje nuevo.
   */
  create({ sessionId, role, content, hasRiskSignal = false }) {
    const db = getDB();
    const id = uuidv4();
    const timestamp = new Date().toISOString();

    db.prepare(
      'INSERT INTO messages (id, sessionId, role, content, hasRiskSignal, timestamp) VALUES (?, ?, ?, ?, ?, ?)'
    ).run(id, sessionId, role, content, hasRiskSignal ? 1 : 0, timestamp);

    // Actualizar el updatedAt de la sesión
    ChatSession.touch(sessionId);

    return { id, sessionId, role, content, hasRiskSignal, timestamp };
  },

  /**
   * Obtener el último mensaje de una sesión.
   */
  getLastBySessionId(sessionId) {
    const db = getDB();
    return db.prepare(
      'SELECT * FROM messages WHERE sessionId = ? ORDER BY timestamp DESC LIMIT 1'
    ).get(sessionId);
  }
};

module.exports = { ChatSession, Message };
