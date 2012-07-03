package org.easysoa.discovery.code;

import java.net.URL;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.easysoa.discovery.rest.model.Deliverable;

public class MavenDeliverable extends Deliverable {
    
    @JsonIgnore
    private String mavenGroupId;
    
    @JsonIgnore
    private String mavenArtifactId;
    
    @JsonIgnore
    private String mavenVersion;
    
    public MavenDeliverable(String name, URL location, String mavenGroupId,
            String mavenArtifactId, String mavenVersion) {
        super(mavenGroupId + ":" + mavenArtifactId, name, mavenVersion, location);
        this.mavenGroupId = mavenGroupId;
        this.mavenArtifactId = mavenArtifactId;
        this.mavenVersion = mavenVersion;
    }

    public String getMavenGroupId() {
        return mavenGroupId;
    }

    public String getMavenArtifactId() {
        return mavenArtifactId;
    }

    public String getMavenVersion() {
        return mavenVersion;
    }

}
