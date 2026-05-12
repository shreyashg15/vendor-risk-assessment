import React, { useEffect, useState } from 'react';
import { getVendors, deleteVendor } from '../services/api';
import { Link } from 'react-router-dom';

const VendorList = () => {
  const [vendors, setVendors] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchVendors();
  }, []);

  const fetchVendors = async () => {
    try {
      const response = await getVendors({ page: 0, size: 10 });
      setVendors(response.data.content || []);
    } catch (error) {
      console.error("Failed to fetch vendors", error);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this vendor?')) {
      try {
        await deleteVendor(id);
        fetchVendors(); // Refresh list
      } catch (error) {
        alert('Failed to delete vendor. You may not have the required permissions.');
      }
    }
  };

  return (
    <div className="p-6 max-w-7xl mx-auto">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold text-gray-800">Vendors</h1>
        <Link to="/vendors/new" className="bg-primary text-white px-4 py-2 rounded hover:bg-blue-800">
          Add Vendor
        </Link>
      </div>

      <div className="bg-white shadow rounded-lg overflow-hidden">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Vendor Name</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Contact</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Score</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {loading ? (
              <tr><td colSpan="5" className="px-6 py-4 text-center">Loading...</td></tr>
            ) : vendors.length === 0 ? (
              <tr><td colSpan="5" className="px-6 py-4 text-center">No vendors found.</td></tr>
            ) : (
              vendors.map(vendor => (
                <tr key={vendor.id}>
                  <td className="px-6 py-4 whitespace-nowrap">{vendor.vendorName}</td>
                  <td className="px-6 py-4 whitespace-nowrap">{vendor.email}</td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full 
                      ${vendor.status === 'HIGH' ? 'bg-red-100 text-red-800' : 
                        vendor.status === 'MEDIUM' ? 'bg-yellow-100 text-yellow-800' : 
                        'bg-green-100 text-green-800'}`}>
                      {vendor.status}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">{vendor.riskScore}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <Link to={`/vendors/${vendor.id}`} className="text-primary hover:text-blue-900 mr-3">View</Link>
                    <Link to={`/vendors/${vendor.id}/edit`} className="text-yellow-600 hover:text-yellow-900 mr-3">Edit</Link>
                    <button onClick={() => handleDelete(vendor.id)} className="text-red-600 hover:text-red-900">Delete</button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default VendorList;
