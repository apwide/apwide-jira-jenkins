package com.apwide.jenkins.plugin;

import java.util.logging.Logger;

import hudson.plugins.jira.JiraSite;

public class ApwideSession {

    private static final Logger LOGGER = Logger.getLogger(ApwideSession.class.getName());

    private ApwideRestService rest;
    private JiraSite site;

    ApwideSession(JiraSite site, ApwideRestService rest) {
	this.site = site;
	this.rest = rest;
    }

    public void updateStatus(String environmentId, String statusId) {
	LOGGER.info("Update environmnent..." + environmentId + " " + statusId);
    }

}
