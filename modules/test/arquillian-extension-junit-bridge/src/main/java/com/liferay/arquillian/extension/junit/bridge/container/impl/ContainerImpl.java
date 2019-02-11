/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.arquillian.extension.junit.bridge.container.impl;

import org.jboss.arquillian.config.descriptor.api.ContainerDef;
import org.jboss.arquillian.config.descriptor.api.ProtocolDef;
import org.jboss.arquillian.container.spi.Container;
import org.jboss.arquillian.container.spi.client.container.ContainerConfiguration;
import org.jboss.arquillian.container.spi.client.container.DeployableContainer;
import org.jboss.arquillian.container.spi.client.container.LifecycleException;
import org.jboss.arquillian.container.spi.client.protocol.ProtocolDescription;
import org.jboss.arquillian.container.spi.event.container.AfterKill;
import org.jboss.arquillian.container.spi.event.container.AfterSetup;
import org.jboss.arquillian.container.spi.event.container.AfterStart;
import org.jboss.arquillian.container.spi.event.container.BeforeKill;
import org.jboss.arquillian.container.spi.event.container.BeforeSetup;
import org.jboss.arquillian.container.spi.event.container.BeforeStart;
import org.jboss.arquillian.container.spi.event.container.ContainerEvent;
import org.jboss.arquillian.core.api.Event;
import org.jboss.arquillian.core.api.annotation.Inject;

/**
 * @author Matthew Tambara
 */
public class ContainerImpl implements Container {

	public ContainerImpl(
		String name, DeployableContainer<?> deployableContainer) {

		_name = name;
		_deployableContainer = deployableContainer;
	}

	@Override
	public ContainerConfiguration createDeployableConfiguration() {
		return null;
	}

	@Override
	public ContainerDef getContainerConfiguration() {
		return null;
	}

	@Override
	public DeployableContainer<?> getDeployableContainer() {
		return _deployableContainer;
	}

	@Override
	public Throwable getFailureCause() {
		return _failureCause;
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public ProtocolDef getProtocolConfiguration(
		ProtocolDescription protocolDescription) {

		return null;
	}

	@Override
	public Container.State getState() {
		return _state;
	}

	@Override
	public boolean hasProtocolConfiguration(
		ProtocolDescription protocolDescription) {

		return false;
	}

	@Override
	public void kill() {
		_containerEvent.fire(new BeforeKill(_deployableContainer));

		_containerEvent.fire(new AfterKill(_deployableContainer));
	}

	@Override
	public void setState(Container.State state) {
		_state = state;
	}

	@Override
	public void setup() {
		_containerEvent.fire(new BeforeSetup(_deployableContainer));

		setState(Container.State.SETUP);

		_containerEvent.fire(new AfterSetup(_deployableContainer));
	}

	@Override
	public void start() throws LifecycleException {
		_containerEvent.fire(new BeforeStart(_deployableContainer));

		try {
			_deployableContainer.start();

			setState(Container.State.STARTED);
		}
		catch (LifecycleException le) {
			setState(Container.State.STARTED_FAILED);

			_failureCause = le;

			throw le;
		}

		_containerEvent.fire(new AfterStart(_deployableContainer));
	}

	@Override
	public void stop() {
	}

	@Inject
	private Event<ContainerEvent> _containerEvent;

	private final DeployableContainer<?> _deployableContainer;
	private Throwable _failureCause;
	private final String _name;
	private Container.State _state = Container.State.STOPPED;

}