/*
 * Created on 11-Aug-2004
 * 
 * (c) 2003-2004 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package jbehave.junit;

import java.io.InputStream;
import java.util.Properties;

import jbehave.core.behaviour.BehaviourClass;
import jbehave.core.exception.JBehaveFrameworkError;
import jbehave.junit.listener.TestSuitePopulater;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * Runs a behaviour class in a JUnit test runner.
 * 
 * Looks for a properties file called <tt>jbehave.properties</tt> on
 * the classpath, containing a property called <tt>behaviourClass</tt>.<br/>
 * <br/>
 * This means you can set your project up with the jars for JBehave core
 * and the JUnit extension, as well as junit.jar, and then run
 * <tt>jbehave.extensions.junit.JUnitAdapter</tt> as a test inside any
 * old test runner (such as your IDE's). This will look for the properties
 * file and run the behaviour class it finds in there.
 * 
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 */
public class JUnitAdapter {

    private static Class BEHAVIOUR_CLASS = null;
    
    public static void setBehaviourClass(Class behaviourClass) {
        JUnitAdapter.BEHAVIOUR_CLASS = behaviourClass;
    }

    public static Test suite() {
        Class classToVerify = (BEHAVIOUR_CLASS != null ? BEHAVIOUR_CLASS : getBehaviourClass());
        final TestSuite[] suiteRef = new TestSuite[1]; // Collecting Parameter
        
        final TestSuitePopulater populater = new TestSuitePopulater(suiteRef);
        BehaviourClass visitableClass = new BehaviourClass(classToVerify);
        visitableClass.accept(populater);
        
        return suiteRef[0];
    }

    private static Class getBehaviourClass() {
        String behaviourClassName = null;
        try {
            Properties props = new Properties(System.getProperties());
            InputStream in = JUnitAdapter.class.getClassLoader().getResourceAsStream("jbehave.properties");
            if (in != null) {
                props.load(in);
                in.close();
            }
            behaviourClassName = props.getProperty("behaviourClass", behaviourClassName);
            return behaviourClassForName(behaviourClassName);
        } catch (Exception e) {
            throw new JBehaveFrameworkError("No behaviour class found for " + behaviourClassName);
        }
    }

    private static Class behaviourClassForName(String behaviourClassName) throws ClassNotFoundException {
        return Class.forName(behaviourClassName);
    }
}