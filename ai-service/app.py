import os
import logging
from flask import Flask, jsonify
from flask_limiter import Limiter
from flask_limiter.util import get_remote_address
from flask_cors import CORS
from routes.ai_routes import ai_bp

logging.basicConfig(level=logging.INFO)

app = Flask(__name__)
CORS(app)

# Basic error handling
@app.errorhandler(404)
def not_found(e):
    return jsonify(error="Not Found", message=str(e)), 404

@app.errorhandler(500)
def internal_error(e):
    return jsonify(error="Internal Server Error", message=str(e)), 500

# Rate Limiter
limiter = Limiter(
    get_remote_address,
    app=app,
    default_limits=["30 per minute"],
    storage_uri=os.environ.get("REDIS_URL", "redis://localhost:6379")
)

# Register Blueprints
app.register_blueprint(ai_bp, url_prefix='/api/ai')

# Health check
@app.route('/health', methods=['GET'])
def health():
    return jsonify({
        "status": "UP",
        "service": "ai-service",
        "model": "llama3-8b-8192",
        "cached": False
    }), 200

if __name__ == '__main__':
    port = int(os.environ.get("AI_PORT", 5000))
    app.run(host='0.0.0.0', port=port, debug=True)
