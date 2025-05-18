import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../../AuthContext';

const PrivateRoute = ({ element: Element }) => {
  const { token } = useAuth();
  return token ? <Element /> : <Navigate to="/" replace />;
};

export default PrivateRoute;
