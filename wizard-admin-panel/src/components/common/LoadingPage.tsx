import React from 'react';
import { Spin, Typography } from 'antd';

const { Text } = Typography;

const LoadingPage: React.FC = () => {
  return (
    <div style={{ 
      display: 'flex', 
      flexDirection: 'column',
      justifyContent: 'center', 
      alignItems: 'center', 
      minHeight: '200px' 
    }}>
      <Spin size="large" />
      <Text style={{ marginTop: '16px' }}>Cargando...</Text>
    </div>
  );
};

export default LoadingPage;