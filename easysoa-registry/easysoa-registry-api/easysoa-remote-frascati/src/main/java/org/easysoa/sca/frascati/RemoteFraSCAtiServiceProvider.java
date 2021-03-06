/**
 * EasySOA Registry
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
package org.easysoa.sca.frascati;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.easysoa.frascati.api.FraSCAtiServiceItf;
import org.easysoa.frascati.api.FraSCAtiServiceProviderItf;

/**
 * @author jguillemotte
 * 
 */
public class RemoteFraSCAtiServiceProvider implements
        FraSCAtiServiceProviderItf
{
    
    public static final String REMOTE_FRASCATI_LIBRARIES_BASEDIR = 
            "org.easysoa.remote.frascati.libraries.basedir";
    
    private static Logger log = Logger.getLogger(
            RemoteFraSCAtiServiceProvider.class.getCanonicalName());
    
    private FraSCAtiServiceItf frascatiService;
    private Object frascati;
    private UpdatableURLClassLoader icl;
    
    private Class<?> componentClass = null;
    private Class<?> lifecycleClass = null;
    private Object factory = null;
    
    /**
     * Constructor 
     * 
     * launch a new FraSCAti instance which is not a Nuxeo application
     * 
     * @param librariesDirectory
     *          the directory where the FraSCAti's libraries are stored
     *          
     * @throws Exception
     */
    public RemoteFraSCAtiServiceProvider(File librariesDirectory) throws Exception
    {
        // Required for Frascati Web explorer
        // If this property is not set, the following error is throwed '<sca:component name="WebExplorer"> - <sca:implementation> must be defined'
        // See discussion at https://groups.google.com/forum/#!msg/easysoa-dev/LE66ptRLL3A/kSSzJrIMzMcJ
        System.setProperty("org.ow2.frascati.bootstrap", "org.ow2.frascati.bootstrap.FraSCAtiWebExplorer");
        
        if(librariesDirectory == null)
        {
            String librariesDirectoryProp = System.getProperty(
                    REMOTE_FRASCATI_LIBRARIES_BASEDIR);
            
            if(librariesDirectoryProp == null 
                    || !(librariesDirectory = new File(
                            librariesDirectoryProp)).exists())
            {
                throw new InstantiationException("Enable to instantiate a new" +
                " remote FraSCAti instance : no libraries directory found" );
            }
        }
        File[] libraries = librariesDirectory.listFiles(
                new FilenameFilter(){
                    @Override
                    public boolean accept(File arg0, String arg1)
                    {
                        if(arg1.endsWith(".jar"))
                        {
                            return true;
                        }
                        return false;
                    }
                    
                });
        
        //Define a new URLClassLoader using a parent which allow to find shared classes
        icl = new UpdatableURLClassLoader(Thread.currentThread().getContextClassLoader());
        if(libraries != null)
        {
            for(File library : libraries)
            {
               icl.addURL(library.toURI().toURL()); 
            }
        }
        Class<?> frascatiClass = icl.loadClass("org.ow2.frascati.FraSCAti");
        
        ClassLoader current = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(icl);
        
        try
        {
        frascati = frascatiClass.getDeclaredMethod("newFraSCAti").invoke(null);
//                ,new Class<?>[]{ClassLoader.class}).invoke(null,
//                        new Object[]{icl});
        } catch(Exception e)
        {
            throw e;
        } finally
        {
            Thread.currentThread().setContextClassLoader(current);
        }
        Class<?> managerClass = icl.loadClass(
                "org.ow2.frascati.assembly.factory.api.CompositeManager");
        
        lifecycleClass = icl.loadClass(
                "org.objectweb.fractal.api.control.LifeCycleController");
        
        componentClass = icl.loadClass(
                "org.objectweb.fractal.api.Component");
        
        Object manager = frascatiClass.getDeclaredMethod(
                "getCompositeManager").invoke(frascati);
        
        Object component = managerClass.getDeclaredMethod(
                "getTopLevelDomainComposite").invoke(manager);
        
        factory = getComponent(component,
                "org.ow2.frascati.FraSCAti/assembly-factory");
       
        frascatiService = (FraSCAtiServiceItf)
                frascatiClass.getDeclaredMethod("getService",new Class<?>[]{
                        componentClass, 
                        String.class,
                        Class.class})
                .invoke(frascati, new Object[]{
                        factory,
                        "easysoa-frascati-service", 
                        FraSCAtiServiceItf.class});
    }
    
    
    /**
     * Find a Fractal Component using a parent of it and its path
     * 
     * @param currentComponent
     *          a parent of the search component
     * @param componentPath
     *          the path of the component
     * @return
     *          the component if it has been found. Null otherwise
     * @throws Exception
     */
    private Object getComponent(Object currentComponent,
            String componentPath) throws Exception
    {
        String[] componentPathElements = componentPath.split("/");
        String lookFor = componentPathElements[0];
        String next = null;
        
        if(componentPathElements.length>1)
        {
            int n = 1;
            StringBuilder nextSB = new StringBuilder();
            for(;n<componentPathElements.length;n++)
            {
                nextSB.append(componentPathElements[n]);
                if(n<componentPathElements.length - 1)
                {
                    nextSB.append("/");
                }
            }
            next = nextSB.toString();
        }  
       
       Class<?> componentClass = icl.loadClass(
               "org.objectweb.fractal.api.Component");
       
       Class<?> contentControllerClass = icl.loadClass(
               "org.objectweb.fractal.api.control.ContentController");
       
       Class<?> nameControllerClass = icl.loadClass(
               "org.objectweb.fractal.api.control.NameController");
      
       Object contentController = componentClass.getDeclaredMethod(
               "getFcInterface",new Class<?>[]{String.class}).invoke(
                       currentComponent,"content-controller");
     
       Object[] subComponents = (Object[]) 
               contentControllerClass.getDeclaredMethod(
                       "getFcSubComponents",(Class<?>[])null).invoke(
                               contentController,(Object[])null);
       
        if(subComponents == null)
        {
            return null;
        }
        for(Object o : subComponents)
        {
            Object nameController = componentClass.getDeclaredMethod("getFcInterface",
                    new Class<?>[]{String.class}).invoke(o,"name-controller");
            
            String name = (String) nameControllerClass.getDeclaredMethod("getFcName",
                   (Class<?>[])null).invoke(nameController,(Object[])null);
            
            if(lookFor.equals(name))
            {
                if(next == null || next.length() ==0)
                {
                    return o;
                } else 
                {
                    return getComponent(o,next);
                }
            }
        }
        return null;
    }
    
    public FraSCAtiServiceItf getFraSCAtiService() {

        return frascatiService;
    }
    
    /**
     * @throws Exception 
     * 
     */
    public void stopFraSCAtiService() throws Exception
    {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName name;
            try
            {
                name = new ObjectName("SCA domain:name0=*,*");
                Set<ObjectName> names = mbs.queryNames(name, name);
                for (ObjectName objectName : names)
                {
                    mbs.unregisterMBean(objectName);
                }
                mbs.unregisterMBean(new ObjectName(
                        "org.ow2.frascati.jmx:name=FrascatiJmx"));
            } catch (MalformedObjectNameException e)
            {
                // e.printStackTrace();
            } catch (NullPointerException e)
            {
                // e.printStackTrace();
            } catch (MBeanRegistrationException e)
            {
                // e.printStackTrace();
            } catch (InstanceNotFoundException e)
            {
                // e.printStackTrace();
            }
            
            Object lifeCycleController = componentClass.getDeclaredMethod(
                    "getFcInterface", new Class<?>[]{String.class}).invoke(
                            factory,new Object[]{"lifecycle-controller"});
            
            lifecycleClass.getDeclaredMethod("stopFc").invoke(
                    lifeCycleController);
            
            frascatiService = null;
            frascati = null;
            icl = null;
            Runtime.getRuntime().runFinalization();
            System.gc();
        }
    
    /**
     * An URLClassLoader which allow to use the addURL method
     */
    private class UpdatableURLClassLoader extends URLClassLoader
    {
        /**
         * @param urls
         * @param parent
         * @param factory
         */
        public UpdatableURLClassLoader(ClassLoader parent)
        {
            super(new URL[0], parent);
        }

        /**
         * @param url
         */
        @Override
        public void addURL(URL url)
        {
            //log.debug("adding url to load : " + url);
            super.addURL(url);
        }
        
        /**
         * {@inheritDoc}
         * 
         * @see java.lang.ClassLoader#loadClass(java.lang.String, boolean)
         */
        protected synchronized Class<?> loadClass(String name, boolean resolve)
                throws ClassNotFoundException
        {
            // First, check if the class has already been loaded

            boolean regular = false;
            Class<?> clazz = findLoadedClass(name);

            if (clazz != null)
            {
                return clazz;
            }
            if (name.startsWith("java.") || name.startsWith("javax.")
                    || name.startsWith("org.w3c.")
                    || name.startsWith("org.apache.log4j"))
            {
                regular = true;
                if (getParent() != null)
                {
                    try
                    {
                        clazz = getParent().loadClass(name);
                        
                    } catch (ClassNotFoundException e)
                    {
                        log.log(Level.CONFIG,
                                "'" + name + "' class not found using the parent classloader");
                    }
                }
            }
            if (clazz == null)
            {
                try
                {
                    clazz = findClass(name);

                } catch (ClassNotFoundException e)
                {
                    log.log(Level.CONFIG,
                            "'" + name  + "' class not found using the classloader classpath");
                }
                if (clazz == null && !regular && getParent() != null)
                {
                        clazz = getParent().loadClass(name);
                }
            }
            if (clazz == null)
            {
                throw new ClassNotFoundException(name);
            }
            if (resolve)
            {
                resolveClass(clazz);
            }
            return clazz;
        }
                     
        /**
         * {@inheritDoc}
         * 
         * @see java.lang.ClassLoader#getResource(java.lang.String)
         */
        @Override
        public URL getResource(String name) 
        {
            URL url = null;
            boolean regular = false;
            
            if (name.startsWith("java.") || name.startsWith("javax.")
                    || name.startsWith("org.w3c.")
                    || name.startsWith("org.apache.log4j"))
            {
                regular = true;
                if (getParent() != null)
                {
                   url = getParent().getResource(name);
                }
            } 
            if(url == null)
            {
                url = findResource(name);
            }
            if (url==null && !regular && getParent() != null)
            {
               url = getParent().getResource(name);
            }
            return url;
        }
    }
}
