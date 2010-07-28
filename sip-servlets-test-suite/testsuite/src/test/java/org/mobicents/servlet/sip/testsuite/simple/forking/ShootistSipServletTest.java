/*
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.mobicents.servlet.sip.testsuite.simple.forking;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import javax.sip.SipProvider;

import org.apache.catalina.deploy.ApplicationParameter;
import org.apache.log4j.Logger;
import org.mobicents.servlet.sip.SipServletTestCase;
import org.mobicents.servlet.sip.core.session.SipStandardManager;
import org.mobicents.servlet.sip.startup.SipContextConfig;
import org.mobicents.servlet.sip.startup.SipStandardContext;

public class ShootistSipServletTest extends SipServletTestCase {
	private static transient Logger logger = Logger.getLogger(ShootistSipServletTest.class);		
	private static final int TIMEOUT = 30000;	
//	private static final int TIMEOUT = 100000000;
	
	public ShootistSipServletTest(String name) {
		super(name);
		startTomcatOnStartup = false;
		autoDeployOnStartup = false;
		addSipConnectorOnStartup = false;
	}

	@Override
	public void deployApplication() {
		assertTrue(tomcat.deployContext(
				projectHome + "/sip-servlets-test-suite/applications/shootist-sip-servlet/src/main/sipapp",
				"sip-test-context", "sip-test"));
	}
	
	public SipStandardContext deployApplication(String name, String value) {
		SipStandardContext context = new SipStandardContext();
		context.setDocBase(projectHome + "/sip-servlets-test-suite/applications/shootist-sip-servlet/src/main/sipapp");
		context.setName("sip-test-context");
		context.setPath("sip-test");
		context.addLifecycleListener(new SipContextConfig());
		context.setManager(new SipStandardManager());
		ApplicationParameter applicationParameter = new ApplicationParameter();
		applicationParameter.setName(name);
		applicationParameter.setValue(value);
		context.addApplicationParameter(applicationParameter);
		assertTrue(tomcat.deployContext(context));
		return context;
	}
	
	public SipStandardContext deployApplication(Map<String, String> params) {
		SipStandardContext context = new SipStandardContext();
		context.setDocBase(projectHome + "/sip-servlets-test-suite/applications/shootist-sip-servlet/src/main/sipapp");
		context.setName("sip-test-context");
		context.setPath("sip-test");
		context.addLifecycleListener(new SipContextConfig());
		context.setManager(new SipStandardManager());
		for (Entry<String, String> param : params.entrySet()) {
			ApplicationParameter applicationParameter = new ApplicationParameter();
			applicationParameter.setName(param.getKey());
			applicationParameter.setValue(param.getValue());
			context.addApplicationParameter(applicationParameter);
		}
		assertTrue(tomcat.deployContext(context));
		return context;
	}
	
	public SipStandardContext deployApplicationServletListenerTest() {
		SipStandardContext context = new SipStandardContext();
		context.setDocBase(projectHome + "/sip-servlets-test-suite/applications/shootist-sip-servlet/src/main/sipapp");
		context.setName("sip-test-context");
		context.setPath("sip-test");
		context.addLifecycleListener(new SipContextConfig());
		context.setManager(new SipStandardManager());
		ApplicationParameter applicationParameter = new ApplicationParameter();
		applicationParameter.setName("testServletListener");
		applicationParameter.setValue("true");
		context.addApplicationParameter(applicationParameter);
		assertTrue(tomcat.deployContext(context));
		return context;
	}	

	@Override
	protected String getDarConfigurationFile() {
		return "file:///" + projectHome + "/sip-servlets-test-suite/testsuite/src/test/resources/" +
				"org/mobicents/servlet/sip/testsuite/simple/shootist-sip-servlet-dar.properties";
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();												
	}
	
	// non regression test for Issue 1468 http://code.google.com/p/mobicents/issues/detail?id=1468
	// Extra 200 class responses to forked INVITE cause Null Pointer Exception
	public void testShootistForking() throws Exception {		
        Shootme shootme1 = new Shootme(5080,true,400*2 - 200*0);
        SipProvider shootmeProvider = shootme1.createProvider();
        shootmeProvider.addSipListener(shootme1);
        Shootme shootme2 = new Shootme(5081,false,400*2 - 200*1);
        SipProvider shootme2Provider = shootme2.createProvider();
        shootme2Provider.addSipListener(shootme2);
		Proxy proxy = new Proxy(5070,2);
		SipProvider provider = proxy.createSipProvider();
        provider.addSipListener(proxy);

        sipConnector = tomcat.addSipConnector(serverName, sipIpAddress, 5060, listeningPointTransport, null);
		tomcat.startTomcat();
		Map<String, String> params= new HashMap<String, String>();
		params.put("route", "sip:127.0.0.1:5070");
		params.put("timeToWaitForBye", "20000");
		deployApplication(params);
		Thread.sleep(TIMEOUT);
		proxy.stop();
		shootme1.stop();
		shootme2.stop();
		assertTrue(shootme2.isAckSeen());		
		assertTrue(shootme2.checkBye());
		assertTrue(shootme1.isAckSeen());
		assertTrue(shootme1.checkBye());		
	}
	
	
	@Override
	protected void tearDown() throws Exception {					
		logger.info("Test completed");
		super.tearDown();
	}
}