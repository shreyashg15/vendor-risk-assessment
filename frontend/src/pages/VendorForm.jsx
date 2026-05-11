import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { createVendor, getVendorById, updateVendor } from '../services/api';

const VendorForm = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEditMode = !!id;

  const [formData, setFormData] = useState({
    vendorName: '',
    contactPerson: '',
    email: '',
    phone: '',
    description: '',
    riskScore: 50,
    status: 'MEDIUM',
    reviewDate: ''
  });
  const [error, setError] = useState('');

  useEffect(() => {
    if (isEditMode) {
      getVendorById(id)
        .then(res => setFormData(res.data))
        .catch(err => setError('Failed to load vendor details.'));
    }
  }, [id, isEditMode]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (isEditMode) {
        await updateVendor(id, formData);
        alert('Vendor updated successfully!');
      } else {
        await createVendor(formData);
        alert('Vendor created successfully!');
      }
      navigate('/dashboard');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to save vendor. Please check your inputs and permissions.');
    }
  };

  return (
    <div className="p-6 max-w-3xl mx-auto">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold text-gray-800">{isEditMode ? 'Edit Vendor' : 'Add New Vendor'}</h1>
        <button onClick={() => navigate('/dashboard')} className="text-gray-600 hover:text-gray-900">Back</button>
      </div>

      {error && <div className="bg-red-100 text-red-700 p-3 rounded mb-4">{error}</div>}

      <form onSubmit={handleSubmit} className="bg-white shadow rounded-lg p-6">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div>
            <label className="block text-gray-700 text-sm font-bold mb-2">Vendor Name *</label>
            <input type="text" name="vendorName" value={formData.vendorName} onChange={handleChange} required className="w-full p-2 border rounded focus:ring-primary" />
          </div>
          <div>
            <label className="block text-gray-700 text-sm font-bold mb-2">Contact Person</label>
            <input type="text" name="contactPerson" value={formData.contactPerson} onChange={handleChange} className="w-full p-2 border rounded focus:ring-primary" />
          </div>
          <div>
            <label className="block text-gray-700 text-sm font-bold mb-2">Email *</label>
            <input type="email" name="email" value={formData.email} onChange={handleChange} required className="w-full p-2 border rounded focus:ring-primary" />
          </div>
          <div>
            <label className="block text-gray-700 text-sm font-bold mb-2">Phone</label>
            <input type="text" name="phone" value={formData.phone} onChange={handleChange} className="w-full p-2 border rounded focus:ring-primary" />
          </div>
          <div>
            <label className="block text-gray-700 text-sm font-bold mb-2">Initial Risk Score (0-100)</label>
            <input type="number" name="riskScore" min="0" max="100" value={formData.riskScore} onChange={handleChange} className="w-full p-2 border rounded focus:ring-primary" />
          </div>
          <div>
            <label className="block text-gray-700 text-sm font-bold mb-2">Status</label>
            <select name="status" value={formData.status} onChange={handleChange} className="w-full p-2 border rounded focus:ring-primary">
              <option value="LOW">Low Risk</option>
              <option value="MEDIUM">Medium Risk</option>
              <option value="HIGH">High Risk</option>
            </select>
          </div>
          <div className="md:col-span-2">
            <label className="block text-gray-700 text-sm font-bold mb-2">Description</label>
            <textarea name="description" value={formData.description} onChange={handleChange} rows="3" className="w-full p-2 border rounded focus:ring-primary"></textarea>
          </div>
          <div className="md:col-span-2">
            <label className="block text-gray-700 text-sm font-bold mb-2">Next Review Date</label>
            <input type="date" name="reviewDate" value={formData.reviewDate} onChange={handleChange} className="w-full p-2 border rounded focus:ring-primary" />
          </div>
        </div>
        <div className="mt-6 flex justify-end">
          <button type="submit" className="bg-primary text-white font-bold py-2 px-4 rounded hover:bg-blue-800 transition-colors">
            {isEditMode ? 'Update Vendor' : 'Save Vendor'}
          </button>
        </div>
      </form>
    </div>
  );
};

export default VendorForm;
