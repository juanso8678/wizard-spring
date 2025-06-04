import React from 'react';
import { Card, Typography, Button, Space } from 'antd';
import { useNavigate } from 'react-router-dom';
import { LoginOutlined, DashboardOutlined } from '@ant-design/icons';

const { Title, Text } = Typography;

const LoginPage: React.FC = () => {
  const navigate = useNavigate();

  const handleGoToDashboard = () => {
    navigate('/dashboard');
  };

  return (
    <div style={{ 
      minHeight: '100vh', 
      display: 'flex', 
      alignItems: 'center', 
      justifyContent: 'center',
      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
    }}>
      <Card style={{ width: 400, textAlign: 'center' }}>
        <Space direction="vertical" size="large" style={{ width: '100%' }}>
          <div>
            <Title level={2} style={{ color: '#1890ff', marginBottom: '8px' }}>
              🏥 WIZARD PACS
            </Title>
            <Text type="secondary">Sistema de Gestión de Imágenes Médicas</Text>
          </div>

          <div>
            <Text strong style={{ color: '#52c41a', fontSize: '16px' }}>
              ✅ Login Page cargada correctamente!
            </Text>
          </div>

          <div>
            <Text type="secondary" style={{ display: 'block', marginBottom: '16px' }}>
              Sistema básico funcionando. Navegación habilitada.
            </Text>
            
            <Space direction="vertical" style={{ width: '100%' }}>
              <Button 
                type="primary" 
                icon={<DashboardOutlined />}
                size="large"
                onClick={handleGoToDashboard}
                style={{ width: '100%' }}
              >
                Ir al Dashboard
              </Button>
              
              <Button 
                icon={<LoginOutlined />}
                size="large"
                style={{ width: '100%' }}
                disabled
              >
                Login Real (Próximamente)
              </Button>
            </Space>
          </div>

          <div style={{ borderTop: '1px solid #f0f0f0', paddingTop: '16px' }}>
            <Text type="secondary" style={{ fontSize: '12px' }}>
              WIZARD PACS System v1.0.0<br />
              © 2024 Wizard Healthcare Solutions
            </Text>
          </div>
        </Space>
      </Card>
    </div>
  );
};

export default LoginPage;