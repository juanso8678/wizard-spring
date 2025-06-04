import React from 'react';
import { Layout, Typography, Button, Space } from 'antd';
import { Outlet, useNavigate } from 'react-router-dom';
import { 
  MedicineBoxOutlined, 
  LogoutOutlined, 
  DashboardOutlined,
  UserOutlined 
} from '@ant-design/icons';

const { Header, Content } = Layout;
const { Title, Text } = Typography;

const MainLayout: React.FC = () => {
  const navigate = useNavigate();

  const handleLogout = () => {
    navigate('/login');
  };

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Header style={{ 
        background: '#001529', 
        padding: '0 20px',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center'
      }}>
        <Space>
          <MedicineBoxOutlined style={{ color: '#1890ff', fontSize: '24px' }} />
          <Title level={3} style={{ color: 'white', margin: 0 }}>
            WIZARD PACS System
          </Title>
        </Space>

        <Space>
          <Text style={{ color: '#rgba(255, 255, 255, 0.7)' }}>
            👨‍⚕️ Usuario Administrador
          </Text>
          <Button 
            type="text" 
            icon={<LogoutOutlined />}
            onClick={handleLogout}
            style={{ color: 'white' }}
          >
            Salir
          </Button>
        </Space>
      </Header>

      <Content style={{ 
        padding: '24px',
        background: '#f0f2f5',
        minHeight: 'calc(100vh - 64px)'
      }}>
        <Outlet />
      </Content>
    </Layout>
  );
};

export default MainLayout;