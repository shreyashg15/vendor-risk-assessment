import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getVendorById } from '../services/api';
import axios from 'axios'; // We use raw axios for AI service calls if needed, or via our api service

const VendorDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [vendor, setVendor] = useState(null);
  const [loading, setLoading] = useState(true);
  
  // AI State
  const [aiAnalysis, setAiAnalysis] = useState('');
  const [analyzing, setAnalyzing] = useState(false);

  useEffect(() => {
    getVendorById(id)
      .then(res => setVendor(res.data))
      .catch(err => console.error("Error loading vendor", err))
      .finally(() => setLoading(false));
  }, [id]);

  const runAiAnalysis = async () => {
    setAnalyzing(true);
    try {
      // Direct call to Flask AI Service via proxy or direct (Assuming Backend exposes it or Frontend calls AI service direct)
      // Since Docker compose maps AI to 5000, we can try calling it.
      // Wait, standard practice is frontend calls backend, or frontend calls AI service. 
      // Let's call the AI service directly since it's an MVP.
      const AI_URL = import.meta.env.VITE_AI_URL || 'http://localhost:5000/api/ai';
      const response = await axios.post(`${AI_URL}/describe`, { vendor });
      setAiAnalysis(response.data.data);
    } catch (err) {
      console.error(err);
      setAiAnalysis("Failed to reach AI service.");
    } finally {
      setAnalyzing(false);
    }
  };

  if (loading) return <div className="p-6">Loading...</div>;
  if (!vendor) return <div className="p-6">Vendor not found.</div>;

  return (
    <div className="p-6 max-w-7xl mx-auto grid grid-cols-1 md:grid-cols-3 gap-6">
      
      {/* Left Column: Vendor Details */}
      <div className="md:col-span-2 bg-white shadow rounded-lg p-6">
        <div className="flex justify-between items-center mb-6">
          <h1 className="text-2xl font-bold text-gray-800">{vendor.vendorName}</h1>
          <button onClick={() => navigate('/dashboard')} className="text-gray-600 hover:text-gray-900">Back</button>
        </div>
        
        <div className="grid grid-cols-2 gap-4">
          <div><p className="text-sm text-gray-500">Contact</p><p className="font-medium">{vendor.contactPerson}</p></div>
          <div><p className="text-sm text-gray-500">Email</p><p className="font-medium">{vendor.email}</p></div>
          <div><p className="text-sm text-gray-500">Phone</p><p className="font-medium">{vendor.phone}</p></div>
          <div>
            <p className="text-sm text-gray-500">Status</p>
            <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full 
                      ${vendor.status === 'HIGH' ? 'bg-red-100 text-red-800' : 
                        vendor.status === 'MEDIUM' ? 'bg-yellow-100 text-yellow-800' : 
                        'bg-green-100 text-green-800'}`}>
                      {vendor.status}
            </span>
          </div>
          <div><p className="text-sm text-gray-500">Risk Score</p><p className="font-medium text-lg">{vendor.riskScore}/100</p></div>
          <div><p className="text-sm text-gray-500">Review Date</p><p className="font-medium">{vendor.reviewDate || 'N/A'}</p></div>
          <div className="col-span-2">
            <p className="text-sm text-gray-500">Description</p>
            <p className="font-medium bg-gray-50 p-3 rounded mt-1">{vendor.description}</p>
          </div>
        </div>
      </div>

      {/* Right Column: AI Analysis */}
      <div className="bg-gradient-to-b from-blue-50 to-white shadow rounded-lg p-6 border border-blue-100">
        <h2 className="text-xl font-bold text-primary mb-4 flex items-center">
          <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M13 10V3L4 14h7v7l9-11h-7z"></path></svg>
          AI Risk Analysis
        </h2>
        
        {!aiAnalysis ? (
          <div className="text-center py-8">
            <p className="text-gray-500 mb-4 text-sm">Generate an AI-powered risk assessment using Groq LLaMA-3 based on the vendor profile.</p>
            <button 
              onClick={runAiAnalysis}
              disabled={analyzing}
              className="bg-primary text-white px-4 py-2 rounded hover:bg-blue-800 disabled:opacity-50"
            >
              {analyzing ? 'Analyzing...' : 'Run Analysis'}
            </button>
          </div>
        ) : (
          <div>
            <p className="text-gray-800 text-sm leading-relaxed whitespace-pre-wrap">{aiAnalysis}</p>
            <button onClick={runAiAnalysis} className="mt-4 text-primary text-sm hover:underline">Re-run Analysis</button>
          </div>
        )}
      </div>

    </div>
  );
};

export default VendorDetail;
