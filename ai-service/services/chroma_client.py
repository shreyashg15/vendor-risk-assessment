import os
import chromadb
from chromadb.config import Settings
from sentence_transformers import SentenceTransformer

class ChromaClient:
    def __init__(self):
        # Configure ChromaDB to store data locally
        db_path = os.environ.get("CHROMA_DB_PATH", "./chroma_data")
        self.client = chromadb.PersistentClient(path=db_path)
        self.collection = self.client.get_or_create_collection(name="vendor_docs")
        
        # Load embedding model
        # using all-MiniLM-L6-v2 as it's small and fast
        self.encoder = SentenceTransformer('all-MiniLM-L6-v2')

    def add_documents(self, documents, metadatas, ids):
        embeddings = self.encoder.encode(documents).tolist()
        self.collection.add(
            documents=documents,
            embeddings=embeddings,
            metadatas=metadatas,
            ids=ids
        )

    def query(self, query_text, n_results=3):
        query_embedding = self.encoder.encode([query_text]).tolist()
        results = self.collection.query(
            query_embeddings=query_embedding,
            n_results=n_results
        )
        return results
