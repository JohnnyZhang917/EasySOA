package org.easysoa.tests;

import static org.junit.Assert.*;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.services.VocabularyService;
import org.easysoa.test.EasySOAFeature;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.BackendType;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.directory.Directory;
import org.nuxeo.ecm.directory.api.DirectoryService;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LocalDeploy;

import com.google.inject.Inject;

/**
 * Tests vocabulary services
 * @author mkalam-alami, mdutoo
 *
 */
@RunWith(FeaturesRunner.class)
@Features(EasySOAFeature.class)
@Deploy({
	"org.nuxeo.runtime.datasource", // required, else errors : [DataSourceHelper] Unknown JNDI Context class: null , [SQLDirectory] dataSource lookup failed , "javax.naming.NoInitialContextException: Need to specify class name...
    "org.nuxeo.ecm.directory.types.contrib:OSGI-INF/DirectoryTypes.xml", // required, else no vocabulary schema in database
    "org.nuxeo.ecm.directory",
    "org.nuxeo.ecm.directory.api",
    "org.nuxeo.ecm.directory.sql", // all required, else no dirService
    "org.easysoa.demo.core:OSGI-INF/nxdirectories-contrib.xml" // required, else no custom easysoa vocabularies
})
@LocalDeploy("org.easysoa.demo.core:org/easysoa/tests/datasource-contrib.xml")
@RepositoryConfig(type=BackendType.H2, user = "Administrator", init=DefaultRepositoryInit.class)
public class VocabulariesTest {

    static final Log log = LogFactory.getLog(VocabulariesTest.class);
    
    @Inject DirectoryService dirService;
    @Inject CoreSession session;

    @Before
    public void setUp() throws Exception {
  	  	assertNotNull("Cannot get directory service component", dirService);
    }
    
    @Test
    public void test() throws Exception {
    	List<Directory> dirList = dirService.getDirectories();
  	  	assertNotNull(dirList);
  	  	assertEquals(dirList.size(), 3);

		String environment = "test test environment";
		
		// none yet :
		VocabularyService.removeEntry(session,
				VocabularyService.VOCABULARY_ENVIRONMENT,
				environment); // in case previous tests let crap be there
		assertTrue(!VocabularyService.entryExists(session,
				VocabularyService.VOCABULARY_ENVIRONMENT, environment));

		// adding one :
		VocabularyService.addEntry(session,
				VocabularyService.VOCABULARY_ENVIRONMENT,
				environment, environment);
		assertTrue(VocabularyService.entryExists(session,
				VocabularyService.VOCABULARY_ENVIRONMENT, environment));

		// removing it :
		VocabularyService.removeEntry(session,
				VocabularyService.VOCABULARY_ENVIRONMENT,
				environment);
		assertTrue(!VocabularyService.entryExists(session,
				VocabularyService.VOCABULARY_ENVIRONMENT, environment));
    }

}