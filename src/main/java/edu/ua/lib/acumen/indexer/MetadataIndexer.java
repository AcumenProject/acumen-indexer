package edu.ua.lib.acumen.indexer;

import java.io.File;
import java.sql.SQLException;
import java.util.Map;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import edu.ua.lib.acumen.Statistics;
import edu.ua.lib.acumen.database.Authority;
import edu.ua.lib.acumen.database.AuthorityDAO;
import edu.ua.lib.acumen.database.AuthorityTypeDAO;
import edu.ua.lib.acumen.database.Metadata;
import edu.ua.lib.acumen.database.MetadataDAO;
import edu.ua.lib.acumen.doc.AcumenDoc;
import edu.ua.lib.acumen.repo.Database;

public class MetadataIndexer {
	
	private AcumenDoc doc = null;
	private File file;
	private String typeId, ext, repoLoc;
	private long authority_parent_id = -1;
	private long authority_item_id = -1;
	private long id = -1;
	private long lastModified = -1;
	//private long insertedID = -1;
	
	public MetadataIndexer(File file, Map<String, String> info){
		this.file = file;
		this.typeId = info.get("type_id");
		this.ext = info.get("ext");
		this.repoLoc = info.get("repo_loc");
		//System.out.print(file.getName()+" -- Metadata\n");
	}
	
	public void index(){
		MetadataDAO mDAO = new MetadataDAO();
		Metadata oldMeta = null;
		Map<String, Long> exists = null;
		
		try {
			exists = mDAO.exists(this.file.getName());
			if (!exists.isEmpty()){
				this.id = exists.get("id");
				this.lastModified = exists.get("lastModified");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (this.id >= 0){
			try {
				if (this.file.lastModified() != this.lastModified){
					this.doc = new AcumenDoc(this.file, ext);
					if (this.doc.getDoc() != null){
						oldMeta = mDAO.get(this.file.getName());
						Metadata metadata = buildNewMetadata(oldMeta, mDAO);
						mDAO.update(metadata);
						if (this.authority_parent_id < 0){
							this.authority_parent_id = metadata.getId();
						}
						indexAuthorities(this.doc.getDoc(), true);
					}
				} else{
					mDAO.found(this.id);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				Statistics.metaFailed(file.getName());
				e.printStackTrace();
			}
		} else {
			this.doc = new AcumenDoc(this.file, ext);
			if (this.doc.getDoc() != null) {
				try {
					Metadata metadata = buildNewMetadata(oldMeta, mDAO);
					this.id = mDAO.insert(metadata);
					
					if (metadata.getStatusTypeId() == 1){
						//System.out.println("repoloc: "+this.repoLoc+", id: "+this.id);
						mDAO.verifyChildren(this.id, metadata.getParentId(), this.repoLoc);
					}
					if (this.authority_parent_id < 0){
						this.authority_parent_id = this.id;
					}
					indexAuthorities(this.doc.getDoc());
					Statistics.addedMetadata();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					Statistics.metaFailed(file.getName());
					e.printStackTrace();
				}
			}
		}
		mDAO = null;
	}
	
	public Metadata buildNewMetadata(Metadata oldMeta, MetadataDAO mDAO) throws SQLException{
		Metadata newMeta = null;
		String title = "Untitled";
		long parent_id = 0;
		
		if ((".txt").equals(this.ext) || (".tags.xml").equals(this.ext)){
			Map<String, String> parentInfo = mDAO.getParentInfo(this.repoLoc);
			title = parentInfo.get("title");
			parent_id = Long.parseLong(parentInfo.get("id"));
			this.authority_parent_id = parent_id;
		} else {
			title = this.doc.getXPathValue("/add/doc/field[@name='title']/text()");
			parent_id = mDAO.getParentID(this.repoLoc);
		}
		
		if (title.length() > 245){
			title = title.substring(0, 245);
		}
		
		
		if (oldMeta != null){
			newMeta = new Metadata(
					oldMeta.getId(),
					oldMeta.getParentId(),
					Integer.parseInt(this.typeId),
					title,
					this.file.getName(),
					this.file.getAbsolutePath(),
					this.file.length(),
					this.file.lastModified(),
					oldMeta.getStatusTypeId(),
					1);
		} else {
			newMeta = new Metadata(
					-1,
					parent_id,
					Integer.parseInt(this.typeId),
					title,
					this.file.getName(),
					this.file.getAbsolutePath(),
					this.file.length(),
					this.file.lastModified(),
					mDAO.inferStatusTypeId(repoLoc, ext),
					1);
		}
		
		return newMeta;
	}
	
	
	public void indexAuthorities(Document doc){
		AuthorityDAO authDAO = new AuthorityDAO();
		String prevVal = null;
		
		if ((".txt").equals(this.ext) || (".tags.xml").equals(this.ext)){
			this.authority_item_id = this.id;
		}
		
		try {
			NodeList nodes = doc.getElementsByTagName("field");
			for (int i=0; i < nodes.getLength(); i++){
				String fieldName = nodes.item(i).getAttributes().getNamedItem("name").getNodeValue();
				String fieldValue = nodes.item(i).getTextContent();
				if (i > 0){
					prevVal = nodes.item(i-1).getTextContent();
				}
				// Ensure there's a column in the Database that matched the authority label 
				// and that the current authority isn't a duplicate of the previous one
				// which is a problem in the EAD XML files.
				if (Database.isAuthority(fieldName) && !fieldValue.equals(prevVal)){
					String value = nodes.item(i).getTextContent();
					if (value.length() != 0 && !("").equals(value)) {
						Authority auth = new Authority((long) -1, this.authority_parent_id, this.authority_item_id, Database.getAuthorityTypeID(fieldName), fieldValue);
						authDAO.insert(auth);
					}
				}
			}
		} catch (DOMException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void indexAuthorities(Document doc, Boolean deletePrev){
		int type_id = 0;
		AuthorityDAO authDAO = null;
		AuthorityTypeDAO atDAO = null;
		try {
			if (deletePrev == true){
				authDAO = new AuthorityDAO();
				atDAO = new AuthorityTypeDAO();
				if ((".txt").equals(this.ext)){
					type_id = atDAO.getTypeId("transcript");
					authDAO.removeAllByType(this.authority_parent_id, type_id);
				}
				else if((".tags.xml").equals(this.ext)){
					type_id = atDAO.getTypeId("tag");
					authDAO.removeAllByType(this.authority_parent_id, type_id);
				}
				else{
					authDAO.removeAll(this.authority_parent_id);
				}
			}
			indexAuthorities(doc);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			authDAO = null;
			atDAO = null;
		}
	}
}