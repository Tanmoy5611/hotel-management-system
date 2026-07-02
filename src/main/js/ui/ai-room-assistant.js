import { initChat } from './ai-assistant/chat.js';
import { initRecommendations } from './ai-assistant/recommendations.js';

export function initAiRoomAssistant() {
  // Shared entry point for every AI widget loaded from the site bundle
  initRecommendations();
  initChat();
}
