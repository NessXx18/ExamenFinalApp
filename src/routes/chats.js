const express = require('express');
const authMiddleware = require('../middleware/authMiddleware');
const { getChatResponse } = require('../services/geminiService');
const { ChatSession, Message } = require('../models/ChatSession');
const router = express.Router();

// Obtener todas las sesiones de un usuario
router.get('/sessions', authMiddleware, async (req, res) => {
  try {
    const sessions = ChatSession.findByUserId(req.userId);

    res.json(sessions.map(s => {
      // Obtener el último mensaje de la sesión
      const lastMsg = Message.getLastBySessionId(s.id);
      return {
        id: s.id,
        title: s.title,
        createdAt: s.createdAt,
        lastMessage: lastMsg ? lastMsg.content : null,
        updatedAt: s.updatedAt
      };
    }));
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// Crear nueva sesión de chat
router.post('/sessions', authMiddleware, async (req, res) => {
  try {
    const count = ChatSession.countByUserId(req.userId);

    const session = ChatSession.create({
      userId: req.userId,
      title: req.body.title || `Sesión ${count + 1}`
    });

    res.status(201).json({
      id: session.id,
      title: session.title,
      createdAt: session.createdAt,
      lastMessage: null,
      updatedAt: session.updatedAt
    });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// Obtener mensajes de una sesión
router.get('/sessions/:sessionId/messages', authMiddleware, async (req, res) => {
  try {
    const session = ChatSession.findById(req.params.sessionId, req.userId);

    if (!session) {
      return res.status(404).json({ error: 'Sesión no encontrada' });
    }

    const messages = Message.getBySessionId(req.params.sessionId, 50);

    res.json(messages.map(m => ({
      id: m.id,
      role: m.role,
      content: m.content,
      hasRiskSignal: m.hasRiskSignal === 1,
      timestamp: m.timestamp
    })));
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// Enviar mensaje y obtener respuesta de IA
router.post('/sessions/:sessionId/messages', authMiddleware, async (req, res) => {
  try {
    const { message } = req.body;
    if (!message?.trim()) {
      return res.status(400).json({ error: 'El mensaje no puede estar vacío' });
    }

    const session = ChatSession.findById(req.params.sessionId, req.userId);

    if (!session) {
      return res.status(404).json({ error: 'Sesión no encontrada' });
    }

    // Agregar mensaje del usuario
    const userMessage = Message.create({
      sessionId: session.id,
      role: 'user',
      content: message
    });

    // Obtener los últimos 10 mensajes para contexto de Gemini
    const recentMessages = Message.getBySessionId(session.id, 10);

    // Obtener respuesta de Gemini
    const aiResponse = await getChatResponse(message, recentMessages);

    const aiMessage = Message.create({
      sessionId: session.id,
      role: 'assistant',
      content: aiResponse.message,
      hasRiskSignal: aiResponse.hasRiskSignal
    });

    res.json({
      userMessage: {
        id: userMessage.id,
        role: userMessage.role,
        content: userMessage.content,
        timestamp: userMessage.timestamp
      },
      aiMessage: {
        id: aiMessage.id,
        role: aiMessage.role,
        content: aiMessage.content,
        hasRiskSignal: aiMessage.hasRiskSignal,
        timestamp: aiMessage.timestamp
      },
      sessionId: session.id
    });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

module.exports = router;