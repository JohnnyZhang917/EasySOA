package org.easysoa.registry;

import org.nuxeo.ecm.core.api.DocumentModel;

public class SoaNodeId {

    private String name;
    private String doctype;

    public SoaNodeId(DocumentModel model) {
        this.setType(model.getType());
        this.setName(model.getName());
    }
    
    public SoaNodeId(String doctype, String name) {
        this.setType(doctype);
        this.setName(name);
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        // Remove eventual suffix added by Nuxeo when some proxies conflict
        // XXX: Side effect is that no document should end its "real" name with a dot followed by numbers
        this.name = name.replaceAll("\\.[0-9]+$", "").replace('/', '-');
    }
    
    public String getType() {
        return doctype;
    }
    
    public void setType(String doctype) {
        this.doctype = doctype;
    }
    
    @Override
    public String toString() {
        return doctype + ":" + name;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SoaNodeId) {
            SoaNodeId otherId = (SoaNodeId) obj;
            return this.doctype.equals(otherId.doctype) && this.name.equals(otherId.name);
        }
        else {
            return false;
        }
    }
    
}
