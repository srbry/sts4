/*******************************************************************************
 * Copyright (c) 2017 Pivotal, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Pivotal, Inc. - initial API and implementation
 *******************************************************************************/
package org.springframework.ide.vscode.project.harness;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;

import org.mockito.Mockito;
import org.springframework.ide.vscode.boot.java.BootJavaLanguageServer;
import org.springframework.ide.vscode.boot.java.handlers.RunningAppProvider;
import org.springframework.ide.vscode.commons.boot.app.cli.SpringBootApp;
import org.springframework.ide.vscode.commons.util.ExceptionUtil;

public class MockRunningAppProvider {

	public final RunningAppProvider provider = mock(RunningAppProvider.class);
	public final Collection<SpringBootApp> mockedApps = new ArrayList<>();

	public MockRunningAppProvider() {
		try {
			when(provider.getAllRunningSpringApps()).thenReturn(mockedApps);
		} catch (Exception e) {
			throw ExceptionUtil.unchecked(e);
		}
	}

	/**
	 * Reset the mocks. Use this if the default's programmed into the mocks don't
	 * suite your test case.
	 * <p>
	 * Note: you may also choose to call {@link Mockito}.mock directly if you do not
	 * want to reset all of the mocks.
	 */
	public void reset() throws Exception {
		mockedApps.clear();
		Mockito.reset(provider);
	}

	public MockAppBuilder builder() {
		return new MockAppBuilder(this);
	}

	public static class MockAppBuilder {
		public final SpringBootApp app = mock(SpringBootApp.class);
		private MockRunningAppProvider runningAppProvider;

		public MockAppBuilder(MockRunningAppProvider runningAppProvider) {
			this.runningAppProvider = runningAppProvider;
		}

		public MockAppBuilder enviroment(String env) throws Exception {
			when(app.getEnvironment()).thenReturn(env);
			return this;
		}

		public MockAppBuilder beans(String beans) throws Exception {
			when(app.getBeans()).thenReturn(beans);
			return this;
		}

		public MockAppBuilder processId(String processId) {
			when(app.getProcessID()).thenReturn(processId);
			return this;
		}

		public MockAppBuilder processName(String name) {
			when(app.getProcessName()).thenReturn(name);
			return this;
		}

		public MockAppBuilder port(String port) throws Exception {
			when(app.getPort()).thenReturn(port);
			return this;
		}

		public MockAppBuilder isSpringBootApp(boolean isBoot) throws Exception {
			when(app.isSpringBootApp()).thenReturn(isBoot);
			return this;
		}

		public MockAppBuilder containsLanguageServerProcessPropery(boolean hasProperty) {
			when(app.containsSystemProperty(BootJavaLanguageServer.LANGUAGE_SERVER_PROCESS_PROPERTY))
					.thenReturn(hasProperty);
			return this;
		}

		public MockAppBuilder getRequestMappings(String mappings) throws Exception {
			when(app.getRequestMappings()).thenReturn(mappings);
			return this;
		}

		public void build() {
			runningAppProvider.mockedApps.add(app);
		}

	}
}