package org;

import java.io.*;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.LinkedInApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class LinkedinReader {
	
	public OAuthService service;
	public Token accessToken;
	public OAuthRequest request;
	public Response response;
	
	//do initialization work
	public LinkedinReader(){
		//initialize params  *** u can skip this
		service = new ServiceBuilder()
					.provider(LinkedInApi.class)
					.apiKey("77csjm87wajmgp")
					.apiSecret("vua37ESj6e7efVyD")
					.build();
		accessToken = new  Token("69163324-40f2-4aca-9e21-d37af8c9a734", "83536e15-6de6-477e-96cb-09de1b931e95");
		request = null;
		response = null;
		//initialize finish 
	}
	
	//get response from Linekdin, stored in String result
	public String launchQuery(String url){
		//set up to launch  *** u can skip this
	    request = new OAuthRequest(Verb.GET, url);
	    service.signRequest(accessToken, request);
	    response = request.send();
	    
	    //String result is what we need. If u print it, it's in xml file format already
	    String result = response.getBody();
	    
	    return result;
	}
	
	//save string to local file
	public void saveToLocal(String data, String fileName){
		try {
	    	Writer out = new BufferedWriter(new OutputStreamWriter(
					 new FileOutputStream(fileName), "UTF-8"));
	    	out.write(data);
	    	out.close();
	    }
	    catch(Exception e){
	    	//do nothing
	    }
	}
	
	
	public void parseXML(String fileName, String targetTitle, String[] columnNames){
		try {
			File jobs = new File(fileName);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(jobs);
			doc.getDocumentElement().normalize();

			//System.out.println("root of xml file" + doc.getDocumentElement().getNodeName());
			NodeList nodes = doc.getElementsByTagName("job");
			System.out.println("jobs count: " + nodes.getLength());

			for (int i = 0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) node;
					for(String column:columnNames){
						System.out.println(column+": " + getValue(column, element));
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	//used for parseXML
	public String getValue(String tag, Element element) {
		NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
		Node node = (Node) nodes.item(0);
		return node.getNodeValue();
	}



	public static void main(String[] args) {

		LinkedinReader jobReader = new LinkedinReader();
		
		//set what to query
		String url = "http://api.linkedin.com/v1/job-search:(jobs:(id,description))?facet=industry,4&facet=industry,96&start=0&count=5";
		
		//start to query
		String result;
		result = jobReader.launchQuery(url);
		
		//save result to local xml file
		String fileName = "jobs.xml";
		jobReader.saveToLocal(result, fileName);
		
		//parse local xml file, print out interested columns
		String targetTitle = "job";
		String[] columnNames = {"id","description"};
		jobReader.parseXML(fileName,targetTitle,columnNames);

	}

}
