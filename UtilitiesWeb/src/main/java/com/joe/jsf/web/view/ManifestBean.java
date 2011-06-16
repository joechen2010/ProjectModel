package com.joe.jsf.web.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

/**
 * The Class ManifestBean. This class returns WAR file manifest information.
 * 
 * @author Jane Eisenstein
 */
@ManagedBean(name="ManifestBean")
@ApplicationScoped
public class ManifestBean {
	
	static Map<String,String> manifest = null;
	
	private static void initManifest() {
		if (manifest == null) {
			try {
				FacesContext facesContext = FacesContext.getCurrentInstance();
				ExternalContext externalContext = facesContext.getExternalContext();
				ServletContext context = (ServletContext) externalContext.getContext();
				String manifestPath = context.getRealPath("/META-INF/MANIFEST.MF");
				File manifestFile = new File(manifestPath);
				BufferedReader in = new BufferedReader(new FileReader(manifestFile));
				manifest = new HashMap<String,String>();
	        	String text = null;
	        	while ((text = in.readLine()) != null) {
					String key, value;
					int colonIndex = text.indexOf(':');
					if (colonIndex > 0) {
					key = text.substring(0, colonIndex);
					int valueIndex = colonIndex+2;
					if (valueIndex < text.length())
						value = text.substring(colonIndex + 2);
					else
						value = null;
					System.out.println("key='"+key+"', value='"+value+"'");
					manifest.put(key, value);
					}
				}
				in.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
	
	public String getArtifactId() {
		initManifest();
		String artifactId = manifest.get("Artifact-Id");
		return artifactId;
	}
	
	public String getImplementationVersion() {
		initManifest();
		String version = manifest.get("Implementation-Version");
		return version;
	}
	
	public String getBuild() {
		initManifest();
		String build = manifest.get("Build");
		return build;
	}
	

}
