// ============================================================================
// WizardPacsEngineService.java - Servicio completo para integración con PACS Engine
// ============================================================================
package com.wizard.core.modules.pacs.integration;

import com.wizard.core.modules.pacs.config.PacsConfig;
import com.wizard.core.modules.pacs.entity.DicomNode;
import com.wizard.core.modules.pacs.entity.Study;
import com.wizard.core.modules.pacs.dto.StudySearchCriteria;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Servicio de integración con Wizard PACS Engine (DCM4CHEE backend)
 * Proporciona funcionalidades completas de PACS incluyendo:
 * - Gestión de AE Titles
 * - Operaciones DICOM (C-ECHO, C-FIND, C-MOVE, C-STORE)
 * - Búsqueda de estudios
 * - Gestión de almacenamiento
 * - Monitoreo y estadísticas
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WizardPacsEngineService {

    private final RestTemplate restTemplate;
    private final PacsConfig pacsConfig;
    
    // URLs base para diferentes operaciones
    private static final String AETS_ENDPOINT = "/dcm4chee-arc/aets";
    private static final String STUDIES_ENDPOINT = "/dcm4chee-arc/aets/{aeTitle}/rs/studies";
    private static final String SERIES_ENDPOINT = "/dcm4chee-arc/aets/{aeTitle}/rs/studies/{studyUID}/series";
    private static final String INSTANCES_ENDPOINT = "/dcm4chee-arc/aets/{aeTitle}/rs/studies/{studyUID}/series/{seriesUID}/instances";
    private static final String DIMSE_ENDPOINT = "/dcm4chee-arc/aets/{callingAET}/dimse/{calledAET}";
    private static final String MONITOR_ENDPOINT = "/dcm4chee-arc/monitor";
    
    // ========== MÉTODOS DE CONECTIVIDAD ==========
    
    /**
     * Verifica la conectividad con Wizard PACS Engine
     */
    public boolean testConnection() {
    // Implementar retry manual
    int maxAttempts = 3;
    for (int attempt = 1; attempt <= maxAttempts; attempt++) {
        try {
            String url = buildUrl(AETS_ENDPOINT);
            HttpHeaders headers = createAuthHeaders();
            HttpEntity<?> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Object> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, Object.class);
            
            boolean connected = response.getStatusCode().is2xxSuccessful();
            log.info("🔗 [WIZARD PACS ENGINE] Connection test: {}", 
                    connected ? "SUCCESS" : "FAILED");
            
            return connected;
        } catch (Exception e) {
            log.warn("🚨 [WIZARD PACS ENGINE] Connection attempt {}/{} failed: {}", 
                    attempt, maxAttempts, e.getMessage());
            
            if (attempt < maxAttempts) {
                try {
                    Thread.sleep(1000 * attempt); // Backoff
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
    
    log.error("🚨 [WIZARD PACS ENGINE] All connection attempts failed");
    return false;
}
    
    /**
     * Obtiene información del sistema PACS Engine
     */
    public Map<String, Object> getSystemInfo() {
        try {
            String url = buildUrl(MONITOR_ENDPOINT + "/serverinfo");
            HttpHeaders headers = createAuthHeaders();
            HttpEntity<?> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, Map.class);
            
            Map<String, Object> systemInfo = response.getBody();
            log.debug("📊 [WIZARD PACS ENGINE] System info retrieved");
            
            return systemInfo != null ? systemInfo : new HashMap<>();
        } catch (Exception e) {
            log.error("🚨 [WIZARD PACS ENGINE] Failed to get system info: {}", e.getMessage());
            return new HashMap<>();
        }
    }
    
    // ========== GESTIÓN DE AE TITLES ==========
    
    /**
     * Lista todos los AE Titles configurados
     */
    public List<Map<String, Object>> listAETitles() {
        try {
            String url = buildUrl(AETS_ENDPOINT);
            HttpHeaders headers = createAuthHeaders();
            HttpEntity<?> entity = new HttpEntity<>(headers);
            
            ResponseEntity<List> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, List.class);
            
            List<Map<String, Object>> aeTitles = response.getBody();
            log.info("📋 [WIZARD PACS ENGINE] Found {} AE Titles", 
                    aeTitles != null ? aeTitles.size() : 0);
            
            return aeTitles != null ? aeTitles : new ArrayList<>();
        } catch (Exception e) {
            log.error("🚨 [WIZARD PACS ENGINE] Failed to list AE Titles: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Verifica si un AE Title existe
     */
    public boolean aeTitleExists(String aeTitle) {
        try {
            String url = buildUrl(AETS_ENDPOINT + "/" + aeTitle);
            HttpHeaders headers = createAuthHeaders();
            HttpEntity<?> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Object> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, Object.class);
            
            return response.getStatusCode().is2xxSuccessful();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return false;
            }
            throw e;
        }
    }
    
    /**
     * Crea un nuevo AE Title en el PACS Engine
     */
    public boolean createAETitle(DicomNode node) {
        try {
            // Verificar si ya existe
            if (aeTitleExists(node.getAeTitle())) {
                log.warn("⚠️ [WIZARD PACS ENGINE] AE Title already exists: {}", node.getAeTitle());
                return updateAETitle(node);
            }

            Map<String, Object> aeTitleConfig = buildAETitleConfig(node);
            String url = buildUrl(AETS_ENDPOINT + "/" + node.getAeTitle());
            
            HttpHeaders headers = createAuthHeaders();
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(aeTitleConfig, headers);
            
            ResponseEntity<Object> response = restTemplate.exchange(
                url, HttpMethod.PUT, entity, Object.class);
            
            boolean success = response.getStatusCode().is2xxSuccessful();
            log.info("🔗 [WIZARD PACS ENGINE] AE Title {} {}: {} [{}]", 
                    success ? "created" : "failed", 
                    node.getAeTitle(), 
                    node.getHostname() + ":" + node.getPort(),
                    response.getStatusCode());
            
            return success;
        } catch (Exception e) {
            log.error("🚨 [WIZARD PACS ENGINE] Failed to create AE Title {}: {}", 
                     node.getAeTitle(), e.getMessage());
            return false;
        }
    }
    
    /**
     * Actualiza un AE Title existente
     */
    public boolean updateAETitle(DicomNode node) {
        try {
            Map<String, Object> aeTitleConfig = buildAETitleConfig(node);
            String url = buildUrl(AETS_ENDPOINT + "/" + node.getAeTitle());
            
            HttpHeaders headers = createAuthHeaders();
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(aeTitleConfig, headers);
            
            ResponseEntity<Object> response = restTemplate.exchange(
                url, HttpMethod.PUT, entity, Object.class);
            
            boolean success = response.getStatusCode().is2xxSuccessful();
            log.info("🔄 [WIZARD PACS ENGINE] AE Title updated: {} - {}", 
                    node.getAeTitle(), response.getStatusCode());
            
            return success;
        } catch (Exception e) {
            log.error("🚨 [WIZARD PACS ENGINE] Failed to update AE Title {}: {}", 
                     node.getAeTitle(), e.getMessage());
            return false;
        }
    }
    
    /**
     * Elimina un AE Title
     */
    public boolean deleteAETitle(String aeTitle) {
        try {
            if (!aeTitleExists(aeTitle)) {
                log.warn("⚠️ [WIZARD PACS ENGINE] AE Title doesn't exist: {}", aeTitle);
                return true;
            }

            String url = buildUrl(AETS_ENDPOINT + "/" + aeTitle);
            HttpHeaders headers = createAuthHeaders();
            HttpEntity<?> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Object> response = restTemplate.exchange(
                url, HttpMethod.DELETE, entity, Object.class);
            
            boolean success = response.getStatusCode().is2xxSuccessful();
            log.info("🗑️ [WIZARD PACS ENGINE] AE Title deleted: {} - {}", 
                    aeTitle, response.getStatusCode());
            
            return success;
        } catch (Exception e) {
            log.error("🚨 [WIZARD PACS ENGINE] Failed to delete AE Title {}: {}", 
                     aeTitle, e.getMessage());
            return false;
        }
    }
    
    // ========== OPERACIONES DICOM ==========
    
    /**
     * Realiza C-ECHO con reintentos y timeout configurable
     */
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public boolean performEcho(DicomNode node) {
        return performEcho(node.getAeTitle(), pacsConfig.getEngine().getDefaultAeTitle());
    }
    
    /**
     * Realiza C-ECHO entre AE Titles específicos
     */
    public boolean performEcho(String calledAET, String callingAET) {
        try {
            String url = buildUrl(DIMSE_ENDPOINT + "/echo", callingAET, calledAET);
            HttpHeaders headers = createAuthHeaders();
            HttpEntity<?> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Object> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, Object.class);
            
            boolean success = response.getStatusCode().is2xxSuccessful();
            log.info("📡 [WIZARD PACS ENGINE] Echo {} for {}: {} ms", 
                    success ? "SUCCESS" : "FAILED", 
                    calledAET,
                    System.currentTimeMillis());
            
            return success;
        } catch (Exception e) {
            log.error("📡 [WIZARD PACS ENGINE] Echo FAILED for {}: {}", calledAET, e.getMessage());
            return false;
        }
    }
    
    /**
     * Realiza C-ECHO masivo a múltiples nodos de forma asíncrona
     */
    public CompletableFuture<Map<String, Boolean>> performBatchEchoAsync(List<DicomNode> nodes) {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, Boolean> results = new HashMap<>();
            
            List<CompletableFuture<Void>> futures = nodes.stream()
                .map(node -> CompletableFuture.runAsync(() -> {
                    try {
                        boolean result = performEcho(node);
                        synchronized (results) {
                            results.put(node.getAeTitle(), result);
                        }
                    } catch (Exception e) {
                        synchronized (results) {
                            results.put(node.getAeTitle(), false);
                        }
                        log.warn("📡 [BATCH ECHO] {} failed: {}", node.getAeTitle(), e.getMessage());
                    }
                }))
                .collect(Collectors.toList());
            
            // Esperar a que todos terminen
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            
            log.info("📡 [WIZARD PACS ENGINE] Batch echo completed for {} nodes", results.size());
            return results;
        });
    }
    
    /**
     * Realiza C-FIND para buscar estudios
     */
    public List<Map<String, Object>> performStudyFind(StudySearchCriteria criteria) {
        try {
            String aeTitle = pacsConfig.getEngine().getDefaultAeTitle();
            String url = buildUrl(STUDIES_ENDPOINT, aeTitle);
            
            // Agregar parámetros de búsqueda
            Map<String, String> queryParams = buildStudySearchParams(criteria);
            
            HttpHeaders headers = createAuthHeaders();
            headers.set("Accept", "application/dicom+json");
            HttpEntity<?> entity = new HttpEntity<>(headers);
            
            ResponseEntity<List> response = restTemplate.exchange(
                addQueryParams(url, queryParams), HttpMethod.GET, entity, List.class);
            
            List<Map<String, Object>> studies = response.getBody();
            log.info("🔍 [WIZARD PACS ENGINE] Study search found {} results", 
                    studies != null ? studies.size() : 0);
            
            return studies != null ? studies : new ArrayList<>();
        } catch (Exception e) {
            log.error("🚨 [WIZARD PACS ENGINE] Study search failed: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Realiza C-MOVE para recuperar estudios
     */
    public boolean performStudyMove(String studyInstanceUID, String destinationAET) {
        try {
            String callingAET = pacsConfig.getEngine().getDefaultAeTitle();
            String url = buildUrl(DIMSE_ENDPOINT + "/" + destinationAET + "/studies/" + studyInstanceUID, 
                                 callingAET, destinationAET);
            
            HttpHeaders headers = createAuthHeaders();
            HttpEntity<?> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Object> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, Object.class);
            
            boolean success = response.getStatusCode().is2xxSuccessful();
            log.info("📤 [WIZARD PACS ENGINE] Study move {} to {}: {}", 
                    studyInstanceUID, destinationAET, success ? "SUCCESS" : "FAILED");
            
            return success;
        } catch (Exception e) {
            log.error("🚨 [WIZARD PACS ENGINE] Study move failed: {}", e.getMessage());
            return false;
        }
    }
    
    // ========== GESTIÓN DE ESTUDIOS ==========
    
    /**
     * Obtiene detalles de un estudio específico
     */
    public Map<String, Object> getStudyDetails(String studyInstanceUID) {
        try {
            String aeTitle = pacsConfig.getEngine().getDefaultAeTitle();
            String url = buildUrl(STUDIES_ENDPOINT + "/" + studyInstanceUID, aeTitle);
            
            HttpHeaders headers = createAuthHeaders();
            headers.set("Accept", "application/dicom+json");
            HttpEntity<?> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, Map.class);
            
            Map<String, Object> study = response.getBody();
            log.debug("📊 [WIZARD PACS ENGINE] Study details retrieved: {}", studyInstanceUID);
            
            return study != null ? study : new HashMap<>();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.warn("⚠️ [WIZARD PACS ENGINE] Study not found: {}", studyInstanceUID);
                return new HashMap<>();
            }
            throw e;
        } catch (Exception e) {
            log.error("🚨 [WIZARD PACS ENGINE] Failed to get study details: {}", e.getMessage());
            return new HashMap<>();
        }
    }
    
    /**
     * Obtiene series de un estudio
     */
    public List<Map<String, Object>> getStudySeries(String studyInstanceUID) {
        try {
            String aeTitle = pacsConfig.getEngine().getDefaultAeTitle();
            String url = buildUrl(SERIES_ENDPOINT, aeTitle, studyInstanceUID);
            
            HttpHeaders headers = createAuthHeaders();
            headers.set("Accept", "application/dicom+json");
            HttpEntity<?> entity = new HttpEntity<>(headers);
            
            ResponseEntity<List> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, List.class);
            
            List<Map<String, Object>> series = response.getBody();
            log.debug("📊 [WIZARD PACS ENGINE] Study series retrieved: {} series for study {}", 
                     series != null ? series.size() : 0, studyInstanceUID);
            
            return series != null ? series : new ArrayList<>();
        } catch (Exception e) {
            log.error("🚨 [WIZARD PACS ENGINE] Failed to get study series: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene instancias de una serie
     */
    public List<Map<String, Object>> getSeriesInstances(String studyInstanceUID, String seriesInstanceUID) {
        try {
            String aeTitle = pacsConfig.getEngine().getDefaultAeTitle();
            String url = buildUrl(INSTANCES_ENDPOINT, aeTitle, studyInstanceUID, seriesInstanceUID);
            
            HttpHeaders headers = createAuthHeaders();
            headers.set("Accept", "application/dicom+json");
            HttpEntity<?> entity = new HttpEntity<>(headers);
            
            ResponseEntity<List> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, List.class);
            
            List<Map<String, Object>> instances = response.getBody();
            log.debug("📊 [WIZARD PACS ENGINE] Series instances retrieved: {} instances", 
                     instances != null ? instances.size() : 0);
            
            return instances != null ? instances : new ArrayList<>();
        } catch (Exception e) {
            log.error("🚨 [WIZARD PACS ENGINE] Failed to get series instances: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Elimina un estudio del PACS Engine
     */
    public boolean deleteStudy(String studyInstanceUID) {
        try {
            String aeTitle = pacsConfig.getEngine().getDefaultAeTitle();
            String url = buildUrl(STUDIES_ENDPOINT + "/" + studyInstanceUID, aeTitle);
            
            HttpHeaders headers = createAuthHeaders();
            HttpEntity<?> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Object> response = restTemplate.exchange(
                url, HttpMethod.DELETE, entity, Object.class);
            
            boolean success = response.getStatusCode().is2xxSuccessful();
            log.info("🗑️ [WIZARD PACS ENGINE] Study deleted: {} - {}", 
                    studyInstanceUID, success ? "SUCCESS" : "FAILED");
            
            return success;
        } catch (Exception e) {
            log.error("🚨 [WIZARD PACS ENGINE] Failed to delete study: {}", e.getMessage());
            return false;
        }
    }
    
    // ========== ESTADÍSTICAS Y MONITOREO ==========
    
    /**
     * Obtiene estadísticas completas del PACS Engine
     */
    public Map<String, Object> getStatistics() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // Información del sistema
            stats.put("systemInfo", getSystemInfo());
            
            // AE Titles
            List<Map<String, Object>> aeTitles = listAETitles();
            stats.put("aeTitlesCount", aeTitles.size());
            stats.put("aeTitles", aeTitles);
            
            // Estadísticas de almacenamiento
            stats.put("storage", getStorageStatistics());
            
            // Conexión
            stats.put("connected", testConnection());
            stats.put("timestamp", System.currentTimeMillis());
            
            log.debug("📊 [WIZARD PACS ENGINE] Statistics retrieved");
            return stats;
        } catch (Exception e) {
            log.error("🚨 [WIZARD PACS ENGINE] Failed to get statistics: {}", e.getMessage());
            return new HashMap<>();
        }
    }
    
    /**
     * Obtiene estadísticas de almacenamiento
     */
    public Map<String, Object> getStorageStatistics() {
        try {
            String url = buildUrl(MONITOR_ENDPOINT + "/storage");
            HttpHeaders headers = createAuthHeaders();
            HttpEntity<?> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, Map.class);
            
            Map<String, Object> storage = response.getBody();
            log.debug("💾 [WIZARD PACS ENGINE] Storage statistics retrieved");
            
            return storage != null ? storage : new HashMap<>();
        } catch (Exception e) {
            log.warn("⚠️ [WIZARD PACS ENGINE] Storage statistics not available: {}", e.getMessage());
            return new HashMap<>();
        }
    }
    
    // ========== MÉTODOS AUXILIARES ==========
    
    /**
     * Construye configuración de AE Title para DCM4CHEE
     */
    private Map<String, Object> buildAETitleConfig(DicomNode node) {
        Map<String, Object> config = new HashMap<>();
        config.put("dicomAETitle", node.getAeTitle());
        config.put("dicomHostname", node.getHostname());
        config.put("dicomPort", node.getPort());
        config.put("dicomDescription", node.getDescription());
        config.put("dcmAcceptedCallingAETitle", List.of("*"));
        config.put("dcmAcceptedUserRole", List.of("user"));
        
        // Capacidades DICOM según configuración del nodo
        List<String> transferCapabilities = new ArrayList<>();
        if (Boolean.TRUE.equals(node.getStore())) {
            transferCapabilities.add("SCP:C-STORE");
        }
        if (Boolean.TRUE.equals(node.getFind())) {
            transferCapabilities.add("SCP:C-FIND");
        }
        if (Boolean.TRUE.equals(node.getMove())) {
            transferCapabilities.add("SCP:C-MOVE");
        }
        if (Boolean.TRUE.equals(node.getGet())) {
            transferCapabilities.add("SCP:C-GET");
        }
        if (Boolean.TRUE.equals(node.getEcho())) {
            transferCapabilities.add("SCP:C-ECHO");
        }
        
        config.put("dcmTransferCapabilities", transferCapabilities);
        
        // Metadatos específicos de Wizard
        List<String> properties = Arrays.asList(
            "wizard.organizationId=" + node.getOrganizationId(),
            "wizard.nodeId=" + node.getId(),
            "wizard.createdBy=wizard-pacs-engine",
            "wizard.nodeType=" + node.getNodeType(),
            "wizard.version=2.0.0"
        );
        config.put("dcmProperty", properties);
        
        return config;
    }
    
    /**
     * Construye parámetros de búsqueda para estudios
     */
    private Map<String, String> buildStudySearchParams(StudySearchCriteria criteria) {
        Map<String, String> params = new HashMap<>();
        
        if (criteria.getPatientId() != null) {
            params.put("PatientID", criteria.getPatientId());
        }
        if (criteria.getPatientName() != null) {
            params.put("PatientName", criteria.getPatientName());
        }
        if (criteria.getStudyDate() != null) {
            params.put("StudyDate", criteria.getStudyDate().format(DateTimeFormatter.BASIC_ISO_DATE));
        }
        if (criteria.getStudyDateFrom() != null && criteria.getStudyDateTo() != null) {
            params.put("StudyDate", 
                criteria.getStudyDateFrom().format(DateTimeFormatter.BASIC_ISO_DATE) + "-" +
                criteria.getStudyDateTo().format(DateTimeFormatter.BASIC_ISO_DATE));
        }
        if (criteria.getModality() != null) {
            params.put("Modality", criteria.getModality());
        }
        if (criteria.getAccessionNumber() != null) {
            params.put("AccessionNumber", criteria.getAccessionNumber());
        }
        if (criteria.getStudyInstanceUID() != null) {
            params.put("StudyInstanceUID", criteria.getStudyInstanceUID());
        }
        
        // Configuración de respuesta
        params.put("includefield", "all");
        params.put("limit", String.valueOf(criteria.getLimit() != null ? criteria.getLimit() : 100));
        params.put("offset", String.valueOf(criteria.getOffset() != null ? criteria.getOffset() : 0));
        
        return params;
    }
    
    /**
     * Crea headers HTTP con autenticación
     */
    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(
            pacsConfig.getEngine().getUsername(), 
            pacsConfig.getEngine().getPassword()
        );
        headers.set("User-Agent", "Wizard-PACS-Engine/2.0");
        headers.set("X-Forwarded-For", "wizard-internal");
        return headers;
    }
    
    /**
     * Construye URL completa para el PACS Engine
     */
    private String buildUrl(String endpoint, Object... params) {
        String baseUrl = pacsConfig.getEngine().getUrl();
        String formattedEndpoint = String.format(endpoint, params);
        return baseUrl + formattedEndpoint;
    }
    
    /**
     * Agrega parámetros de query a una URL
     */
    private String addQueryParams(String url, Map<String, String> params) {
        if (params.isEmpty()) {
            return url;
        }
        
        StringBuilder sb = new StringBuilder(url);
        sb.append("?");
        
        params.entrySet().stream()
            .forEach(entry -> sb.append(entry.getKey())
                               .append("=")
                               .append(entry.getValue())
                               .append("&"));
        
        // Remover último &
        sb.deleteCharAt(sb.length() - 1);
        
        return sb.toString();
    }
}