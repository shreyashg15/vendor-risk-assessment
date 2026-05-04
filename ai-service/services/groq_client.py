import json
import os
import re
from dataclasses import dataclass
from typing import Any

from groq import Groq


MODEL = "llama-3.3-70b-versatile"

SYSTEM_PROMPT = """
You are a senior vendor risk analyst.

Analyze vendor risk and respond ONLY in valid JSON.

STRICT RULES:
- Output ONLY JSON
- No explanation outside JSON
- No markdown
- No extra text

FORMAT:
{
  "category": "Low Risk | Medium Risk | High Risk",
  "confidence": 0.0,
  "reasoning": "...",
  "signals": {
    "positive": [],
    "negative": []
  },
  "suggested_actions": []
}
"""

USER_TEMPLATE = """Analyze vendor:

Vendor: {vendor_name}
Industry: {industry}
Region: {region}

{text}
"""

PROMPT_INJECTION_PATTERNS = [
    r"ignore\s+previous",
    r"system\s+prompt",
]


class InputValidationError(Exception):
    pass


@dataclass
class ClassificationResult:
    data: dict[str, Any]
    model: str
    tokens_used: int


def sanitize_vendor_text(text: str):
    if not text or len(text.strip()) < 10:
        raise InputValidationError("Text must be at least 10 characters")

    flagged = any(re.search(p, text.lower()) for p in PROMPT_INJECTION_PATTERNS)
    return text.strip(), flagged


class GroqRiskClient:
    def __init__(self, model=MODEL):
        self.client = None
        self.model = model

    def _get_client(self):
        if self.client is None:
            self.client = Groq(api_key=os.getenv("GROQ_API_KEY"))
        return self.client

    def classify_vendor_risk(self, vendor_name, text, industry, region, contract_value):
        prompt = USER_TEMPLATE.format(
            vendor_name=vendor_name,
            industry=industry,
            region=region,
            text=text,
        )

        client = self._get_client()

        response = client.chat.completions.create(
            model=self.model,
            messages=[
                {"role": "system", "content": SYSTEM_PROMPT},
                {"role": "user", "content": prompt},
            ],
            temperature=0.2,
        )

        content = response.choices[0].message.content

        try:
            data = json.loads(content)
        except json.JSONDecodeError:
            retry_prompt = prompt + "\n\nReply ONLY JSON."

            response = client.chat.completions.create(
                model=self.model,
                messages=[
                    {"role": "system", "content": SYSTEM_PROMPT},
                    {"role": "user", "content": retry_prompt},
                ],
                temperature=0.2,
            )

            try:
                data = json.loads(response.choices[0].message.content)
            except json.JSONDecodeError:
                data = {
                    "category": "Medium Risk",
                    "confidence": 0.5,
                    "reasoning": "Fallback due to invalid AI response",
                    "signals": {"positive": [], "negative": []},
                    "suggested_actions": ["Review manually"],
                }

        usage = getattr(response, "usage", None)
        tokens_used = getattr(usage, "total_tokens", 0) if usage else 0

        return ClassificationResult(
            data=data,
            model=self.model,
            tokens_used=tokens_used,
        )
