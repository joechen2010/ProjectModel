package com.joe.utilities.core.startup.listener;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;


public class DebugPhaseListener  implements
		javax.faces.event.PhaseListener {

		/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		public void afterPhase(PhaseEvent event) {
			System.out.println("after phase: " + event.getPhaseId());
			FacesContext.getCurrentInstance().getExternalContext().log("AFTER - "+
	                                  event.getPhaseId());
		}
		public void beforePhase(PhaseEvent event) {
			System.out.println("before phase: " + event.getPhaseId());
			FacesContext.getCurrentInstance().getExternalContext().log("BEFORE - "+
	                                 event.getPhaseId());

		}
		public PhaseId getPhaseId() {
			return PhaseId.ANY_PHASE;
		}
}
