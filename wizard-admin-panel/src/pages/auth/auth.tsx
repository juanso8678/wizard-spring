// ============================================================================
// PÁGINA DE LOGIN - WIZARD Admin Panel
// ============================================================================

import React, { useState, useEffect } from 'react';
import { 
  Form, 
  Input, 
  Button, 
  Card, 
  Typography, 
  Space, 
  Alert,
  Row,
  Col,
  Divider
} from 'antd';
import { 
  UserOutlined, 
  LockOutlined, 
  MedicineBoxOutlined,
  SafetyOutlined,
  EyeInvisibleOutlined,
  EyeTwoTone
} from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { useAuth, useUI } from '@/store';
import { apiService } from '@/services/api';
import { notify } from '@/utils';

const { Title, Text, Paragraph } = Typography;

// ============================================================================
// INTERFAZ DE DATOS DEL FORMULARIO
// ============================================================================

interface LoginFormData {
  usernameOrEmail: string;
  password: string;
}

// ============================================================================
// COMPONENTE PRINCIPAL
// ============================================================================

const LoginPage: React.FC = () => {
  const navigate = useNavigate();
  const { login, isAuthenticated } = useAuth();
  const { setLoading } = useUI();
  
  const [form] = Form.useForm();
  const [loginError, setLoginError] = useState<string>('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  // Redireccionar si ya está autenticado
  useEffect(() => {
    if (isAuthenticated) {
      navigate('/dashboard');
    }
  }, [isAuthenticated, navigate]);

  // Manejar el envío del formulario
  const handleSubmit = async (values: LoginFormData) => {
    setIsSubmitting(true);
    setLoginError('');
    setLoading(true);

    try {
      console.log('🔍 [LOGIN] Attempting login for:', values.usernameOrEmail);
      
      // Realizar login a través del servicio API
      const response = await apiService.login({
        usernameOrEmail: values.usernameOrEmail,
        password: values.password
      });

      console.log('✅ [LOGIN] API response received:', response);

      // Por ahora, crear un usuario temporal con datos básicos
      // TODO: Obtener datos reales del usuario desde el backend
      const userData = {
        id: 'temp-user-id',
        email: values.usernameOrEmail,
        username: values.usernameOrEmail,
        name: 'Usuario Administrador',
        role: 'ADMIN_ORGANIZACION', // Por defecto
        activo: true,
        organizationId: undefined // Se puede establecer después
      };

      // Actualizar el estado global
      login(userData, response.token);

      notify.success('Sesión iniciada correctamente');
      
      // Redireccionar al dashboard
      navigate('/dashboard');

    } catch (error: any) {
      console.error('❌ [LOGIN] Error:', error);
      
      const errorMessage = error.message || 'Error de autenticación. Verifica tus credenciales.';
      setLoginError(errorMessage);
      notify.error(errorMessage);
    } finally {
      setIsSubmitting(false);
      setLoading(false);
    }
  };

  // Función para probar credenciales por defecto (desarrollo)
  const handleTestLogin = () => {
    form.setFieldsValue({
      usernameOrEmail: 'admin@wizard.es',
      password: 'admin123'
    });
  };

  return (
    <div style={{
      minHeight: '100vh',
      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      padding: '20px'
    }}>
      <Row style={{ width: '100%', maxWidth: '1200px' }}>
        <Col xs={24} md={12} lg={14} style={{ display: 'flex', alignItems: 'center' }}>
          {/* Panel izquierdo - Información del sistema */}
          <div style={{ 
            color: '#fff', 
            padding: '0 40px',
            textAlign: 'center',
            width: '100%'
          }}>
            <Space direction="vertical" size="large" style={{ width: '100%' }}>
              <div>
                <MedicineBoxOutlined style={{ fontSize: '80px', color: '#fff' }} />
                <Title level={1} style={{ color: '#fff', margin: '20px 0 10px' }}>
                  WIZARD PACS
                </Title>
                <Text style={{ color: '#f0f0f0', fontSize: '18px' }}>
                  Sistema de Gestión de Imágenes Médicas
                </Text>
              </div>

              <div style={{ textAlign: 'left', maxWidth: '400px', margin: '0 auto' }}>
                <Title level={3} style={{ color: '#fff', marginBottom: '20px' }}>
                  Características del Sistema:
                </Title>
                
                <Space direction="vertical" size="middle">
                  <div style={{ display: 'flex', alignItems: 'center' }}>
                    <SafetyOutlined style={{ fontSize: '20px', marginRight: '12px', color: '#91d5ff' }} />
                    <Text style={{ color: '#f0f0f0' }}>Gestión segura de estudios DICOM</Text>
                  </div>
                  
                  <div style={{ display: 'flex', alignItems: 'center' }}>
                    <UserOutlined style={{ fontSize: '20px', marginRight: '12px', color: '#91d5ff' }} />
                    <Text style={{ color: '#f0f0f0' }}>Control de acceso multi-organizacional</Text>
                  </div>
                  
                  <div style={{ display: 'flex', alignItems: 'center' }}>
                    <MedicineBoxOutlined style={{ fontSize: '20px', marginRight: '12px', color: '#91d5ff' }} />
                    <Text style={{ color: '#f0f0f0' }}>Integración completa con PACS Engine</Text>
                  </div>
                </Space>
              </div>
            </Space>
          </div>
        </Col>

        <Col xs={24} md={12} lg={10}>
          {/* Panel derecho - Formulario de login */}
          <Card
            style={{
              maxWidth: '400px',
              margin: '0 auto',
              borderRadius: '12px',
              boxShadow: '0 8px 32px rgba(0, 0, 0, 0.1)'
            }}
            bodyStyle={{ padding: '40px' }}
          >
            <div style={{ textAlign: 'center', marginBottom: '30px' }}>
              <Title level={2} style={{ marginBottom: '8px' }}>
                Iniciar Sesión
              </Title>
              <Text type="secondary">
                Accede al panel de administración WIZARD
              </Text>
            </div>

            {/* Mostrar error si existe */}
            {loginError && (
              <Alert
                message="Error de Autenticación"
                description={loginError}
                type="error"
                showIcon
                style={{ marginBottom: '20px' }}
                closable
                onClose={() => setLoginError('')}
              />
            )}

            {/* Formulario de login */}
            <Form
              form={form}
              name="login"
              onFinish={handleSubmit}
              autoComplete="off"
              size="large"
              layout="vertical"
            >
              <Form.Item
                name="usernameOrEmail"
                label="Usuario o Email"
                rules={[
                  { required: true, message: 'Por favor ingresa tu usuario o email' },
                  { min: 3, message: 'Mínimo 3 caracteres' }
                ]}
              >
                <Input
                  prefix={<UserOutlined />}
                  placeholder="usuario@ejemplo.com"
                  autoComplete="username"
                />
              </Form.Item>

              <Form.Item
                name="password"
                label="Contraseña"
                rules={[
                  { required: true, message: 'Por favor ingresa tu contraseña' },
                  { min: 6, message: 'Mínimo 6 caracteres' }
                ]}
              >
                <Input.Password
                  prefix={<LockOutlined />}
                  placeholder="Contraseña"
                  autoComplete="current-password"
                  iconRender={visible => visible ? <EyeTwoTone /> : <EyeInvisibleOutlined />}
                />
              </Form.Item>

              <Form.Item style={{ marginBottom: '16px' }}>
                <Button
                  type="primary"
                  htmlType="submit"
                  loading={isSubmitting}
                  style={{
                    width: '100%',
                    height: '45px',
                    fontSize: '16px',
                    borderRadius: '6px'
                  }}
                >
                  {isSubmitting ? 'Iniciando sesión...' : 'Iniciar Sesión'}
                </Button>
              </Form.Item>
            </Form>

            {/* Panel de desarrollo - Credenciales de prueba */}
            {process.env.NODE_ENV === 'development' && (
              <>
                <Divider>Modo Desarrollo</Divider>
                <div style={{ textAlign: 'center' }}>
                  <Button 
                    type="dashed" 
                    onClick={handleTestLogin}
                    style={{ marginBottom: '16px' }}
                  >
                    Usar credenciales de prueba
                  </Button>
                  
                  <div style={{ fontSize: '12px', color: '#666' }}>
                    <Paragraph style={{ margin: 0, fontSize: '12px' }}>
                      <strong>Credenciales de prueba:</strong><br />
                      Usuario: admin@wizard.es<br />
                      Contraseña: admin123
                    </Paragraph>
                  </div>
                </div>
              </>
            )}

            {/* Footer */}
            <div style={{ textAlign: 'center', marginTop: '24px' }}>
              <Text type="secondary" style={{ fontSize: '12px' }}>
                WIZARD PACS System v1.0.0<br />
                © 2024 Wizard Healthcare Solutions
              </Text>
            </div>
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default LoginPage;