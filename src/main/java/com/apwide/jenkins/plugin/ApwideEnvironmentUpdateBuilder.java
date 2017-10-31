package com.apwide.jenkins.plugin;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.model.Result;
import hudson.model.TaskListener;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.plugins.jira.Messages;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;

import java.io.IOException;

import javax.servlet.ServletException;

import jenkins.tasks.SimpleBuildStep;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

public class ApwideEnvironmentUpdateBuilder extends Builder implements SimpleBuildStep {
    private final String environmentId;
    private final String statusId;

    @DataBoundConstructor
    public ApwideEnvironmentUpdateBuilder(String environmentId, String statusId) {
	this.environmentId = Util.fixEmptyAndTrim(environmentId);
	this.statusId = Util.fixEmptyAndTrim(statusId);
    }

    public String getEnvironmentId() {
	return environmentId;
    }

    public String getStatusId() {
	return statusId;
    }

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener) throws InterruptedException, IOException {
	String realEnvironmentId = Util.fixEmptyAndTrim(run.getEnvironment(listener).expand(environmentId));
	String realStatusId = Util.fixEmptyAndTrim(run.getEnvironment(listener).expand(statusId));

	ApwideSite site = ApwideSite.get(run.getParent());

	if (site == null) {
	    listener.getLogger().println(Messages.NoJiraSite());
	    run.setResult(Result.FAILURE);
	}
	
	ApwideSession session = site.getSession();

	if (StringUtils.isNotEmpty(environmentId)) {
	    listener.getLogger().println(Messages.JiraIssueUpdateBuilder_UpdatingWithAction("real workflow apwide Name"));
	}

	listener.getLogger().println("[APWIDE] environmentId: " + environmentId + statusId);
	session.updateStatus(realEnvironmentId, realStatusId);
	run.setResult(Result.UNSTABLE);
    }

    @Override
    public DescriptorImpl getDescriptor() {
	return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

	public FormValidation doCheckEnvironmentId(@QueryParameter String value) throws IOException, ServletException {
	    if (value.length() == 0) {
		return FormValidation.error("Environment cannot be null!");// Messages.JiraIssueUpdateBuilder_NoJqlSearch());
	    }
	    return FormValidation.ok();
	}
	
	public FormValidation doCheckStatusId(@QueryParameter String value) throws IOException, ServletException {
	    return FormValidation.ok();
	}


	public boolean isApplicable(Class<? extends AbstractProject> klass) {
	    return true;
	}

	public String getDisplayName() {
	    // return Messages.JiraIssueUpdateBuilder_DisplayName();
	    return "Apwide environments";
	}
    }
}
