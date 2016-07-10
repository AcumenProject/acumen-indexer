package edu.ua.lib.acumen.doc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.io.input.ReaderInputStream;
import org.apache.commons.io.input.XmlStreamReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.ua.lib.acumen.AcumenError;
import edu.ua.lib.acumen.Statistics;
import edu.ua.lib.acumen.repo.Location;

public class AcumenDoc {
	protected Document doc = null;
	
	public AcumenDoc(File file, String ext){
		String xslFilePath = Location.ofSolrXSL(ext);
		if (xslFilePath != null){
			Document d = parseXMLFile(file);
			if (d != null){
				this.doc = transformXML(d, new File(xslFilePath));
			} else {
				Statistics.metaFailed(file.getName());
				System.out.println(file.getName()+" -- BROKEN - Likely invalid XML");
				this.doc = null;
			}
		}
		else{
			this.doc = parseTXTFile(file);
		}
	}
	
	public Document parseTXTFile(File file) {
		try{
			BufferedReader in = new BufferedReader(new FileReader(file));
			StringWriter trans = new StringWriter();
			String str;
			while ((str = in.readLine()) != null){
				trans.write(str);
			}
			in.close();
			
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			docFactory.setIgnoringComments(true);
			docFactory.setExpandEntityReferences(false);
			DocumentBuilder builder = docFactory.newDocumentBuilder();
	        Document tmpDoc = builder.newDocument();
	        
	        Element root = tmpDoc.createElement("add");
	        tmpDoc.appendChild(root);
	        
	        Element docElm = tmpDoc.createElement("doc");
	        root.appendChild(docElm);
	        
			Element transcript = tmpDoc.createElement("field");
			transcript.setAttribute("name", "transcript");
			docElm.appendChild(transcript);
			transcript.appendChild(tmpDoc.createTextNode(trans.toString()));
			
			return tmpDoc;
		} catch (IOException e){
			System.out.println("### "+file.getName()+" ---> IOException: "+e);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			Statistics.metaFailed(file.getName());
			System.out.println("### "+file.getName()+" ---> ParserConfigurationException: "+e);
			e.printStackTrace();
		}
		return null;
	}
	
	public Document parseXMLFile(File file) {
		XmlStreamReader xmlFile = null;
		ReaderInputStream ris = null;
		InputStream inStream = null;
		FileInputStream xslis = null;
        try {
        	xmlFile = new XmlStreamReader(file);
        	ris = new ReaderInputStream(xmlFile, "UTF-8");
        	inStream = new BOMInputStream(ris);
			
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			docFactory.setNamespaceAware(true);
			docFactory.setIgnoringElementContentWhitespace(true);
			
			DocumentBuilder inDoc = docFactory.newDocumentBuilder();
			inDoc.setErrorHandler(new AcumenError());
			Document tmpDoc = inDoc.parse(inStream);
        	
			return tmpDoc;
        } catch (SAXException e) {
            // A parsing error occurred; the xml input is not valid
        	System.out.println("### "+file.getName()+" ---> SAXEception:"+e);
        } catch (ParserConfigurationException e) {
        	System.out.println("### "+file.getName()+" ---> ParserConfigurationException: "+e);
        } catch (IOException e) {
        	System.out.println("### "+file.getName()+" ---> IOException: "+e);
        } finally {
        	IOUtils.closeQuietly(inStream);
        	IOUtils.closeQuietly(ris);
        	IOUtils.closeQuietly(xmlFile);
        	IOUtils.closeQuietly(xslis);
        }
        return null;
    }
	
	// Method adopted from:
		// http://www.exampledepot.com/egs/javax.xml.transform/Xsl2Dom.html
		protected Document transformXML(Document doc, File xslFile) {
			FileInputStream xslis = null;
			try {
				TransformerFactory factory = TransformerFactory.newInstance();
				xslis = new FileInputStream(xslFile);
				Templates template = factory.newTemplates(new StreamSource(xslis));
				Transformer xformer = template.newTransformer();
				Source source = new DOMSource(doc);
				
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				docFactory.setIgnoringComments(true);
				docFactory.setExpandEntityReferences(false);
				DocumentBuilder builder = docFactory.newDocumentBuilder();
				Document transDoc = builder.newDocument();
				Result result = new DOMResult(transDoc);

				xformer.transform(source, result);
				return transDoc;
			} catch (FileNotFoundException e) {
			} catch (TransformerConfigurationException e) {
				// An error occurred in the XSL file
			} catch (TransformerException e) {
				// An error occurred while applying the XSL file
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				IOUtils.closeQuietly(xslis);
			}
			return null;
		}	
		
		public void appendXML(Document childDoc){
			NodeList list = childDoc.getElementsByTagName("doc").item(0).getChildNodes();
			Element parentNode = (Element) this.doc.getElementsByTagName("doc").item(0);
			for (int i=0; i<list.getLength(); i++){
				Element el = (Element)list.item(i);
				Node dup = this.doc.importNode(el, true);
				//System.out.println("Apennding node: "+list.item(i).getNodeValue());
				parentNode.appendChild(dup);
			}
			
		}
	
	public void addNode(String name, String value){
		Element parentNode = (Element) this.doc.getElementsByTagName("doc").item(0);
		Element newNode = this.doc.createElement("field");
		newNode.setAttribute("name", name);
		newNode.appendChild(this.doc.createTextNode(value));
		parentNode.appendChild(newNode);
	}
	
	public String getTagValue(String tag){
		return this.doc.getElementsByTagName(tag).item(0).getTextContent();
	}
	
	public Document getDoc(){
		return this.doc;
	}
	
	public String getXPathValue(String xp){
		try {
			XPathExpression expr = newXPath(xp);
			return (String)expr.evaluate(this.doc, XPathConstants.STRING);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public XPathExpression newXPath(String xp){
		try {
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			xpath.setNamespaceContext(new NamespaceResolver());
			XPathExpression expr = (XPathExpression) xpath.compile(xp);
			return expr;
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void writeXMLFile(File file){
		try {
			Source source = new DOMSource(this.doc);
			Result result = new StreamResult(file);
			
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			xformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
	    } catch (TransformerException e) {
	    }
	}
	
	public void printDoc(){
		NodeList list = this.doc.getElementsByTagName("*");
		for (int i=0; i<list.getLength(); i++) {
		    Node n = list.item(i);
		    System.out.println(n.getNodeName()+" ::> "+n.getNodeValue());
		}
	}
	
	//subclass borrowed from http://www.ibm.com/developerworks/library/x-nmspccontext/
	public class NamespaceResolver implements NamespaceContext {

		public String getNamespaceURI(String prefix) {
	        if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
	            return doc.lookupNamespaceURI(null);
	        } else {
	            return doc.lookupNamespaceURI(prefix);
	        }
	    }

	    /**
	     * This method is not needed in this context, but can be implemented in a
	     * similar way.
	     */
	    public String getPrefix(String namespaceURI) {
	        return doc.lookupPrefix(namespaceURI);
	    }

	    public Iterator<?> getPrefixes(String namespaceURI) {
	        // not implemented yet
	        return null;
	    }
		
	}
	
}