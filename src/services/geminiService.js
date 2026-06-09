const { GoogleGenerativeAI } = require('@google/generative-ai');

const genAI = new GoogleGenerativeAI(process.env.GEMINI_API_KEY);

const SYSTEM_PROMPT = `Eres un asistente de salud mental empático y profesional llamado "MindGuard". 
Tu objetivo es brindar apoyo emocional a personas que pueden estar atravesando momentos difíciles.

REGLAS IMPORTANTES:
- Siempre responde con empatía y sin juzgar
- Si detectas señales de crisis o ideación suicida, proporciona recursos de ayuda inmediata (SAPTEL: 55 5259-8121)
- No des diagnósticos médicos
- Anima al usuario a buscar ayuda profesional cuando sea apropiado
- Mantén un tono cálido, esperanzador y de apoyo
- Responde siempre en español
- Máximo 150 palabras por respuesta para mantener la conversación fluida`;

async function getChatResponse(message, conversationHistory = []) {
  try {
    const model = genAI.getGenerativeModel({ model: 'gemini-1.5-flash' });
    
    const chat = model.startChat({
      history: conversationHistory.map(msg => ({
        role: msg.role === 'user' ? 'user' : 'model',
        parts: [{ text: msg.content }]
      })),
      systemInstruction: SYSTEM_PROMPT
    });
    
    const result = await chat.sendMessage(message);
    const response = result.response.text();
    
    // Análisis básico de riesgo
    const riskKeywords = ['suicidio', 'morir', 'no quiero vivir', 'hacerme daño', 'quitarme la vida'];
    const hasRiskSignal = riskKeywords.some(kw => 
      message.toLowerCase().includes(kw)
    );
    
    return {
      message: response,
      hasRiskSignal,
      timestamp: new Date()
    };
  } catch (error) {
    console.error('Gemini error:', error);
    throw new Error('Error al procesar tu mensaje');
  }
}

module.exports = { getChatResponse };