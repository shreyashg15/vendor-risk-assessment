import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, useNavigate } from 'react-router-dom';
import Login from './pages/Login';
import VendorList from './pages/VendorList';
import VendorForm from './pages/VendorForm';
import VendorDetail from './pages/VendorDetail';

const ProtectedRoute = ({ children }) => {
  const token = localStorage.getItem('token');
  if (!token) {
    return <Navigate to="/login" replace />;
  }
  return children;
};

const Layout = ({ children }) => {
  const navigate = useNavigate();
  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    navigate('/login');
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <nav className="bg-primary text-white shadow-md p-4 flex justify-between items-center">
        <h1 className="text-xl font-bold cursor-pointer" onClick={() => navigate('/dashboard')}>Tool-41 VRA</h1>
        <button onClick={handleLogout} className="bg-white text-primary px-3 py-1 rounded font-semibold hover:bg-gray-200">
          Logout
        </button>
      </nav>
      <main>
        {children}
      </main>
    </div>
  );
};

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/dashboard" element={
          <ProtectedRoute>
            <Layout><VendorList /></Layout>
          </ProtectedRoute>
        } />
        <Route path="/vendors/new" element={
          <ProtectedRoute>
            <Layout><VendorForm /></Layout>
          </ProtectedRoute>
        } />
        <Route path="/vendors/:id" element={
          <ProtectedRoute>
            <Layout><VendorDetail /></Layout>
          </ProtectedRoute>
        } />
        <Route path="/vendors/:id/edit" element={
          <ProtectedRoute>
            <Layout><VendorForm /></Layout>
          </ProtectedRoute>
        } />
        <Route path="/" element={<Navigate to="/dashboard" replace />} />
      </Routes>
    </Router>
  );
}

export default App;
