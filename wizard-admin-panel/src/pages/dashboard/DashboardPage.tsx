import React from 'react';
import { Card, Typography, Space, Statistic, Row, Col } from 'antd';
import { 
  UserOutlined, 
  MedicineBoxOutlined, 
  FileImageOutlined,
  SafetyOutlined 
} from '@ant-design/icons';

const { Title, Text } = Typography;

const DashboardPage: React.FC = () => {
  return (
    <div>
      <Title level={2}>Dashboard WIZARD PACS</Title>
      <Text type="secondary">¡Bienvenido al panel de administración!</Text>
      
      <Row gutter={[16, 16]} style={{ marginTop: '20px' }}>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Usuarios"
              value={25}
              prefix={<UserOutlined style={{ color: '#1890ff' }} />}
            />
          </Card>
        </Col>
        
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Estudios"
              value={1284}
              prefix={<FileImageOutlined style={{ color: '#52c41a' }} />}
            />
          </Card>
        </Col>
        
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Nodos DICOM"
              value={8}
              prefix={<SafetyOutlined style={{ color: '#722ed1' }} />}
            />
          </Card>
        </Col>
        
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="PACS Engine"
              value="ONLINE"
              prefix={<MedicineBoxOutlined style={{ color: '#faad14' }} />}
            />
          </Card>
        </Col>
      </Row>
      
      <Card style={{ marginTop: '20px' }}>
        <Title level={4}>🎉 ¡Sistema WIZARD funcionando!</Title>
        <p>El dashboard básico está cargado correctamente.</p>
        <p><strong>Siguiente paso:</strong> Conectar con el backend y cargar datos reales.</p>
      </Card>
    </div>
  );
};

export default DashboardPage;