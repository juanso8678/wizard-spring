window.config = {
  routerBasename: '/',
  
  // Branding Wizard
  appTitle: 'Wizard DICOM Viewer',
  
  dataSources: [
    {
      namespace: '@ohif/extension-default.dataSourcesModule.dicomweb',
      sourceName: 'dicomweb',
      configuration: {
        friendlyName: 'Wizard PACS Engine',
        name: 'wizard-pacs-engine',
        
        // URLs apuntando al PACS Engine (DCM4CHEE oculto)
        wadoUriRoot: 'http://localhost:8081/dcm4chee-arc/aets/WIZARD_PACS/wado',
        qidoRoot: 'http://localhost:8081/dcm4chee-arc/aets/WIZARD_PACS/rs',
        wadoRoot: 'http://localhost:8081/dcm4chee-arc/aets/WIZARD_PACS/rs',
        
        qidoSupportsIncludeField: false,
        supportsReject: true,
        imageRendering: 'wadors',
        thumbnailRendering: 'wadors',
        enableStudyLazyLoad: true,
        supportsFuzzyMatching: false,
        supportsWildcard: true,
      },
    },
  ],
  
  defaultDataSourceName: 'dicomweb',
  
  // UI Customization
  showStudyList: true,
  maxNumberOfWebWorkers: 3,
  
  // Branding
  whiteLabeling: {
    createLogoComponentFn: function(React) {
      return React.createElement(
        'div',
        { style: { color: '#fff', fontSize: '18px', fontWeight: 'bold' } },
        'Wizard DICOM Viewer'
      );
    },
  },
  
  hotkeys: [
    {
      commandName: 'incrementActiveViewport',
      label: 'Next Viewport',
      keys: ['right'],
    },
    {
      commandName: 'decrementActiveViewport',
      label: 'Previous Viewport',
      keys: ['left'],
    },
  ],
};