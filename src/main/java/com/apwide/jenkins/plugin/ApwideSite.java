package com.apwide.jenkins.plugin;

import hudson.model.Job;
import hudson.plugins.jira.JiraSite;
import hudson.util.Secret;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Logger;

public class ApwideSite {

    private static final Logger LOGGER = Logger.getLogger(ApwideSite.class.getName());

    private ApwideSession session;
    private JiraSite site;

    private ApwideSite(JiraSite site) {
	this.site = site;
    }

    public static ApwideSite get(Job<?, ?> job) {
	JiraSite jiraSite = JiraSite.get(job);
	return new ApwideSite(jiraSite);
    }

    public ApwideSession getSession() throws IOException {
	if (session == null)
	    session = createSession();
	return session;
    }

    private ApwideSession createSession() throws IOException {
	String userName = site.userName;
	Secret password = site.password;
	URL url = site.url;
	int timeout = JiraSite.DEFAULT_TIMEOUT;

	if (userName == null || password == null)
	    return null; // remote access not supported

	final URI uri;
	try {
	    uri = url.toURI();
	} catch (URISyntaxException e) {
	    LOGGER.warning("convert URL to URI error: " + e.getMessage());
	    throw new RuntimeException("failed to create ApwideSession due to convert URI error");
	}
	LOGGER.fine("creating Apwide Session: " + uri);

	return new ApwideSession(this.site, new ApwideRestService(uri, userName, password.getPlainText(), timeout));
    }

}
