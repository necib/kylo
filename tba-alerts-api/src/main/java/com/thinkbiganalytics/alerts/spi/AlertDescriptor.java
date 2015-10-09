/**
 * 
 */
package com.thinkbiganalytics.alerts.spi;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.thinkbiganalytics.alerts.api.Alert;

/**
 *
 * @author Sean Felten
 */
public class AlertDescriptor {
    
    private final URI alertType;
    private final String contentType;
    private final String description;
    private final boolean respondable;
    private final Map<Alert.State, String> stateContentTypes;
    
    /**
     * 
     */
    public AlertDescriptor(URI type, String content, String descr, boolean respondable) {
        this(type, content, descr, respondable, Collections.<Alert.State, String>emptyMap());
    }
    
    /**
     * 
     */
    public AlertDescriptor(URI type, String content, String descr, boolean respondable, Map<Alert.State, String> states) {
        this.alertType = type;
        this.contentType = content;
        this.description = descr;
        this.respondable = respondable;
        this.stateContentTypes = Collections.unmodifiableMap(new HashMap<>(states));
    }

    public URI getAlertType() {
        return alertType;
    }

    public String getContentType() {
        return contentType;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isRespondable() {
        return respondable;
    }

    public Map<Alert.State, String> getStateContentTypes() {
        return stateContentTypes;
    }
    
}
