/**
 * EasySOA - Nuxeo FraSCAti
 * Copyright 2011 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */
package org.nuxeo.frascati;


import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.easysoa.frascati.processor.intent.ParserIntentObservable;
import org.easysoa.frascati.processor.intent.ParserIntentObserver;
import org.nuxeo.frascati.api.AbstractProcessingContext;
import org.nuxeo.frascati.api.FraSCAtiServiceItf;
import org.nuxeo.frascati.api.FraSCAtiCompositeItf;
import org.nuxeo.frascati.api.ProcessingModeProxy;
import org.nuxeo.frascati.factory.ClassLoaderSingleton;
import org.ow2.frascati.util.reflect.ReflectionHelper;
import org.ow2.frascati.util.reflect.ReflectionUtil;
import org.nuxeo.runtime.bridge.Application;


/**
 * Implementation of the FraSCAtiServiceItf interface
 * NuxeoFraSCAti allow to instantiate an new FraSCAti instance in Nuxeo
 */
public class NuxeoFraSCAti implements Application, FraSCAtiServiceItf {
	
	Logger log = Logger.getLogger(NuxeoFraSCAti.class.getCanonicalName());
	
	ParserIntentObservable parserIntentObservable = null;
	
	ReflectionHelper frascati = new ReflectionHelper(
			ClassLoaderSingleton.classLoader(),"org.ow2.frascati.FraSCAti");
	
	ReflectionHelper classLoaderManager = new ReflectionHelper(
			ClassLoaderSingleton.classLoader(),"org.ow2.frascati.assembly.factory.api.ClassLoaderManager");
	
	ReflectionHelper compositeManager = new ReflectionHelper(
			ClassLoaderSingleton.classLoader(),"org.ow2.frascati.assembly.factory.api.CompositeManager");

	ReflectionHelper frascatiClassLoader = new ReflectionHelper(
			ClassLoaderSingleton.classLoader(),"org.ow2.frascati.util.FrascatiClassLoader");
	
	Class<?> processingContextClass = ReflectionUtil.forName(ClassLoaderSingleton.classLoader(),
			"org.ow2.frascati.assembly.factory.api.ProcessingContext");

	Class<?> easysoaProcessingContextClass = ReflectionUtil.forName(ClassLoaderSingleton.classLoader(),
			"org.easysoa.frascati.processor.EasySOAProcessingContext");
	
	Class<?> componentClass = ReflectionUtil.forName(ClassLoaderSingleton.classLoader(),
			"org.objectweb.fractal.api.Component");
	
	/**
	 * Constructor
	 * @throws NuxeoFraSCAtiException 
	 */
	public NuxeoFraSCAti() throws NuxeoFraSCAtiException{
		
		log.log(Level.INFO,"new FraSCAti instance initialisation");
		
		frascati.set(frascati.invokeStatic("newFraSCAti"));		
		if(frascati.get() != null){
			classLoaderManager.set(frascati.invoke("getClassLoaderManager"));
			compositeManager.set(frascati.invoke("getCompositeManager"));
		}	
		frascatiClassLoader.set(frascati.invoke("getClassLoader"));
		//Destroy if error
		if(classLoaderManager.get() == null ||
				compositeManager.get() == null || 
					frascatiClassLoader.get() == null){
			destroy();
			throw new NuxeoFraSCAtiException("Enable to instantiate properly the FraSCAti instance");			
		} 				
		FraSCAtiCompositeItf fraSCAtiComponent = getTopLevelDomainComposite().getSubComponents()[0];
		FraSCAtiCompositeItf[] composites  = fraSCAtiComponent.getSubComponents();
		
		//Weaves an intent to the composite parser 
		FraSCAtiCompositeItf componentIntent = null;		
		FraSCAtiCompositeItf componentParser = null;
		
		for(FraSCAtiCompositeItf composite : composites){	
			
			if("parser-intent".equals(composite.getName())){								
				componentIntent = composite;
				if(componentParser!=null){
					break;
				}								
			} else if("sca-parser".equals(composite.getName())){
				componentParser = composite;
				if(componentIntent!=null){
					break;
				}								
			}
		}
		if(componentIntent!=null && componentParser!=null){
			
			ReflectionHelper component  = new ReflectionHelper(ClassLoaderSingleton.classLoader(),
					"org.objectweb.fractal.api.Component");		
			
			ReflectionHelper intentController = new ReflectionHelper(ClassLoaderSingleton.classLoader(),
					"org.ow2.frascati.tinfi.api.control.SCABasicIntentController");

			component.set(componentParser.getComponent());
			
			intentController.set(component.invoke("getFcInterface",
					new Class<?>[]{String.class},
					new Object[]{intentController.getStatic("NAME")}));
						
			ReflectionHelper intentHandler = new ReflectionHelper(ClassLoaderSingleton.classLoader(),
					"org.ow2.frascati.tinfi.api.IntentHandler");
			
			component.set(componentIntent.getComponent());
			
			intentHandler.set(getService(componentIntent.getComponent(),
					"intent",intentHandler.getReflectedClass()));
			
			parserIntentObservable = (ParserIntentObservable) component.invoke("getFcInterface",
					new Class<?>[]{String.class},
					new Object[]{"intent-observable"});			
			
			if(intentController!=null && intentHandler != null){				
				componentParser.stop();
				intentController.invoke("addFcIntentHandler",
						new Class<?>[]{intentHandler.getReflectedClass()},
						new Object[]{intentHandler.get()});
				componentParser.start();				
			} else {
				log.log(Level.WARNING,
					"Enable to retrieve intent controller or intent handler");
			}				
		} else {
			log.log(Level.WARNING,
				"Enable to retrieve intent component or parser component");
		}			
	}

	/* (non-Javadoc)
	 * @see org.nuxeo.frascati.api.FraSCAtiServiceItf#remove(java.lang.String)
	 */
	public void remove(String composite) throws NuxeoFraSCAtiException {		
		FraSCAtiCompositeItf component = getComposite(composite);				
		compositeManager.invoke("removeComposite",
				new Class<?>[]{String.class},
				new Object[]{composite});				
	}
	
	/* (non-Javadoc)
	 * @see org.nuxeo.frascati.api.FraSCAtiServiceItf#getComposite(java.lang.String)
	 */
	public FraSCAtiCompositeItf getComposite(String composite) throws NuxeoFraSCAtiException {	
		
		ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();		
		Thread.currentThread().setContextClassLoader((ClassLoader) frascatiClassLoader.get());
		
		Object component = compositeManager.invoke("getComposite",
				new Class<?>[]{String.class},
				new Object[]{composite});
		
		Thread.currentThread().setContextClassLoader(currentClassLoader);
		if(component == null){
			throw new NuxeoFraSCAtiException("Enable to found the Composite '" + composite + "'");
		} else {
			return new FraSCAtiComposite(component);
		}		
	}
	
	/* (non-Javadoc)
	 * @see org.nuxeo.frascati.api.FraSCAtiServiceItf
	 * #processComposite(java.lang.String, org.nuxeo.frascati.api.AbstractProcessingContext)
	 */
	public FraSCAtiCompositeItf processComposite(String composite,
			AbstractProcessingContext processingContext) throws NuxeoFraSCAtiException {
		
		Object o = processComposite(composite,processingContext.getProcessingContext());
		if(o == null){
			throw new NuxeoFraSCAtiException("Enable to process the " + composite +" composite");
		}		
		FraSCAtiComposite result = new FraSCAtiComposite(o);
		return result;
	}
	
	/* (non-Javadoc)
	 * @see org.nuxeo.frascati.api.FraSCAtiServiceItf
	 * #processComposite(java.lang.String, org.nuxeo.frascati.api.AbstractProcessingContext)
	 */
	public FraSCAtiCompositeItf processComposite(String composite,
			int mode, URL...urls) throws NuxeoFraSCAtiException {
	
		if(mode < 0 || mode > 7){
			mode = ProcessingModeProxy.all;
		}
		ReflectionHelper processingContext = newProcessingContext(urls);
		
		processingContext.invoke("setProcessingMode",
				new Class<?>[]{ProcessingModeProxy.getProcessingModeClass()},
				new Object[]{ProcessingModeProxy.getProcessingMode(mode)});
		
		Object o = processComposite(composite, processingContext.get());		
		if(o == null){
			throw new NuxeoFraSCAtiException("Enable to process the " + composite +" composite");
		}		
		FraSCAtiComposite result = new FraSCAtiComposite(o);		
		return result;
	}
	
	/* (non-Javadoc)
	 * @see org.nuxeo.frascati.api.FraSCAtiServiceItf
	 * #processComposite(java.lang.String)
	 */
	public FraSCAtiCompositeItf processComposite(String composite) throws NuxeoFraSCAtiException {		
		return processComposite(composite,ProcessingModeProxy.all,new URL[]{});
	}
	
	/**
	 * @param composite
	 * @param processingContext
	 * @return
	 */
	private Object processComposite(String composite,Object processingContext){
		
		if(composite == null || composite.length() == 0 || processingContext == null){
			return null;
		}		
		ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();		
		Thread.currentThread().setContextClassLoader((ClassLoader) frascatiClassLoader.get());
		
		Object o = frascati.invoke("processComposite",
				new Class<?>[]{String.class,processingContextClass},
				new Object[]{composite,	processingContext});
		
		Thread.currentThread().setContextClassLoader(currentClassLoader);
		return o;
	}

	
	/* (non-Javadoc)
	 * @see org.nuxeo.frascati.api.FraSCAtiServiceItf
	 * #processContribution(java.lang.String, org.nuxeo.frascati.api.AbstractProcessingContext)
	 */
	public FraSCAtiCompositeItf[] processContribution(String contribution,
			AbstractProcessingContext processingContext) throws NuxeoFraSCAtiException {
		
		Object[] components = processContribution(contribution,processingContext.getProcessingContext());
		
		if(components == null){
			throw new NuxeoFraSCAtiException("No component found in '"+ contribution +"' contribution");
		} 
		int n=0;
		FraSCAtiComposite[]  fcomponents = new FraSCAtiComposite[components.length]; 
		for(;n<components.length;n++){
			fcomponents[n] = new FraSCAtiComposite(components[n]);
		}
		return fcomponents;
	}	

	/* (non-Javadoc)
	 * @see org.nuxeo.frascati.api.FraSCAtiServiceItf#processContribution(java.lang.String, int, java.net.URL[])
	 */
	public FraSCAtiCompositeItf[] processContribution(String contribution,
			int mode,URL... urls) throws NuxeoFraSCAtiException {
				
		if(mode < 0 || mode > 7){
			mode = ProcessingModeProxy.all;
		}
		ReflectionHelper processingContext = newProcessingContext(urls);
		
		processingContext.invoke("setProcessingMode",
				new Class<?>[]{ProcessingModeProxy.getProcessingModeClass()},
				new Object[]{ProcessingModeProxy.getProcessingMode(mode)});
		
		Object[] components = processContribution(contribution,processingContext.get());
		
		if(components == null){
			throw new NuxeoFraSCAtiException("No component found in '"+ contribution +"' contribution");
		} 
		int n=0;
		FraSCAtiComposite[]  fcomponents = new FraSCAtiComposite[components.length]; 
		for(;n<components.length;n++){
			fcomponents[n] = new FraSCAtiComposite(components[n]);
		}
		return fcomponents;
	}
	
	/* (non-Javadoc)
	 * @see org.nuxeo.frascati.api.FraSCAtiServiceItf#processContribution(java.lang.String)
	 */
	public FraSCAtiCompositeItf[] processContribution(String contribution) throws NuxeoFraSCAtiException {		
		return processContribution(contribution,ProcessingModeProxy.all,new URL[]{});
	}
	
	/**
	 * @param composite
	 * @param processingContext
	 * @return
	 */
	private Object[] processContribution(String contribution,Object processingContext){
		
		if(contribution == null || contribution.length() == 0 || processingContext == null){
			return null;
		}		
		ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();		
		Thread.currentThread().setContextClassLoader((ClassLoader) frascatiClassLoader.get());		

		Object[] components = (Object[]) compositeManager.invoke("processContribution",
				new Class<?>[]{String.class,processingContextClass},
				new Object[]{contribution,processingContext});		

		Thread.currentThread().setContextClassLoader(currentClassLoader);
		return components;
	}

	/* (non-Javadoc)
	 * @see org.nuxeo.frascati.api.FraSCAtiServiceItf
	 * #newProcessingContext(java.net.URL[])
	 */
	public ReflectionHelper newProcessingContext(URL... urls) {
	
		ReflectionHelper processingContext = new ReflectionHelper(
				processingContextClass);

		ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();		
		Thread.currentThread().setContextClassLoader((ClassLoader) frascatiClassLoader.get());
		
		if(urls == null || urls.length == 0){
			try {
				processingContext.set(easysoaProcessingContextClass.newInstance());
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		} else {
			try {
				processingContext.set(easysoaProcessingContextClass.getConstructor(
						new Class<?>[]{URL[].class}).newInstance(new Object[]{urls}));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {	
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
		Thread.currentThread().setContextClassLoader(currentClassLoader);
		return processingContext;
		
	}
	

	/* (non-Javadoc)
	 * @see org.nuxeo.frascati.api.FraSCAtiServiceItf#getComposites()
	 */
	public FraSCAtiCompositeItf[] getComposites() {		
		int length;
		Object[] components = (Object[]) compositeManager.invoke("getComposites");
		
		if(components == null){
			length = 0;
		} else {
			length = components.length;
		}
		int n=0;
		FraSCAtiComposite[]  fcomponents = new FraSCAtiComposite[length]; 
		for(;n<length;n++){
			fcomponents[n] = new FraSCAtiComposite(components[n]);
		}
		return fcomponents;
	}

	/* (non-Javadoc)
	 * @see org.nuxeo.frascati.api.FraSCAtiServiceItf#getTopLevelDomainComposite()
	 */
	public FraSCAtiCompositeItf getTopLevelDomainComposite() {		
		Object component = compositeManager.invoke(
				"getTopLevelDomainComposite");			
		FraSCAtiComposite  fcomponent = new FraSCAtiComposite(component); 		
		return fcomponent;
	}
	
	/* (non-Javadoc)
	 * @see org.nuxeo.frascati.api.FraSCAtiServiceItf
	 * #getService(java.lang.Object, java.lang.String, java.lang.Class)
	 */
	public <T> T getService(Object component,String serviceName,Class<T> serviceClass)
			throws NuxeoFraSCAtiException{
		
		Object comp = null;
		if(component instanceof FraSCAtiCompositeItf){
			comp = ((FraSCAtiCompositeItf)component).getComponent();
		} else {
			comp = component;
		}
		T result = (T) frascati.invoke("getService",
				new Class<?>[]{componentClass,String.class,Class.class},
				new Object[]{comp,serviceName,serviceClass});
		if(result  == null){
			throw new NuxeoFraSCAtiException("No " + serviceName + " service implementing " + 
			serviceClass.getName() + " found");
		}
		return result;		
	}

	
	/* (non-Javadoc)
	 * @see org.nuxeo.frascati.api.FraSCAtiServiceItf#addParserIntentObserver(org.easysoa.frascati.processor.intent.ParserIntentObserver)
	 */
	public void addParserIntentObserver(ParserIntentObserver observer) {
		if(parserIntentObservable == null){
			log.log(Level.WARNING,"No ParserIntentObservable has been found");
			return;
		}
		parserIntentObservable.addParserIntentObserver(observer);
	}

	 /* (non-Javadoc)
     * @see org.nuxeo.runtime.bridge.Application#getService(java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    public <T> T getService(Class<T> type) {
        if (type.isAssignableFrom(getClass())) {
            return (T)this;
        }
        return null;
    }

	/* (non-Javadoc)
	 * @see org.nuxeo.frascati.api.FraSCAtiServiceIt
	 * f#close(org.nuxeo.frascati.api.FractalComponentItf)
	 */
	public void close(FraSCAtiCompositeItf component) {
		if(component == null){
			return;
		}
		Object c = component.getComponent();
		if(c == null){
			return;
		}	
		component.stop();
	}
	
    /**
     * Close recursively the FraSCAtiCompositeItf embedded FraSCAti composite instance
     * 
     * @param component
     * 	the FractalComponentItf containing the FraSCAti composite instance to close
     */
    private void closeComponent(FraSCAtiCompositeItf component) {
		if(component == null){
			return;
		}
		Object c = component.getComponent();
		if(c == null){
			return;
		}	
		FraSCAtiCompositeItf[] fcomponents  = component.getSubComponents();
		if(fcomponents != null){
			int n = 0;
			for (;n<fcomponents.length;n++){
				closeComponent(fcomponents[n]);
			}
		} 
		close(component);
	}
    
    /* (non-Javadoc)
     * @see org.nuxeo.runtime.bridge.Application#destroy()
     */
    public void destroy() {    	
    	
    	MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
    	ObjectName name;
		try {
			name = new ObjectName("SCA domain:name0=*,*");
			Set<ObjectName> names = mbs.queryNames(name, name);
			for (ObjectName objectName : names) {
				mbs.unregisterMBean(objectName);
			}
			mbs.unregisterMBean(new ObjectName("org.ow2.frascati.jmx:name=FrascatiJmx"));
		} catch (MalformedObjectNameException e) {
			//e.printStackTrace();
		} catch (NullPointerException e) {
			//e.printStackTrace();
		} catch (MBeanRegistrationException e) {
			//e.printStackTrace();
		} catch (InstanceNotFoundException e) {
			//e.printStackTrace();
		}
		ReflectionHelper jettyHTTPServerEngineFactory  = new ReflectionHelper(
				ClassLoaderSingleton.classLoader(),
				"org.apache.cxf.transport.http_jetty.JettyHTTPServerEngineFactory");
		
		ReflectionHelper busFactory = new ReflectionHelper(
				ClassLoaderSingleton.classLoader(),
				"org.apache.cxf.BusFactory");
		
		ReflectionHelper bus = new ReflectionHelper(
				ClassLoaderSingleton.classLoader(),
				"org.apache.cxf.Bus");
		
		bus.set(busFactory.invokeStatic("getDefaultBus"));
		
		jettyHTTPServerEngineFactory.set(bus.invoke("getExtension",
			new Class<?>[]{Class.class},
			new Object[]{jettyHTTPServerEngineFactory.getReflectedClass()}));
		
	    jettyHTTPServerEngineFactory.invoke("destroyForPort",
	    		new Class<?>[]{int.class},
	    		new Object[]{8090});
	    
	    
    	FraSCAtiCompositeItf[] components = getComposites();
    	for(FraSCAtiCompositeItf component : components){    	
    		closeComponent(component);
    	}
    	
    	frascati = null;
    	classLoaderManager = null;    	
    	compositeManager = null;
    	frascatiClassLoader = null;
    }
}
