/**
 *
 */
package org.easysoa.proxy.strategy;

/**
 * @author jguillemotte
 *
 */
public class EmbeddedEasySOAGeneratedAppIdFactoryStrategy implements EasySOAGeneratedAppIdFactoryStrategy {

    public static final String SINGLETON_ID = "default";

    /**
     * Build an ID with the provided parameters
     * @param user Easysoa user
     * @param projectId Project ID
     * @param componentIds Components ID
     * @return The generated ID
     */
    // TODO : complete this method
    public String getId(String user, String projectId, String componentIds){
        return SINGLETON_ID;
    }

}
