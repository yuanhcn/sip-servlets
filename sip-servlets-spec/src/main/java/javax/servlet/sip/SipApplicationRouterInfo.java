package javax.servlet.sip;

import java.io.Serializable;

/**
 * The SipApplicationRouterInfo class encapsulates the different pieces of
 * information that the application router returns to the container when the
 * container calls the SipApplicationRouter.getNextApplication() method.
 * 
 * @since 1.1
 */
public class SipApplicationRouterInfo {
	private String nextApplicationName = null;

	private String subscriberURI = null;

	private String route = null;

	private SipRouteModifier mod = null;

	private Serializable stateInfo = null;

	/**
	 * Creates a SipApplicationRouterInfo object containing the information
	 * necessary for the conatiner to perform its routing decision.
	 * 
	 * @param nextApplicationName
	 *            The name of the application that the application router
	 *            selects to service this request. If no further application is
	 *            needed in the current region, this is set to null.
	 * @param subscriberURI
	 *            The URI that the application is selected to serve
	 * @param route
	 *            The route header that could either be an external or internal
	 *            route, the internal route replaces the route popped by the
	 *            container
	 * @param mod
	 *            An enum modifier which qualifies the route returned and the
	 *            router behavior
	 * @param stateInfo
	 *            Arbitrary state information of the application router that it
	 *            wishes the container to store on its behalf
	 */
	public SipApplicationRouterInfo(
			java.lang.String nextApplicationName,
			java.lang.String subscriberURI, 
			java.lang.String route,
			javax.servlet.sip.SipRouteModifier mod,
			java.io.Serializable stateInfo) {
		this.nextApplicationName = nextApplicationName;
		this.subscriberURI = subscriberURI;
		this.route = route;
		this.mod = mod;
		this.stateInfo = stateInfo;
	}

	public java.lang.String getNextApplicationName() {
		return nextApplicationName;
	}

	/**
	 * A SIP Route, if external is to be used by the container to route the
	 * request to the external entity. The internal route indicates the route
	 * which led the request to the container. This internal route is not used
	 * for any routing purposes but to let application router potentially modify
	 * the route popped by the container.
	 */
	public java.lang.String getRoute() {
		return route;
	}

	public javax.servlet.sip.SipRouteModifier getRouteModifier() {
		return mod;
	}

	public javax.servlet.sip.SipApplicationRoutingRegion getRoutingRegion() {
		return null; // TODO ask the EG why it's not in the constructor !!
	}

	public java.io.Serializable getStateInfo() {
		return stateInfo;
	}

	public java.lang.String getSubscriberURI() {
		return subscriberURI;
	}

}
