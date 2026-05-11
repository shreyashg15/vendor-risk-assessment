import os
import time
import json
import logging
from groq import Groq, GroqError

logger = logging.getLogger(__name__)

class GroqClient:
    def __init__(self):
        self.api_key = os.getenv("GROQ_API_KEY")
        if self.api_key:
            self.client = Groq(api_key=self.api_key)
        else:
            self.client = None
            logger.warning("GROQ_API_KEY is not set. AI will use fallback templates.")

    def generate_response(self, prompt, max_tokens=1024, temperature=0.7):
        start_time = time.time()
        meta = {
            "cached": False,
            "is_fallback": False,
            "model_used": "llama3-8b-8192",
            "tokens_used": 0,
            "response_time_ms": 0,
            "confidence": 1.0
        }

        # Fallback if no API key
        if not self.client:
            return self._get_fallback_response(prompt, start_time, meta)

        try:
            # Add retries logic (simplified 3-retry)
            for attempt in range(3):
                try:
                    chat_completion = self.client.chat.completions.create(
                        messages=[
                            {"role": "system", "content": "You are a professional vendor risk analyst."},
                            {"role": "user", "content": prompt}
                        ],
                        model="llama3-8b-8192",
                        max_tokens=max_tokens,
                        temperature=temperature,
                    )
                    
                    response_text = chat_completion.choices[0].message.content
                    meta["tokens_used"] = chat_completion.usage.total_tokens
                    meta["response_time_ms"] = int((time.time() - start_time) * 1000)
                    
                    return {"data": response_text, "meta": meta}
                
                except GroqError as e:
                    logger.error(f"Groq API Error on attempt {attempt+1}: {e}")
                    if attempt == 2:
                        raise e
                    time.sleep(2 ** attempt) # Exponential backoff
                    
        except Exception as e:
            logger.error(f"Failed to generate response: {e}")
            return self._get_fallback_response(prompt, start_time, meta)

    def _get_fallback_response(self, prompt, start_time, meta):
        meta["is_fallback"] = True
        meta["response_time_ms"] = int((time.time() - start_time) * 1000)
        meta["confidence"] = 0.0
        
        # Determine which fallback to use based on the prompt content
        if "describe" in prompt.lower() or "summary" in prompt.lower():
            data = "This is a placeholder fallback description. The vendor provides critical services but lacks proper compliance documentation. Review immediately."
        elif "recommend" in prompt.lower():
            data = [
                {"action_type": "AUDIT", "description": "Conduct a full security audit.", "priority": "HIGH"},
                {"action_type": "REVIEW", "description": "Review recent compliance certificates.", "priority": "MEDIUM"}
            ]
        elif "categorise" in prompt.lower() or "category" in prompt.lower():
            data = {"category": "Technology & IT", "confidence": 0.85, "reasoning": "Fallback reasoning based on keywords."}
        elif "generate_report" in prompt.lower():
             data = {"title": "Vendor Risk Report", "executive_summary": "Fallback summary.", "overview": "Fallback overview", "top_items": ["Risk 1", "Risk 2"], "recommendations": ["Audit", "Review"]}
        else:
            data = "Fallback response generated because Groq API is unavailable."

        # Return json serialized string if it's a dict/list to mimic LLM json output
        if isinstance(data, (dict, list)):
             data = json.dumps(data)

        return {"data": data, "meta": meta}
