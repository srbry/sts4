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
package org.springframework.ide.vscode.commons.cloudfoundry.client.cftarget;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.springframework.ide.vscode.commons.util.StringUtil;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

/**
 * CF Client parameter provider based on the JSON data. the JSON data is
 * cosidered to be data from the from config changed message defined in the LSP
 * spec
 * {@link https://github.com/Microsoft/language-server-protocol/blob/master/protocol.md#workspace_didChangeConfiguration}
 * 
 * @author Alex Boyko
 *
 */
public class CfJsonParamsProvider implements ClientParamsProvider {
	
	private static final String NO_TARGETS_FOUND_MESSAGE = "No targets found";
	private static final String NO_NETWORK_CONNECTION = "No connection to Cloud Foundry";
	private static final String NO_ORG_SPACE = "No org/space selected";
	
	private static final String TARGET = "Target";
	private static final String REFRESH_TOKEN = "RefreshToken";
	private static final String SSL_DISABLED = "SSLDisabled";
	private static final String ORG_NAME = "OrgName";
	private static final String SPACE_NAME = "SpaceName";
	
	private static final String PROP_NO_TARGETS_FOUND = "noTargetsFound";
	private static final String PROP_UNAUTHORISED = "unauthorised";
	private static final String PROP_NO_NETWORK_CONNECTION = "noNetworkConnection";
	private static final String PROP_NO_ORG_SPACE = "noOrgSpace";
	
	private Supplier<Collection<CFClientParams>> paramsSupplier;
	private Map<String, String> messages;
	
	public CfJsonParamsProvider(List<?> json, Map<String, String> messages) {
		this.messages = messages;
		this.paramsSupplier = Suppliers.memoize(() -> json
				.stream()
				.filter(o -> o instanceof Map<?, ?>)
				.map(m -> parseCfClientParams((Map<?,?>)m))
				.filter(Objects::nonNull)
				.collect(Collectors.toList()));
	}

	@Override
	public Collection<CFClientParams> getParams() throws NoTargetsException, ExecutionException {
		Collection<CFClientParams> params = paramsSupplier.get();
		if (params == null || params.isEmpty()) {
			throw new NoTargetsException(getMessages().noTargetsFound());
		}
		return params;
	}
	
	private static CFClientParams parseCfClientParams(Map<?, ?> userData) {
		String refreshToken = (String) userData.get(REFRESH_TOKEN);
		// Only support connecting to CF via refresh token for now
		if (StringUtil.hasText(refreshToken)) {
			CFCredentials credentials = CFCredentials.fromRefreshToken(refreshToken);
			boolean sslDisabled = (Boolean) userData.get(SSL_DISABLED);
			String target = (String) userData.get(TARGET);
			String orgName = (String) userData.get(ORG_NAME);
			String spaceName = (String) userData.get(SPACE_NAME);
			if (target != null && StringUtil.hasText(orgName) && StringUtil.hasText(spaceName)) {
				return new CFClientParams(target, null, credentials, orgName, spaceName, sslDisabled);
			}
		}
		return null;
	}


	@Override
	public CFParamsProviderMessages getMessages() {
		return new CFParamsProviderMessages() {

			@Override
			public String noTargetsFound() {
				if (messages != null && messages.containsKey(PROP_NO_TARGETS_FOUND)) {
					return messages.get(PROP_NO_TARGETS_FOUND);
				}
				return NO_TARGETS_FOUND_MESSAGE;
			}

			@Override
			public String unauthorised() {
				if (messages != null && messages.containsKey(PROP_UNAUTHORISED)) {
					return messages.get(PROP_UNAUTHORISED);
				}
				return NO_NETWORK_CONNECTION;
			}

			@Override
			public String noNetworkConnection() {
				if (messages != null && messages.containsKey(PROP_NO_NETWORK_CONNECTION)) {
					return messages.get(PROP_NO_NETWORK_CONNECTION);
				}
				return NO_NETWORK_CONNECTION;
			}

			@Override
			public String noOrgSpace() {
				if (messages != null && messages.containsKey(PROP_NO_ORG_SPACE)) {
					return messages.get(PROP_NO_ORG_SPACE);
				}
				return NO_ORG_SPACE;
			}

		};
	}

}
