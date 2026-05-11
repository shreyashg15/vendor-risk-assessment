import json
import logging
from flask import Blueprint, request, jsonify, Response
from services.groq_client import GroqClient
from services.chroma_client import ChromaClient

ai_bp = Blueprint('ai_bp', __name__)
logger = logging.getLogger(__name__)

groq_client = GroqClient()
# Lazy initialization for ChromaClient to save startup time if not needed immediately
chroma_client = None

def get_chroma_client():
    global chroma_client
    if not chroma_client:
        chroma_client = ChromaClient()
    return chroma_client

# Helper for input sanitisation
def sanitize_input(text):
    if not text:
        return ""
    # Strip basic HTML and potential prompt injection patterns
    sanitized = text.replace("<", "&lt;").replace(">", "&gt;")
    # Very basic injection prevention
    if "ignore all previous instructions" in sanitized.lower():
        raise ValueError("Invalid input detected.")
    return sanitized

@ai_bp.route('/describe', methods=['POST'])
def describe():
    try:
        data = request.json
        vendor_data = sanitize_input(json.dumps(data.get("vendor", {})))
        
        prompt = f"""
        Analyze the following vendor data and provide a concise, professional risk description (max 3 sentences).
        Vendor Data: {vendor_data}
        """
        response = groq_client.generate_response(prompt, max_tokens=150)
        return jsonify(response)
    except Exception as e:
        logger.error(f"Error in /describe: {e}")
        return jsonify({"error": str(e)}), 400

@ai_bp.route('/categorise', methods=['POST'])
def categorise():
    try:
        data = request.json
        vendor_data = sanitize_input(json.dumps(data.get("vendor", {})))
        
        prompt = f"""
        Categorise the following vendor into one of these categories: [Technology & IT, Financial Services, Operations, HR & Training, Marketing].
        Vendor Data: {vendor_data}
        Return ONLY a JSON object with 'category', 'confidence' (0.0-1.0), and 'reasoning'.
        """
        response = groq_client.generate_response(prompt, max_tokens=100)
        # We try to parse the string to actual JSON if Groq gave a string
        if isinstance(response.get("data"), str):
            try:
                response["data"] = json.loads(response["data"])
            except json.JSONDecodeError:
                pass # leave as string if parsing fails
        return jsonify(response)
    except Exception as e:
        return jsonify({"error": str(e)}), 400

@ai_bp.route('/recommend', methods=['POST'])
def recommend():
    try:
        data = request.json
        vendor_data = sanitize_input(json.dumps(data.get("vendor", {})))
        
        prompt = f"""
        Based on this vendor data, provide exactly 3 actionable recommendations to mitigate risk.
        Vendor Data: {vendor_data}
        Return ONLY a JSON array of objects, each with 'action_type' (e.g. AUDIT, REVIEW), 'description', and 'priority' (HIGH/MEDIUM/LOW).
        """
        response = groq_client.generate_response(prompt, max_tokens=250)
        if isinstance(response.get("data"), str):
            try:
                response["data"] = json.loads(response["data"])
            except json.JSONDecodeError:
                pass
        return jsonify(response)
    except Exception as e:
        return jsonify({"error": str(e)}), 400

@ai_bp.route('/generate-report', methods=['POST'])
def generate_report():
    try:
        data = request.json
        vendor_data = sanitize_input(json.dumps(data.get("vendor", {})))
        
        prompt = f"""
        Generate a comprehensive risk report for this vendor.
        Vendor Data: {vendor_data}
        Return a JSON object with keys: title, executive_summary, overview, top_items (array of strings), recommendations (array of strings).
        """
        response = groq_client.generate_response(prompt, max_tokens=1000)
        if isinstance(response.get("data"), str):
            try:
                response["data"] = json.loads(response["data"])
            except json.JSONDecodeError:
                pass
        return jsonify(response)
    except Exception as e:
        return jsonify({"error": str(e)}), 400

@ai_bp.route('/query', methods=['POST'])
def query_rag():
    try:
        data = request.json
        question = sanitize_input(data.get("question", ""))
        
        client = get_chroma_client()
        results = client.query(question, n_results=3)
        
        # Extract context
        context_docs = results['documents'][0] if results['documents'] else []
        context_str = "\n".join(context_docs)
        
        prompt = f"""
        Answer the following question based ONLY on the provided context. If the answer is not in the context, say "I don't know based on the provided documents."
        Context:
        {context_str}
        
        Question: {question}
        """
        response = groq_client.generate_response(prompt, max_tokens=300)
        response["sources"] = results['metadatas'][0] if results['metadatas'] else []
        return jsonify(response)
    except Exception as e:
        return jsonify({"error": str(e)}), 400
