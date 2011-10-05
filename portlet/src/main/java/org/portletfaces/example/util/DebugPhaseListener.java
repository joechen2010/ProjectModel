/**
 * Copyright (c) 2010 portletfaces.org All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.portletfaces.example.util;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.portletfaces.logging.Logger;
import org.portletfaces.logging.LoggerFactory;


public class DebugPhaseListener implements PhaseListener {

	private static final long serialVersionUID = 3533369390905922676L;
	private static final Logger logger = LoggerFactory.getLogger(DebugPhaseListener.class);

	public void afterPhase(PhaseEvent phaseEvent) {
		logger.debug("After Phase: {0}", phaseEvent.getPhaseId().toString());
	}

	public void beforePhase(PhaseEvent phaseEvent) {
		logger.debug("Before Phase: {0}", phaseEvent.getPhaseId().toString());
	}

	public PhaseId getPhaseId() {
		return PhaseId.ANY_PHASE;
	}
}
