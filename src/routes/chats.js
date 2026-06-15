const express = require('express');
const authMiddleware = require('../middleware/authMiddleware');
const { getChatResponse } = require('../services/geminiService');
const ChatSession = require('../models/ChatSession');
const router = express.Router();

// Obtener todas las sesiones de un usuario
router.get('/sessions', authMiddleware, async (req, res) => {
  try {
    const userSessions = await ChatSession.find({ userId: req.userId }).sort({ updatedAt: -1 });
    res.json(userSessions.map(s => ({
      id: s._id,
      title: s.title,
      lastMessage: s.messages[s.messages.length - 1]?.content || '',
      updatedAt: s.updatedAt,
      messageCount: s.messages.length
    })));
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// Crear nueva sesión de chat
router.post('/sessions', authMiddleware, async (req, res) => {
  try {
    const count = await ChatSession.countDocuments({ userId: req.userId });
    
    const session = new ChatSession({
      userId: req.userId,
      title: req.body.title || `Sesión ${count + 1}`,
      messages: []
    });
    
    await session.save();
    
    res.status(201).json({
      id: session._id,
      title: session.title,
      messages: session.messages,
      createdAt: session.createdAt,
      updatedAt: session.updatedAt
    });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// Obtener mensajes de una sesión
router.get('/sessions/:sessionId/messages', authMiddleware, async (req, res) => {
  try {
    // Para implementar una paginación sencilla usando limit y skip de Mongoose,
    // primero consultamos el documento para obtener la cantidad total de mensajes.
    const sessionDoc = await ChatSession.findOne({ _id: req.params.sessionId, userId: req.userId });
    
    if (!sessionDoc) {
      return res.status(404).json({ error: 'Sesión no encontrada' });
    }
    
    const totalMessages = sessionDoc.messages.length;
    const limit = 50;
    const skip = Math.max(0, totalMessages - limit);
    
    // Proyectamos el array usando limit y skip mediante la propiedad $slice en Mongoose
    const session = await ChatSession.findOne(
      { _id: req.params.sessionId, userId: req.userId },
      { messages: { $slice: [skip, limit] } }
    );
    
    const messages = session.messages.map(m => ({
      id: m._id,
      role: m.role,
      content: m.content,
      hasRiskSignal: m.hasRiskSignal,
      timestamp: m.timestamp
    }));
    
    res.json(messages);
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
    
    const session = await ChatSession.findOne({ _id: req.params.sessionId, userId: req.userId });
    
    if (!session) {
      return res.status(404).json({ error: 'Sesión no encontrada' });
    }
    
    // Agregar mensaje del usuario
    const userMessage = {
      role: 'user',
      content: message,
      timestamp: new Date()
    };
    session.messages.push(userMessage);
    
    // Obtener respuesta de Gemini pasando los últimos 10 mensajes
    const aiResponse = await getChatResponse(message, session.messages.slice(-10));
    
    const aiMessage = {
      role: 'assistant',
      content: aiResponse.message,
      hasRiskSignal: aiResponse.hasRiskSignal,
      timestamp: new Date()
    };
    session.messages.push(aiMessage);
    
    await session.save();
    
    const savedUserMessage = session.messages[session.messages.length - 2];
    const savedAiMessage = session.messages[session.messages.length - 1];
    
    res.json({
      userMessage: {
        id: savedUserMessage._id,
        role: savedUserMessage.role,
        content: savedUserMessage.content,
        timestamp: savedUserMessage.timestamp
      },
      aiMessage: {
        id: savedAiMessage._id,
        role: savedAiMessage.role,
        content: savedAiMessage.content,
        hasRiskSignal: savedAiMessage.hasRiskSignal,
        timestamp: savedAiMessage.timestamp
      },
      sessionId: session._id
    });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

module.exports = router;