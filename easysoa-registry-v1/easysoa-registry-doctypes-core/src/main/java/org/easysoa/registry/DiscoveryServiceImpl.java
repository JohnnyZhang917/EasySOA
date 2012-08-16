package org.easysoa.registry;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.nuxeo.common.utils.IdUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.api.Framework;

public class DiscoveryServiceImpl implements DiscoveryService {

    public DocumentModel importDiscovery(CoreSession documentManager, SoaNodeId identifier,
            Map<String, String> properties, List<SoaNodeId> correlatedDocuments) throws Exception {
        DocumentService documentService = Framework.getService(DocumentService.class);
        
        // Fetch or create document
        DocumentModel documentModel = documentService.find(documentManager, identifier);
        if (documentModel == null) {
            documentModel = documentService.create(documentManager, identifier, identifier.getName());
        }
        
        // Set properties
        if (properties != null) {
            for (Entry<String, String> property : properties.entrySet()) {
                documentModel.setPropertyValue(property.getKey(), property.getValue());
            }
            documentManager.saveDocument(documentModel);
        }
        
        // Link to correlated documents
        if (correlatedDocuments != null && !correlatedDocuments.isEmpty()) {
            String type = documentModel.getType();
            SoaMetamodelService soaMetamodelService = Framework.getService(SoaMetamodelService.class);
            for (SoaNodeId correlatedDocument : correlatedDocuments) {
                List<String> path = soaMetamodelService.getPath(type, correlatedDocument.getType());
                
                // Swap the documents if necessary
                DocumentModel parentDocument;
                if (path == null) {
                    parentDocument = documentService.find(documentManager, correlatedDocument);
                    if (parentDocument == null) {
                        parentDocument = documentService.create(documentManager, correlatedDocument, correlatedDocument.getType());
                    }
                    correlatedDocument = SoaNodeId.fromModel(documentModel);
                }
                else {
                    parentDocument = documentModel;
                }
                
                // If we have unknown documents between the two, create placeholders
                // TODO More intelligent, pluggable correlation logic?
                if (!path.isEmpty()) {
                    for (String pathStepType : path.subList(0, path.size() - 1)) {
                        // Before creating a placeholder, check if the intermediate type
                        // is not already listed in the correlated documents
                        boolean placeholderNeeded = true;
                        for (SoaNodeId placeholderReplacementCandidate : correlatedDocuments) {
                            if (pathStepType.equals(placeholderReplacementCandidate.getType())) {
                                parentDocument = documentService.create(documentManager, placeholderReplacementCandidate,
                                        parentDocument.getPathAsString(), null);
                                placeholderNeeded = false;
                                break;
                            }
                        }
                        
                        if (placeholderNeeded) {
                            parentDocument = documentService.create(documentManager,
                                    new SoaNodeId(pathStepType, IdUtils.generateStringId()),
                                    parentDocument.getPathAsString(), "(Placeholder)");
                        }
                    }
                }
                documentService.create(documentManager, correlatedDocument,
                        parentDocument.getPathAsString(), correlatedDocument.getName());
                
            } 
        }
        
        return documentModel;
    }

}
